package com.simmongs.bom;

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
@RequestMapping(value = "bom")
public class BOMController {

    private final BOMRepository bomRepository;
    private final BOMService bomService;

    @PostMapping("registration") // BOM 정보 등록
    public Map<String, Object> BOMRegistration(@RequestBody String json){
        JSONObject obj = new JSONObject(json);
        return bomService.uploadBOM(obj);
    }

    @GetMapping("showAll") // BOM 전체 조회
    public List<BOMs> ShowAllBOM(){
        return bomRepository.findAll();
    }

    @GetMapping("productList") // 제품 리스트 출력
    public List<HashMap<String, Object>> showProductList(){

        return bomService.showProductList();
    }

    @PostMapping("selectProduct")
    public Map<String, Object> selectedProductInfo(@RequestBody String json) throws JSONException {
        JSONObject obj = new JSONObject(json);

        return bomService.selectedProductInfo(obj);
    }

    @DeleteMapping("delete")
    public Map<String, Object> deleteBOM(@RequestBody String json) throws JSONException {
        JSONObject obj = new JSONObject(json);

        return bomService.deleteBOM(obj);
    }
}
