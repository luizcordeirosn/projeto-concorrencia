
public abstract class ObjetoDoJogo  {

	protected int posX, posY;
	
	public boolean foraDaTela() {
		return posX >= Jogo.LARGURA_TELA || posY >= Jogo.ALTURA_TELA;
	}
	
	public void setPosicao(int x, int y) {
		posX = x;
		posY = y;
	}
	
	public void atualiza() {}
}
