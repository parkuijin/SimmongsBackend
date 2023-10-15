package com.simmongs.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public int delete(Long id) {
        Optional<Products> products = productRepository.findById(id);
        if (products.isPresent()) {
            productRepository.delete(products.get());
            return 1;
        }
        return 0;
    }

}
