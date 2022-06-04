import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Retangulo extends ObjetoDoJogo implements Colisao, Renderizavel {

	Shape forma;
	Color cor;
	int largura, altura;
	public Retangulo(int largura, int altura) {
		this.largura = largura;
		this.altura = altura;
		
		forma = new Rectangle2D.Float(posX, posY, largura, altura);
	}
	
	@Override
	public boolean verificaColisao(Colisao outroObjeto) {
		return forma.intersects(outroObjeto.getAreaDeColisao());
	}

	@Override
	public Rectangle getAreaDeColisao() {
		return forma.getBounds();
	}

	@Override
	public void renderize(Graphics2D g2) {
		g2.setColor(cor);
		g2.fill(forma);
		cor = Color.RED;
	}
	
	@Override
	public void setPosicao(int x, int y) {
		super.setPosicao(x, y);
		forma = new Rectangle2D.Float(posX, posY, largura, altura);
	}

	@Override
	public void colidiu(Colisao other) {
		cor = Color.GRAY;
	}
	
}
