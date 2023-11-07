package com.simmongs.workorder;

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

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderService workOrderService;
    private final BOMRepository bomRepository;
    private final ProductRepository productRepository;

    @PostMapping("mrpCalculation") // 제품 목표수량 입력하면 필요한 부품 총개수 계산
    public List<HashMap<String, Object>> MRPCalculation(@RequestBody String json) throws JSONException {

        JSONObject obj = new JSONObject(json);
        String productCode = obj.getString("productCode");
        Integer workTargetQuantity = Integer.parseInt(obj.getString("workTargetQuantity"));

        List<HashMap<String, Object>> response = new ArrayList<HashMap<String, Object>>();
        List<BOMs> boMsList = bomRepository.findByProductCode(productCode);

        for (BOMs boms : boMsList) {
            HashMap<String, Object> hashMap = new HashMap<>();
            Products products = productRepository.getByProductCode(boms.getChildProductCode());
            hashMap.put("neededProductCode", boms.getChildProductCode());
            hashMap.put("neededProductName", products.getProductName());
            hashMap.put("neededProductUnit", products.getProductUnit());
            hashMap.put("neededProductAmount", boms.getBomAmount()*workTargetQuantity);
            hashMap.put("CurrentProductAmount", products.getProductAmount());
            response.add(hashMap);
        }

        return response;
    }

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
        JSONArray neededProductArray = obj.getJSONArray("neededProduct");

        switch (workOrderService.workOrderRegistration(departmentName, workStartDate, productCode, workTargetQuantity, workDeadline, workStatus, neededProductArray)){
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
            case -7:
                response.put("success", false);
                response.put("message", "작업지시 생성이 실패했습니다.");
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
