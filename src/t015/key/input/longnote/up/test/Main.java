package t015.key.input.longnote.up.test;

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

	// 롱노트 정보를 저장할 내부 클래스
	private static class LongNote {
		float startY; // 롱노트의 '꼬리' 부분 Y 좌표 (키를 누른 시점의 Y=900.0f)
		float endY; // 롱노트의 '머리' 부분 Y 좌표 (가장 작은 Y 값)

		public LongNote(float startY, float endY) {
			this.startY = startY;
			this.endY = endY;
		}
	}

	// 키 입력 상태를 저장하는 맵 (눌림 상태: 0~5, 안 눌림: -1)
	private Map<String, Integer> keyMap = new HashMap<String, Integer>();

	// 현재 활성화된(누르고 있는) 롱노트의 '머리' Y 좌표 (계속 위로 이동함)
	// 값이 0.0f 이상: 활성화됨. 값이 -1.0f: 비활성화됨.
	private Map<String, Float> longNoteStarts = new HashMap<>();

	// 키가 떼어져서 길이가 확정된 롱노트 리스트
	private Map<String, List<LongNote>> finishedLongNotes = new HashMap<>();

	// 노트 이동 속도 (픽셀/프레임 근사치)
	private float noteSpeed = 5.0f;
	private final float INITIAL_Y = 900.0f; // 노트가 시작하는 화면 하단 Y 좌표

	public Main(String title) {
		super(title);

		// 레인 순서 및 초기 상태 설정
		keyMap.put("1", -1);
		keyMap.put("2", -1);
		keyMap.put("3", -1);
		keyMap.put("4", -1);
		keyMap.put("5", -1);
		keyMap.put("6", -1);
	}

	@Override
	public void keyPressed(int key, char c) {
		String keyName = Input.getKeyName(key);

		int laneIndex = -1;
		String laneKey = null;

		// 1. 키 이름에 따라 레인 인덱스와 키를 설정
		switch (keyName) {
		case "1":
			laneIndex = 0;
			laneKey = "1";
			break;
		case "2":
			laneIndex = 1;
			laneKey = "2";
			break;
		case "3":
			laneIndex = 2;
			laneKey = "3";
			break;
		case "4":
			laneIndex = 3;
			laneKey = "4";
			break;
		case "5":
			laneIndex = 4;
			laneKey = "5";
			break;
		case "6":
			laneIndex = 5;
			laneKey = "6";
			break;
		}

		if (laneKey != null) {
			// 2. 키를 눌렀을 때의 시각적 효과
			keyMap.put(laneKey, laneIndex);

			// 3. 롱노트 시작 로직: 현재 롱노트가 활성화되지 않았을 때만 시작 (키 반복 방지)
			if (longNoteStarts.get(laneKey) < 0.0f) {
				// 롱노트의 '머리' 시작 Y 좌표 설정
				longNoteStarts.put(laneKey, INITIAL_Y);
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

			// 롱노트 종료 로직
			float currentHeadY = longNoteStarts.getOrDefault(keyName, -1.0f);
			if (currentHeadY >= 0.0f) {
				// 활성화된 롱노트를 finishedLongNotes에 추가
				// 롱노트는 항상 INITIAL_Y(900.0f)에서 시작하며, 현재 머리 Y(currentHeadY)에서 끝납니다.
				// 롱노트의 꼬리 (startY)는 키를 누른 시점의 Y 좌표입니다.
				finishedLongNotes.get(keyName).add(new LongNote(INITIAL_Y, currentHeadY));

				// 롱노트 비활성화
				longNoteStarts.put(keyName, -1.0f);
			}
		}
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		String[] keys = { "1", "2", "3", "4", "5", "6" }; // <--- 변경된 코드
		for (String key : keys) {
			longNoteStarts.put(key, -1.0f);
			finishedLongNotes.put(key, new ArrayList<>());
		}
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		// delta를 사용하여 프레임 독립적인 이동 계산 (16.6666ms는 60FPS 기준 시간)
		float moveDistance = noteSpeed * (delta / 16.6666f);

		// 1. 활성화된 (눌리고 있는) 롱노트 이동 처리
		for (String key : longNoteStarts.keySet()) {
			float startY = longNoteStarts.get(key);
			if (startY >= 0.0f) {
				// Y 좌표를 줄이면 롱노트의 '머리' 부분이 위로 이동합니다.
				longNoteStarts.put(key, startY - moveDistance);
			}
		}

		// 2. 끝난 (떼어진) 롱노트 이동 처리 및 제거
		for (List<LongNote> notes : finishedLongNotes.values()) {
			for (int i = 0; i < notes.size(); i++) {
				LongNote note = notes.get(i);
				// 꼬리(startY)와 머리(endY)를 모두 위로 이동시킵니다.
				note.startY -= moveDistance;
				note.endY -= moveDistance;
			}

			// 화면을 벗어난 노트 제거 (노트 꼬리가 화면 밖으로 완전히 벗어난 경우)
			// 롱노트의 Y 좌표는 화면의 위쪽이 0, 아래쪽이 900입니다.
			notes.removeIf(note -> note.startY < 0);
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {

		int laneWidth = 60; // 노트/레인의 너비
		// int hitY = 880; // 히트 라인의 Y 좌표

		// 배경을 검은색으로 채우기
		g.setColor(Color.gray);
		g.fillRect(0, 0, gc.getWidth(), gc.getHeight());

		Set<String> keySet = keyMap.keySet();
		for (String key : keySet) {
			int laneIndex = -1;
			// 렌더링을 위해 레인 인덱스 계산
			switch (key) {
			case "S":
				laneIndex = 0;
				break;
			case "D":
				laneIndex = 1;
				break;
			case "F":
				laneIndex = 2;
				break;
			case "J":
				laneIndex = 3;
				break;
			case "K":
				laneIndex = 4;
				break;
			case "L":
				laneIndex = 5;
				break;
			}

			if (laneIndex == -1)
				continue;

			float laneX = laneIndex * laneWidth;

			// 3. 롱노트 렌더링
			g.setColor(Color.white);

			// 3a. 활성화된 (눌리고 있는) 롱노트 렌더링
			float activeNoteHeadY = longNoteStarts.getOrDefault(key, -1.0f);
			if (activeNoteHeadY >= 0.0f) {
				float tailY = INITIAL_Y; // 꼬리는 항상 시작 위치 (900.0f)

				// [수정] 노트 높이: 꼬리(tailY) - 머리(activeNoteHeadY)
				float height = tailY - activeNoteHeadY;

				// Y 위치는 '머리' (가장 작은 Y)
				g.fillRect(laneX, activeNoteHeadY, laneWidth, height);
			}

			// 3b. 끝난 (떼어진) 롱노트 렌더링
			List<LongNote> notes = finishedLongNotes.get(key);
			if (notes != null) {
				for (LongNote note : notes) {
					// [수정] 노트 높이: 꼬리(note.startY) - 머리(note.endY)
					float height = note.startY - note.endY;

					// Y 위치는 '머리' (가장 작은 Y)
					g.fillRect(laneX, note.endY, laneWidth, height);
				}
			}

			// 키가 눌려있을 때 레인 배경을 밝게 렌더링 (시각적 피드백)
			if (keyMap.get(key) != -1) {
				g.setColor(new Color(255, 255, 255, 60)); // 반투명 흰색
				g.fillRect(laneX, 0, laneWidth, gc.getHeight());
			}
		}

		// 히트라인 렌더링 (가이드용)
		g.setColor(Color.red);
		g.drawLine(0, INITIAL_Y, gc.getWidth(), INITIAL_Y);
	}

	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new Main("Slick2D Rhythm LongNote Test"));
			// 6개 레인 * 60px = 360 너비
			app.setDisplayMode(6 * 60, 900, false);
			app.setTargetFrameRate(60);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}