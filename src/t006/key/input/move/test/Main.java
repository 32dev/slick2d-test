package t006.key.input.move.test;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

class Square {
	int x;
	int y;

	public Square(int x, int y) {
		this.x = x;
		this.y = y;

	}

	public void move(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(this.x, this.y, 100, 100);
	}

	@Override
	public String toString() {

		return "this.x:" + this.x + ",this.y:" + this.y;
	}
}

public class Main extends BasicGame {
	private Square square;
	private long t = 0;

	public Main(String title) {
		super(title);

	}

	@Override
	public void init(GameContainer c) throws SlickException {
		t = System.currentTimeMillis();
		square = new Square(0, 0);

	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		square.move(g);
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		Input input = gc.getInput();
		float speed = 1f;
		// 오른쪽 이동
		if (input.isKeyDown(Input.KEY_RIGHT)) {
			square.x += speed * delta;
		}
		// 왼쪽 이동
		if (input.isKeyDown(Input.KEY_LEFT)) {
			square.x -= speed * delta;
		}
		// 위
		if (input.isKeyDown(Input.KEY_UP)) {
			square.y -= speed * delta;
		}
		// 아래
		if (input.isKeyDown(Input.KEY_DOWN)) {
			square.y += speed * delta;
		}
	}

	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new Main("Rectangle Falling"));
			app.setDisplayMode(1000, 1000, false);
			app.setTargetFrameRate(60);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
