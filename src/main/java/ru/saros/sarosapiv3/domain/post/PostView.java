package ru.saros.sarosapiv3.domain.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostView {

    private Long id;
    private String title;
    private String text;
    private Long imageId;
    private LocalDateTime postDate;
}
