package com.ayeshan.InventoryManagementApp.controllers;

import com.ayeshan.InventoryManagementApp.models.Product;
import com.ayeshan.InventoryManagementApp.models.ProductDto;
import com.ayeshan.InventoryManagementApp.services.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductRepository repo;

    @GetMapping({"","/"})
    public String showProductList(Model model){
        List<Product> products = repo.findAll(); //Add this inside brackets to reverse the order of Id  =>  Sort.by(Sort.Direction.DESC,"id")
        model.addAttribute("products",products);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model){
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto",productDto);
        return "products/CreateProduct";
    }


}
