package com.simmongs.storingunstoring;

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
@RequestMapping(value = "storingunstoring")
public class StoringUnStoringController {

    private final StoringUnStoringRepository storingUnStoringRepository;
    private final StoringUnStoringService storingUnStoringService;

    @PostMapping("registration") // 입출고 등록
    public Map<String, Object> StoringUnStoringRegistration(@RequestBody String json) throws JSONException {

        Map<String, Object> response = new HashMap<>();
        JSONObject obj = new JSONObject(json);

        switch (storingUnStoringService.RegStoringUnStoring(obj)) {
            case -1:
                response.put("success", true);
                response.put("message", "출고할 제품 개수가 부족합니다.");
                return response;
            case -2:
                response.put("success", true);
                response.put("message", "부품만 발주할 수 있습니다.");
                return response;
            case 0:
                response.put("success", true);
                return response;
        }

        return null;
    }

    @GetMapping("showAll")
    public List<StoringUnStoring> ShowAllStoringUnStoring() {
        return storingUnStoringRepository.findAll();
    }

    @PostMapping("searchStoringUnstoring")
    public List<StoringUnStoring> SearchStoringUnStoring(@RequestBody String json) throws JSONException {
        JSONObject obj = new JSONObject(json);

        return storingUnStoringService.searchStoringUnstoring(obj);
    }

}
