import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cena {

	List<ObjetoDoJogo> objeto = new ArrayList<>();
	
	public void add(ObjetoDoJogo outroObjeto) {
		this.objeto.add(outroObjeto);
	}
	
	public Iterator<ObjetoDoJogo> iterate(){ return objeto.iterator();}
	
	public void desenhe(Graphics2D g2) {
		objeto.stream().
		filter(objeto -> objeto instanceof Renderizavel).
		forEach(objeto -> ((Renderizavel) objeto).renderize(g2));
	}
	
	public void clear() {
		objeto.clear();
	}
	
}
