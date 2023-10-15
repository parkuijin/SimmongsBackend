package com.simmongs.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor // Final 객체를 Constructor Injection 해준다 (Autowired 역할)
@RequestMapping(value = "products")
public class ProductController { // Repository, Service 수행 후, API 응답을 리턴

    private final ProductRepository productRepository;
    private final ProductService productService;

    @PostMapping("registration") // 제품, 부품 정보 등록
    public Products ProductRegistration(@RequestBody Products products){
        return productRepository.save(products);
    }

    @GetMapping("showAll") // 제품, 부품 전체 조회
    public List<Products> ShowAllProducts(){
        return productRepository.findAll();
    }

    @DeleteMapping("delete") // 제품 ID 받아서 삭제
    public Map<String, Object> ProductDelete(@RequestParam(value = "product_id") Long product_id) {
        Map<String, Object> response = new HashMap<>();

        if (productService.delete(product_id) > 0) {
            response.put("result", "DELETE SUCCESS");
        } else {
            response.put("result", "DELETE FAIL");
            response.put("reason", "ID IS NOT EXISTS");
        }

        return response;

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
