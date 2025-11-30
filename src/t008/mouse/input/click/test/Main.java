package t008.mouse.input.click.test;

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

	public void render(Graphics g) {
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
		square.render(g);
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		Input input = gc.getInput();
		int xpos = input.getMouseX();
		int ypos = input.getMouseY();
		if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
	        // --- Click Logic Goes Here ---
	        System.out.println("Left Mouse Clicked at: (" + xpos + ", " + ypos + ")");
	        
	        // Example: Move the square to the click position
	        square.x = xpos - 50; 
	        square.y = ypos - 50;
	        
	        // *Important Note:* The isMousePressed() method returns true 
	        // as long as the button is held down. If you want a single event 
	        // per click, you might need to track the button state yourself or 
	        // use the MouseListener methods below.
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
