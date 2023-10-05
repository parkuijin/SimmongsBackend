package com.simmongs.bom;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor // Final 객체를 Constructor Injection 해준다 (Autowired 역할)
@RequestMapping(value = "bom")
public class BOMController {

    private final BOMRepository bomRepository;

    @PostMapping("registration") // BOM 정보 등록
    public BOM BOMRegistration(@RequestBody BOM bom){
        return bomRepository.save(bom);
    }

    @GetMapping("showAll") // BOM 전체 조회
    public List<BOM> ShowAllBOM(){
        return bomRepository.findAll();
    }
}
