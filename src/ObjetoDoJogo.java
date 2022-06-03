
public abstract class ObjetoDoJogo  {

	protected int posX, posY;
	
	public boolean foraDaTela() {
		return false;
	}
	
	public void setPosicao(int x, int y) {
		posX = x;
		posY = y;
	}
	
	public void atualiza() {}
}
