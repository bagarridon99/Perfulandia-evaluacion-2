package com.example.perfulandia.producto.repository;

import com.example.perfulandia.model.ProductoModel; //
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoModel, Long> {

}