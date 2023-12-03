package com.simmongs.workperformance;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkPerformanceRepositoryCustom {
    List<SearchWorkPerformanceDto> findBySearchOption(String workOrderId, LocalDateTime startDate, LocalDateTime endDate);
}
