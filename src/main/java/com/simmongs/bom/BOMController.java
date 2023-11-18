package com.simmongs.bom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

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
}
