package com.example.shoppingcart.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.shoppingcart.model.CartItem;
import com.example.shoppingcart.model.Item;
import com.example.shoppingcart.model.ShoppingCart;
import com.example.shoppingcart.model.User;
import com.example.shoppingcart.repository.CartItemRepository;
import com.example.shoppingcart.repository.ItemRepository;
import com.example.shoppingcart.repository.ShoppingCartRepository;
import com.example.shoppingcart.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get or create a shopping cart for a user
     */
    public ShoppingCart getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return shoppingCartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(user));
    }

    /**
     * Create a new shopping cart for a user
     */
    private ShoppingCart createNewCart(User user) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());

        ShoppingCart savedCart = shoppingCartRepository.save(cart);
        log.info("New shopping cart created for user: {}", user.getId());
        return savedCart;
    }

    /**
     * Get cart items for a user
     */
    public List<CartItem> getCartItems(Long userId) {
        ShoppingCart cart = getOrCreateCart(userId);
        return cart.getItems();
    }

    /**
     * Add item to cart (or update quantity if already exists)
     */
    public CartItem addItemToCart(Long userId, Long itemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        ShoppingCart cart = getOrCreateCart(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + itemId));

        // Check if item already in cart
        CartItem existingItem = cart.getItems().stream()
                .filter(ci -> ci.getItem().getId().equals(itemId))
                .findFirst()
                .orElse(null);

        CartItem cartItem;
        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItem = cartItemRepository.save(existingItem);
            log.info("Updated item quantity in cart. Item: {}, New quantity: {}", itemId, cartItem.getQuantity());
        } else {
            // Create new cart item
            cartItem = new CartItem();
            cartItem.setShoppingCart(cart);
            cartItem.setItem(item);
            cartItem.setQuantity(quantity);
            cartItem = cartItemRepository.save(cartItem);
            cart.getItems().add(cartItem);
            log.info("Added item to cart. User: {}, Item: {}, Quantity: {}", userId, itemId, quantity);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        shoppingCartRepository.save(cart);

        return cartItem;
    }

    /**
     * Update item quantity in cart
     */
    public CartItem updateItemQuantity(Long userId, Long itemId, int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        ShoppingCart cart = getOrCreateCart(userId);
        CartItem cartItem = cart.getItems().stream()
                .filter(ci -> ci.getItem().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

        cartItem.setQuantity(newQuantity);
        CartItem updated = cartItemRepository.save(cartItem);
        cart.setUpdatedAt(LocalDateTime.now());
        shoppingCartRepository.save(cart);

        log.info("Updated item quantity. User: {}, Item: {}, New quantity: {}", userId, itemId, newQuantity);
        return updated;
    }

    /**
     * Remove item from cart
     */
    public void removeItemFromCart(Long userId, Long itemId) {
        ShoppingCart cart = getOrCreateCart(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + itemId));

        CartItem cartItem = cart.getItems().stream()
                .filter(ci -> ci.getItem().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

        cart.getItems().remove(cartItem);
        cartItemRepository.deleteByShoppingCartAndItem(cart, item);
        cart.setUpdatedAt(LocalDateTime.now());
        shoppingCartRepository.save(cart);

        log.info("Removed item from cart. User: {}, Item: {}", userId, itemId);
    }

    /**
     * Get total price of cart
     */
    public double getCartTotal(Long userId) {
        ShoppingCart cart = getOrCreateCart(userId);
        return cart.calculateTotal();
    }

    /**
     * Clear all items from cart
     */
    public void clearCart(Long userId) {
        ShoppingCart cart = getOrCreateCart(userId);
        cart.clearCart();
        cart.setUpdatedAt(LocalDateTime.now());
        shoppingCartRepository.save(cart);

        log.info("Cart cleared for user: {}", userId);
    }

    /**
     * Checkout - finalize the cart (placeholder for future enhancements)
     */
    public ShoppingCart checkout(Long userId) {
        ShoppingCart cart = getOrCreateCart(userId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot checkout empty cart");
        }

        // In future, this could process payment, create order, etc.
        double total = cart.calculateTotal();
        log.info("Checkout successful for user: {}. Total: ${}", userId, total);

        return cart;
    }
}
