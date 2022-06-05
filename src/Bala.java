import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class Bala extends ObjetoDoJogo implements Colisao, Renderizavel {

	public static final int RAIO = 2;
	public static final float VELOCIDADE = 10;
	Vetor direcao = new Vetor();
	boolean estaVivo = false;

	public void setEstaVivo(boolean vivo) {
		this.estaVivo = vivo;
	}

	public boolean getEstaVivo() {
		return estaVivo;
	}

	@Override
	public boolean verificaColisao(Colisao outroObjeto) {
		Rectangle areaColisao = outroObjeto.getAreaDeColisao();

		if (areaColisao.intersects(getAreaDeColisao())) {
			outroObjeto.colidiu(this);
			return true;
		}
		return false;
	}

	@Override
	public Rectangle getAreaDeColisao() {
		return new Rectangle(posX, posY, RAIO * 2, RAIO * 2);
	}

	@Override
	public void atualiza() {
		setPosicao(posX + (int) (direcao.x * VELOCIDADE), posY + (int) (direcao.y * VELOCIDADE));
	}

	@Override
	public void renderize(Graphics2D g2) {
		Shape forma = new Ellipse2D.Float(posX, posY, RAIO * 2f, RAIO * 2f);
		g2.setColor(Color.BLUE);
		g2.fill(forma);
	}

	public void setDirection(Vetor vetor) {
		direcao = vetor;
	}

	@Override
	public void colidiu(Colisao outroObjeto) {

	}
}
