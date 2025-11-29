package t005.key.input.test;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class Main extends BasicGame {

	private float rectX = 200; // X 위치
	private float rectY = -50; // Y 위치 (초기값: 화면 위)
	private float speed = 100; // 초당 이동 속도 (100픽셀)
	private String message = "Press a key to see the input.";
	public Main(String title) {
		super(title);
	}

	@Override
    public void keyPressed(int key, char c) {
        // 'key' is the key code (e.g., Input.KEY_ENTER)
        // 'c' is the character pressed (e.g., 'a', '1', etc.)

        // Get the human-readable name of the key
        String keyName = Input.getKeyName(key);
        
        this.message = "Key Pressed! Code: " + key + " | Name: " + keyName + " | Char: '" + c + "'";

        // Optional: Exit the game when a specific key is pressed (e.g., ESC)
        if (key == Input.KEY_ESCAPE) {
            System.out.println("Exiting game.");
            System.exit(0);
        }
    }

    // You can also override keyReleased if needed
    @Override
    public void keyReleased(int key, char c) {
        // You can add logic here for when a key is released
        // message = "Key Released! " + Input.getKeyName(key); 
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
		g.drawString(message, 100, 100);
	}

	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new Main("Rectangle Falling"));
			app.setDisplayMode(800, 600, false);
			app.setTargetFrameRate(60);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
