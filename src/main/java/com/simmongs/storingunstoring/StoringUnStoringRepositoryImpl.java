package com.simmongs.storingunstoring;

import static com.simmongs.storingunstoring.QStoringUnStoring.storingUnStoring;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class StoringUnStoringRepositoryImpl implements StoringUnStoringRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<StoringUnStoring> findBySearchOption(String productCode, String productName, String storingUnstoringType, String productType, LocalDateTime startDate, LocalDateTime endDate) {

        List<StoringUnStoring> result = jpaQueryFactory
                .selectFrom(storingUnStoring)
                .where(containsProductCode(productCode), containsProductName(productName), eqStoringUnstoringType(storingUnstoringType), eqProductType(productType), betweenStoringUnstoringDate(startDate, endDate))
                .fetch();

        return result;
    }

    private BooleanExpression containsProductCode(String productCode) {
        if (productCode == null || productCode.isEmpty()) {
            return null;
        }
        return storingUnStoring.productCode.contains(productCode);
    }

    private BooleanExpression containsProductName(String productName) {
        if (productName == null || productName.isEmpty()) {
            return null;
        }
        return storingUnStoring.productName.contains(productName);
    }

    private BooleanExpression eqStoringUnstoringType(String storingUnstoringType) {
        if (storingUnstoringType == null || storingUnstoringType.isEmpty()) {
            return null;
        }
        return storingUnStoring.storingUnstoringType.eq(storingUnstoringType);
    }

    private BooleanExpression eqProductType(String productType) {
        if (productType == null || productType.isEmpty()) {
            return null;
        }
        return storingUnStoring.productType.eq(productType);
    }

    private BooleanExpression betweenStoringUnstoringDate(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        return storingUnStoring.storingUnstoringDate.between(startDate, endDate);
    }

}
