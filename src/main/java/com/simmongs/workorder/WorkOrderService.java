package com.simmongs.workorder;

import com.simmongs.bom.BOMRepository;
import com.simmongs.bom.BOMs;
import com.simmongs.department.DepartmentRepository;
import com.simmongs.mrp.MRPRepository;
import com.simmongs.mrp.MRPs;
import com.simmongs.product.ProductRepository;
import com.simmongs.product.Products;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final DepartmentRepository departmentRepository;
    private final ProductRepository productRepository;
    private final BOMRepository bomRepository;
    private final MRPRepository mrpRepository;

    @Transactional
    public Map<String, Object> workOrderRegistration (String departmentName, LocalDateTime workStartDate, String productCode, int workTargetQuantity, LocalDateTime workDeadline, String workStatus, JSONArray neededProductArray) throws JSONException {

        Map<String, Object> response = new HashMap<>();

        if (!departmentRepository.findByDepartmentName(departmentName).isPresent()) {
            response.put("success", false);
            response.put("message", "부서 이름이 유효하지 않습니다.");
            return response; // departmentName 없을 경우
        }
        if (!productRepository.findByProductCode(productCode).isPresent()) {
            response.put("success", false);
            response.put("message", "제품 코드가 유효하지 않습니다.");
            return response; // productCode 없을 경우
        }
        if (workTargetQuantity <= 0) {
            response.put("success", false);
            response.put("message", "목표 수량이 1개 이상이어야 합니다.");
            return response; // 목표 수량이 0 이하일 경우
        }
        List<BOMs> boMsList = bomRepository.findByProductCode(productCode);
        if (boMsList.isEmpty()) {
            response.put("success", false);
            response.put("message", "해당 제품의 BOM이 등록되어 있지 않습니다.");
            return response; // 선택한 제품의 BOM이 없을 경우
        }
        if (!boMsList.isEmpty())
            for (BOMs boMs : boMsList) {
                String usedProductCode = boMs.getChildProductCode();
                int usedProductAmount = boMs.getBomAmount() * workTargetQuantity;
                int currentAmount = productRepository.getByProductCode(usedProductCode).getProductAmount();
                if (usedProductAmount > currentAmount) {
                    response.put("success", false);
                    response.put("message", "부품 총필요량이 현재 재고보다 많습니다.");
                    return response; // 부품 총필요량이 현재 재고보다 적을 경우
                }
            }

        // 작업지시 코드 생성
        String workOrderId = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = Date.from(workStartDate.atZone(ZoneId.systemDefault()).toInstant());
        String frontPartWorkOrderId = simpleDateFormat.format(date) + productCode;
        for (int i=1; i<10000; i++) {
            String newWorkOrderId = frontPartWorkOrderId + String.format("%05d", i);
            if (workOrderRepository.findByWorkOrderId(newWorkOrderId).isPresent())
                continue;
            else if (!workOrderRepository.findByWorkOrderId(newWorkOrderId).isPresent()) {
                if (newWorkOrderId == null) {
                    response.put("success", false);
                    response.put("message", "작업지시 코드 생성이 실패했습니다.");
                    return response; // 작업지시 코드 생성 실패했을 경우
                }
                else {
                    // 작업지시 등록
                    WorkOrders workOrders = new WorkOrders(newWorkOrderId, departmentName, workStartDate, productCode, workTargetQuantity, workDeadline, workStatus);
                    workOrderId = workOrderRepository.save(workOrders).getWorkOrderId();
                    response.put("success", true);
                    response.put("id", workOrderId);
                    break;
                }
            }
        }

        // MRP 등록
        for(int i=0; i<neededProductArray.length(); i++) {
            JSONObject obj = neededProductArray.getJSONObject(i);

            String neededProductCode = obj.getString("neededProductCode");
            int neededProductAmount = Integer.parseInt(obj.getString("neededProductAmount"));

            if (workOrderId.equals("")) {
                response.put("success", false);
                response.put("message", "작업지시 생성이 실패했습니다.");
                return response; // 작업지시 ID가 없어 MRP 등록에 실패할 경우
            }
            MRPs mrps = new MRPs(workOrderId, neededProductCode, neededProductAmount, 0 );
            mrpRepository.save(mrps);
        }

        return response;
    }

    @Transactional
    public List<HashMap<String, Object>> mrpCalculation (String productCode, Integer workTargetQuantity) {
        List<HashMap<String, Object>> response = new ArrayList<HashMap<String, Object>>();
        List<BOMs> boMsList = bomRepository.findByProductCode(productCode);

        for (BOMs boms : boMsList) {
            HashMap<String, Object> hashMap = new HashMap<>();
            Products products = productRepository.getByProductCode(boms.getChildProductCode());
            hashMap.put("neededProductCode", boms.getChildProductCode());
            hashMap.put("neededProductName", products.getProductName());
            hashMap.put("neededProductUnit", products.getProductUnit());
            hashMap.put("neededProductAmount", boms.getBomAmount()*workTargetQuantity);
            hashMap.put("CurrentProductAmount", products.getProductAmount());
            response.add(hashMap);
        }

        return response;
    }

    @Transactional
    public List<WorkOrders> ShowAllWorkOrders() {

        List<WorkOrders> workOrdersList = workOrderRepository.findAll();
        List<WorkOrders> responseList = new ArrayList<>();

        // 작업 상태가 중단, 완료 제외
        for (int i=0; i<workOrdersList.size(); i++) {
            WorkOrders workOrders = workOrdersList.get(i);
            switch (workOrders.getWorkStatus()) {
                case "중단": case "완료":
                    break;
                default:
                    responseList.add(workOrders);
            }
        }
        return responseList;
    }

}
