import java.util.Iterator;
import java.util.List;

public class ProcessamentoBalas extends Thread {

	Cena cena;
	List<Balas> balas;

	public ProcessamentoBalas(Cena cena, List<Balas> balas) {
		this.balas = balas;
		this.cena = cena;
	}

	@Override
	public void run() {
		for (Balas bala : balas) {
			Iterator<ObjetoDoJogo> objeto = cena.iterate();
			while (objeto.hasNext()) {
				ObjetoDoJogo g = objeto.next();
				if (g instanceof Colisao && bala.verificaColisao((Colisao) g)) {
					bala.setEstaVivo(false);
				}

			}
		}
	}
}
