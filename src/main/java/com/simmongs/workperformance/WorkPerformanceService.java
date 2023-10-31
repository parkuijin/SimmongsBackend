package com.simmongs.workperformance;

import com.simmongs.product.ProductRepository;
import com.simmongs.product.Products;
import com.simmongs.workorder.WorkOrderRepository;
import com.simmongs.workorder.WorkOrders;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class WorkPerformanceService {

    private final WorkPerformanceRepository workPerformanceRepository;
    private final WorkOrderRepository workOrderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public List<WorkPerformance> findWorkOrderById(Long id) {
        List<WorkPerformance> workPerformanceList = workPerformanceRepository.findByWorkOrderId(id);

        return workPerformanceList;
    }

    @Transactional
    public int uploadWorkPerformance(Long workOrderId, Integer currentWorkload, JSONArray usedProduct) {

        for(int i=0; i<usedProduct.length(); i++) {

            JSONObject obj = usedProduct.getJSONObject(i);

            String usedProductCode = obj.getString("usedProductCode");
            Integer usedProductAmount = Integer.parseInt(obj.getString("usedProductAmount"));

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

        /*if (workOrders.getWorkCurrentQuantity() + currentWorkload >= workOrders.getWorkTargetQuantity())
            return -7; // 목표 수량 초과 제작*/

        for(int i=0; i<usedProduct.length(); i++) {

            JSONObject obj = usedProduct.getJSONObject(i);

            String usedProductCode = obj.getString("usedProductCode");
            Integer usedProductAmount = Integer.parseInt(obj.getString("usedProductAmount"));

            // 작업 실적 등록
            LocalDateTime workPerformanceDate = LocalDateTime.now();
            WorkPerformance workPerformance = new WorkPerformance(workOrderId, currentWorkload, usedProductCode, usedProductAmount, workPerformanceDate);
            workPerformanceRepository.save(workPerformance);

            // 부품 재고 개수 차감
            Products products = productRepository.getByProductCode(usedProductCode);
            products.amountSub(usedProductAmount);
        }

        // 제품 재고 개수 증가
        Products products = productRepository.getByProductCode(workOrders.getProductCode());
        products.amountAdd(currentWorkload);

        // 작업 지시의 현재 작업량 갱신
        workOrders.workCurrentQuantityUpdate(currentWorkload);

        return 0;
    }

}
