package ru.saros.sarosapiv3.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Product controller", description = "Endpoints for products manipulating")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(tags = "Product controller", description = "Get products by pages and (or) category")
    public List<ProductResponse> getAllProductsByPage(@RequestParam(name = "page", required = false) Integer page,
                                                      @RequestParam(name = "category", required = false) String category) {
        return productService.getProducts(page, category);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(tags = "Product controller", description = "Get product by its id")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'MODERATOR')")
    @Operation(tags = "Product controller", description = "Create new product")
    public Product createProduct(@RequestParam(name = "title") String title,
                                 @RequestParam(name = "category") String category,
                                 @RequestParam(name = "price") int price,
                                 @RequestParam(name = "description") String description,
                                 @RequestParam(name = "images[]") MultipartFile[] files,
                                 @RequestParam(name = "link", required = false) String link) throws IOException {
        return productService.saveProduct(title, category, price, description, files, link);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'MODERATOR')")
    @Operation(tags = "Product controller", description = "Delete existing product")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/last")
    @ResponseStatus(HttpStatus.OK)
    @Operation(tags = "Product controller", description = "Get 6 last products")
    public List<Product> getLastProducts() {
        return productService.getLastProducts();
    }
}
