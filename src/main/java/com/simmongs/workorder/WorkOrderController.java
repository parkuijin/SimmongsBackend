package com.simmongs.workorder;

import com.querydsl.core.Tuple;
import com.simmongs.bom.BOMRepository;
import com.simmongs.bom.BOMs;
import com.simmongs.product.ProductRepository;
import com.simmongs.product.Products;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "workorder")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @PostMapping("mrpCalculation") // 제품 목표수량 입력하면 필요한 부품 총개수 계산
    public List<HashMap<String, Object>> MRPCalculation(@RequestBody String json) throws JSONException {
        JSONObject obj = new JSONObject(json);

        return workOrderService.mrpCalculation(obj);
    }

    @PostMapping("registration") // 작업지시 등록
    public Map<String, Object> WorkOrderRegistration(@RequestBody String json) throws JSONException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JSONObject obj = new JSONObject(json);

        String departmentName = obj.getString("departmentName");
        LocalDateTime workStartDate = LocalDateTime.parse(obj.getString("workStartDate"), formatter);
        String productCode = obj.getString("productCode");
        int workTargetQuantity = Integer.parseInt(obj.getString("workTargetQuantity"));
        LocalDateTime workDeadline = LocalDateTime.parse(obj.getString("workDeadline"), formatter);
        JSONArray neededProductArray = obj.getJSONArray("neededProduct");

        return workOrderService.workOrderRegistration(departmentName, workStartDate, productCode, workTargetQuantity, workDeadline, neededProductArray);
    }

    @DeleteMapping("delete") // 작업지시 삭제
    public Map<String, Object> WorkOrderDelete(@RequestBody String json) throws JSONException {
        JSONObject obj = new JSONObject(json);
        String workOrderId = obj.getString("workOrderId");

        return workOrderService.workOrderDelete(workOrderId);
    }

    @GetMapping("showAll") // 작업지시 전체 조회
    public List<HashMap<String, Object>> ShowAllWorkOrders(){
        return workOrderService.ShowAllWorkOrders();
    }

    @PostMapping("stopWorkOrder") // 작업지시 중단
    public Map<String, Object> stopWorkOrder(@RequestBody String json) throws JSONException {
        JSONObject obj = new JSONObject(json);

        return workOrderService.stopWorkOrder(obj);
    }

    @PostMapping("searchWorkOrder") // 작업지시 검색
    public List<SearchWorkOrderDto> searchWorkOrder(@RequestBody String json) throws JSONException {
        JSONObject obj = new JSONObject(json);

        return workOrderService.searchWorkOrder(obj);
    }
}
