package ru.saros.sarosapiv3.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.saros.sarosapiv3.domain.product.Product;
import ru.saros.sarosapiv3.domain.product.ProductResponse;
import ru.saros.sarosapiv3.domain.product.ProductService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/products")
@CrossOrigin
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProductsByPage(@RequestParam(name = "page", required = false) Integer page,
                                                      @RequestParam(name = "category", required = false) String category) {
        return productService.getProducts(page, category);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'MODERATOR')")
    public Product createProduct(@RequestParam(name = "title") String title,
                                 @RequestParam(name = "category") String category,
                                 @RequestParam(name = "price") String price,
                                 @RequestParam(name = "description") String description,
                                 @RequestParam(name = "images[]") MultipartFile[] files) throws IOException {
        return productService.saveProduct(title, category, Integer.parseInt(price), description, files);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'MODERATOR')")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
