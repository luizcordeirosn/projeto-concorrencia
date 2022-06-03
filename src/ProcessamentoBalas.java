import java.util.Iterator;
import java.util.List;

public class ProcessamentoBalas extends Thread {

	Cena cena;
	List<Bala> balas;

	public ProcessamentoBalas(Cena cena, List<Bala> balas) {
		this.balas = balas;
		this.cena = cena;
	}

	@Override
	public void run() {
		for (Bala bala : balas) {
			Iterator<ObjetoDoJogo> objetos = cena.iterate();
			while (objetos.hasNext()) {
				ObjetoDoJogo objeto = objetos.next();
				if (objeto instanceof Colisao && bala.verificaColisao((Colisao) objeto)) {
					bala.setEstaVivo(false);
				}

			}
		}
	}
}
