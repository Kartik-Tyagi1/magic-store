package com.kt.magicstore.service.product;

import com.kt.magicstore.dto.ProductDto;
import com.kt.magicstore.model.Product;
import com.kt.magicstore.request.AddProductRequest;
import com.kt.magicstore.request.UpdateProductRequest;

import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest product);
    Product getProductById(Long productId);

    void deleteProductById(Long productId);
    Product updateProduct(UpdateProductRequest product, Long productId);

    List<Product> getAllProducts();
    List<Product> getProductsByCategory(String category);
    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByCategoryAndBrand(String category, String brand);

    List<Product> getProductsByName(String name);
    List<Product> getProductsByBrandAndName(String brand, String name);

    Long countProductsByBrandAndName(String brand, String name);

    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToProductDto(Product product);
}
