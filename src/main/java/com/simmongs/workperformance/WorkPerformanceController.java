package com.simmongs.workperformance;

import com.simmongs.workorder.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "workperformance")
public class WorkPerformanceController {

    private final WorkPerformanceRepository workPerformanceRepository;
    private final WorkPerformanceService workPerformanceService;

    @PostMapping("registration") // 작업실적 등록
    public Map<String, Object> WorkPerformanceRegistration(@RequestBody String json) throws JSONException {

        Map<String, Object> response = new HashMap<>();

        JSONObject obj = new JSONObject(json);
        Long workOrderId = Long.parseLong(obj.getString("workOrderId"));
        Integer currentWorkload = Integer.parseInt(obj.getString("currentWorkload"));
        String usedProductCode = obj.getString("usedProductCode");
        Integer usedProductAmount = Integer.parseInt(obj.getString("usedProductAmount"));

        switch (workPerformanceService.uploadWorkPerformance(workOrderId, currentWorkload, usedProductCode, usedProductAmount)) {
            case -1:
                response.put("success", false);
                response.put("message", "빈칸을 모두 채워주세요.");
                return response;
            case -2:
                response.put("success", false);
                response.put("message", "작업지시가 존재하지 않습니다.");
                return response;
            case -3:
                response.put("success", false);
                response.put("message", "부품코드가 존재하지 않습니다.");
                return response;
            case 0:
                response.put("success", true);
                return response;
        }
        return null;
    }

    @GetMapping("showAll")
    public List<WorkPerformance> ShowAll() { return workPerformanceRepository.findAll(); }

    @GetMapping("showWorkPerformance") // Work_Order_Id로 검색하여 작업실적 전체 조회
    public List<WorkPerformance> ShowWorkPerformance(@RequestParam(value = "work_order_id") Long id){
        List<WorkPerformance> workPerformanceList = workPerformanceService.findWorkOrderById(id);

        return workPerformanceList;
    }

}
