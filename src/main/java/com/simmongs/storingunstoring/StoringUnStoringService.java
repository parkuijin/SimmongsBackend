package com.simmongs.storingunstoring;

import com.simmongs.product.ProductRepository;
import com.simmongs.product.Products;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoringUnStoringService {

    private final StoringUnStoringRepository storingUnStoringRepository;
    private final ProductRepository productRepository;

    @Transactional
    public int RegStoringUnStoring(JSONObject obj) {
        LocalDateTime storingUnstoringDate = LocalDateTime.now();
        JSONArray storingUnstoringList = obj.getJSONArray("list");

        for (int i=0; i<storingUnstoringList.length(); i++) {

            JSONObject storingUnstoring = storingUnstoringList.getJSONObject(i);

            String storingUnstoringType = storingUnstoring.getString("storingUnstoringType");
            String productCode = storingUnstoring.getString("productCode");
            String productName = productRepository.getByProductCode(productCode).getProductName();
            String productType = productRepository.getByProductCode(productCode).getProductType();
            if (productType.equals("제품"))
                return -2;
            int storingUnstoringAmount = Integer.parseInt(storingUnstoring.getString("storingUnstoringAmount"));

            if (storingUnstoringType.equals("입고")) {

                // 재고 개수 증가
                Products products = productRepository.getByProductCode(productCode);
                products.amountAdd(storingUnstoringAmount);

                StoringUnStoring storingUnStoring = new StoringUnStoring(storingUnstoringDate, storingUnstoringType, productCode, productName, productType, storingUnstoringAmount);
                storingUnStoringRepository.save(storingUnStoring);

            } else if (storingUnstoringType.equals("출고")) {

                Products products = productRepository.getByProductCode(productCode);
                if (products.getProductAmount() <= 0)
                    return -1; // 출고할 부품 개수가 부족할 경우

                // 재고 개수 차감
                products.amountSub(storingUnstoringAmount);

                StoringUnStoring storingUnStoring = new StoringUnStoring(storingUnstoringDate, storingUnstoringType, productCode, productName, productType, storingUnstoringAmount);
                storingUnStoringRepository.save(storingUnStoring);

            }
        }

        return 0;
    }

    @Transactional
    public List<StoringUnStoring> searchStoringUnstoring(JSONObject obj) throws JSONException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String productCode = null;
        String productName = null;
        String storingUnstoringType = null;
        String productType = null;
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (obj.has("productCode"))
            productCode = obj.getString("productCode");
        if (obj.has("productName"))
            productName = obj.getString("productName");
        if (obj.has("storingUnstoringType"))
            storingUnstoringType = obj.getString("storingUnstoringType");
        if (obj.has("productType"))
            productType = obj.getString("productType");
        if (obj.has("startDate") && !obj.getString("startDate").equals(""))
            startDate = LocalDateTime.parse(obj.getString("startDate"), formatter);
        if (obj.has("endDate") && !obj.getString("endDate").equals(""))
            endDate = LocalDateTime.parse(obj.getString("endDate"), formatter);

            return storingUnStoringRepository.findBySearchOption(productCode, productName, storingUnstoringType, productType, startDate, endDate);
    }

}
