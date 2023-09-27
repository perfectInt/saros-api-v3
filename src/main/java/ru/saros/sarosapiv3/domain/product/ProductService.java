package ru.saros.sarosapiv3.domain.product;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.saros.sarosapiv3.api.exception.ProductNotFoundException;
import ru.saros.sarosapiv3.domain.image.Image;
import ru.saros.sarosapiv3.domain.image.ImageMapper;
import ru.saros.sarosapiv3.domain.image.ImageRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final ImageMapper imageMapper;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    @Value("${pagination:12}")
    private Integer pagination;

    public List<ProductResponse> getProducts(Integer page, String category) {
        if (page == null) page = 0;
        Pageable paging = PageRequest.of(page, pagination, Sort.by("dateOfCreation"));
        Page<Product> products;
        if (category == null) products = productRepository.findAll(paging);
        else products = productRepository.findAllByCategory(paging, category);
        List<ProductResponse> productViews = new ArrayList<>();
        for (Product product : products) {
            ProductResponse productResponse = productMapper.toView(product);
            productViews.add(productResponse);
        }
        return productViews;
    }

    @Transactional
    public Product saveProduct(String title, String category, int price, String description, MultipartFile[] files) throws IOException {
        Product product = productMapper.toEntity(title, category, price, description);
        List<Long> images = new ArrayList<>();
        for (MultipartFile file : files) {
            Image image = imageMapper.toEntity(file);
            images.add(imageRepository.save(image).getId());
        }
        product.setPreviewImageId(images.get(0));
        product.setImages(images);
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Cannot find this product"));
        return productMapper.toView(product);
    }
}

