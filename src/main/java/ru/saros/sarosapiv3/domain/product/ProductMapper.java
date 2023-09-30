package ru.saros.sarosapiv3.domain.product;

import org.springframework.stereotype.Component;


@Component
public class ProductMapper {

    public ProductResponse toView(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setTitle(product.getTitle());
        productResponse.setCategory(product.getCategory());
        productResponse.setDescription(product.getDescription());
        productResponse.setPreviewImageId(product.getPreviewImageId());
        productResponse.setPrice(product.getPrice());
        productResponse.setImagesIds(product.getImages());
        return productResponse;
    }

    public Product toEntity(String title, String category, int price, String description, String link) {
        Product product = new Product();
        product.setTitle(title);
        product.setCategory(category);
        product.setPrice(price);
        product.setDescription(description);
        product.setLink(link);
        return product;
    }
}
