package stoneyspring.SegundUM.repositorio.categorias;


import stoneyspring.SegundUM.dominio.Categoria;
import stoneyspring.SegundUM.repositorio.RepositorioXML;

public class RepositorioCategoriasXML extends RepositorioXML<Categoria> {

	@Override
	public Class<Categoria> getClase() {
		return Categoria.class;
	}
	
}
