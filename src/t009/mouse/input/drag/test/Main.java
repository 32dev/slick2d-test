package t009.mouse.input.drag.test;

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
	int width = 100;
	int height = 100;

	public Square(int x, int y) {
		this.x = x;
		this.y = y;

	}

	public void render(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(this.x, this.y, this.width, this.height);
	}

	@Override
	public String toString() {

		return "this.x:" + this.x + ",this.y:" + this.y + ",this.width:" + this.width + ",this.height:" + this.height;
	}
}

public class Main extends BasicGame {
	private Square square;
	private boolean isDragging = false;

	public Main(String title) {
		super(title);

	}

	@Override
	public void init(GameContainer c) throws SlickException {
		square = new Square(0, 0);

	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		square.render(g);
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		Input input = gc.getInput();
		int mouseX = input.getMouseX();
		int mouseY = input.getMouseY();

		// 1. Check if the mouse is pressed
		if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			// --- START DRAG ---

			// Check if the click is *inside* the square's bounds
			if (mouseX >= square.x && mouseX <= square.x + square.width && mouseY >= square.y
					&& mouseY <= square.y + square.height) {

				isDragging = true;

				// Optional: Calculate offset to click from the square's corner
				// (This prevents the square from snapping its top-left corner
				// directly to the mouse cursor on the initial click).
				// For simplicity, we'll skip the offset here and just set isDragging.
			}
		}

		// 2. Handle the Dragging Motion
		if (isDragging) {
			// --- DRAG MOVE ---

			// Update the square's position to follow the mouse
			// Assuming square.width and square.height are 100 for centering
			square.x = mouseX - 50;
			square.y = mouseY - 50;
		}

		// 3. Check if the mouse button is released
		if (!input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			// --- END DRAG ---

			// Stop dragging when the button is released, regardless of where the mouse is
			isDragging = false;
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
