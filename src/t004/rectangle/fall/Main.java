package t004.rectangle.fall;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Main extends BasicGame {

	private float rectX = 200; // X 위치
	private float rectY = -50; // Y 위치 (초기값: 화면 위)
	private float speed = 100; // 초당 이동 속도 (100픽셀)

	public Main(String title) {
		super(title);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		// 초기화 코드 (필요시)
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		// delta는 지난 프레임과의 시간 차 (ms)
		rectY += speed * delta / 1000.0f;

		// 바닥 도달시 리셋
		if (rectY > gc.getHeight()) {
			rectY = -50; // 다시 위로
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		g.setColor(Color.white);
		g.fillRect(rectX, rectY, 50, 50);
	}

	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new Main("Rectangle Falling"));
			app.setDisplayMode(800, 600, false);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
