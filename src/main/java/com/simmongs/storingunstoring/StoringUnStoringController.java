package com.simmongs.storingunstoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "storingunstoring")
public class StoringUnStoringController {

    private final StoringUnStoringRepository storingUnStoringRepository;

    @PostMapping("registration")
    public StoringUnStoring StoringUnStoringRegistration(@RequestBody StoringUnStoring storingUnStoring) {
        return storingUnStoringRepository.save(storingUnStoring);
    }

    @GetMapping("showAll")
    public List<StoringUnStoring> ShowAllStoringUnStoring() {
        return storingUnStoringRepository.findAll();
    }


}
