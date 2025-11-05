package stoneyspring.SegundUM.repositorio;

import java.io.InputStream;
import java.util.Properties;

/**
 * Factoría que encapsula la implementación de los repositorios.
 * Utiliza un fichero de propiedades para cargar la implementación.
 */
public class FactoriaRepositorios {
    
    private static final String PROPERTIES_FILE = "repositorios.properties";
    
    @SuppressWarnings("unchecked")
    public static <T, K, R extends Repositorio<T, K>> R getRepositorio(Class<?> entidad) {
        try {
            Properties properties = new Properties();
            
            // Cargar fichero de propiedades
            InputStream is = FactoriaRepositorios.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE);
            
            if (is == null) {
                throw new RuntimeException("No se encuentra el fichero " + PROPERTIES_FILE);
            }
            
            properties.load(is);
            
            // Obtener la clase de implementación
            String className = properties.getProperty(entidad.getName());
            
            if (className == null) {
                throw new RuntimeException("No hay implementación configurada para " + entidad.getName());
            }
            
            // Instanciar la clase
            return (R) Class.forName(className).getConstructor().newInstance();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener repositorio para " + entidad.getName(), e);
        }
    }
}