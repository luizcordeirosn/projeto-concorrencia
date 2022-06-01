
public abstract class GameObject  {

	protected int posX, posY;
	
	public boolean isOutScreen() {
		return false;
	}
	
	public void setPos(int x, int y) {
		posX = x;
		posY = y;
	}
	
	public void update() {}
}
