import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cena {

	List<GameObject> go = new ArrayList<GameObject>();
	
	public void add(GameObject g) {
		this.go.add(g);
	}
	
	public Iterator<GameObject> iterate(){ return go.iterator(); }
	
	public void draw(Graphics2D g2) {
		
		for (GameObject g : go) {
			if (g instanceof Renderable) {
				((Renderable) g).render(g2);
			}
		}
	}
	
	public void clear() {
		go.clear();
	}
	
}
