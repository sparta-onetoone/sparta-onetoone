package com.eureka.spartaonetoone.domain.product.application;

import com.eureka.spartaonetoone.domain.product.application.dtos.ProductCreateRequestDto;
import com.eureka.spartaonetoone.domain.product.application.dtos.ProductGetResponseDto;
import com.eureka.spartaonetoone.domain.product.application.dtos.ProductSearchRequestDto;
import com.eureka.spartaonetoone.domain.product.application.dtos.ProductUpdateRequestDto;
import com.eureka.spartaonetoone.domain.product.application.exception.ProductException;
import com.eureka.spartaonetoone.domain.product.domain.Product;
import com.eureka.spartaonetoone.domain.product.domain.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public UUID saveProduct(ProductCreateRequestDto productCreateRequestDto) {
        Product product = Product.of(productCreateRequestDto.getStoreId(), productCreateRequestDto.getName(), productCreateRequestDto.getDescription(),
                productCreateRequestDto.getPrice(), productCreateRequestDto.getQuantity(), productCreateRequestDto.getIsDeleted());

        Boolean isExistsSameProduct = productRepository.existsByStoreIdAndName(product.getStoreId(), product.getName());
        if (isExistsSameProduct) {
            throw new ProductException.ProductAlreadyExistsException();
        }

        productRepository.save(product);
        return product.getId();
    }

    public ProductGetResponseDto getProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(ProductException.ProductNotFoundException::new);
        return ProductGetResponseDto.from(product);
    }

    public Page<ProductGetResponseDto> getProducts(Pageable pageable) {
        return productRepository.getProducts(pageable);
    }

    public UUID updateProduct(UUID productId, @Valid ProductUpdateRequestDto productUpdateRequestDto) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(ProductException.ProductNotFoundException::new);

        product.updateProduct(productUpdateRequestDto.getName(), productUpdateRequestDto.getDescription(),
                productUpdateRequestDto.getPrice(), productUpdateRequestDto.getQuantity());

        productRepository.save(product);

        return product.getId();
    }

    public UUID deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(ProductException.ProductNotFoundException::new);
        product.deleteProduct();
        productRepository.save(product);
        return product.getId();
    }

    public Page<ProductGetResponseDto> searchProducts(final ProductSearchRequestDto productSearchRequestDto, final Pageable pageable) {
        return productRepository.searchByCondition(productSearchRequestDto, pageable);
    }
}
