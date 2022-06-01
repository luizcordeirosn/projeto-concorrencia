import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D.Float;

public class Quad extends GameObject implements Colisao, Renderable {

	Shape s;
	Color color;
	int width, height;
	public Quad(int w, int h) {
		width = w;
		height = h;
		
		s = new Rectangle2D.Float(posX, posY, w, h);
	}
	
	@Override
	public boolean checkCollision(Colisao other) {
		return s.intersects(other.getCollisionRect());
	}

	@Override
	public Rectangle getCollisionRect() {
		return s.getBounds();
	}

	@Override
	public void render(Graphics2D g2) {
		g2.setColor(color);
		g2.fill(s);
		color = Color.GREEN;
	}
	
	@Override
	public void setPos(int x, int y) {
		super.setPos(x, y);
		s = new Rectangle2D.Float(posX, posY, width, height);
	}

	@Override
	public void onCollision(Colisao other) {
		color = Color.GRAY;
	}
	
}
