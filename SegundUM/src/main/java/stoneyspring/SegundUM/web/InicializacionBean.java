package stoneyspring.SegundUM.web;

import java.io.File;
import java.io.FilenameFilter;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import stoneyspring.SegundUM.controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean(eager = true)
@ApplicationScoped
public class InicializacionBean {

    private static final Logger logger = LoggerFactory.getLogger(InicializacionBean.class);
    
    // Ruta relativa a la raíz del proyecto
    private static final String CARPETA_CATEGORIAS = "categoriasXML";

    @PostConstruct
    public void init() {
        
        Controller controller = new Controller();
        File directorio = new File(CARPETA_CATEGORIAS);
        
        if (!directorio.exists()) {
             logger.error("ERROR: No existe la carpeta: " + directorio.getAbsolutePath());
             return;
        }

        File[] archivosXML = directorio.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".xml");
            }
        });

        if (archivosXML != null) {
            int cargados = 0;
            int fallidos = 0;
            
            for (File archivo : archivosXML) {
                boolean exito = controller.cargarCategorias(archivo.getName()); 
                
                if (exito) {
                    cargados++;
                } else {
                    fallidos++;
                    logger.warn("FALLO al cargar: " + archivo.getName());
                }
            }
            logger.info("=== FIN CARGA: " + cargados + " cargados, " + fallidos + " fallidos ===");
        }
    }
}