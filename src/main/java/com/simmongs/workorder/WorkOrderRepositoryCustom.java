package com.simmongs.workorder;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkOrderRepositoryCustom {
    List<SearchWorkOrderDto> findBySearchOption(String workOrderId, LocalDateTime startDate, LocalDateTime deadline, String productCode, String departmentName, String workStatus);
}
