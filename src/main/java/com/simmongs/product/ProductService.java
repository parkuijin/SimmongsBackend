package com.simmongs.product;

import com.simmongs.bom.BOMRepository;
import com.simmongs.bom.BOMs;
import com.simmongs.mrp.MRPRepository;
import com.simmongs.mrp.MRPs;
import com.simmongs.storingunstoring.StoringUnStoring;
import com.simmongs.storingunstoring.StoringUnStoringRepository;
import com.simmongs.usedcode.UsedCodeRepository;
import com.simmongs.usedcode.UsedCodes;
import com.simmongs.workorder.WorkOrderRepository;
import com.simmongs.workorder.WorkOrders;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final BOMRepository bomRepository;
    private final StoringUnStoringRepository storingUnStoringRepository;
    private final WorkOrderRepository workOrderRepository;
    private final MRPRepository mrpRepository;
    private final UsedCodeRepository usedCodeRepository;

    @Transactional
    public String generateNewProductCode(String type) {
        String productCode;
        UsedCodes usedCodes = usedCodeRepository.findAll().get(0);

        if (type.equals("제품")) { // 생성 타입이 제품(product)일 경우
            productCode = "P" + String.format("%05d", usedCodes.getUsedProductCode() + 1);
            usedCodes.usedProductCodeUpdate();
            return productCode;
        } else if (type.equals("부품")) {
            productCode = "C" + String.format("%05d", usedCodes.getUsedComponentCode() + 1);
            usedCodes.usedComponentCodeUpdate();
            return productCode;
        }

        return null;
    }

    @Transactional
    public Products checkProductIdByProductCode(String productCode){ // productCode로 productId 조회
        Optional<Products> products = productRepository.findByProductCode(productCode);
        if( products.isPresent() )
            return products.get();
        else
            return null;
    }

    @Transactional
    public Map<String, Object> deleteProduct(JSONObject obj){
        Map<String, Object> response = new HashMap<>();
        List<String> existingProductCodesList = new ArrayList<>();

        String productCode = obj.getString("productCode");

        if (productCode.equals("")) {
            response.put("success", false);
            response.put("message", "빈칸을 입력해주세요.");
            return response;
        }

        // BOM 테이블에서 해당되는 값 삭제
        if (productRepository.findByProductCode(productCode).get().getProductType().equals("부품")) {
            if (!bomRepository.findByChildProductCode(productCode).isEmpty()) {
                List<BOMs> bomListByChildProduct = bomRepository.findByChildProductCode(productCode);
                for( BOMs bom : bomListByChildProduct ) {
                    existingProductCodesList.add(bom.getProductCode());
                    bomRepository.delete(bom);
                }
            }
            for (int l=0; l<existingProductCodesList.size(); l++) {
                List<BOMs> boMListByProductCode = bomRepository.findByProductCode(existingProductCodesList.get(l));
                for (BOMs bom : boMListByProductCode)
                    bomRepository.delete(bom);
            }
        } else if (productRepository.findByProductCode(productCode).get().getProductType().equals("제품")) {
            if (!bomRepository.findByProductCode(productCode).isEmpty()) {
                List<BOMs> bomListByProductCode = bomRepository.findByProductCode(productCode);
                for (BOMs bom : bomListByProductCode) {
                    existingProductCodesList.add(bom.getProductCode());
                    bomRepository.delete(bom);
                }
            }
        }

        existingProductCodesList.stream().distinct().collect(Collectors.toList());

        for (int i=0; i<existingProductCodesList.size(); i++) {
            String existingProductCode = existingProductCodesList.get(i);

            List<WorkOrders> workOrdersList = workOrderRepository.searchByProductCode(existingProductCode);
            for (int j=0; j<workOrdersList.size(); j++) {
                WorkOrders workOrders = workOrdersList.get(j);

                // 준비, 진행 상태의 작업지시를 중단상태로 변경
                if (workOrders.getWorkStatus().equals("준비") || workOrders.getWorkStatus().equals("진행")) {
                    workOrders.stopWorkOrder();

                    // 중단된 작업지시에 해당되는 MRP를 삭제
                    List<MRPs> mrps = mrpRepository.findByWorkOrderId(workOrders.getWorkOrderId());
                    for (int k=0; k<mrps.size(); k++)
                        mrpRepository.delete(mrps.get(k));
                }
            }
        }

        // Product 테이블에서 해당되는 값 삭제
        Optional<Products> products = productRepository.findByProductCode(productCode);
        if( products.isPresent() )
            productRepository.delete(products.get());

        response.put("success", true);
        return response;
    }

    @Transactional
    public Map<String, Object> uploadProduct(JSONObject obj) throws JSONException {

        Map<String, Object> response = new HashMap<>();
        LocalDateTime productCreationDate = LocalDateTime.now();

        JSONArray productList = obj.getJSONArray("productList");

        for (int i=0; i<productList.length(); i++) {

            JSONObject product = productList.getJSONObject(i);

            String productType = product.getString("productType");
            String productCode = product.getString("productCode");
            String productName = product.getString("productName").trim();
            String productUnit = product.getString("productUnit");
            Integer productAmount = product.getInt("productAmount");

            if (productType.equals("") || productCode.equals("") || productName.equals("") || productUnit.equals("") || productAmount == null) {
                response.put("success", false);
                response.put("message", "값을 입력해주세요.");
                return response;
            }

            // ProductCode 중복 확인
            Optional<Products> CodeCheck = productRepository.findByProductCode(productCode);
            if (CodeCheck.isPresent()) {
                if (productType.equals("제품")) {
                    response.put("success", false);
                    response.put("message", "제품 코드가 이미 존재합니다.");
                    return response;
                } else if (productType.equals("부품")) {
                    response.put("success", false);
                    response.put("message", "부품 코드가 이미 존재합니다.");
                    return response;
                }
            }

            // Product 등록
            Products products = new Products(productCode, productName, productAmount, productUnit, productType, productCreationDate);
            productRepository.save(products);
        }

        response.put("success", true);
        return response;
    }

    @Transactional
    public Map<String, Object> updateProduct(JSONObject obj) throws JSONException { // 제품(Product) 수정

        Map<String, Object> response = new HashMap<>();

        String productCode = obj.getString("productCode");
        String productName = obj.getString("productName");
        Integer productAmount = obj.getInt("productAmount");
        String productUnit = obj.getString("productUnit");

        // Null 체크
        if (productCode.equals("") || productName.equals("") || productAmount == null || productUnit.equals("")) {
            response.put("success", false);
            response.put("message", "빈칸을 채워주세요.");
            return response;
        }

        // productCode 존재 여부 확인
        Optional<Products> productExistCheck = productRepository.findByProductCode(productCode);
        if (!productExistCheck.isPresent()) { // productCode 존재하지 않을 경우
            response.put("success", false);
            response.put("message", "제품, 부품 코드가 존재하지 않습니다.");
            return response;
        }

        // 입출고 목록에서 수정된 제품, 부품 이름으로 수정
        List <StoringUnStoring> storingUnStoringList = storingUnStoringRepository.findByProductCode(productCode);
        for (int i=0; i<storingUnStoringList.size(); i++) {
            StoringUnStoring storingUnStoring = storingUnStoringList.get(i);
            storingUnStoring.changeProductName(productName);
        }

        // 제품, 부품 수정
        Products product = productRepository.getByProductCode(productCode);
        product.changeProductName(productName);
        product.changeProductAmount(productAmount);
        product.changeProductUnit(productUnit);

        response.put("success", true);
        return response;
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
                            if( product2.getProductCode().equals(bom.getChildProductCode()) )
                            {
                                JSONObject childProductObj = new JSONObject();
                                childProductObj.put("bomId", bom.getBomId());
                                childProductObj.put("productId", product2.getProductCode());
                                childProductObj.put("productName", product2.getProductName());
                                childProductObj.put("productAmount", product2.getProductAmount());
                                childProductObj.put("bomAmount", bom.getBomAmount());
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
    } // findSearchProduct

}
