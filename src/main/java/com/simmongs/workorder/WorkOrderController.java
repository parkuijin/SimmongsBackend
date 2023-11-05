package com.simmongs.workorder;

import com.simmongs.bom.BOMRepository;
import com.simmongs.bom.BOMs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "workorder")
public class WorkOrderController {

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderService workOrderService;

    @PostMapping("registration") // 작업지시 등록
    public Map<String, Object> WorkOrderRegistration(@RequestBody String json) throws JSONException {

        Map<String, Object> response = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JSONObject obj = new JSONObject(json);

        String departmentName = obj.getString("departmentName");
        LocalDateTime workStartDate = LocalDateTime.parse(obj.getString("workStartDate"), formatter);
        String productCode = obj.getString("productCode");
        int workTargetQuantity = Integer.parseInt(obj.getString("workTargetQuantity"));
        LocalDateTime workDeadline = LocalDateTime.parse(obj.getString("workDeadline"), formatter);
        String workStatus = obj.getString("workStatus");

        switch (workOrderService.workOrderRegistration(departmentName, workStartDate, productCode, workTargetQuantity, workDeadline, workStatus)){
            case -1:
                response.put("success", false);
                response.put("message", "부서 이름이 유효하지 않습니다.");
                return response;
            case -2:
                response.put("success", false);
                response.put("message", "제품 코드가 유효하지 않습니다.");
                return response;
            case -3:
                response.put("success", false);
                response.put("message", "목표 수량이 1개 이상이어야 합니다.");
                return response;
            case -4:
                response.put("success", false);
                response.put("message", "해당 제품의 BOM이 등록되어 있지 않습니다.");
                return response;
            case -5:
                response.put("success", false);
                response.put("message", "부품 총필요량이 현재 재고보다 많습니다.");
                return response;
            case -6:
                response.put("success", false);
                response.put("message", "작업지시 코드 생성이 실패했습니다.");
                return response;
            case 0:
                response.put("success", true);
                return response;
        }

        return null;
    }

    @GetMapping("showAll") // 작업지시 전체 조회
    public List<WorkOrders> ShowAllWorkOrders(){
        return workOrderRepository.findAll();
    }

}
