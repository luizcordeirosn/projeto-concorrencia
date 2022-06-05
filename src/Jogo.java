import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;

public class Jogo extends JFrame implements Runnable {

	public static final int ALTURA_TELA = 800;
	public static final int LARGURA_TELA = 800;
	private static final long serialVersionUID = 4879015074359108598L;
	private static final int INTERVALO = 100;
	private static final int NUMERO_DE_BALAS = 3600;
	private static final int NUMERO_DE_OBJETOS = 1800;
	private static final int NUMERO_DE_THREADS = 3;
	private static final int INTERVALO_POR_DISPARO = 10;
	private static final int TAMANHO_RETANGULO = 15;

	Thread[] processingBulletCollision;

	private volatile boolean executando;

	Thread gameLoop = null;
	Cena cena = new Cena();
	volatile List<Bala> balas = new ArrayList<>();
	int intervaloParaDisparoAtual = 0;

	Semaphore semafaro = new Semaphore(1);

	BufferStrategy bs;

	public Jogo() {
		configurarJanela();
		// Thread usada para computar colisão contra os elementos na tela
		// Utilizando apenas uma thread para simular um codigo sequencial
		processingBulletCollision = new Thread[NUMERO_DE_THREADS];
	}

	private void criarCena() {
		// Limpa a tela
		cena.clear();
		balas.clear();

		// Adiciona os retangulos na tela
		for (int i = 0; i < NUMERO_DE_OBJETOS; i++) {
			int posX = (int) (Math.random() * (double) getWidth() - TAMANHO_RETANGULO);
			int posY = (int) (Math.random() * (double) getHeight() - TAMANHO_RETANGULO);
			Retangulo retangulo = new Retangulo(TAMANHO_RETANGULO, TAMANHO_RETANGULO);
			retangulo.setPosicao(posX, posY);
			cena.add(retangulo);
		}
	}

	public void startGame() {
		setPreferredSize(new Dimension(LARGURA_TELA, ALTURA_TELA));
		pack();
		criarCena();
		executando = true;
		gameLoop = new Thread(this);
		gameLoop.start();
	}

	public void pararJogo() {
		executando = false;
		try {
			gameLoop.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		setVisible(false);
		dispose();
	}

	@Override
	public void run() {

		while (executando) {
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
				Thread.sleep(INTERVALO);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	private void draw(Graphics2D g2) {
		cena.desenhe(g2);

		try {
			semafaro.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (Bala bala : balas) {
			bala.renderize(g2);
		}

		semafaro.release();
	}

	private void adicionarBalas() {
		try {
			semafaro.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		int grau = NUMERO_DE_BALAS / 360;
		//Para as balas ficarem em um circulo ao serem adicionadas
		for (int i = 0; i < grau; i++) {
			for (int j = 0; j < 360; j++) {

				double dX = Math.cos((double) j);
				double dY = +Math.sin((double) j);

				Bala bala = new Bala();
				bala.setPosicao((getWidth() / 2) + (int) (dX * (15.0 * (i + 1))),
						(getHeight() / 2) + (int) (dY * (15.0 * (i + 1))));
				bala.setDirection(new Vetor((float) dX, (float) dY));

				balas.add(bala);
			}
		}

		semafaro.release();
	}

	private void update() {
		balas.removeIf(Bala::foraDaTela);
		try {
			// Espera alguma thread ficar livre ou ser interrompida
			semafaro.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		balas.stream().forEach(Bala::atualiza);
		// Iniciando a contagem do tempo...
		long start = System.currentTimeMillis();

		// Ativar quando for testar o paralelismo (Mudar NUM_THREADS para 2)
		int balasPorThread = balas.size() / NUMERO_DE_THREADS;

		// List<Future> processamentos = new ArrayList<>();
		for (int i = 0; i < NUMERO_DE_THREADS; i++) {
			int offsetInic = balasPorThread * i;
			List<Bala> balasParaProcessarPorThread = balas.subList(offsetInic, offsetInic + balasPorThread);
			processingBulletCollision[i] = new ProcessamentoBalas(cena, balasParaProcessarPorThread);
			processingBulletCollision[i].start();
		}

		for (int i = 0; i < NUMERO_DE_THREADS; i++) {
			try {
				// Espera finalizar a Thread
				processingBulletCollision[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Finalizando a contagem
		long end = System.currentTimeMillis();

		// Tempo de execução
		long time = end - start;

		//Mostra a quantidade de balas que tem na tela e o tempo que demorou 
		//para calcular todas elas naquele milionesimo de segundo para cada objeto na tela
		System.out.println(balas.size() + ";" + time + "\n");

		semafaro.release();

		// Controla a frequencia que o jogador pode disparar
		if (intervaloParaDisparoAtual > 0) {
			intervaloParaDisparoAtual -= 10;
		}
	}

	private void configurarJanela() {
		setTitle("Projeto Concorrencia");
		setLayout(new BorderLayout());
		//Serve para recuperar os eventos da tela
		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {

			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				pararJogo();
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
			}
		});

		setVisible(true);
		createBufferStrategy(2);
		bs = getBufferStrategy();

		//Serve para recuperar os eventos do teclado
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {

			}

			@Override
			public void keyPressed(KeyEvent evt) {
				// Dispara as balas se apertar espaço
				if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
					if (intervaloParaDisparoAtual <= 0) {
						adicionarBalas();
						intervaloParaDisparoAtual = INTERVALO_POR_DISPARO;
					}
				}
				// Fecha o jogo ao apertar ESC
				if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
					pararJogo();
				}

			}
		});
	}
}
