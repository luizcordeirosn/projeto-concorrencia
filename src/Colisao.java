import java.awt.Rectangle;

public interface Colisao {
	
	boolean checkCollision(Colisao other);
	Rectangle getCollisionRect();
	void onCollision(Colisao other);
}
