package com.akshat.shopsphere.ai.tool;

import com.akshat.shopsphere.ai.dto.ProductAiResponse;
import com.akshat.shopsphere.product.dto.ProductSearchRequest;
import com.akshat.shopsphere.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductAiTool {

    private final ProductService productService;

    @Tool(description = """
            Search products available in ShopSphere.
            Use this when user asks to find, compare or recommend ShopSphere products
            """)
    public List<ProductAiResponse> searchProducts(
            @ToolParam(description = "Product name or keyword" ,required = false)
            String name,

            @ToolParam(description = "Minimum price",required = false)
            BigDecimal minPrice,

            @ToolParam(description = "Maximum price",required = false)
            BigDecimal maxPrice

            ){

        ProductSearchRequest request = new ProductSearchRequest(
                name,
                null,
                null,
                minPrice,
                maxPrice,
                0,
                10,
                "createdAt",
                "desc");

        return  productService.searchProducts(request)
                .content()
                .stream()
                .map(product -> new ProductAiResponse(
                        product.id(),
                        product.name(),
                        product.price(),
                        product.sku(),
                        product.categoryId(),
                        product.subcategoryId()
                )).toList();

    }
}
