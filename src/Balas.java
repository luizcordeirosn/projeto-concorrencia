import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D.Float;

public class Balas extends GameObject implements Colisao, Renderable {

	public static final int RADIUS = 2;
	public static final float VELOCITY = 10;
	Vector direction = new Vector();
	boolean isAlive = false;
	
	public void setAlive(boolean alive) { this.isAlive = alive; }
	public boolean getAlive() { return isAlive; }
	
	@Override
	public boolean checkCollision(Colisao other) {
		Rectangle or = other.getCollisionRect();
		
		if (or.intersects(getCollisionRect())){
			other.onCollision(this);
			return true;
		}
		return false;
	}
	
	@Override
	public Rectangle getCollisionRect() {
		return new Rectangle(posX, posY, RADIUS * 2, RADIUS * 2);
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		setPos(posX + (int) (direction.x * VELOCITY), 
			posY + (int) (direction.y * VELOCITY));
	}
	@Override
	public void render(Graphics2D g2) {
		Shape s = new Ellipse2D.Float(posX, posY, RADIUS * 2, RADIUS * 2);
		g2.setColor(Color.RED);
		g2.fill(s);
	}
	
	public void setDirection(Vector v) {
		direction = v;
	}
	
	@Override
	public void onCollision(Colisao other) {
		
	}
}
