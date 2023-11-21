package com.simmongs.storingunstoring;

import java.time.LocalDateTime;
import java.util.List;

public interface StoringUnStoringRepositoryCustom {

    List<StoringUnStoring> findBySearchOption(String productCode, String productName, String storingUnstoringType, String productType, LocalDateTime startDate, LocalDateTime endDate);
}
