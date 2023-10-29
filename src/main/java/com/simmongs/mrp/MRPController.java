package com.simmongs.mrp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "mrp")
public class MRPController {

    private final MRPRepository mrpRepository;

    @GetMapping("showAll")
    public List<MRPs> showAll() {
        return mrpRepository.findAll();
    }
}
