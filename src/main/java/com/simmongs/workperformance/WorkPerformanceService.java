package com.simmongs.workperformance;

import com.simmongs.bom.BOMRepository;
import com.simmongs.bom.BOMs;
import com.simmongs.mrp.MRPRepository;
import com.simmongs.mrp.MRPs;
import com.simmongs.product.ProductRepository;
import com.simmongs.product.Products;
import com.simmongs.workorder.WorkOrderRepository;
import com.simmongs.workorder.WorkOrders;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class WorkPerformanceService {

    private final WorkPerformanceRepository workPerformanceRepository;
    private final WorkOrderRepository workOrderRepository;
    private final ProductRepository productRepository;
    private final MRPRepository mrpRepository;
    private final BOMRepository bomRepository;

    @Transactional
    public int uploadWorkPerformance(JSONObject obj) {

        JSONArray workPerformanceList = obj.getJSONArray("workPerformanceList");
        String workNumber = "";

        // 오류 처리
        for (int k = 0; k < workPerformanceList.length(); k++) {
            JSONObject workPerformanceJsonObject = workPerformanceList.getJSONObject(k);
            String workOrderId = workPerformanceJsonObject.getString("workOrderId");
            Integer currentWorkload = workPerformanceJsonObject.getInt("currentWorkload");
            JSONArray usedProductList = workPerformanceJsonObject.getJSONArray("usedProduct");

            for (int i = 0; i < usedProductList.length(); i++) {
                JSONObject usedProduct = usedProductList.getJSONObject(i);

                String usedProductCode = usedProduct.getString("usedProductCode");
                Integer usedProductAmount = Integer.parseInt(usedProduct.getString("usedProductAmount"));

                if (workOrderId == null || currentWorkload == null || usedProductCode.trim().equals("") || usedProductAmount == null)
                    return -1; // null 체크

                Optional<Products> productsCheck = productRepository.findByProductCode(usedProductCode);
                if (!productsCheck.isPresent())
                    return -3; // UsedProductCode 없을 경우

                Products products = productRepository.getByProductCode(usedProductCode);
                if (products.getProductAmount() <= 0)
                    return -5; // 재고가 부족할 경우
            }

            Optional<WorkOrders> workOrdersCheck = workOrderRepository.findByWorkOrderId(workOrderId);
            if (!workOrdersCheck.isPresent())
                return -2; // WorkOrder 없을 경우

            if (currentWorkload <= 0)
                return -4; // 작업량 0 이하일 경우

            WorkOrders workOrders = workOrderRepository.getByWorkOrderId(workOrderId);
            if (workOrders.getWorkTargetQuantity() <= workOrders.getWorkCurrentQuantity())
                return -6; // 목표 수량이 이미 달성된 경우
            else if (workOrders.getWorkStatus().equals("중단")) {
                return -7; // 중단된 작업 지시일 경우
            }
        }

        for (int k = 0; k < workPerformanceList.length(); k++) {
            JSONObject workPerformanceJsonObject = workPerformanceList.getJSONObject(k);
            String workOrderId = workPerformanceJsonObject.getString("workOrderId");
            Integer currentWorkload = workPerformanceJsonObject.getInt("currentWorkload");
            JSONArray usedProductList = workPerformanceJsonObject.getJSONArray("usedProduct");

            // 작업번호 생성
            for (int j = 1; j < 10000; j++) {
                workNumber = "WP" + String.format("%05d", j);
                if (!workPerformanceRepository.findByWorkNumber(workNumber).isEmpty())
                    continue; // 작업번호가 존재시 다음 번호 생성
                else break;
            }

            for (int i = 0; i < usedProductList.length(); i++) {
                JSONObject usedProduct = usedProductList.getJSONObject(i);

                String usedProductCode = usedProduct.getString("usedProductCode");
                Integer usedProductAmount = Integer.parseInt(usedProduct.getString("usedProductAmount"));

                // 작업 실적 등록
                LocalDateTime workPerformanceDate = LocalDateTime.now();
                WorkPerformance workPerformance = new WorkPerformance(workNumber, workOrderId, currentWorkload, usedProductCode, usedProductAmount, workPerformanceDate);
                workPerformanceRepository.save(workPerformance);

                // 부품 재고 개수 차감
                Products products = productRepository.getByProductCode(usedProductCode);
                products.amountSub(usedProductAmount);

                // MRP 테이블 사용한 부품 개수 증가
                MRPs mrps = mrpRepository.getByNeededProductCode(workOrderId, usedProductCode);
                mrps.currentUsedProductAmountAdd(usedProductAmount);
            }

            WorkOrders workOrders = workOrderRepository.getByWorkOrderId(workOrderId);

            // 제품 재고 개수 증가
            Products products = productRepository.getByProductCode(workOrders.getProductCode());
            products.amountAdd(currentWorkload);

            // 작업 지시의 현재 작업량 갱신
            workOrders.workCurrentQuantityAdd(currentWorkload);

            // 작업 지시의 상태를 변경
            if (workOrders.getWorkCurrentQuantity() >= 1 && workOrders.getWorkCurrentQuantity() < workOrders.getWorkTargetQuantity()) {
                workOrders.underWayWorkOrder(); // 작업상태 진행으로 변경 : 1 <= currentQuantity < targetQuantity
            } else if (workOrders.getWorkCurrentQuantity() == workOrders.getWorkTargetQuantity()) {
                workOrders.completeWorkOrder(); // 작업상태 완료로 변경 : currentQuantity = targetQuantity
            } else if (workOrders.getWorkCurrentQuantity() >= workOrders.getWorkTargetQuantity()) {
                workOrders.overWorkOrder(); // 작업상태 초과로 변경 : currentQuantity <= targetQuantity
            }
        }

        return 0;
    }

    @Transactional
    public Map<String, Object> deleteWorkPerformance(JSONObject obj) throws JSONException {
        Map<String, Object> response = new HashMap<>();

        String workNumber = obj.getString("workNumber");
        String workOrderId = obj.getString("workOrderId");
        int currentWorkload = obj.getInt("currentWorkload");

        WorkOrders workOrders = workOrderRepository.getByWorkOrderId(workOrderId);
        Products products = productRepository.getByProductCode(workOrderRepository.getByWorkOrderId(workOrderId).getProductCode());
        List<WorkPerformance> workPerformanceList = workPerformanceRepository.findByWorkNumber(workNumber);

        for (int i = 0; i < workPerformanceList.size(); i++) {
            WorkPerformance workPerformance = workPerformanceList.get(i);
            currentWorkload = workPerformance.getCurrentWorkload();

            // 부품 재고 증가
            Products components = productRepository.getByProductCode(workPerformance.getUsedProductCode());
            components.amountAdd(workPerformance.getUsedProductAmount());

            // MRP 테이블 사용한 부품 개수 차감
            MRPs mrps = mrpRepository.getByNeededProductCode(workPerformance.getWorkOrderId(), workPerformance.getUsedProductCode());
            mrps.currentUsedProductAmountSub(workPerformance.getUsedProductAmount());

            // 작업실적 삭제
            workPerformanceRepository.delete(workPerformance);

        }

        // 제품 재고 차감
        products.amountSub(currentWorkload);

        // 작업지시 진행량 차감
        workOrders.workCurrentQuantitySub(currentWorkload);

        response.put("success", true);
        return response;
    }

    @Transactional
    public List<HashMap<String, Object>> MRPCalculation(JSONArray mrpCalcArr) throws JSONException {

        List<HashMap<String, Object>> response = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < mrpCalcArr.length(); i++) {
            JSONObject obj = mrpCalcArr.getJSONObject(i);
            String workOrderId = obj.getString("workOrderId");
            int currentWorkload = obj.getInt("currentWorkload");

            List<BOMs> boMsList = bomRepository.findByProductCode(workOrderRepository.getByWorkOrderId(workOrderId).getProductCode());
            for (BOMs boms : boMsList) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("workOrderId", workOrderId);
                hashMap.put("usedProductCode", boms.getChildProductCode());
                hashMap.put("usedProductName", productRepository.getByProductCode(boms.getChildProductCode()).getProductName());
                hashMap.put("usedProductUnit", productRepository.getByProductCode(boms.getChildProductCode()).getProductUnit());
                hashMap.put("currentUsedProductAmount", mrpRepository.getByNeededProductCode(workOrderId, boms.getChildProductCode()).getCurrentUsedProductAmount());
                hashMap.put("totalNeededProductAmount", mrpRepository.getByNeededProductCode(workOrderId, boms.getChildProductCode()).getTotalNeededProductAmount());
                hashMap.put("usedProductAmount", boms.getBomAmount() * currentWorkload);
                response.add(hashMap);
            }
        }

        return response;
    }

    @Transactional
    public List<Map<String, Object>> showWorkPerformance(JSONObject obj) throws JSONException {
    String workOrderId = obj.getString("workOrderId");

    return workPerformanceRepository.searchWorkPerformanceByWorkOrderId(workOrderId);
    }

    @Transactional List<Map<String, Object>> showUsedComponent(JSONObject obj) throws JSONException {
        List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();

        String workNumber = obj.getString("workNumber");

        List<WorkPerformance> workPerformanceList = workPerformanceRepository.findByWorkNumber(workNumber);
        for (WorkPerformance workPerformance : workPerformanceList) {
            Map<String, Object> map = new HashMap<>();
            map.put("componentName", productRepository.getByProductCode(workPerformance.getUsedProductCode()).getProductName());
            map.put("componentCode",workPerformance.getUsedProductCode());
            map.put("componentUnit", productRepository.getByProductCode(workPerformance.getUsedProductCode()).getProductUnit());
            map.put("usedComponentAmount", workPerformance.getUsedProductAmount());
            response.add(map);
        }

        return response;
    }
}
