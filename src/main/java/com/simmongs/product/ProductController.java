package com.simmongs.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "products")
public class ProductController { // Repository, Service 수행 후, API 응답을 리턴

    private final ProductRepository productRepository;
    private final ProductService productService;

    @GetMapping("showAll") // 제품, 부품 전체 조회
    public List<Products> ShowAllProducts(){
        return productRepository.findAll();
    }

    @PostMapping("getNewProductId") // 신규 제품 ID 조회
    public Map<String, Object> getNewProductId(@RequestBody String json) throws JSONException{

        JSONObject obj = new JSONObject(json);
        String type = obj.getString("type");

        Map<String, Object> response = new HashMap<>();

        String newProductCode = productService.generateNewProductCode(type);

        if( newProductCode != null )
        {
            response.put("success", true);
            response.put("product_id", newProductCode);
        }
        else
        {
            response.put("success", false);
            response.put("message", "제품 코드 생성에 실패하였습니다.");
        }


        return response;
    }


    @PostMapping("checkProductId") // productId 중복 체크
    public Map<String, Object> checkProductId(@RequestBody String json) throws JSONException{

        JSONObject obj = new JSONObject(json);
        String productCode = obj.getString("productId");
        String productType = obj.getString("type");

        Map<String, Object> response = new HashMap<>();

        Products products = productService.checkProductIdByProductCode(productCode);

        if( products != null && products.getProductType().equals(productType) )
        {
            response.put("success", false);
            response.put("productId", products.getProductCode());
            response.put("name", products.getProductName());
            response.put("unit", products.getProductUnit());
            response.put("type", products.getProductType());
        }
        else
        {
            response.put("success", true);
        }

        return response;
    }


    @PostMapping("uploadProduct") // 부품, 상품 및 BOM 등록
    public Map<String, Object> uploadProduct(@RequestBody String json) throws JSONException {
        JSONObject obj = new JSONObject(json);

        return productService.uploadProduct(obj);
    }

    @PostMapping("updateProduct") // 부품, 상품 및 BOM 수정
    public Map<String, Object> updateBOM(@RequestBody String json) throws JSONException {
        JSONObject obj = new JSONObject(json);

        return productService.updateProduct(obj);
    }


    @PostMapping("searchKeyword") // 품목 키워드 검색
    public List<Products> searchKeyword(@RequestBody String json) throws JSONException, java.text.ParseException{
        JSONObject request_obj = new JSONObject(json);
        String productType = request_obj.getString("type"); // 품목유형(제품, 부품)
        productType = productType.equals("product") ? "제품" : "부품";
        String keyword = request_obj.getString("keyword"); // 키워드

        List<Products> foundProducts = productRepository.findSearchComponentByKeyword(productType, keyword);

        return foundProducts;
    }

    @PostMapping("searchProducts") // 품목 검색
    public String searchProducts(@RequestBody String json) throws JSONException, java.text.ParseException{
        JSONObject request_obj = new JSONObject(json);


        String type = (request_obj.getString("type").trim().equals("component") ? "부품" : ( request_obj.getString("type").trim().equals("product") ? "제품" : "품" ));
        String maxDate = (request_obj.getString("maxDate").trim().equals("")) ? "9999-12-31" : request_obj.getString("maxDate").trim();
        String minDate = (request_obj.getString("minDate").trim().equals("")) ? "0000-01-01" : request_obj.getString("minDate").trim();
        String productId = (request_obj.getString("productId").trim().equals("")) ? "" : request_obj.getString("productId").trim();
        String productName = (request_obj.getString("productName").trim().equals("")) ? "" : request_obj.getString("productName").trim();
        String productUnit = (request_obj.getString("productUnit").trim().equals("")) ? "" : request_obj.getString("productUnit").trim();

        JSONObject response = new JSONObject();

        JSONArray productArr = productService.findSearchProduct(productId, productName, productUnit, type, minDate, maxDate);

        response.put("products", productArr);
        return response.toString();
    }

    @DeleteMapping("deleteProduct") // 품목 삭제
    public Map<String, Object> deleteProducts(@RequestBody String json) throws JSONException{
        JSONObject obj = new JSONObject(json);

        return productService.deleteProduct(obj);
    }

    @PostMapping("overview") // 품목 개요
    public Map<String, Object> overview() throws JSONException{
        Map<String, Object> response = new HashMap<>();
        List<Products> allProducts = productRepository.findAll();

        int todayTotal = 0;
        LocalDate todayDate = LocalDate.now();
        for( Products product : allProducts ) // productId 존재 여부 확인
        {
            LocalDateTime productCreationDate = product.getProductCreationDate();
            LocalDate productDate = productCreationDate.toLocalDate();

            if( productDate.equals(todayDate) )
                todayTotal++;
        }

        response.put("success", true);
        response.put("total", allProducts.size());
        response.put("todayTotal", todayTotal);

        return response;
    }

}
