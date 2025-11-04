package t002.helloworld;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
 
public class Main extends BasicGame{
 
    public Main(String title) {
        super(title);
    }
 
    @Override
    public void init(GameContainer container) throws SlickException {
        // 초기화 (필요 시)
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        // 매 프레임마다 업데이트할 내용 (예: 게임 로직)
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {
        g.drawString("Hello, Slick2D World!", 100, 100);
    }

    public static void main(String[] args) {
        try {
            AppGameContainer appgc = new AppGameContainer(new Main("Hello, Slick2d"));
            appgc.setDisplayMode(800, 600, false); // 창 크기 설정
            appgc.start(); // 게임 시작
        } catch (SlickException ex) {
            ex.printStackTrace();
        }
    }
 
}