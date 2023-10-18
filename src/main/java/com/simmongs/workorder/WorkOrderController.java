package com.simmongs.workorder;

import com.simmongs.bom.BOMRepository;
import com.simmongs.bom.BOMs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "workorder")
public class WorkOrderController {

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderService workOrderService;

    @PostMapping("registration") // 작업지시 등록
    public WorkOrders WorkOrderRegistration(@RequestBody WorkOrders workOrders){
        return workOrderRepository.save(workOrders);
    }

    @GetMapping("showAll") // 작업지시 전체 조회
    public List<WorkOrders> ShowAllWorkOrders(){
        return workOrderRepository.findAll();
    }

}
