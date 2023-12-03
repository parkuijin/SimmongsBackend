package com.simmongs.bom;

import com.simmongs.product.ProductRepository;
import com.simmongs.product.Products;
import com.simmongs.workorder.WorkOrderRepository;
import com.simmongs.workorder.WorkOrders;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BOMService {

    private final BOMRepository bomRepository;
    private final ProductRepository productRepository;
    private final WorkOrderRepository workOrderRepository;

    @Transactional
    public Map<String, Object> uploadBOM(JSONObject obj) throws JSONException {
        Map<String, Object> response = new HashMap<>();

        String productCode = obj.getString("productCode");
        JSONArray childProductList = obj.getJSONArray("childProduct");

        Optional<Products> parentsCodeCheck = productRepository.findByProductCode(productCode);
        if (!parentsCodeCheck.isPresent()) {
            response.put("success", false);
            response.put("message", "제품 코드가 존재하지 않습니다.");
            return response;
        }

        for (int i = 0; i < childProductList.length(); i++) {
            JSONObject childProduct = childProductList.getJSONObject(i);
            String childProductCode = childProduct.getString("childProductCode");
            int bomAmount = childProduct.getInt("bomAmount");

            Optional<Products> childCodeCheck = productRepository.findByProductCode(childProductCode);
            if (!childCodeCheck.isPresent()) {
                response.put("success", false);
                response.put("message", "부품 코드가 존재하지 않습니다.");
                return response;
            }

            if (bomAmount <= 0) {
                response.put("success", false);
                response.put("message", "부품 필요수량이 1 이상이어야 합니다.");
                return response;
            }

            // BOM 코드 생성
            String newBOMCode = null;
            for (int j = 1; j < 10000; j++) {
                newBOMCode = "B" + String.format("%05d", j);
                Optional<BOMs> boms = bomRepository.findByBomId(newBOMCode);
                if(!boms.isPresent())
                    break;
            }

            if (newBOMCode.equals("")) {
                response.put("success", false);
                response.put("message", "코드 생성에 실패했습니다.");
                return response;
            }

            BOMs bom = new BOMs(newBOMCode, productCode, childProductCode, bomAmount);
            bomRepository.save(bom);
        }

        response.put("success", true);
        return response;
    }

    @Transactional
    public List<HashMap<String, Object>> showProductList() {
        List<HashMap<String, Object>> response = new ArrayList<HashMap<String, Object>>();

        List<Products> productsList = productRepository.findAll();

        for (Products products : productsList) {
            if (products.getProductType().equals("제품")) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("productCode", products.getProductCode());
                hashMap.put("productName", products.getProductName());
                hashMap.put("productUnit", products.getProductUnit());
                hashMap.put("productType", products.getProductType());
                response.add(hashMap);
            }
        }

        return response;
    }

    @Transactional
    public Map<String, Object> selectedProductInfo(JSONObject obj) throws JSONException {
        Map<String, Object> response = new HashMap<>();

        String productCode = obj.getString("productCode");
        String productName = productRepository.getByProductCode(productCode).getProductName();
        String productUnit = productRepository.getByProductCode(productCode).getProductUnit();

        response.put("productName", productName);
        response.put("productUnit", productUnit);

        return response;
    }

    @Transactional
    public Map<String, Object> deleteBOM(JSONObject obj) throws JSONException {
        Map<String, Object> response = new HashMap<>();

        String productCode = obj.getString("productCode");

        if (bomRepository.findByProductCode(productCode).isEmpty()) {
            response.put("success", false);
            response.put("message", "BOM이 존재하지 않습니다.");
        }

        List<BOMs> bomListByProductCode = bomRepository.findByProductCode(productCode);
        for (BOMs boms : bomListByProductCode)
            bomRepository.delete(boms);

        List<WorkOrders> workOrdersList = workOrderRepository.searchByProductCode(productCode);
        for (WorkOrders workOrders : workOrdersList)
            if (workOrders.getWorkStatus().equals("준비") || workOrders.getWorkStatus().equals("진행"))
                workOrders.stopWorkOrder();

        response.put("success", true);
        return response;
    }
}
