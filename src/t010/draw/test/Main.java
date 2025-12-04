package t010.draw.test;

import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

/**
 * 간단한 Slick2D 펜 드로잉 애플리케이션입니다.
 * 마우스 왼쪽 버튼을 누른 채 움직여 자유롭게 그림을 그릴 수 있습니다.
 */
public class Main extends BasicGame {
	
	/**
	 * 내부 클래스로, 하나의 좌표를 나타냅니다.
	 */
	private static class Point {
	    int x;
	    int y;
	    public Point(int x, int y) {
	        this.x = x;
	        this.y = y;
	    }
	}

    // 모든 완성된 선(스트로크)을 저장하는 리스트. 각 스트로크는 점들의 리스트입니다.
    private final List<List<Point>> strokes = new ArrayList<>();
    // 현재 그리고 있는 스트로크 (마우스를 누르고 있는 동안)
    private List<Point> currentStroke = null;
    
    private final Color drawColor = Color.white;
    private final float lineWidth = 5f;

    public Main(String title) {
        super(title);
    }

    @Override
    public void init(GameContainer c) throws SlickException {
        // 드로잉 앱에서는 화면이 매 프레임마다 지워져야 잔상이 남지 않습니다.
        c.setClearEachFrame(true);
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        // 드로잉 스타일 설정
        g.setColor(drawColor);
        g.setLineWidth(lineWidth);
        
        // 1. 저장된 모든 완성된 스트로크를 그립니다.
        for (List<Point> stroke : strokes) {
            drawStroke(g, stroke);
        }
        
        // 2. 현재 그리고 있는 미완성 스트로크를 그립니다.
        if (currentStroke != null) {
            drawStroke(g, currentStroke);
        }

        // 사용 설명서 표시
        g.setColor(Color.white);
        g.setLineWidth(1); // 텍스트를 위해 선 굵기 초기화
        g.drawString("LMB(마우스 왼쪽 버튼): 그리기 | C: 캔버스 지우기 | ESC: 종료", 10, 10);
    }
    
    /** * 점들의 리스트를 연결된 선으로 그리는 헬퍼 메서드 
     */
    private void drawStroke(Graphics g, List<Point> stroke) {
        if (stroke.size() < 2) {
            // 점이 하나만 있을 경우 작은 원으로 표시
            if (stroke.size() == 1) {
                Point p = stroke.get(0);
                g.fillOval(p.x - lineWidth / 2, p.y - lineWidth / 2, lineWidth, lineWidth);
            }
            return;
        }

        // 연속된 점들을 연결하여 선을 그립니다.
        Point p1 = stroke.get(0);
        for (int i = 1; i < stroke.size(); i++) {
            Point p2 = stroke.get(i);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
            p1 = p2;
        }
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        Input input = gc.getInput();
        int mouseX = input.getMouseX();
        int mouseY = input.getMouseY();
        
        // 'C' 키를 누르면 캔버스 클리어
        if (input.isKeyPressed(Input.KEY_C)) {
            strokes.clear();
            currentStroke = null;
        }
        
        // 1. 마우스가 눌렸을 때 (새 스트로크 시작)
        if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
            currentStroke = new ArrayList<>();
            // 첫 번째 점 추가
            currentStroke.add(new Point(mouseX, mouseY));
        }

        // 2. 마우스 버튼이 눌려있는 동안 (드로잉 모션)
        if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && currentStroke != null) {
            // 마지막 점을 가져와서 불필요한 점 추가를 방지합니다.
            if (!currentStroke.isEmpty()) {
                Point lastPoint = currentStroke.get(currentStroke.size() - 1);
                
                // 마우스가 일정 거리(여기서는 2픽셀) 이상 움직였을 때만 새 점을 기록
                if (Math.abs(lastPoint.x - mouseX) > 2 || Math.abs(lastPoint.y - mouseY) > 2) {
                    currentStroke.add(new Point(mouseX, mouseY));
                }
            }
        }

        // 3. 마우스 버튼이 놓였을 때 (스트로크 종료 및 저장)
        if (!input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && currentStroke != null) {
            // 스트로크를 확정하고 저장합니다.
            if (!currentStroke.isEmpty()) {
                strokes.add(currentStroke);
            }
            currentStroke = null;
        }
    }

    public static void main(String[] args) {
        try {
            // 애플리케이션 타이틀 변경
            AppGameContainer app = new AppGameContainer(new Main("Slick2D Pen Drawing"));
            app.setDisplayMode(720, 1280, false);
            app.setTargetFrameRate(60); // 프레임 속도 설정
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}