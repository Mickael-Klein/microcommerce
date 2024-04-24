package com.ecommerce.microcommerce.web.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ecommerce.microcommerce.web.DAO.ProductDao;
import com.ecommerce.microcommerce.web.model.Product;
import com.ecommerce.microcommerce.web.util.DTO.ProductMarge;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import jakarta.validation.Valid;

@RestController
public class ProductController {

    @Autowired
    ProductDao productDao;

    @GetMapping("/produits")
    public MappingJacksonValue listeProduits() {
        Iterable<Product> produits = productDao.findAll();
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listeDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);
        produitsFiltres.setFilters(listeDeNosFiltres);
        return produitsFiltres;
    }

    @GetMapping("/produit/{id}")
    public ResponseEntity<Product> afficherUnProduit(@PathVariable int id) {
        Product produit = productDao.findById(id);
        if (produit == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(produit);
    }

    @GetMapping("test/produits/{prixLimit}")
    public List<Product> testeDeRequetes(@PathVariable int prixLimit) {
        return productDao.findByPrixGreaterThan(400);
    }

    @PostMapping("/produit/save")
    public ResponseEntity<?> ajouterProduit(@Valid @RequestBody Product product) {
        if (product.getPrix() < 1) {
            return ResponseEntity.badRequest().body("Le prix ne peut pas être inférieur à 1");
        }

        Product productAdded = productDao.save(product);

        if (Objects.isNull(productAdded)) {
            return ResponseEntity.noContent().build();
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(productAdded.getId()).toUri();

        return ResponseEntity.created(location).build();

    }

    @DeleteMapping("/produit/{id}")
    public void supprimerProduit(@PathVariable Long id) {
        productDao.deleteById(id);
    }

    @PutMapping("/produit")
    public void updateProduit(@RequestBody Product product) {
        productDao.save(product);
    }

    @GetMapping("/test/customquery/{prixLimit}")
    public List<Product> recupererProduitSelonPrix(@PathVariable int prixLimit) {
        return productDao.chercherUnProduitCher(prixLimit);
    }

    @GetMapping("/produits/marge")
    public ResponseEntity<List<ProductMarge>> calculerMarge() {
        Iterable<Product> produits = productDao.findAll();
        List<ProductMarge> produitsMargesToReturn = new ArrayList<ProductMarge>();

        for (Product produit : produits) {
            ProductMarge produitMarge = new ProductMarge();
            produitMarge.setId(produit.getId()).setNom(produit.getNom()).setPrix(produit.getPrix())
                    .setPrixAchat(produit.getPrixAchat());
            produitMarge.setMarge(produitMarge.getPrix() - produitMarge.getPrixAchat());
            produitsMargesToReturn.add(produitMarge);
        }

        return ResponseEntity.ok().body(produitsMargesToReturn);
    }

    @GetMapping("/produits/alphabetical")
    public ResponseEntity<List<Product>> trierParOrdreAlphabetique() {
        Iterable<Product> produits = productDao.findAll();
        List<Product> produitsTriesParOrdreAlphabetiqueDeNom = new ArrayList<Product>();

        for (Product produit : produits) {
            produitsTriesParOrdreAlphabetiqueDeNom.add(produit);
        }

        Collections.sort(produitsTriesParOrdreAlphabetiqueDeNom, Comparator.comparing(Product::getNom));

        return ResponseEntity.ok().body(produitsTriesParOrdreAlphabetiqueDeNom);
    }
}
