package ru.saros.sarosapiv3.domain.image;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.*;
import ru.saros.sarosapiv3.domain.post.Post;
import ru.saros.sarosapiv3.domain.product.Product;

import java.sql.Types;

@Entity
@Table(name = "images")
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String originalFileName;

    private Long size;

    private String contentType;

    @Lob
    @JdbcTypeCode(Types.LONGVARBINARY)
    private byte[] bytes;
}
