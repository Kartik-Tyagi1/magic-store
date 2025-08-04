package com.kt.magicstore.service.cart;

import com.kt.magicstore.model.Cart;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCart(Long id);
    void clearAndDeleteCart(Long id);
    BigDecimal getTotalPrice(Long id);
}
