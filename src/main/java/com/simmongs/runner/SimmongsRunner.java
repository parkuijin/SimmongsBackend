package com.simmongs.runner;

import com.simmongs.usedcode.UsedCodeRepository;
import com.simmongs.usedcode.UsedCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SimmongsRunner implements ApplicationRunner {

    @Autowired
    private UsedCodeRepository usedCodeRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (usedCodeRepository.findAll().isEmpty()) {
            UsedCodes usedCodes = new UsedCodes(0, 0);
            usedCodeRepository.save(usedCodes);
        }
    }

}
