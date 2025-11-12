package stoneyspring.SegundUM.pruebas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stoneyspring.SegundUM.controller.Controller;
import stoneyspring.SegundUM.dominio.Categoria;
import stoneyspring.SegundUM.dominio.EstadoProducto;
import stoneyspring.SegundUM.dominio.Producto;
import stoneyspring.SegundUM.dominio.ResumenProducto;
public class AppJava {

    public static void main(String[] args) {
    	Logger logger = LoggerFactory.getILoggerFactory().getLogger(AppJava.class.getName());
        
        Controller controlador = new Controller();
        
        logger.info("=== CASOS DE USO DE USUARIO ===\n");
		
		// CU1: Registrar usuario
		logger.info("1. Registrando nuevo usuario...");
		String usuarioId = controlador.registrarUsuario(
		    "juan.perez@email.com",
		    "Juan",
		    "Pérez García",
		    "password123",
		    LocalDate.of(1990, 5, 15),
		    "666123456"
		);
		logger.info("Usuario registrado con ID: " + usuarioId + "\n");
		
		// CU2: Modificar datos personales
		logger.info("2. Modificando datos del usuario...");
		boolean modificar = controlador.modificarDatosPersonales(
		    usuarioId,
		    null,  // No cambiar nombre
		    null,  // No cambiar apellidos
		    "newPassword456",  // Nueva contraseña
		    null,  // No cambiar fecha nacimiento
		    "677987654"  // Nuevo teléfono
		);
		if (modificar)logger.info("Datos del usuario actualizados\n"); else logger.warn("No se pudo modificar los datos del usuario\n");
		
		// CU8 (Administrador): Cargar categorías desde XML
		logger.info("=== CASOS DE USO DE ADMINISTRADOR ===\n");
		logger.info("8. Cargando categorías desde XML...");
		controlador.cargarCategorias("Electronica.xml");
		logger.info("Categorías cargadas\n");
		
		// Obtener categorías raíz para usar en los ejemplos
		List<Categoria> categoriasRaiz = controlador.obtenerCategoriasRaiz();
		if (categoriasRaiz.isEmpty()) {
			logger.error("No hay categorías disponibles");
		    return;
		}
		String categoriaId = categoriasRaiz.get(0).getId();
		logger.info("Usando categoría: " + categoriasRaiz.get(0).getNombre() + "\n");
		
		// CU9 (Administrador): Modificar descripción de categoría
		logger.info("9. Modificando descripción de categoría...");
		controlador.modificarDescripcionCategoria(
		    categoriaId,
		    "Categoría actualizada con nueva descripción"
		);
		logger.info("Descripción de categoría actualizada\n");
		
		logger.info("=== CONTINUANDO CON CASOS DE USO DE USUARIO ===\n");
		
		// CU3: Dar de alta un producto
		logger.info("3. Dando de alta un producto...");
		String productoId = controlador.darAltaProducto(
		    "iPhone 13 Pro",
		    "iPhone 13 Pro de 256GB en perfecto estado, con todos los accesorios originales",
		    new BigDecimal("799.99"),
		    EstadoProducto.COMO_NUEVO,
		    categoriaId,
		    true,
		    usuarioId
		);
		logger.info("Producto dado de alta con ID: " + productoId + "\n");
		
		// CU4: Modificar producto
		logger.info("4. Modificando precio y descripción del producto...");
		controlador.modificarProducto(
		    productoId,
		    new BigDecimal("749.99"),  // Nuevo precio rebajado
		    "iPhone 13 Pro de 256GB en perfecto estado, con todos los accesorios originales. ¡REBAJADO!"
		);
		logger.info("Producto modificado\n");
		
		// CU5: Asociar lugar de recogida
		logger.info("5. Asociando lugar de recogida al producto...");
		controlador.asociarLugarRecogida(
		    productoId,
		    "Centro Comercial La Vaguada, entrada principal",
		    -3.7038,  // Longitud (Madrid)
		    40.4168   // Latitud (Madrid)
		);
		logger.info("Lugar de recogida asociado\n");
		
		// Registrar algunas visualizaciones
		for (int i = 0; i < 5; i++) {
		    controlador.registrarVisualizacionProducto(productoId);
		}
		logger.info("5 visualizaciones registradas\n");
		
		// CU6: Obtener resumen mensual
		logger.info("6. Obteniendo resumen mensual de productos...");
		int mesActual = LocalDate.now().getMonthValue();
		int anioActual = LocalDate.now().getYear();
		List<ResumenProducto> resumen = controlador.obtenerResumenMensual(mesActual, anioActual);
		
		logger.info("Resumen del mes " + mesActual + "/" + anioActual + ":");
		for (ResumenProducto r : resumen) {
			logger.info("  - " + r.getTitulo() + 
		                     " | Precio: " + r.getPrecio() + "€" +
		                     " | Visualizaciones: " + r.getVisualizaciones());
		}
		
		// CU7: Buscar productos con filtros
		logger.info("7. Buscando productos con filtros...");
		
		// Búsqueda 1: Por texto
		logger.info("\ta) Búsqueda por texto 'iPhone':");
		List<Producto> resultados1 = controlador.buscarProductos(
		    null,
		    "iPhone",
		    null,
		    null
		);
		logger.info("\tEncontrados: " + resultados1.size() + " productos\n");
		
		// Búsqueda 2: Por categoría y precio máximo
		logger.info("\tb) Búsqueda por categoría y precio máximo 800€:");
		List<Producto> resultados2 = controlador.buscarProductos(
		    categoriaId,
		    null,
		    null,
		    new BigDecimal("800.00")
		);
		logger.info("\tEncontrados: " + resultados2.size() + " productos\n");
		
		// Búsqueda 3: Por estado mínimo
		logger.info("\tc) Búsqueda por estado mínimo BUEN_ESTADO:");
		List<Producto> resultados3 = controlador.buscarProductos(
		    null,
		    null,
		    EstadoProducto.BUEN_ESTADO,
		    null
		);
		logger.info("\tEncontrados: " + resultados3.size() + " productos\n");
		
		// Búsqueda 4: Combinando varios filtros
		logger.info("\td) Búsqueda combinada (categoría, texto, estado y precio):");
		List<Producto> resultados4 = controlador.buscarProductos(
		    categoriaId,
		    "iPhone",
		    EstadoProducto.COMO_NUEVO,
		    new BigDecimal("1000.00")
		);
		logger.info("\tEncontrados: " + resultados4.size() + " productos");
		for (Producto p : resultados4) {
			logger.info("\t\t• " + p.getTitulo() + 
		                     " - " + p.getPrecio() + "€" +
		                     " - Estado: " + p.getEstado() + 
		                     " - Vendedor : " + p.getVendedor().getEmail());
		}
		
		logger.info("\n=== TODOS LOS CASOS DE USO EJECUTADOS CORRECTAMENTE ===");
    }
}