package com.simmongs.workorder;

import com.simmongs.bom.BOMRepository;
import com.simmongs.bom.BOMs;
import com.simmongs.department.DepartmentRepository;
import com.simmongs.mrp.MRPRepository;
import com.simmongs.mrp.MRPs;
import com.simmongs.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

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
    public int workOrderRegistration (String departmentName, LocalDateTime workStartDate, String productCode, int workTargetQuantity, LocalDateTime workDeadline, String workStatus, JSONArray neededProductArray) throws JSONException {

        if (!departmentRepository.findByDepartmentName(departmentName).isPresent())
            return -1; // departmentName 없을 경우
        if (!productRepository.findByProductCode(productCode).isPresent())
            return -2; // productCode 없을 경우
        if (workTargetQuantity <= 0)
            return -3; // 목표 수량이 0 이하일 경우
        List<BOMs> boMsList = bomRepository.findByProductCode(productCode);
        if (boMsList.isEmpty())
            return -4; // 선택한 제품의 BOM이 없을 경우
        if (!boMsList.isEmpty())
            for (BOMs boMs : boMsList) {
                String usedProductCode = boMs.getChildProductCode();
                int usedProductAmount = boMs.getBomAmount() * workTargetQuantity;
                int currentAmount = productRepository.getByProductCode(usedProductCode).getProductAmount();
                if (usedProductAmount > currentAmount)
                    return -5; // 부품 총필요량이 현재 재고보다 적을 경우
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
                if (newWorkOrderId == null)
                    return -6; // 작업지시 코드 생성 실패했을 경우
                else {
                    // 작업지시 등록
                    WorkOrders workOrders = new WorkOrders(newWorkOrderId, departmentName, workStartDate, productCode, workTargetQuantity, workDeadline, workStatus);
                    workOrderId = workOrderRepository.save(workOrders).getWorkOrderId();
                    break;
                }
            }
        }

        // MRP 등록
        for(int i=0; i<neededProductArray.length(); i++) {
            JSONObject obj = neededProductArray.getJSONObject(i);

            String neededProductCode = obj.getString("neededProductCode");
            int neededProductAmount = Integer.parseInt(obj.getString("neededProductAmount"));

            if (workOrderId.equals(""))
                return -7; // 작업지시 ID가 없어 MRP 등록에 실패할 경우

            MRPs mrps = new MRPs(workOrderId, neededProductCode, neededProductAmount, 0 );
            mrpRepository.save(mrps);
        }

        return 0;
    }
}
