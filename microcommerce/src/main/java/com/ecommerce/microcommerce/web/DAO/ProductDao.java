package com.ecommerce.microcommerce.web.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.microcommerce.web.model.Product;

@Repository
public interface ProductDao extends JpaRepository<Product, Long> {
    Product findById(int id);

    java.util.List<Product> findByPrixGreaterThan(int prixLimite);

    @Query("SELECT p FROM Product p WHERE p.prix > :prixLimit")
    List<Product> chercherUnProduitCher(@Param("prixLimit") int prixLimit);
}
