import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferStrategy;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;

public class Jogo extends JFrame implements Runnable {
	
	// TODO: implements the frame interval
	private static final int INTERVAL = 100;
	private static final int NUM_BULLETS = 1800;
	private static final int NUM_OBJECTS = 500;
	private static final int NUM_THREADS = 1;
	private static final int INTERVAL_FOR_SHOOTING = 10;
	private static final int QUAD_SIZE = 15;
	
	Thread [] processingBulletCollision;
	FileWriter file;
	
	private volatile boolean isRunning;
	Thread gameLoop = null;
	Cena scene = new Cena();
	List<Balas> bullets = new ArrayList<Balas>();
	int currentIntervalForShooting = 0;
	
	Semaphore sem = new Semaphore(1);
	
	BufferStrategy bs;
	
	public Jogo() {
		configureWindow();
			
		// Thread used for computing collision against elements in the scene
		//Utilizando apenas uma thread para simular um codigo sequencial
		processingBulletCollision = new Thread[NUM_THREADS];
	}
	
	private void createScene() {
		
		scene.clear();
		bullets.clear();
		
		for (int i = 0; i < NUM_OBJECTS; i++) {
			
			int posX = (int)(Math.random() * (double)getWidth() - QUAD_SIZE);
			int posY = (int)(Math.random() * (double)getHeight() - QUAD_SIZE);
			
			Quad q = new Quad(QUAD_SIZE, QUAD_SIZE);
			q.setPos(posX, posY);
			scene.add(q);
		}
		
	}
	
	/* 
	 * This method starts the game
	 */
	public void startGame() {
		setPreferredSize(new Dimension(800,800));
		pack();
		
		// Create the scene
		createScene();
		
		isRunning = true;
		gameLoop = new Thread(this);
		gameLoop.start();
	}
	
	/* 
	 * This method stops the game and ends the program
	 */
	public void stopGame() {
		isRunning = false;
		try {
			gameLoop.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setVisible(false);
		dispose();
		
		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		while (isRunning) {
			
			update();
			
			Graphics2D g2 = null;
			try {
				g2 = (Graphics2D) bs.getDrawGraphics();
				g2.setColor(Color.WHITE);
				g2.fillRect(0, 0, getWidth(), getHeight());
				draw(g2);
			} finally {
				g2.dispose();
			}
			bs.show();
			
			
			try {
				Thread.sleep(INTERVAL);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private void draw(Graphics2D g2) {	
		scene.draw(g2);
		
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Balas b : bullets) {
			b.render(g2);
		}
		
		sem.release();
	}
	
	/* 
	 * This method is responsible for adding a ring of bullets
	 */
	private void addBullets() {
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int steps = NUM_BULLETS / 360;
		
		for (int i = 0; i < steps; i++) {
			
			for (int j = 0; j < 360; j++) {
				
				double dX = Math.cos((double) j);
				double dY = + Math.sin((double) j);
				
				Balas b = new Balas();
				b.setPos((getWidth() / 2) + (int)(dX * (15.0 * (i + 1))), 
						(getHeight() / 2) + (int)(dY * (15.0 * (i + 1))));
				b.setDirection(new Vector((float)dX, (float)dY));
				
				bullets.add(b);
			}
		}
		
		sem.release();
	}
	
	private void update() {
		try {
			sem.acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (Balas b : bullets) {
			b.update();
		}

		//Iniciando a contagem do tempo...
		long start = System.currentTimeMillis(); 

		//Ativar quando for testar o paralelismo (Mudar NUM_THREADS para 2)
		//int numElemensThread = bullets.size() / NUM_THREADS;
		
		for (int i = 0; i < NUM_THREADS; i++) {

			//Ativar quando for testar o sequencial
			processingBulletCollision[i] = 
					new ProcessamentoBalas(scene, 
							bullets.subList(0, bullets.size()));

			processingBulletCollision[i].start();


			//Ativar quando for testar o paralelismo
			
			/*int offsetInic = numElemensThread * i;
			processingBulletCollision[i] = 
					new ProcessamentoBalas(scene, 
							bullets.subList(offsetInic, offsetInic + numElemensThread));
			processingBulletCollision[i].start();*/
		}
		
		for (int i = 0; i < NUM_THREADS; i++) {
			try {
				processingBulletCollision[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Finalizando a contagem 
		long end = System.currentTimeMillis();

		//Tempo de execução
		long time = end - start;
		
		try {
			file.write(bullets.size() + ";" + time + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sem.release();
		
		
		// Control the frequency the player can shoot
		if (currentIntervalForShooting > 0) {
			currentIntervalForShooting -= 10;
		}
	}
	
	private void configureWindow() {
		setTitle("Game");
		setLayout(new BorderLayout());
		
		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				stopGame();
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});	
		
		
		setVisible(true);
		createBufferStrategy(2);
		bs = getBufferStrategy();
		
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
					if (currentIntervalForShooting <= 0) {
						addBullets();
						currentIntervalForShooting = INTERVAL_FOR_SHOOTING;
					}
				}
				
			}
		});
		
		
		// File for saving time results
		try {
			file = new FileWriter("Tempo.txt");
			file.write("Bullets;Time;\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
