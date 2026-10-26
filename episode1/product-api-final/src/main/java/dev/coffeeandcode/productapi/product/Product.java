package dev.coffeeandcode.productapi.product;

import java.math.BigDecimal;

public record Product(Long id, String name, BigDecimal price, int stock) {

    public Product withPrice(BigDecimal newPrice) {
        return new Product(id, name, newPrice, stock);
    }
}
