package com.simmongs.workorder;

import com.querydsl.core.Tuple;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkOrderRepositoryCustom {
    List<SearchWorkOrderDto> findBySearchOption(String workOrderId, LocalDateTime workStartDate, LocalDateTime workDeadline, String productName, String workStatus);
}
