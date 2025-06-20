package com.example.perfulandia.producto.service;

import com.example.perfulandia.model.ProductoModel; // Asegúrate de que esta sea la ruta correcta de tu entidad ProductoModel
import com.example.perfulandia.producto.ProductoServiceImpl;
import com.example.perfulandia.producto.repository.ProductoRepository; // Asegúrate de que esta sea la ruta correcta de tu repositorio ProductoRepository
import org.junit.jupiter.api.BeforeEach; // Para JUnit 5
import org.junit.jupiter.api.Test;    // Para JUnit 5
import org.junit.jupiter.api.extension.ExtendWith; // Para Mockito con JUnit 5
import org.mockito.InjectMocks;      // Para inyectar mocks en la clase a probar
import org.mockito.Mock;            // Para crear objetos mock
import org.mockito.junit.jupiter.MockitoExtension; // Extensión de Mockito para JUnit 5

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*; // Para aserciones de JUnit (ej. assertEquals, assertNotNull)
import static org.mockito.Mockito.*; // Para métodos estáticos de Mockito (ej. when, verify, any, never, times)

@ExtendWith(MockitoExtension.class) // Habilita el uso de anotaciones de Mockito para JUnit 5
public class ProductoServiceImplTest {

    // @Mock crea una instancia simulada (mock) de ProductoRepository.
    // Este objeto no se conectará a ninguna base de datos real.
    @Mock
    private ProductoRepository productoRepository;

    // @InjectMocks crea una instancia real de ProductoServiceImpl.
    // Mockito intentará inyectar automáticamente los @Mock (como productoRepository)
    // en esta instancia a través de su constructor o setters.
    @InjectMocks
    private ProductoServiceImpl ProductoService; // Esta es la clase que estamos probando (System Under Test - SUT)

    // Este método se ejecuta antes de CADA método de test (@Test) en esta clase.
    // Puedes usarlo para configurar el estado inicial o resetear mocks si es necesario.
    @BeforeEach
    void setUp() {
        // En este caso, con @ExtendWith(MockitoExtension.class), Mockito ya maneja la inicialización
        // de los mocks y la inyección, por lo que este método puede estar vacío o usarse para
        // configuraciones más específicas si se requieren antes de cada test individual.
    }

    @Test // Marca este método como un caso de test unitario.
    void testAnadirProducto_Exito() {
        // 1. Configuración (Arrange): Define el comportamiento esperado del mock.
        // Creamos un producto de entrada sin ID (simulando un producto nuevo antes de guardarlo).
        ProductoModel productoEntrada = new ProductoModel("Perfume de Rosas", "Fragancia fresca y floral", "Floralia", "Floral", 50.0, 100);
        // Creamos el producto que esperamos que sea devuelto por el repositorio después de "guardar" (con un ID asignado).
        ProductoModel productoEsperado = new ProductoModel(1L, "Perfume de Rosas", "Fragancia fresca y floral", "Floralia", "Floral", 50.0, 100);

        // Le decimos a Mockito: "Cuando se llame a productoRepository.save() con cualquier objeto ProductoModel,
        // entonces devuelve 'productoEsperado'". 'any(ProductoModel.class)' es un matcher que acepta cualquier instancia de ProductoModel.
        when(productoRepository.save(any(ProductoModel.class))).thenReturn(productoEsperado);

        // 2. Ejecución (Act): Llama al método de la clase que estamos probando.
        ProductoModel resultado = ProductoService.anadirProducto(productoEntrada);

        // 3. Verificación (Assert): Comprueba que el resultado es el esperado.
        assertNotNull(resultado, "El producto devuelto no debería ser nulo.");
        assertEquals(1L, resultado.getId(), "El ID del producto debería ser 1L.");
        assertEquals("Perfume de Rosas", resultado.getNombre(), "El nombre del producto debería coincidir.");
        assertEquals("Floralia", resultado.getMarca(), "La marca debería coincidir.");

        // 4. Verificación de interacciones (Verify): Comprueba que los mocks fueron utilizados como se esperaba.
        // Verifica que productoRepository.save() fue llamado EXACTAMENTE una vez con 'productoEntrada'.
        verify(productoRepository, times(1)).save(productoEntrada);
    }

    @Test
    void testBuscarProductoPorId_Encontrado() {
        // Configuración
        Long idBuscado = 2L;
        ProductoModel productoEncontrado = new ProductoModel(idBuscado, "Perfume Cítrico", "Frescura vibrante", "CitrusCo", "Cítrico", 75.0, 120);
        // Cuando se llama findById() con 'idBuscado', devuelve un Optional que contiene 'productoEncontrado'.
        when(productoRepository.findById(idBuscado)).thenReturn(Optional.of(productoEncontrado));

        // Ejecución
        Optional<ProductoModel> resultado = ProductoService.buscarProductoPorId(idBuscado);

        // Verificación
        assertTrue(resultado.isPresent(), "El producto debería estar presente.");
        assertEquals(idBuscado, resultado.get().getId(), "El ID del producto debería coincidir.");
        assertEquals("Perfume Cítrico", resultado.get().getNombre(), "El nombre del producto debería coincidir.");

        // Verificación de interacciones
        verify(productoRepository, times(1)).findById(idBuscado);
    }

    @Test
    void testBuscarProductoPorId_NoEncontrado() {
        // Configuración
        Long idNoEncontrado = 99L;
        // Cuando se llama findById() con 'idNoEncontrado', devuelve un Optional vacío.
        when(productoRepository.findById(idNoEncontrado)).thenReturn(Optional.empty());

        // Ejecución
        Optional<ProductoModel> resultado = ProductoService.buscarProductoPorId(idNoEncontrado);

        // Verificación
        assertFalse(resultado.isPresent(), "El producto NO debería estar presente.");

        // Verificación de interacciones
        verify(productoRepository, times(1)).findById(idNoEncontrado);
    }

    @Test
    void testBuscarTodosLosProductos_Exito() {
        // Configuración
        ProductoModel p1 = new ProductoModel(1L, "Prod A", "Desc A", "M1", "C1", 10.0, 10);
        ProductoModel p2 = new ProductoModel(2L, "Prod B", "Desc B", "M2", "C2", 20.0, 20);
        List<ProductoModel> productosEsperados = Arrays.asList(p1, p2);

        // Cuando se llama findAll(), devuelve la lista predefinida.
        when(productoRepository.findAll()).thenReturn(productosEsperados);

        // Ejecución
        List<ProductoModel> resultado = ProductoService.buscarTodosLosProductos();

        // Verificación
        assertNotNull(resultado, "La lista de productos no debería ser nula.");
        assertEquals(2, resultado.size(), "La lista debería contener 2 productos.");
        assertEquals("Prod A", resultado.get(0).getNombre(), "El nombre del primer producto debería coincidir.");
        assertEquals("Prod B", resultado.get(1).getNombre(), "El nombre del segundo producto debería coincidir.");

        // Verificación de interacciones
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void testEliminarProducto_Exito() {
        // Configuración
        Long idAEliminar = 3L;
        // Cuando existsById() es llamado con 'idAEliminar', devuelve true.
        when(productoRepository.existsById(idAEliminar)).thenReturn(true);
        // Cuando deleteById() es llamado, no hagas nada (para métodos void, usar doNothing()).
        doNothing().when(productoRepository).deleteById(idAEliminar);

        // Ejecución y Verificación (assertDoesNotThrow para verificar que no lanza excepción)
        assertDoesNotThrow(() -> ProductoService.eliminarProducto(idAEliminar), "No debería lanzar ninguna excepción.");

        // Verificación de interacciones
        verify(productoRepository, times(1)).existsById(idAEliminar);
        verify(productoRepository, times(1)).deleteById(idAEliminar);
    }

    @Test
    void testEliminarProducto_NoEncontradoLanzaExcepcion() {
        // Configuración
        Long idNoEncontrado = 99L;
        when(productoRepository.existsById(idNoEncontrado)).thenReturn(false);

        // Ejecución y Verificación (assertThrows para verificar que se lanza una excepción específica)
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            ProductoService.eliminarProducto(idNoEncontrado);
        }, "Debería lanzar una RuntimeException si el producto no se encuentra.");

        assertEquals("Producto no encontrado con id: 99 para eliminar.", thrown.getMessage(), "El mensaje de error debería coincidir.");

        // Verificación de interacciones
        verify(productoRepository, times(1)).existsById(idNoEncontrado);
        // Verifica que deleteById() NUNCA fue llamado, ya que el producto no existía.
        verify(productoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testEditarProducto_Exito() {
        // Configuración
        Long idAEditar = 4L;
        ProductoModel productoExistente = new ProductoModel(idAEditar, "Old Name", "Old Desc", "Old Brand", "Old Cat", 10.0, 50);
        ProductoModel productoConNuevosDatos = new ProductoModel(idAEditar, "New Name", "New Desc", "New Brand", "New Cat", 15.0, 75);
        ProductoModel productoActualizadoDevuelto = new ProductoModel(idAEditar, "New Name", "New Desc", "New Brand", "New Cat", 15.0, 75); // Lo que save() devolvería

        when(productoRepository.findById(idAEditar)).thenReturn(Optional.of(productoExistente));
        // Cuando save() es llamado con cualquier ProductoModel, devuelve el 'productoActualizadoDevuelto'.
        when(productoRepository.save(any(ProductoModel.class))).thenReturn(productoActualizadoDevuelto);

        // Ejecución
        ProductoModel resultado = ProductoService.editarProducto(idAEditar, productoConNuevosDatos);

        // Verificación
        assertNotNull(resultado, "El producto actualizado no debería ser nulo.");
        assertEquals("New Name", resultado.getNombre(), "El nombre debería haberse actualizado.");
        assertEquals("New Brand", resultado.getMarca(), "La marca debería haberse actualizado.");
        assertEquals(15.0, resultado.getPrecio(), "El precio debería haberse actualizado.");

        // Verificación de interacciones
        verify(productoRepository, times(1)).findById(idAEditar);
        verify(productoRepository, times(1)).save(any(ProductoModel.class)); // Se llamó a save con el producto modificado
    }

    @Test
    void testEditarProducto_NoEncontradoLanzaExcepcion() {
        // Configuración
        Long idNoEncontrado = 99L;
        ProductoModel productoConNuevosDatos = new ProductoModel(idNoEncontrado, "New Name", "New Desc", "New Brand", "New Cat", 15.0, 75);

        when(productoRepository.findById(idNoEncontrado)).thenReturn(Optional.empty());

        // Ejecución y Verificación
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            ProductoService.editarProducto(idNoEncontrado, productoConNuevosDatos);
        }, "Debería lanzar una RuntimeException si el producto no se encuentra para editar.");

        assertEquals("Producto no encontrado con id: 99 para editar.", thrown.getMessage(), "El mensaje de error debería coincidir.");

        // Verificación de interacciones
        verify(productoRepository, times(1)).findById(idNoEncontrado);
        verify(productoRepository, never()).save(any(ProductoModel.class)); // save() no debería haber sido llamado
    }

}