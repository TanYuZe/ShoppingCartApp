/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.example.shoppingcart.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.shoppingcart.model.Item;

/**
 *
 * @author yuze1
 */
@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    @GetMapping
    public List<Item> getCart() {
        return List.of();

    }

}
