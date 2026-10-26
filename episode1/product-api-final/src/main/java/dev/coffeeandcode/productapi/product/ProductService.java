package dev.coffeeandcode.productapi.product;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    // Step 3: cache by id
    @Cacheable(value = "products", key = "#id")
    public Product getProduct(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Cacheable("productList")
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    // The fix for the stale-price bug: evict from BOTH caches
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "productList", allEntries = true)
    })
    public Product updatePrice(Long id, BigDecimal newPrice) {
        return repository.updatePrice(id, newPrice);
    }

    // condition = checked BEFORE the method (arguments only)
    // unless    = checked AFTER the method (#result is available)
    @Cacheable(value = "productSearch",
               key = "#keyword",
               condition = "#keyword.length() >= 3",
               unless = "#result.isEmpty()")
    public List<Product> search(String keyword) {
        return repository.searchByName(keyword);
    }
}
