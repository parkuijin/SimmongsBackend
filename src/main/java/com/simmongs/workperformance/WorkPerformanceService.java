package com.simmongs.workperformance;

import com.simmongs.product.ProductRepository;
import com.simmongs.product.Products;
import com.simmongs.workorder.WorkOrderRepository;
import com.simmongs.workorder.WorkOrders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public int uploadWorkPerformance(Long workOrderId, Integer currentWorkload, String usedProductCode, Integer usedProductAmount) {

        if( workOrderId == null || currentWorkload == null || usedProductCode.trim().equals("") || usedProductAmount == null )
            return -1;

        Optional<WorkOrders> workOrders = workOrderRepository.findByWorkOrderId(workOrderId);
        if (!workOrders.isPresent()) // WorkOrder 없을 경우
            return -2;

        Optional<Products> products = productRepository.findByProductCode(usedProductCode);
        if (!products.isPresent()) // UsedProductCode 없을 경우
            return -3;

        LocalDateTime workPerformanceDate = LocalDateTime.now();

        WorkPerformance workPerformance = new WorkPerformance(workOrderId, currentWorkload, usedProductCode, usedProductAmount, workPerformanceDate);
        workPerformanceRepository.save(workPerformance);

        return 0;
    }

}
