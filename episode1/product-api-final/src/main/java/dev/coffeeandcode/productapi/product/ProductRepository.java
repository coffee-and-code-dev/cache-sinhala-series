package dev.coffeeandcode.productapi.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Pretends to be a slow database.
 * Every read sleeps for 2 seconds, like a heavy query with joins.
 * Watch for the "DB HIT" log line: if it prints, we went to the database.
 */
@Repository
public class ProductRepository {

    private static final Logger log = LoggerFactory.getLogger(ProductRepository.class);
    private static final long SLOW_QUERY_MS = 2000;

    private final Map<Long, Product> store = new ConcurrentHashMap<>();

    public ProductRepository() {
        save(new Product(1L, "iPhone 16", new BigDecimal("1000"), 25));
        save(new Product(2L, "Galaxy S25", new BigDecimal("1200"), 40));
        save(new Product(3L, "Pixel 10", new BigDecimal("980"), 15));
        save(new Product(4L, "iPad Air", new BigDecimal("850"), 30));
        save(new Product(5L, "AirPods Pro", new BigDecimal("250"), 100));
        save(new Product(6L, "MacBook Air", new BigDecimal("1300"), 10));
    }

    public Optional<Product> findById(Long id) {
        simulateSlowQuery();
        log.info("🐢 DB HIT → product {}", id);
        return Optional.ofNullable(store.get(id));
    }

    public List<Product> findAll() {
        simulateSlowQuery();
        log.info("🐢 DB HIT → all products");
        return store.values().stream()
                .sorted(Comparator.comparing(Product::id))
                .toList();
    }

    public List<Product> searchByName(String keyword) {
        simulateSlowQuery();
        log.info("🐢 DB HIT → search '{}'", keyword);
        String k = keyword.toLowerCase();
        return store.values().stream()
                .filter(p -> p.name().toLowerCase().contains(k))
                .sorted(Comparator.comparing(Product::id))
                .toList();
    }

    public Product updatePrice(Long id, BigDecimal newPrice) {
        Product current = store.get(id);
        if (current == null) {
            throw new ProductNotFoundException(id);
        }
        Product updated = current.withPrice(newPrice);
        save(updated);
        log.info("✏️  DB UPDATE → product {} price = {}", id, newPrice);
        return updated;
    }

    private void save(Product product) {
        store.put(product.id(), product);
    }

    private void simulateSlowQuery() {
        try {
            Thread.sleep(SLOW_QUERY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
