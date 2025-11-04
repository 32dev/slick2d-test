package t003.rectangle;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Main extends BasicGame {

	public Main() {
		super("Slick2D - Rectangle Example");
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		// 초기화 코드
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		// 프레임 업데이트 처리 (생략 가능)
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		// 테두리 있는 사각형
		g.drawRect(100, 100, 200, 150); // x, y, width, height

		// 채워진 사각형
		g.fillRect(350, 100, 200, 150); // x, y, width, height
	}

	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new Main());
			app.setDisplayMode(800, 600, false);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}