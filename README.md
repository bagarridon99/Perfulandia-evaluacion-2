 Perfulandia  Simulación de Microservicios con Spring Boot

Este proyecto representa una **migración de un sistema monolítico a una arquitectura basada en microservicios** simulada dentro de un solo proyecto Spring Boot, como parte de la evaluación semestral del curso.



 Arquitectura del Proyecto

La arquitectura se simuló a través de diferentes paquetes por servicio, aplicando el modelo CSR (Controller - Service - Repository) para cada uno.

Servicios simulados:

-servicio-autentificacion  
-servicio-usuario  
-servicio-producto  
- servicio-inventario  
- servicio-pedido  
- servicio-notificacion

Todos los servicios acceden a la base de datos mediante Hibernate (Spring Data JPA) y se conectan a MYSQL mediante xaamp.



 Tecnologías usadas


Java:Lenguaje de programación principal        
Spring Boot:Framework para microservicios             
Maven:Gestión de dependencias                   
Spring Data JPA:Acceso a base de datos con Hibernate      
MYSQL:Base de datos                   
Lombok:Reducción de código repetitivo            
Postman:Pruebas de endpoints                      
Git + GitHub:Control de versiones                      



