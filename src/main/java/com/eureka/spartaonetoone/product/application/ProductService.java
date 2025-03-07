package com.eureka.spartaonetoone.product.application;

import com.eureka.spartaonetoone.common.client.StoreClient;
import com.eureka.spartaonetoone.product.application.dtos.request.ProductCreateRequestDto;
import com.eureka.spartaonetoone.product.application.dtos.request.ProductSearchRequestDto;
import com.eureka.spartaonetoone.product.application.dtos.request.ProductUpdateRequestDto;
import com.eureka.spartaonetoone.product.application.dtos.response.ProductGetResponseDto;
import com.eureka.spartaonetoone.product.application.exception.ProductException;
import com.eureka.spartaonetoone.product.domain.Product;
import com.eureka.spartaonetoone.product.domain.repository.ProductRepository;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final StoreClient storeClient;

    public UUID saveProduct(ProductCreateRequestDto request) {
        Product product = Product.createProduct(request.getStoreId(), request.getName(),
                request.getImageUrl(), request.getDescription(),
                request.getPrice(), request.getQuantity());

        Boolean isExistsSameProduct = productRepository.existsByStoreIdAndName(product.getStoreId(), product.getName());
        if (isExistsSameProduct) {
            throw new ProductException.ProductAlreadyExistsException();
        }

        Product savedProduct = productRepository.save(product);
        return savedProduct.getId();
    }

    @Transactional(readOnly = true)
    public ProductGetResponseDto getProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(ProductException.ProductNotFoundException::new);
        return ProductGetResponseDto.from(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductGetResponseDto> getProducts(Pageable pageable) {
        return productRepository.getProducts(pageable);
    }

    public UUID updateProduct(UUID productId,
                              ProductUpdateRequestDto request,
                              UserDetailsImpl userDetails) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(ProductException.ProductNotFoundException::new);

        if (userDetails.getUser().getRole().toString().equals("ADMIN")) {
            product.updateProduct(request.getName(), request.getDescription(),
                    request.getPrice(), request.getQuantity());
            productRepository.save(product);
            return product.getId();
        }

        UUID ownerId = storeClient.getOwnerId(product.getStoreId());

        if (userDetails.getUserId().equals(ownerId)) {
            product.updateProduct(request.getName(), request.getDescription(),
                    request.getPrice(), request.getQuantity());
            productRepository.save(product);
            return product.getId();
        }

        throw new ProductException.ProductAccessDeniedException();

    }

    public UUID deleteProduct(UUID productId, UserDetailsImpl userDetails) {
        Product product = productRepository.findById(productId).orElseThrow(ProductException.ProductNotFoundException::new);

        if (userDetails.getUser().getRole().toString().equals("ADMIN")) {
            product.deleteProduct(userDetails.getUserId());
            productRepository.save(product);
            return product.getId();
        }

        UUID ownerId = storeClient.getOwnerId(product.getStoreId());

        if (userDetails.getUserId().equals(ownerId)) {
            product.deleteProduct(userDetails.getUserId());
            productRepository.save(product);
            return product.getId();
        }
        
        throw new ProductException.ProductAccessDeniedException();
    }

    public Page<ProductGetResponseDto> searchProducts(final ProductSearchRequestDto request, final Pageable pageable) {
        return productRepository.searchByCondition(request.getStoreId(), request.getMinPrice(), request.getMaxPrice(), request.getName(), pageable);
    }

    public Boolean reduceProduct(List<UUID> productIds, List<Integer> quantities) {
        List<Product> products = productRepository.findAllById(productIds);
        for (int i = 0; i < quantities.size(); i++) {
            if (products.get(i).getQuantity() < quantities.get(i)) {
                return Boolean.FALSE;
            }
        }
        for (int i = 0; i < products.size(); i++) {
            Product updatedProduct = products.get(i);
            updatedProduct.updateQuantity(updatedProduct.getQuantity() - quantities.get(i));
        }
        return Boolean.TRUE;
    }
}
