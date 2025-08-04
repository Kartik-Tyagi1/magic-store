package com.kt.magicstore.repository;

import com.kt.magicstore.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}

