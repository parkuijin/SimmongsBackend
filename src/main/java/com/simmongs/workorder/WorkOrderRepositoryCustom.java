package com.simmongs.workorder;

import com.querydsl.core.Tuple;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkOrderRepositoryCustom {
    List<SearchWorkOrderDto> findBySearchOption(String workOrderId, LocalDateTime startDate, LocalDateTime deadline, String productName, String departmentName, String workStatus);
}
