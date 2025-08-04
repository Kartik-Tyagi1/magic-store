package com.kt.magicstore.service.cart;

import com.kt.magicstore.exceptions.ResourceNotFoundException;
import com.kt.magicstore.model.Cart;
import com.kt.magicstore.model.CartItem;
import com.kt.magicstore.model.Product;
import com.kt.magicstore.repository.CartItemRepository;
import com.kt.magicstore.repository.CartRepository;
import com.kt.magicstore.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final CartRepository cartRepository;

    @Override
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        // Get Cart and Product
        Cart cart = cartService.getCart(cartId);
        Product product = productService.getProductById(productId);

        // Check product exists as cartItem in the cart
        CartItem cartItem = getCartItem(productId, cartId);

        // Does not Exist - create new cart item
        if (cartItem.getId() == null) {
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getPrice());
        }
        // Exists - update quantity
        else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        // Update total price of cart item and save
        cartItem.setTotalPrice();
        cart.addItem(cartItem);

        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        CartItem cartItemToRemove = getCartItem(productId, cartId);

        if (cartItemToRemove.getId() == null) {
            throw new ResourceNotFoundException("CartItem not found");
        }

        cart.removeItem(cartItemToRemove);
        cartRepository.save(cart);
    }

    @Override
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCart(cartId);
        CartItem cartItem = getCartItem(productId, cartId);

        if (cartItem.getId() == null) {
            throw new ResourceNotFoundException("CartItem not found");
        }

        cartItem.setQuantity(quantity);
        cartItem.setUnitPrice(cartItem.getProduct().getPrice());
        cartItem.setTotalPrice();

        BigDecimal totalAmount = cart.getTotalAmount();
        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
    }

    @Override
    public CartItem getCartItem(Long productId, Long cartId) {
        Cart cart = cartService.getCart(cartId);
        return cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(new CartItem());
    }
}
