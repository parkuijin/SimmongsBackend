package com.simmongs.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

   @GetMapping("search") // 검색 조건에 따라 검색
    public List<Products> SearchProduct(
            @RequestParam(value = "product_code") String product_code
            , @RequestParam(value = "product_name") String product_name
            , @RequestParam(value = "product_unit") String product_unit
            , @RequestParam(value = "product_type") String product_type
            , @RequestParam(value = "product_start_date") String product_start_date
            , @RequestParam(value = "product_end_date") String product_end_date) {
        return productRepository.findSearchProduct(product_code, product_name, product_unit, product_type, product_start_date, product_end_date);
    }

}
