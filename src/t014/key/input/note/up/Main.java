package t014.key.input.note.up;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    // 키 입력 상태를 저장하는 맵 (눌림 상태: 0~5, 안 눌림: -1)
	private Map<String, Integer> keyMap = new HashMap<String, Integer>();
    
    // 각 레인(키)에 해당하는 노트의 Y 좌표 리스트
    private Map<String, List<Float>> noteLanes = new HashMap<>();
    
    // 노트 이동 속도 (픽셀/프레임 근사치)
    private float noteSpeed = 5.0f; 

	public Main(String title) {
		super(title);
        
        // 레인 순서 및 초기 상태 설정
		keyMap.put("S", -1);
		keyMap.put("D", -1);
		keyMap.put("F", -1);
		keyMap.put("J", -1);
		keyMap.put("K", -1);
		keyMap.put("L", -1);
	}

	@Override
	public void keyPressed(int key, char c) {
		String keyName = Input.getKeyName(key);

        int laneIndex = -1;
        String laneKey = null;

        // 1. 키 이름에 따라 레인 인덱스와 키를 설정
        switch (keyName) {
            case "S": laneIndex = 0; laneKey = "S"; break;
            case "D": laneIndex = 1; laneKey = "D"; break;
            case "F": laneIndex = 2; laneKey = "F"; break;
            case "J": laneIndex = 3; laneKey = "J"; break;
            case "K": laneIndex = 4; laneKey = "K"; break;
            case "L": laneIndex = 5; laneKey = "L"; break;
        }
        
        if (laneKey != null) {
            // 2. 키를 눌렀을 때의 시각적 효과 (히트 라인 색상 변경)
            keyMap.put(laneKey, laneIndex); 

            // 3. 노트 생성 로직 추가: 화면 하단(Y=900.0f)에서 시작
            if (noteLanes.containsKey(laneKey)) {
                // 키를 누를 때마다 해당 레인에 새로운 노트의 Y 좌표를 추가합니다.
                noteLanes.get(laneKey).add(900.0f); 
            }
        }

		// ESC 키로 게임 종료
		if (key == Input.KEY_ESCAPE) {
			System.out.println("Exiting game.");
			System.exit(0);
		}
	}

	@Override
	public void keyReleased(int key, char c) {
		String keyName = Input.getKeyName(key);
        
        // 등록된 키인지 확인하고, 떼었으면 -1로 상태 변경
		if (keyMap.containsKey(keyName)) {
			// 키가 떼어지면 -1 (안 눌린 상태)로 설정
            keyMap.put(keyName, -1);
		}
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		// noteLanes 초기화: 각 키에 빈 리스트 할당
		noteLanes.put("S", new ArrayList<>());
		noteLanes.put("D", new ArrayList<>());
		noteLanes.put("F", new ArrayList<>());
		noteLanes.put("J", new ArrayList<>());
		noteLanes.put("K", new ArrayList<>());
		noteLanes.put("L", new ArrayList<>());

		// 키 입력 시 노트가 생성되므로, 여기에 테스트 노트는 추가하지 않습니다.
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		// delta를 사용하여 프레임 독립적인 이동 계산
		float moveDistance = noteSpeed * (delta / 16.6666f); 

        // 모든 레인의 노트 이동 처리
		for (List<Float> notes : noteLanes.values()) {
			for (int i = 0; i < notes.size(); i++) {
				float currentY = notes.get(i);
				// Y 좌표를 줄이면 화면상에서 위로 이동합니다.
				notes.set(i, currentY - moveDistance); 
			}
			
			// 화면을 벗어난 노트 제거 (Y < 0, 노트 높이 20 고려)
			notes.removeIf(y -> y < -20);
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		
        int laneWidth = 60; // 노트/레인의 너비
        int hitY = 880;     // 히트 라인의 Y 좌표

        // 모든 키 레인 순회
		Set<String> keySet = keyMap.keySet();
		for (String key : keySet) {
            
            // keyMap의 값은 현재 키 입력 상태(-1이면 안 눌림, 0~5면 눌림)
            int laneIndex = -1;
            // 렌더링을 위해 레인 인덱스 재계산
            switch (key) {
                case "S": laneIndex = 0; break;
                case "D": laneIndex = 1; break;
                case "F": laneIndex = 2; break;
                case "J": laneIndex = 3; break;
                case "K": laneIndex = 4; break;
                case "L": laneIndex = 5; break;
            }

            if (laneIndex == -1) continue; // 정의되지 않은 키는 건너뜀

            // 레인 X 좌표 계산
            float laneX = laneIndex * laneWidth;
            
            // 1. 히트 라인 렌더링 (키 입력 상태 반영)
            // keyMap.get(key)가 0 이상이면 눌림 상태 (흰색)
			if (keyMap.get(key) >= 0) {
				g.setColor(Color.white);
			} else {
				g.setColor(Color.black);
			}
			g.fillRect(laneX, hitY, laneWidth, 20); // 히트 라인 그리기
            
            // 2. 생성된 노트 렌더링
			g.setColor(Color.cyan); // 노트 색상
			
			List<Float> notes = noteLanes.get(key);

			if (notes != null) {
				for (Float noteY : notes) {
					// 노트 그리기: laneX, noteY, 너비(60), 높이(20)
					g.fillRect(laneX, noteY, laneWidth, 20); 
				}
			}
		}
	}

	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new Main("Slick2D Rhythm Test"));
            // 6개 레인 * 60px = 360 너비
			app.setDisplayMode(6 * 60, 900, false); 
			app.setTargetFrameRate(60);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}