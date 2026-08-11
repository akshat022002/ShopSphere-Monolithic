package com.akshat.shopsphere.product.service;

import com.akshat.shopsphere.common.exception.DuplicateResourceException;
import com.akshat.shopsphere.common.exception.ResourceNotFoundException;
import com.akshat.shopsphere.product.dto.ProductCreateRequest;
import com.akshat.shopsphere.product.dto.ProductResponse;
import com.akshat.shopsphere.product.dto.ProductUpdateRequest;
import com.akshat.shopsphere.product.entity.Product;
import com.akshat.shopsphere.product.mapper.ProductMapper;
import com.akshat.shopsphere.product.repository.ProductRepository;
import com.akshat.shopsphere.user.entity.User;
import com.akshat.shopsphere.user.enums.Role;
import com.akshat.shopsphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.AccessDeniedException;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse createProduct(ProductCreateRequest request, Long userId) {

        log.info("Creating product with Sku : {} and user: {},", request.sku(), userId);

        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("Product with Sku already exists : " + request.sku());
        }

        User seller = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User does not exist with id :" + userId));

        Product product = productMapper.toEntity(request);

        product.setSeller(seller);
        product.setActive(true);

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with id {}",savedProduct.getId());

        return productMapper.toResponse(savedProduct);


    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProductById(Long productId) {

        log.info("Fetching Product with id: {}",productId);

        Product product = productRepository.findById(productId)
                .filter(Product::isActive)
                .orElseThrow(()->
                        new ResourceNotFoundException("Product not found with id"+productId));

        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long productId, Long userId, ProductUpdateRequest request, Role role)  {

        log.info("Updating product: {} by user: {} with role : {}",productId,userId,role);

        Product product = productRepository.findById(productId)
                .filter(Product::isActive)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found with id "+productId));

        boolean isAdmin = Role.ROLE_ADMIN.equals(role);

        boolean isOwner = product.getSeller().getId().equals(userId);

        if(!isAdmin && !isOwner){
            throw new AccessDeniedException("You are not authorized to update this product");
        }



        if(request.name()!= null){
            product.setName(request.name());
        }

        if(request.description()!=null){
            product.setDescription(request.description());
        }

        if(request.price()!=null){
            product.setPrice(request.price());
        }

        Product updatedProduct = productRepository.save(product);

        log.info("Product updated successfully: {}",productId);

        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long productId, Long userId,Role role)  {

        log.info("Soft deleting product : {} by user : {} with role : {}",productId,userId,role);

        Product product = productRepository.findById(productId)
                .filter(Product::isActive)
                .orElseThrow(()-> new
                        ResourceNotFoundException("Product does not exist with id :"+productId));

        boolean isAdmin = Role.ROLE_ADMIN.equals(role);

        boolean isOwner = product.getSeller().getId().equals(userId);

        if(!isAdmin && !isOwner){
            throw new AccessDeniedException("You are not authorized to delete this product");
        }

        product.setActive(false);

        log.info("Product soft deleted successfully :{}",productId);

    }
}
