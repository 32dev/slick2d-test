package t013.key.input.note.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class Main extends BasicGame {

//	private String message = "Press a key to see the input.";
	private Map<String, Integer> keyMap = new HashMap<String, Integer>();

	public Main(String title) {
		super(title);
		keyMap.put("S", -1);
		keyMap.put("D", -1);
		keyMap.put("F", -1);
		keyMap.put("J", -1);
		keyMap.put("K", -1);
		keyMap.put("L", -1);
	}

	@Override
	public void keyPressed(int key, char c) {
		// 'key' is the key code (e.g., Input.KEY_ENTER)
		// 'c' is the character pressed (e.g., 'a', '1', etc.)

		// Get the human-readable name of the key
		String keyName = Input.getKeyName(key);

//		this.message = "Key Pressed! Code: " + key + " | Name: " + keyName + " | Char: '" + c + "'";
		int t = keyMap.get(keyName);
		if (t < 0) {
			switch (keyName) {
			case "S":
				keyMap.put(keyName, 0);
				break;
			case "D":
				keyMap.put(keyName, 1);
				break;
			case "F":
				keyMap.put(keyName, 2);
				break;
			case "J":
				keyMap.put(keyName, 3);
				break;
			case "K":
				keyMap.put(keyName, 4);
				break;
			case "L":
				keyMap.put(keyName, 5);
				break;
			}
		}

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
//		this.message = "Key Released! " + Input.getKeyName(key);
		String keyName = Input.getKeyName(key);
		if (keyMap.get(keyName) >= 0) {
			keyMap.put(keyName, -1);
		}
//    	
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		// 초기화 코드 (필요시)
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {

	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
//		g.drawString(message, 100, 100);
		Set<String> keySet = keyMap.keySet();
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()) {
			String key = it.next();
//			System.out.println(key);
			int t = keyMap.get(key);
			if (t >= 0) {
				g.setColor(Color.white);
			} else {
				g.setColor(Color.black);

			}
			g.fillRect(t * 60, 880, 60, 20);
		}
	}

	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new Main("Test"));
			app.setDisplayMode(1600, 900, false);
			app.setTargetFrameRate(60);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
