package com.example.shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shoppingcart.model.Item;

/**
 *
 * @author yuze1
 */
public interface ItemRepository extends JpaRepository<Item, Long> {
}
