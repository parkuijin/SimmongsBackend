package com.simmongs.product;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor // Final 객체를 Constructor Injection 해준다 (Autowired 역할)
@RequestMapping(value = "products")
public class ProductController { // Repository, Service 수행 후, API 응답을 리턴

    private final ProductRepository productRepository;

    @PostMapping("registration") // 제품, 부품 정보 등록
    public Products productRegistration(@RequestBody Products products){
        return productRepository.save(products);
    }

    @GetMapping("showAll") // 제품, 부품 전체 조회
    public List<Products> ShowAllProducts(){
        return productRepository.findAll();
    }

}
