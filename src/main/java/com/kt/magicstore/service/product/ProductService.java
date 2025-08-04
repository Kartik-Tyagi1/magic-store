package com.kt.magicstore.service.product;

import com.kt.magicstore.dto.ImageDto;
import com.kt.magicstore.dto.ProductDto;
import com.kt.magicstore.exceptions.ResourceNotFoundException;
import com.kt.magicstore.model.Category;
import com.kt.magicstore.model.Image;
import com.kt.magicstore.model.Product;
import com.kt.magicstore.repository.CategoryRepository;
import com.kt.magicstore.repository.ImageRepository;
import com.kt.magicstore.repository.ProductRepository;
import com.kt.magicstore.request.AddProductRequest;
import com.kt.magicstore.request.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;


    @Override
    public Product addProduct(AddProductRequest request) {
        // Check if the category exists in the db
        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() -> {
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        request.setCategory(category);
        return productRepository.save(createProduct(request, category));
    }

    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category
        );
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product not found"));
    }

    @Override
    public void deleteProductById(Long productId) {
        productRepository.findById(productId)
                .ifPresentOrElse(productRepository::delete,
                        ()->{throw new ResourceNotFoundException("Product not found");});
    }

    @Override
    public Product updateProduct(UpdateProductRequest request, Long productId) {
        return productRepository.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct, request))
                .map(productRepository :: save)
                .orElseThrow(()->new ResourceNotFoundException("Product not found"));
    }

    private Product updateExistingProduct(Product existingProduct, UpdateProductRequest request) {
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());

        Category category = categoryRepository.findByName(request.getCategory().getName());
        existingProduct.setCategory(category);

        return existingProduct;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream().map(this::convertToProductDto).toList();
    }

    @Override
    public ProductDto convertToProductDto(Product product) {
        ProductDto dto = modelMapper.map(product, ProductDto.class);
        List<Image> images =  imageRepository.findByProductId(product.getId());
        List<ImageDto> imagesDto = images.stream().map(image -> modelMapper.map(image, ImageDto.class)).toList();

        dto.setImages(imagesDto);
        return dto;
    }
}
