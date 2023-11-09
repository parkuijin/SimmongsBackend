package com.simmongs.workperformance;

import com.simmongs.bom.BOMRepository;
import com.simmongs.bom.BOMs;
import com.simmongs.workorder.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @PostMapping("mrpCalculation") // 제품 완성 개수 입력하면 소모된 부품 개수 계산
    public List<HashMap<String, Object>> MRPCalculation(@RequestBody String json) throws JSONException {

        JSONObject obj = new JSONObject(json);
        JSONArray mrpCalcArr = obj.getJSONArray("workOrderList");

        return workPerformanceService.MRPCalculation(mrpCalcArr);
    }

    @PostMapping("registration") // 작업실적 등록
    public Map<String, Object> WorkPerformanceRegistration(@RequestBody String json) throws JSONException {
        Map<String, Object> response = new HashMap<>();

        JSONObject obj = new JSONObject(json);
        String workOrderId = obj.getString("workOrderId");
        Integer currentWorkload = Integer.parseInt(obj.getString("currentWorkload"));
        JSONArray usedProductArray = obj.getJSONArray("usedProduct");

        switch (workPerformanceService.uploadWorkPerformance(workOrderId, currentWorkload, usedProductArray)) {
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
            case -4:
                response.put("success", false);
                response.put("message", "작업량이 0을 초과해야 합니다.");
                return response;
            case -5:
                response.put("success", false);
                response.put("message", "재고가 부족합니다.");
                return response;
            case -6:
                response.put("success", false);
                response.put("message", "이미 완료된 작업지시입니다.");
                return response;
            case 0:
                response.put("success", true);
                return response;
        }

        return null;
    }

    @DeleteMapping("delete") // 실적 삭제
    public Map<String, Object> WorkPerformanceDelete(@RequestBody String json) throws JSONException {
        Map<String, Object> response = new HashMap<>();

        JSONObject obj = new JSONObject(json);
        Long workPerformanceId = obj.getLong("workPerformanceId");
        switch (workPerformanceService.deleteWorkPerformance(workPerformanceId)) {
            case 0:
                response.put("success", true);
                return response;
        }

        return null;
    }

    @GetMapping("showAll")
    public List<WorkPerformance> ShowAll() { return workPerformanceRepository.findAll(); }

    @PostMapping("showWorkPerformance") // WorkOrderId로 검색하여 작업실적 전체 조회
    public List<WorkPerformance> ShowWorkPerformance(@RequestBody String json){
        JSONObject obj = new JSONObject(json);

        String workOrderId = obj.getString("workOrderId");

        List<WorkPerformance> workPerformanceList = workPerformanceRepository.findByWorkOrderId(workOrderId);

        return workPerformanceList;
    }

}
