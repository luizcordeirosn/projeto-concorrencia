import java.awt.Rectangle;

public interface Colisao {
	
	boolean verificaColisao(Colisao outroObjeto);
	Rectangle getAreaDeColisao();
	void colidiu(Colisao outroObjeto);
}
