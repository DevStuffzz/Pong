package pongers;

import java.util.Random;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.jengine.engine.ecs.Collision;
import com.jengine.engine.ecs.Entity;
import com.jengine.engine.io.Canvas;
import com.jengine.engine.io.Display;
import com.jengine.engine.io.Input;
import com.jengine.engine.io.Text;
import com.jengine.engine.math.Vector2;

public class Main implements Runnable {
	Display display;
	Canvas canvas;
	Input io;
	Entity paddle1, paddle2, ball;
	Collision paddle1c, paddle2c, ballc;
	Collision top, bottom, right, left;

	Text score;
	int playerOneScore = 0, playerTwoScore = 0;

	boolean aion = false;

	Image white, red, circle;

	String balldir;

	boolean running;

	public static void main(String[] args) throws InterruptedException {
		Thread thread = new Thread(new Main());
		thread.run();
		thread.join();
	}

	@Override
	public void run() {
		Random r = new Random();
		// TODO Auto-generated method stub

		String players = JOptionPane.showInputDialog("Enter ammount of players: 1 or 2:");

		score = new Text("Player 1: " + playerOneScore + " \t\t|\t\t Player 2: " + playerTwoScore, new Vector2(50, 50),
				25);

		score.color = Color.RED;

		if (players.equalsIgnoreCase("1"))
			aion = true;

		display = new Display(600, 600, "Pongers");
		canvas = new Canvas();
		canvas.setBounds(400, 400, 50, 50);
		canvas.setBackground(Color.black);
		display.add(canvas);
		display.setResizable(false);
		io = new Input();
		display.addKeyListener(io);
		display.pack();

		white = new ImageIcon("images/block.png").getImage();
		circle = new ImageIcon("images/circle.png").getImage();
		red = new ImageIcon("images/red.png").getImage();

		paddle1 = new Entity(new Vector2(20, 300), 25, 100, white);
		paddle2 = new Entity(new Vector2(550, 300), 25, 100, white);
		ball = new Entity(new Vector2(300, 300), 25, 25, circle);
		balldir = "ur";

		paddle1c = new Collision(paddle1, io);
		paddle2c = new Collision(paddle2, io);
		ballc = new Collision(ball, io);

		paddle1.add(paddle2c);
		paddle2.add(paddle2c);
		ball.add(ballc);

		top = new Collision(new Entity(new Vector2(0, 2), 100000, 10, white), io);
		bottom = new Collision(new Entity(new Vector2(0, 550), 100000, 10, white), io);

		// Left and right collisions
		right = new Collision(new Entity(new Vector2(2, 2), 10, 1000000, white), io);
		left = new Collision(new Entity(new Vector2(575, 2), 10, 1000000, white), io);

		canvas.addText(score);

		canvas.addEntity(new Entity(new Vector2(2, 2), 10, 1000000, red));
		canvas.addEntity(new Entity(new Vector2(575, 2), 10, 1000000, red));

		canvas.addEntity(new Entity(new Vector2(0, 550), 100000, 10, white));
		canvas.addEntity(new Entity(new Vector2(0, 2), 100000, 10, white));
		canvas.addEntity(paddle1);
		canvas.addEntity(paddle2);
		canvas.addEntity(ball);

		running = true;
		int dir = r.nextInt(4 - 1) + 1;
		int random = r.nextInt(5 - 1) + 1;
		switch (dir) {
		case 1:
			balldir = "ur";
			break;
		case 2:
			balldir = "ul";
			break;
		case 3:
			balldir = "dr";
			break;
		case 4:
			balldir = "dl";
			break;
		}

		while (running) {

			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			io.poll();

			// Move the ball

			switch (balldir) {
			case "ul":
				ball.location = new Vector2(ball.location.x - 5, ball.location.y - random);
				break;
			case "ur":
				ball.location = new Vector2(ball.location.x + 5, ball.location.y - random);
				break;
			case "dl":
				ball.location = new Vector2(ball.location.x - 5, ball.location.y + random);
				break;
			case "dr":
				ball.location = new Vector2(ball.location.x + 5, ball.location.y + random);
				break;
			}

			// Collisions
			if (ballc.checkCollision(paddle1c)) {
				random = r.nextInt(10 - 1) + 1;
				int num = r.nextInt(3 - 1) + 1;
				System.out.println(num);
				if (num == 1)
					balldir = "ur";
				else
					balldir = "dr";
			} else if (ballc.checkCollision(paddle2c)) {
				random = r.nextInt(5 - 1) + 1;
				int num = r.nextInt(3 - 1) + 1;
				if (num == 1)
					balldir = "ul";
				else
					balldir = "dl";
			}

			// EOS Detection
			if (ballc.checkCollision(top)) {
				random = r.nextInt(5 - 1) + 1;
				System.out.println("Hit Top");
				if (balldir.equals("ur")) {
					balldir = ("dr");
				} else {
					balldir = ("dl");
				}
			}
			if (ballc.checkCollision(bottom)) {
				random = r.nextInt(5 - 1) + 1;
				if (balldir.equals("dr")) {
					balldir = ("ur");
				} else {
					balldir = ("ul");
				}
			}

			if (ballc.checkCollision(left)) {
				System.out.println("Player 1 Scored");
				ball.location = new Vector2(300, 300);
				playerOneScore++;
			}
			if (ballc.checkCollision(right)) {
				System.out.println("Player 2 Scored");
				ball.location = new Vector2(300, 300);
				playerTwoScore++;
			}

			// Input for paddle1
			if (paddle1.location.y >= 20 && paddle1.location.y <= 450) {
				if (io.keyDown(KeyEvent.VK_S)) {
					paddle1.location = new Vector2(paddle1.location.x, paddle1.location.y + 10);
				}
				if (io.keyDown(KeyEvent.VK_W)) {
					paddle1.location = new Vector2(paddle1.location.x, paddle1.location.y - 10);
				}
			} else if (paddle1.location.y <= 20) {
				if (io.keyDown(KeyEvent.VK_S)) {
					paddle1.location = new Vector2(paddle1.location.x, paddle1.location.y + 10);
				}
			} else if (paddle1.location.y >= 450) {
				if (io.keyDown(KeyEvent.VK_W)) {
					paddle1.location = new Vector2(paddle1.location.x, paddle1.location.y - 10);
				}
			}
			// "AI"
			if (aion)
				paddle2.location = new Vector2(paddle2.location.x, ball.location.y - 50 - r.nextInt(9) + 1);
			else {
				if (paddle2.location.y >= 20 && paddle2.location.y <= 450) {
					if (io.keyDown(KeyEvent.VK_DOWN)) {
						paddle2.location = new Vector2(paddle2.location.x, paddle2.location.y + 10);
					}
					if (io.keyDown(KeyEvent.VK_UP)) {
						paddle2.location = new Vector2(paddle2.location.x, paddle2.location.y - 10);
					}
				} else if (paddle2.location.y <= 20) {
					if (io.keyDown(KeyEvent.VK_DOWN)) {
						paddle2.location = new Vector2(paddle2.location.x, paddle2.location.y + 10);
					}
				} else if (paddle2.location.y >= 450) {
					if (io.keyDown(KeyEvent.VK_UP)) {
						paddle2.location = new Vector2(paddle2.location.x, paddle2.location.y - 10);
					}
				}
			}
			score.setText("Player 1: " + playerOneScore + " \t\t|\t\t Player 2: " + playerTwoScore);

			canvas.render();

		}
	}
}
