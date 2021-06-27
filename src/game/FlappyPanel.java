package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class FlappyPanel extends JPanel implements ActionListener, KeyListener {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 725;

	private static final int TIMERDELAY = 16;
	private Timer timer = new Timer(TIMERDELAY, this);

	private int fSizeX = 25, fSizeY = 25;
	private int fVelocity = 0;
	private double fAccelerate = 8;

	private static final int LVL1 = 4, LVL2 = 4, LVL3 = 5;
	private int flappyFlap = -2;
	private double gravity = 0.6;
	private int wallVelocity = LVL1;
	public static final int WALLWIDTH = 70, GAPHEIGHT = 160;

	private boolean gameOver, gameRunning, gameNotFirstTimeStartPage = true, spaceBarMsgNotShown;
	private int score, scoreX;
	private JButton strtBtn;

	int fLocX = WIDTH / 10;
	int fHeight = HEIGHT / 4;
	int[] wallX = new int[3];
	int[] gapTop = new int[3];

	private BufferedImage groundImage, pressSpacebar, barrelImage, backgroundImage, flappyBirdImg, gameOverImg;
	private BufferedImage[] flappyImage = new BufferedImage[3], btnImgs = new BufferedImage[2],
			numsImages = new BufferedImage[10];
	private int groundX = 0;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Flappy Bird");
		frame.setContentPane(new FlappyPanel());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public FlappyPanel() {

		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(new Color(213, 252, 255));
		addKeyListener(this);
		setLayout(null);
		timer.setActionCommand("timer");
		gameOver = true;

		try {
			groundImage = ImageIO.read(getClass().getResourceAsStream("/fbGround.png"));
			barrelImage = ImageIO.read(getClass().getResourceAsStream("/barrel.png"));
			backgroundImage = ImageIO.read(getClass().getResourceAsStream("/background.png"));
			pressSpacebar = ImageIO.read(getClass().getResourceAsStream("/pressspacebar.png"));
			BufferedImage tempflappysheet = ImageIO.read(getClass().getResourceAsStream("/flappyflying.png"));
			BufferedImage tempbtnssheet = ImageIO.read(getClass().getResourceAsStream("/btns.png"));
			BufferedImage tempnumssheet = ImageIO.read(getClass().getResourceAsStream("/nums.png"));
			BufferedImage tempMessageSheet = ImageIO.read(getClass().getResourceAsStream("/fbmessage.png"));
			for (int i = 0; i < 10; i++) {
				numsImages[i] = tempnumssheet.getSubimage(0, 12 * i, 9, 12);

				if (i < 3) {
					flappyImage[i] = tempflappysheet.getSubimage(0, 36 * i, 51, 36);
				}
				if (i < 2) {
					btnImgs[i] = tempbtnssheet.getSubimage(0, 48 * i, 126, 48);
				}

			}
			flappyBirdImg = tempMessageSheet.getSubimage(0, 0, 96, 22);
			gameOverImg = tempMessageSheet.getSubimage(0, 22, 96, 21);

		} catch (IOException e) {
			e.printStackTrace();
		}

		strtBtn = new JButton();
		strtBtn.setBounds(WIDTH / 2 - 63, HEIGHT / 2 + 20, 126, 48);
		strtBtn.setFocusable(true);
		strtBtn.requestFocusInWindow();
		strtBtn.setIcon(new ImageIcon(btnImgs[1]));
		strtBtn.addActionListener(this);
		strtBtn.setActionCommand("startBtn");
		add(strtBtn);
		strtBtn.setVisible(true);
		strtBtn.repaint();

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.drawImage(backgroundImage, 0, -400, WIDTH, (int) (backgroundImage.getHeight() * 5.5), null);

		if (!gameRunning && gameOver) {
			drawStartPage(g2);
		} else {
			if (!timer.isRunning() && spaceBarMsgNotShown) {
				g2.drawImage(pressSpacebar, 170, 190, pressSpacebar.getWidth() / 2, pressSpacebar.getHeight() / 2,
						null);
				spaceBarMsgNotShown = false;

			}
			randomGenerator();
			drawWall(g2);
			drawFlappy(g2);
			drawGround(g2);
		}

		drawScoreandPBtn(g2);

		g2.dispose();
	}

	// ----------------------------------------------------------------------------------------------------------------------

	private void drawStartPage(Graphics2D g2) {
		timer.setDelay(400);
		timer.start();
		if (gameNotFirstTimeStartPage) {
			g2.drawImage(flappyBirdImg, WIDTH / 2 - 300, (int) (HEIGHT / 2 - 200 + System.currentTimeMillis() % 4),
					96 * 5, 22 * 5, null);
		} else {
			g2.drawImage(gameOverImg, WIDTH / 2 - 300, (int) (HEIGHT / 2 - 200 + System.currentTimeMillis() % 4),
					96 * 5, 22 * 5, null);
		}
		g2.drawImage(flappyImage[(int) (System.currentTimeMillis() % 3)], WIDTH / 2 + 220,
				(int) (HEIGHT / 2 - 190 + System.currentTimeMillis() % 4), 51 * 2, 36 * 2, null);

		strtBtn.setVisible(true);
		strtBtn.repaint();

	}

	private void drawScoreandPBtn(Graphics2D g2) {

		for (int i = 0; i < (score + "").length(); i++) {
			scoreX = scoreX < (WIDTH / 2 + (9 * 2) * (i - 1)) ? WIDTH / 2 + (9 * 2) * (i - 1) : scoreX;
			g2.drawImage(numsImages[(score / (int) Math.pow(10, i)) % 10], scoreX - 9 * 4 * i, 30, 9 * 4, 12 * 4, null);
		}

		if (gameRunning) { // pause btn
							// ---------------------------------------------------------------------------------------------------------
		}

	}

	private void drawGround(Graphics2D g2) {

		int groundwidth = groundX;
		g2.drawImage(groundImage, groundX, HEIGHT - groundImage.getHeight() / 2, groundImage.getWidth(),
				groundImage.getHeight(), null);

		while (WIDTH - groundwidth > 0) {
			groundwidth += groundImage.getWidth();
			g2.drawImage(groundImage, groundwidth, HEIGHT - groundImage.getHeight() / 2, groundImage.getWidth(),
					groundImage.getHeight(), null);
		}

	}

	private void drawFlappy(Graphics2D g2) {
		g2.setColor(Color.black);
		g2.drawImage(flappyImage[(int) ((System.currentTimeMillis() / 200) % 3)], fLocX - 7, fHeight + fVelocity - 2,
				(int) (17 * 2.3), (int) (12 * 2.3), null);

	}

	private void drawWall(Graphics2D g2) {

		g2.setColor(Color.green);

		for (int i = 0; i < 3; i++) {

			g2.drawImage(barrelImage, wallX[i], gapTop[i] - (int) (barrelImage.getHeight() * 4), WALLWIDTH,
					(int) (barrelImage.getHeight() * 4), null);
			g2.drawImage(barrelImage, wallX[i], gapTop[i] + GAPHEIGHT + (int) (barrelImage.getHeight() * 4), WALLWIDTH,
					-(int) (barrelImage.getHeight() * 4), null);

		}
	}

	private void randomGenerator() {
		for (int i = 0; i < 3; i++) {

			if (wallX[i] <= -WALLWIDTH) {
				wallX[i] = WIDTH + 200;

				gapTop[i] = new Random().nextInt(HEIGHT - groundImage.getHeight() - GAPHEIGHT - 20) + 20;
			}

		}
	}

	private boolean checkCollision() {

		int fx1 = fLocX;
		int fx2 = fLocX + fSizeX;
		int fy1 = fHeight + fVelocity;
		int fy2 = fHeight + fVelocity + fSizeY;

		for (int i = 0; i < 3; i++) {

			int wxLEFT = wallX[i];
			int wxRIGHT = wxLEFT + WALLWIDTH;

			int gapUP = gapTop[i];
			int gapBOTTOM = gapUP + GAPHEIGHT;

			if (fx2 > wxLEFT && fx1 < wxRIGHT) {

				if (fy1 < gapUP || fy2 > gapBOTTOM) {
					return true;
				}
			}
			if (fy1 + 8 < 0 || fy2 - 1 >= HEIGHT - groundImage.getHeight() / 2) {
				return true;
			}

			if (fx1 > wxRIGHT && fx1 <= wxRIGHT + wallVelocity) {
				score++;
			}

		}

		return false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("startBtn")) {

			gameNotFirstTimeStartPage = false;
			spaceBarMsgNotShown = true;
			fVelocity = 0;
			wallX[0] = (int) (WIDTH * 0.8) + 400;
			wallX[1] = (int) (WIDTH * 0.8) + 770;
			wallX[2] = (int) (WIDTH * 0.8) + 1140;

			gameOver = false;
			score = 0;
			scoreX = WIDTH / 2 - 9 * 2;
			flappyFlap = -9;
			timer.setDelay(TIMERDELAY);
			timer.stop();
			wallVelocity = LVL1;
			gameRunning = true;
			requestFocusInWindow();
			strtBtn.setFocusable(false);
			strtBtn.setVisible(false);

			for (int i = 0; i < 3; i++) {

				gapTop[i] = new Random().nextInt(HEIGHT - groundImage.getHeight() - GAPHEIGHT - 20) + 20;
			}

		}

		else if (e.getActionCommand().equals("timer")) {
			if (!gameRunning) {

			} else {

				fAccelerate += gravity;
				fVelocity += fAccelerate;

				groundX -= wallVelocity;

				for (int i = 0; i < 3; i++) {
					wallX[i] -= wallVelocity;
				}

				gameOver = checkCollision();
				if (gameOver == true) {
					timer.stop();
					gameRunning = false;
				}

				if (gameRunning) {
					switch (score) {
					case 40:
						wallVelocity = LVL2;
						break;
					case 80:
						wallVelocity = LVL3;
						flappyFlap = -10;
						break;

					}
				}
			}
		}

		repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (gameRunning && !timer.isRunning()) {
				timer.start();
				gameRunning = true;
			}

			fAccelerate = flappyFlap;

		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
