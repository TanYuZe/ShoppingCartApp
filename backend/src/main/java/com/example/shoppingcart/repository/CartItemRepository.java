package com.example.shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shoppingcart.model.CartItem;
import com.example.shoppingcart.model.Item;
import com.example.shoppingcart.model.ShoppingCart;

/**
 *
 * @author yuze1
 */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    void deleteByShoppingCartAndItem(ShoppingCart shoppingCart, Item item);
}
