package com.simmongs.workorder;

import static com.simmongs.workorder.QWorkOrders.workOrders;
import static com.simmongs.product.QProducts.products;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class WorkOrderRepositoryImpl implements WorkOrderRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SearchWorkOrderDto> findBySearchOption(String workOrderId, LocalDateTime startDate, LocalDateTime deadline, String productCode, String departmentName, String workStatus, String productName) {

        List<SearchWorkOrderDto> result = jpaQueryFactory
                .select(new QSearchWorkOrderDto(workOrders.workOrderId, workOrders.departmentName.coalesce("NA"), workOrders.workStartDate, workOrders.workDeadline, workOrders.productCode, products.productName.coalesce("NA"), products.productUnit.coalesce("NA"), workOrders.workCurrentQuantity, workOrders.workTargetQuantity, workOrders.workStatus))
                .from(workOrders)
                .leftJoin(products).on(workOrders.productCode.eq(products.productCode))
                .where(containsWorkOrderId(workOrderId), betweenWorkDeadline(startDate, deadline), containsProductCode(productCode), eqDepartmentName(departmentName), eqWorkStatus(workStatus), containsProductName(productName))
                .fetch();

        return result;
    }

    private BooleanExpression containsWorkOrderId(String workOrderId) {
        if (workOrderId == null || workOrderId.isEmpty()) {
            return null;
        }
        return workOrders.workOrderId.contains(workOrderId);
    }

    private BooleanExpression betweenWorkDeadline(LocalDateTime startDate, LocalDateTime deadline) {
        if (startDate == null || deadline == null) {
            return null;
        }
        return workOrders.workDeadline.between(startDate, deadline);
    }

    private BooleanExpression containsProductCode(String productCode) {
        if (productCode == null || productCode.isEmpty()) {
            return null;
        }
        return workOrders.productCode.contains(productCode);
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

    private BooleanExpression containsProductName(String productName) {
        if (productName == null || productName.isEmpty())
            return null;

        return products.productName.contains(productName);
    }

}
