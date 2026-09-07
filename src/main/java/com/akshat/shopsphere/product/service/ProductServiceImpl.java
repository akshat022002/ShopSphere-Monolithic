package com.akshat.shopsphere.product.service;

import com.akshat.shopsphere.audit.annotation.Audit;
import com.akshat.shopsphere.audit.enums.AuditAction;
import com.akshat.shopsphere.audit.enums.AuditEntityType;
import com.akshat.shopsphere.category.entity.Category;
import com.akshat.shopsphere.category.entity.Subcategory;
import com.akshat.shopsphere.category.repository.CategoryRepository;
import com.akshat.shopsphere.category.repository.SubcategoryRepository;
import com.akshat.shopsphere.common.exception.DuplicateResourceException;
import com.akshat.shopsphere.common.exception.ResourceNotFoundException;
import com.akshat.shopsphere.product.dto.*;
import com.akshat.shopsphere.product.entity.Product;
import com.akshat.shopsphere.product.mapper.ProductMapper;
import com.akshat.shopsphere.product.repository.ProductRepository;
import com.akshat.shopsphere.product.specification.ProductSpecification;
import com.akshat.shopsphere.user.entity.User;
import com.akshat.shopsphere.user.enums.Role;
import com.akshat.shopsphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;

    @Audit(action = AuditAction.PRODUCT_CREATED,entityType = AuditEntityType.PRODUCT)
    @CacheEvict(value = "product-search",allEntries = true)
    @Override
    public ProductResponse createProduct(ProductCreateRequest request, Long userId) {

        log.info("Creating product with Sku : {} by user: {},", request.sku(), userId);

        User seller = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User does not exist with id :" + userId));

        if(!seller.isEnabled() || seller.isDeleted()){
            throw new ResourceNotFoundException("Seller account is inactive");
        }

        if(!Role.ROLE_SELLER.equals(seller.getRole())){
            throw new AccessDeniedException("Only sellers are allowed to create a product");
        }

        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("Product with Sku already exists : " + request.sku());
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(()->
                        new ResourceNotFoundException("Category not found with id : "+request.categoryId()));

        if(!category.isActive()){
            throw new ResourceNotFoundException("Category not found with id"+request.categoryId());
        }

        Subcategory subcategory = subcategoryRepository.findById(request.subcategoryId())
                .orElseThrow(()->
                        new ResourceNotFoundException("Subcategory not found with id : "+request.subcategoryId()));

        if(!subcategory.isActive()){
            throw new ResourceNotFoundException("Subcategory not found with id"+request.subcategoryId());
        }

        if(!subcategory.getCategory().getId().equals(category.getId())){
            throw new IllegalArgumentException("Subcategory does not belong to selected category");
        }

        Product product = productMapper.toEntity(request);

        product.setSeller(seller);
        product.setActive(true);
        product.setCategory(category);
        product.setSubcategory(subcategory);

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with id {}",savedProduct.getId());

        return productMapper.toResponse(savedProduct);

    }


    @Cacheable(value = "products", key = "#productId")
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


    @Audit(action = AuditAction.PRODUCT_UPDATED,
            entityType = AuditEntityType.PRODUCT,
            entityIdParam = "productId")
    @CacheEvict(value = "product-search",allEntries = true)
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

    @Audit(action = AuditAction.PRODUCT_DELETED,
            entityType = AuditEntityType.PRODUCT,
            entityIdParam = "productId"
    )
    @CacheEvict(value = "product-search",allEntries = true)
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

    @Cacheable(value = "product-search",
            key = "#request.page() + '-' + #request.size() + '-' + #request.sortBy() + '-' + #request.sortDir() + '-' + #request.name() + '-' + #request.categoryId() + '-' + #request.subcategoryId() + '-' + #request.minPrice() + '-' + #request.maxPrice()"
    )
    @Override
    @Transactional(readOnly = true)
    public ProductSearchResponse searchProducts(ProductSearchRequest request) {

        int page = request.page()!=null ? request.page() : 0;

        int size = request.size()!=null ? request.size() : 10;

        String sortBy = request.sortBy()!=null ? request.sortBy() : "createdAt";

        Sort.Direction direction = "desc"
                .equalsIgnoreCase(request.sortDir())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page,size,Sort.by(direction,sortBy));

        Specification<Product> specification = ProductSpecification.filter(
                request.name(),
                request.categoryId(),
                request.subcategoryId(),
                request.minPrice(),
                request.maxPrice());

        Page<ProductResponse> result =  productRepository
                .findAll(specification,pageable).map(productMapper::toResponse);

        return new ProductSearchResponse(result.getContent(),result.getNumber(),result.getSize());

    }
}
