package com.simmongs.workorder;

import static com.simmongs.workorder.QWorkOrders.workOrders;
import static com.simmongs.product.QProducts.products;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.simmongs.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class WorkOrderRepositoryImpl implements WorkOrderRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SearchWorkOrderDto> findBySearchOption(String workOrderId, LocalDateTime startDate, LocalDateTime deadline, String productCode, String departmentName, String workStatus) {

        List<SearchWorkOrderDto> result = jpaQueryFactory
                .select(new QSearchWorkOrderDto(workOrders.workOrderId, workOrders.departmentName.coalesce("NA"),workOrders.workStartDate, workOrders.workDeadline, workOrders.productCode, products.productName.coalesce("NA"), products.productUnit.coalesce("NA"), workOrders.workCurrentQuantity, workOrders.workTargetQuantity, workOrders.workStatus))
                .from(workOrders)
                .leftJoin(products).on(workOrders.productCode.eq(products.productCode))
                .where(eqWorkOrderId(workOrderId), betweenWorkDeadline(startDate, deadline), eqProductCode(productCode), eqDepartmentName(departmentName), eqWorkStatus(workStatus))
                .fetch();

        return result;
    }

    private BooleanExpression eqWorkOrderId(String workOrderId) {
        if (workOrderId == null || workOrderId.isEmpty()) {
            return null;
        }
        return workOrders.workOrderId.eq(workOrderId);
    }

    private BooleanExpression betweenWorkDeadline(LocalDateTime startDate, LocalDateTime deadline) {
        if (startDate == null || deadline == null) {
            return null;
        }
        return workOrders.workDeadline.between(startDate, deadline);
    }

    private BooleanExpression eqProductCode(String productCode) {
        if (productCode == null || productCode.isEmpty()) {
            return null;
        }
        return workOrders.productCode.eq(productCode);
    }

    private BooleanExpression eqDepartmentName(String departmentName) {
        if (departmentName == null || departmentName.isEmpty()) {
            return null;
        }
        return workOrders.departmentName.eq(departmentName);
    }

    private BooleanExpression eqWorkStatus(String workStatus) {
        if (workStatus == null || workStatus.isEmpty()) {
            return null;
        }
        return workOrders.workStatus.eq(workStatus);
    }

}
