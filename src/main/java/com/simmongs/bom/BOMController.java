package com.simmongs.bom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "bom")
public class BOMController {

    private final BOMRepository bomRepository;

    @PostMapping("registration") // BOM 정보 등록
    public BOMs BOMRegistration(@RequestBody BOMs BOMs){
        return bomRepository.save(BOMs);
    }

    @GetMapping("showAll") // BOM 전체 조회
    public List<BOMs> ShowAllBOM(){
        return bomRepository.findAll();
    }
}
