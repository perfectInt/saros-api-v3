package ru.saros.sarosapiv3.domain.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private Double price;
    private List<Long> imagesIds;
    private Long previewImageId;
}
