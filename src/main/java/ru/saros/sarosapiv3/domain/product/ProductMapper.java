package ru.saros.sarosapiv3.domain.product;

import org.springframework.stereotype.Component;
import ru.saros.sarosapiv3.domain.image.Image;

import java.util.ArrayList;
import java.util.List;

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
        List<Long> ids = new ArrayList<>();
        for (Image image : product.getImages()) {
            ids.add(image.getId());
        }
        productResponse.setImagesIds(ids);
        return productResponse;
    }

    public Product toEntity(String title, String category, Integer price, String description) {
        Product product = new Product();
        product.setTitle(title);
        product.setCategory(category);
        product.setPrice(price);
        product.setDescription(description);
        return product;
    }
}
