package com.simmongs.storingunstoring;

import com.simmongs.product.ProductRepository;
import com.simmongs.product.Products;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoringUnStoringService {

    private final StoringUnStoringRepository storingUnStoringRepository;
    private final ProductRepository productRepository;

    @Transactional
    public int RegStoringUnStoring(String storingUnstoringType, String productCode, String productName, String productType, int storingUnstoringAmount) {

        LocalDateTime storingUnstoringDate = LocalDateTime.now();

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

        return 0;
    }

}
