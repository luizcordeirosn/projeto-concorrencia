import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProcessamentoBalas extends Thread {
	
	Cena scene;
	List<Balas> bullets;
	
	public ProcessamentoBalas(Cena scene, List<Balas> b) {
		this.bullets = b;
		this.scene = scene;
	}
	
	
	@Override
	public void run() {
		
		for (Balas b : bullets) {
			
			Iterator<GameObject> go = scene.iterate();
			while (go.hasNext()) {
				
				GameObject g = go.next();
				if (g instanceof Colisao) {
					if (b.checkCollision((Colisao) g)) 
						b.setAlive(false);
				}
				
			}
		}
	}
}
