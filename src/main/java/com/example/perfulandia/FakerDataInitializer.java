/* ELIMINAR ESTO PARA QUE EL CODIGO FUNCIONE


package com.example.perfulandia;

import com.example.perfulandia.model.*;
import com.example.perfulandia.model.enums.EstadoPedido;

// IMPORTACIONES DE REPOSITORIOS (AJUSTADAS A TUS PAQUETES REALES)
import com.example.perfulandia.usuario.repository.UsuarioRepository;
import com.example.perfulandia.producto.repository.ProductoRepository;
import com.example.perfulandia.inventario.repository.InventarioRepository;
import com.example.perfulandia.pedidos.repository.PedidosRepository; // <-- CORRECTO
import com.example.perfulandia.pedidos.repository.DetallePedidoRepository; // <-- CORRECTO
import com.example.perfulandia.notificacion.repository.NotificacionRepository;
import java.util.Locale;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class FakerDataInitializer implements CommandLineRunner {

    // DECLARACIÓN DE LOS CAMPOS FINALES PARA LOS REPOSITORIOS
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final PedidosRepository pedidosRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final NotificacionRepository notificacionRepository;

    // CONSTRUCTOR PARA LA INYECCIÓN DE DEPENDENCIAS DE SPRING
    public FakerDataInitializer(UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder,
                                ProductoRepository productoRepository,
                                InventarioRepository inventarioRepository,
                                PedidosRepository pedidosRepository,
                                DetallePedidoRepository detallePedidoRepository,
                                NotificacionRepository notificacionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
        this.pedidosRepository = pedidosRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.notificacionRepository = notificacionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Faker faker = new Faker(new Locale("es", "ES"));
        Random random = new Random();

        // ----------------------------------------------------
        // 1. Generar Usuarios Ficticios (si no hay suficientes)
        // ----------------------------------------------------
        // Asumiendo que tu DataInitializer (no este FakerDataInitializer)
        // ya crea admin@admin.com y user@user.com
        if (usuarioRepository.count() < 5) {
            System.out.println("Generando usuarios ficticios adicionales...");
            for (int i = 0; i < 3; i++) { // Genera 3 usuarios más
                UsuarioModel usuario = new UsuarioModel();
                usuario.setNombre(faker.name().fullName());
                usuario.setEmail(faker.internet().emailAddress());
                usuario.setPassword(passwordEncoder.encode(faker.internet().password(8, 12)));
                usuario.setRole(Role.ROLE_USER);
                usuarioRepository.save(usuario);
            }
            System.out.println("✅ " + usuarioRepository.count() + " usuarios en total (incluyendo los ficticios).");
        }
        List<UsuarioModel> allUsers = usuarioRepository.findAll();
        if (allUsers.isEmpty()) {
            System.err.println("❌ No hay usuarios para asociar a pedidos o notificaciones. Por favor, asegúrate de tener usuarios en la DB.");
            return;
        }

        // ----------------------------------------------------
        // 2. Generar Productos e Inventarios (si no existen)
        // ----------------------------------------------------
        if (productoRepository.count() == 0) {
            System.out.println("Generando productos e inventarios ficticios...");
            String[] categoriasPerfumilandia = {"Perfumes Hombre", "Perfumes Mujer", "Cuidado Corporal", "Maquillaje", "Accesorios"};
            String[] marcasPerfumilandia = {"Chanel", "Dior", "Versace", "Gucci", "Calvin Klein", "Victoria's Secret", "Body Shop"};

            for (int i = 0; i < 60; i++) { // Genera 60 productos
                ProductoModel producto = new ProductoModel();
                producto.setNombre(faker.commerce().productName() + " " + faker.color().name() + " (" + faker.number().digits(4) + ")");
                producto.setDescripcion(faker.lorem().paragraph(random.nextInt(3) + 1));
                producto.setMarca(marcasPerfumilandia[random.nextInt(marcasPerfumilandia.length)]);
                producto.setCategoria(categoriasPerfumilandia[random.nextInt(categoriasPerfumilandia.length)]);
                producto.setPrecio(faker.number().randomDouble(2, 20, 500));
                producto.setTamanioMl(faker.number().numberBetween(30, 200));

                productoRepository.save(producto);

                InventarioModel inventario = new InventarioModel();
                inventario.setProducto(producto);
                inventario.setCantidadDisponible(faker.number().numberBetween(0, 150));

                inventarioRepository.save(inventario);

                producto.setInventario(inventario);
                productoRepository.save(producto);
            }
            System.out.println("✅ " + productoRepository.count() + " productos creados.");
            System.out.println("✅ " + inventarioRepository.count() + " inventarios creados.");
        }
        List<ProductoModel> allProducts = productoRepository.findAll();
        if (allProducts.isEmpty()) {
            System.err.println("❌ No hay productos para asociar a detalles de pedido. Asegúrate de tener productos en la DB.");
            return;
        }

        // ----------------------------------------------------
        // 3. Generar Pedidos y Detalles de Pedido (si no existen)
        // ----------------------------------------------------
        if (pedidosRepository.count() == 0) {
            System.out.println("Generando pedidos y detalles de pedido ficticios...");
            EstadoPedido[] estadosPedido = EstadoPedido.values();

            for (int i = 0; i < 40; i++) { // Genera 40 pedidos
                PedidosModel pedido = new PedidosModel();
                UsuarioModel usuarioAleatorio = allUsers.get(random.nextInt(allUsers.size()));
                pedido.setUsuario(usuarioAleatorio);

                pedido.setEstado(estadosPedido[random.nextInt(estadosPedido.length)]);

                Date pastDate = faker.date().past(365, TimeUnit.DAYS);
                pedido.setFechaPedido(pastDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

                pedidosRepository.save(pedido);

                // --- Generar Detalles de Pedido para este Pedido ---
                int numDetalles = faker.number().numberBetween(1, 5);
                BigDecimal totalActualPedido = BigDecimal.ZERO;

                for (int j = 0; j < numDetalles; j++) {
                    DetallePedidoModel detalle = new DetallePedidoModel();
                    ProductoModel productoAleatorio = allProducts.get(random.nextInt(allProducts.size()));

                    detalle.setProducto(productoAleatorio);
                    detalle.setCantidad(faker.number().numberBetween(1, 3));
                    detalle.setPrecioUnitarioAlMomentoDeCompra(BigDecimal.valueOf(productoAleatorio.getPrecio()));
                    detalle.setSubtotal(BigDecimal.valueOf(productoAleatorio.getPrecio()).multiply(BigDecimal.valueOf(detalle.getCantidad())));

                    detalle.setPedido(pedido);
                    pedido.agregarDetalle(detalle);

                    totalActualPedido = totalActualPedido.add(detalle.getSubtotal());

                    detallePedidoRepository.save(detalle);
                }
                pedido.setTotalPedido(totalActualPedido);
                pedidosRepository.save(pedido);
            }
            System.out.println("✅ " + pedidosRepository.count() + " pedidos creados.");
            System.out.println("✅ " + detallePedidoRepository.count() + " detalles de pedido creados.");
        }

        // ----------------------------------------------------
        // 4. Generar Notificaciones (si no existen)
        // ----------------------------------------------------
        if (notificacionRepository.count() == 0) {
            System.out.println("Generando notificaciones ficticias...");
            for (int i = 0; i < 50; i++) {
                NotificacionModel notificacion = new NotificacionModel();
                notificacion.setMensaje(faker.lorem().sentence(random.nextInt(10) + 5));
                notificacion.setLeida(faker.bool().bool());

                Date pastDate = faker.date().past(90, TimeUnit.DAYS);
                notificacion.setFechaCreacion(pastDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

                UsuarioModel usuarioDestinatario = allUsers.get(random.nextInt(allUsers.size()));
                notificacion.setUsuarioDestinatario(usuarioDestinatario);

                notificacionRepository.save(notificacion);
            }
            System.out.println("✅ " + notificacionRepository.count() + " notificaciones creadas.");
        }
    }
}

*/