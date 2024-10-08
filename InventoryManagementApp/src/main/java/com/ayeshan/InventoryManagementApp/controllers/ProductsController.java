package com.ayeshan.InventoryManagementApp.controllers;

import com.ayeshan.InventoryManagementApp.models.Product;
import com.ayeshan.InventoryManagementApp.models.ProductDto;
import com.ayeshan.InventoryManagementApp.services.ProductRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
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

    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result
    ) {
        if (productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "The image file is required"));
        }
        if (result.hasErrors()){
            return "products/CreateProduct";
        }

        MultipartFile image = productDto.getImageFile();
        Date createAt = new Date();
        String storageFileName = createAt.getTime() + "_" + image.getOriginalFilename();

        try {
            String uploadDir = "InventoryManagementApp/src/main/resources/static/images/";
            Path uploadpath = Paths.get(uploadDir);

            if (!Files.exists(uploadpath)) {
                Files.createDirectories(uploadpath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }catch (Exception ex){
                System.out.println("Exception" + ex.getMessage());
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createAt);
        product.setImageFileName(storageFileName);

        repo.save(product);

        return "redirect:/products";
    }


    @GetMapping("/edit")
    public String showEditPage(
            Model model,
            @RequestParam int id
    ) {

        try {
            Product product = repo.findById(id).get();
            model.addAttribute("product", product);

            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);
        }
        catch (Exception ex){
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/products";
        }
        return "products/EditProduct";
    }

    @PostMapping("/edit")
    public String updateProduct(
            Model model,
            @RequestParam int id,
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result
    ) {

        try{
            Product product = repo.findById(id).get();
            model.addAttribute("product", product);

            if(result.hasErrors()){
                return "products/EditProduct";
            }

            if(!productDto.getImageFile().isEmpty()){
                //delete old image
                String uploadDir = "InventoryManagementApp/src/main/resources/static/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());

                try{
                    Files.delete(oldImagePath);
                }
                catch (Exception ex) {
                    System.out.println("Exception: " + ex.getMessage());
                }

                //Save new image File
                MultipartFile image = productDto.getImageFile();
                Date createAt = new Date();
                String storageFileName = createAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }

                product.setImageFileName(storageFileName);
            }

            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());

            repo.save(product);
        }
        catch (Exception ex){
            System.out.println("Exception: " + ex.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(
            @RequestParam int id
    ) {
        try{
            Product product = repo.findById(id).get();

            //delete product image
            Path imagepath = Paths.get("InventoryManagementApp/src/main/resources/static/images/" + product.getImageFileName());

            try{
                Files.delete(imagepath);
            }catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
            }

            //delete the product
            repo.delete(product);
        }
        catch (Exception ex){
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/products";
    }

}
