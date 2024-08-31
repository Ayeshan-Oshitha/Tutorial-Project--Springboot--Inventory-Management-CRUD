package com.ayeshan.InventoryManagementApp.services;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ayeshan.InventoryManagementApp.models.Product;



public interface ProductRepository extends JpaRepository<Product , Integer> {
}
