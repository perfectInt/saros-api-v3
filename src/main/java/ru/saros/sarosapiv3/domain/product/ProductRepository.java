package ru.saros.sarosapiv3.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByCategory(Pageable paging, String category);

    @Query(value =
            """
            SELECT *
            FROM products
            ORDER BY date_of_creation DESC
            LIMIT 6;
            """,
            nativeQuery = true
    )
    List<Product> findLastProducts();
}
