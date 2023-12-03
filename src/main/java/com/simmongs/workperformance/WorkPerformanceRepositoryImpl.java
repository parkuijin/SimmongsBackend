package com.simmongs.workperformance;

import static com.simmongs.workperformance.QWorkPerformance.workPerformance;
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
public class WorkPerformanceRepositoryImpl implements WorkPerformanceRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SearchWorkPerformanceDto> findBySearchOption(String workOrderId, LocalDateTime startDate, LocalDateTime endDate) {

        List<SearchWorkPerformanceDto> result = jpaQueryFactory
                .select(new QSearchWorkPerformanceDto(products.productName.coalesce("NA"), workPerformance.workOrderId, workPerformance.workNumber, workPerformance.workPerformanceDate, workPerformance.currentWorkload))
                .from(workPerformance)
                .leftJoin(workOrders).on(workPerformance.workOrderId.eq(workOrders.workOrderId))
                .leftJoin(products).on(products.productCode.eq(workOrders.productCode))
                .where(eqWorkOrderId(workOrderId), betweenDate(startDate, endDate))
                .distinct()
                .fetch();

        return result;

    }

    private BooleanExpression eqWorkOrderId(String workOrderId) {
        if (workOrderId == null || workOrderId.isEmpty()) {
            return null;
        }
        return workPerformance.workOrderId.eq(workOrderId);
    }

    private BooleanExpression betweenDate(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) {
            return null;
        } else if (startDate != null && endDate == null) {
            return workPerformance.workPerformanceDate.goe(startDate); // >= startDate
        } else if (startDate == null && endDate != null) {
            return workPerformance.workPerformanceDate.loe(endDate); // <= endDate
        } else if (startDate != null && endDate != null) {
            return workPerformance.workPerformanceDate.between(startDate, endDate); // startDate <=, <= endDate
        }
            return null;
    }

}
