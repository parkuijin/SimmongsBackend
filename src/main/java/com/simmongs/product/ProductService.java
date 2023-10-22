package com.simmongs.product;

import com.simmongs.bom.BOMRepository;
import com.simmongs.bom.BOMs;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final BOMRepository bomRepository;

    @Transactional
    public Products update(Long id, String name, int amount, String unit) {
        Products products = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID IS NOT EXISTS"));

        products.update(name, amount, unit);

        return products;
    }

    @Transactional
    public String generateNewProductCode(String type, List<String> exceptProductIdList){ // productCode 생성

        if( type.equals("product") ) // 생성 타입이 제품(product)일 경우
        {
            for( int i = 0; i < 100000; i++ )
            {
                String productCode = "P" + String.format("%05d", i);

                if( exceptProductIdList.contains(productCode) )
                    continue;

                Optional<Products> products = productRepository.findByProductCode(productCode);
                if( !products.isPresent() ) // productCode가 존재하지 않을 경우
                    return productCode;
            }
        }
        else if( type.equals("component") )
        {
            for( int i = 0; i < 100000; i++ )
            {
                String productCode = "C" + String.format("%05d", i);

                if( exceptProductIdList.contains(productCode) )
                    continue;

                Optional<Products> products = productRepository.findByProductCode(productCode);
                if( !products.isPresent() ) // productCode가 존재하지 않을 경우
                    return productCode;
            }
        }

        return null;
    }

    @Transactional
    public Products checkProductIdByProductCode(String productCode){ // productCode로 product_id 조회
        Optional<Products> products = productRepository.findByProductCode(productCode);
        if( products.isPresent() )
            return products.get();
        else
            return null;
    }

    @Transactional
    public int deleteProductByProductCode(String productCode){ // productCode로 product 삭제

        // BOMs 테이블에서 productCode를 가지고 있는 row 삭제
        List<BOMs> boms = bomRepository.findByProductCode(productCode);
        for( BOMs bom : boms )
            bomRepository.delete(bom);

        List<BOMs> boms2 = bomRepository.findByChildProductCode(productCode);
        for( BOMs bom : boms2 )
            bomRepository.delete(bom);

        Optional<Products> products = productRepository.findByProductCode(productCode);
        if( products.isPresent() )
        {
            productRepository.delete(products.get());
            return 1;
        }
        else
            return 0;
    }

    @Transactional
    public int uploadComponent(JSONArray componentArray) throws JSONException // 부품(Component) 신규 등록
    {

        // 검증 단계
        for( int i = 0; i < componentArray.length(); i++ )
        {
            JSONObject componentObj = componentArray.getJSONObject(i);
            String product_code = componentObj.getString("productId");
            product_code = product_code.toUpperCase().trim();
            String product_name = componentObj.getString("productName");
            product_name = product_name.trim();
            String product_unit = componentObj.getString("productUnit");

            if( product_code.trim().equals("") || product_name.trim().equals("") || product_unit.trim().equals("") )
                return -1;

            Optional<Products> products = productRepository.findByProductCode(product_code);
            if( products.isPresent() ) // productCode가 존재할 경우
                return -2;
        }

        // 생성 단계
        for( int i = 0; i < componentArray.length(); i++ )
        {
            JSONObject componentObj = componentArray.getJSONObject(i);
            String product_code = componentObj.getString("productId");
            product_code = product_code.toUpperCase().trim();
            String product_name = componentObj.getString("productName");
            product_name = product_name.trim();
            int product_amount = 0;
            String product_unit = componentObj.getString("productUnit");
            String product_type = "부품";

            LocalDateTime product_creation_date = LocalDateTime.now();

            Products product = new Products(product_code, product_name, product_amount, product_unit, product_type, product_creation_date);
            productRepository.save(product);
        }
        return 0;
    }

    @Transactional
    public int uploadProduct(String parentProductCode, String parentProductName, String parentProductUnit, JSONArray componentArray) throws JSONException // 제품(Product) 신규 등록
    {
        LocalDateTime product_creation_date = LocalDateTime.now();

        // parentProductCode 중복 확인
        Optional<Products> products = productRepository.findByProductCode(parentProductCode);
        if( products.isPresent() ) // parentProductCode가 이미 존재할 경우
            return -1;

        // 검증 단계
        for( int i = 0; i < componentArray.length(); i++ )
        {
            JSONObject componentObj = componentArray.getJSONObject(i);
            String child_product_code = componentObj.getString("productId");
            int product_amount = 0;
            try{
                product_amount = componentObj.getInt("productAmount");
            } catch(Exception e){}

            Optional<Products> child_products = productRepository.findByProductCode(child_product_code);
            if( !child_products.isPresent() ) // child_product_code가 존재하지 않을 경우
                return -2;

            if(child_product_code.trim().equals("") || product_amount <= 0)
            {
                return -3;
            }
        }

        // 생성 단계
        for( int i = 0; i < componentArray.length(); i++ )
        {
            JSONObject componentObj = componentArray.getJSONObject(i);
            String child_product_code = componentObj.getString("productId");
            int product_amount = componentObj.getInt("productAmount");


            // BOMs 테이블에 추가
            String new_bom_code = null;
            for( int j = 0; j < 100000; j++ ) // BOM 코드 생성
            {
                String bom_code = "B" + String.format("%05d", j);
                Optional<BOMs> boms = bomRepository.findByBomId(bom_code);
                if( !boms.isPresent() ) // bomCode가 존재하지 않을 경우
                {
                    new_bom_code = bom_code;
                    break;
                }
            }

            if( new_bom_code == null )
                return -4;

            BOMs bom = new BOMs(new_bom_code, parentProductCode, child_product_code, product_amount);
            bomRepository.save(bom);
        }

        // Products 테이블에 추가
        Products product = new Products(parentProductCode, parentProductName, 0, parentProductUnit, "제품", product_creation_date);
        productRepository.save(product);
        return 0;
    }

    @Transactional
    public JSONArray findSearchProduct(String product_code, String product_name, String product_unit, String product_type, String product_start_date, String product_end_date) throws java.text.ParseException, JSONException {
        JSONArray productArr = new JSONArray();

        List<Products> foundProducts = productRepository.findSearchProduct(product_code, product_name, product_unit, product_type, product_start_date, product_end_date);

        for( Products product : foundProducts )
        {
            JSONObject productObj = new JSONObject();
            productObj.put("productId", product.getProductCode());
            productObj.put("productName", product.getProductName());
            productObj.put("productAmount", product.getProductAmount());
            productObj.put("productUnit", product.getProductUnit());
            productObj.put("productType", product.getProductType());
            productObj.put("productCreationDate", product.getProductCreationDate().toLocalDate());

            if(product.getProductType().equals("제품")){
                List<BOMs> allBoms = bomRepository.findAll();
                for( BOMs bom : allBoms )
                {
                    if( bom.getProductCode().equals(product.getProductCode()) )
                    {
                        List<Products> allProducts2 = productRepository.findAll();
                        for( Products product2 : allProducts2 )
                        {
                            if( product2.getProductCode().equals(bom.getProductCode()) )
                            {
                                JSONObject childProductObj = new JSONObject();
                                childProductObj.put("bomId", bom.getBomId());
                                childProductObj.put("productId", product2.getProductCode());
                                childProductObj.put("productName", product2.getProductName());
                                childProductObj.put("productAmount", bom.getBomAmount());
                                childProductObj.put("productUnit", product2.getProductUnit());
                                childProductObj.put("productType", product2.getProductType());
                                childProductObj.put("productCreationDate", product2.getProductCreationDate().toLocalDate());
                                productObj.append("childProducts", childProductObj);
                            }
                        }
                    }
                }
            }

            productArr.put(productObj);
        }

        return productArr;

    }

}
