package t016.key.input.trigger.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // **[수정]** 모든 키 관련 맵은 이제 '실제 Slick2D 키 이름'을 키로 사용합니다.
    // 키 입력 상태를 저장하는 맵 (눌림 상태: 레인 인덱스, 안 눌림: -1)
    private Map<String, Integer> keyInputState = new HashMap<>();

    // 현재 활성화된(누르고 있는) 롱노트의 '머리' Y 좌표
    private Map<String, Float> longNoteStarts = new HashMap<>();

    // 키가 떼어져서 길이가 확정된 롱노트 리스트
    private Map<String, List<LongNote>> finishedLongNotes = new HashMap<>();

    // 레인 순서를 정의하고 레인 인덱스를 얻기 위한 배열
    private final String[] LANE_KEYS = {
        "Q", "W", "E", "NUMPAD7", "NUMPAD8", "NUMPAD9", "SPACE", "RIGHT"
    };

    // 노트 이동 속도 (픽셀/프레임 근사치)
    private float noteSpeed = 5.0f;
    private final float INITIAL_Y = 900.0f; // 노트가 시작하는 화면 하단 Y 좌표

    public Main(String title) {
        super(title);
        // **[수정]** 초기 상태 설정
        for (int i = 0; i < LANE_KEYS.length; i++) {
            String key = LANE_KEYS[i];
            keyInputState.put(key, -1); // 초기 상태는 안 눌림
        }
    }

    @Override
    public void keyPressed(int key, char c) {
        String keyName = Input.getKeyName(key);

        // ESC 키로 게임 종료
        if (key == Input.KEY_ESCAPE) {
            System.out.println("Exiting game.");
            System.exit(0);
        }

        // **[수정]** 등록된 레인 키인지 확인
        if (keyInputState.containsKey(keyName)) {
            int laneIndex = getLaneIndex(keyName);

            if (laneIndex != -1 && keyInputState.get(keyName) == -1) {
                // 1. 키를 눌렀을 때의 시각적 효과 (누름 상태)
                keyInputState.put(keyName, laneIndex);

                // 2. 롱노트 시작 로직
                // longNoteStarts.get(keyName)는 -1.0f로 초기화되어 있음
                if (longNoteStarts.getOrDefault(keyName, -1.0f) < 0.0f) {
                    // 롱노트의 '머리' 시작 Y 좌표 설정
                    longNoteStarts.put(keyName, INITIAL_Y);
                }
            }
        }
    }

    @Override
    public void keyReleased(int key, char c) {
        String keyName = Input.getKeyName(key);

        // **[수정]** 등록된 레인 키인지 확인
        if (keyInputState.containsKey(keyName)) {
            // 1. 키가 떼어지면 -1 (안 눌린 상태)로 설정
            keyInputState.put(keyName, -1);

            // 2. 롱노트 종료 로직
            float currentHeadY = longNoteStarts.getOrDefault(keyName, -1.0f);
            if (currentHeadY >= 0.0f) {
                // 활성화된 롱노트를 finishedLongNotes에 추가 (INITIAL_Y는 꼬리 Y, currentHeadY는 머리 Y)
                finishedLongNotes.get(keyName).add(new LongNote(INITIAL_Y, currentHeadY));

                // 롱노트 비활성화
                longNoteStarts.put(keyName, -1.0f);
            }
        }
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        // **[수정]** LANE_KEYS를 기반으로 맵 초기화
        for (String key : LANE_KEYS) {
            longNoteStarts.put(key, -1.0f);
            finishedLongNotes.put(key, new ArrayList<>());
        }
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        // delta를 사용하여 프레임 독립적인 이동 계산
        float moveDistance = noteSpeed * (delta / 16.6666f);

        // 1. 활성화된 (눌리고 있는) 롱노트 이동 처리
        for (String key : LANE_KEYS) {
            float startY = longNoteStarts.get(key);
            if (startY >= 0.0f) {
                // 롱노트의 머리 Y 좌표를 위로 이동 (Y 값 감소)
                longNoteStarts.put(key, startY - moveDistance);
            }
        }

        // 2. 끝난 (떼어진) 롱노트 이동 처리 및 제거
        for (String key : LANE_KEYS) {
            List<LongNote> notes = finishedLongNotes.get(key);
            if (notes == null) continue;

            for (int i = notes.size() - 1; i >= 0; i--) { // 역순으로 순회
                LongNote note = notes.get(i);
                note.startY -= moveDistance; // 꼬리 이동
                note.endY -= moveDistance;   // 머리 이동
                
                // 화면을 벗어난 노트 제거 (꼬리가 0보다 작으면 제거)
                if (note.startY < 0) {
                    notes.remove(i);
                }
            }
        }
    }

    /**
     * 주어진 키 이름에 해당하는 레인 인덱스를 반환합니다.
     */
    private int getLaneIndex(String key) {
        for (int i = 0; i < LANE_KEYS.length; i++) {
            if (LANE_KEYS[i].equals(key)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {

        int laneWidth = 60; // 노트/레인의 너비
        // int totalLanes = LANE_KEYS.length; // 8

        // 배경을 회색으로 채우기
        g.setColor(Color.gray);
        g.fillRect(0, 0, gc.getWidth(), gc.getHeight());

        // **[수정]** LANE_KEYS 배열을 순회하여 렌더링
        for (String key : LANE_KEYS) {
            
            int laneIndex = getLaneIndex(key); 
            if (laneIndex == -1) continue;

            float laneX = laneIndex * laneWidth;

            // 1. 끝난 (떼어진) 롱노트 렌더링
            List<LongNote> notes = finishedLongNotes.get(key);
            g.setColor(Color.white);
            if (notes != null) {
                for (LongNote note : notes) {
                    float height = note.startY - note.endY;
                    g.fillRect(laneX, note.endY, laneWidth, height);
                }
            }
            
            // 2. 활성화된 (눌리고 있는) 롱노트 렌더링 (아직 키를 떼지 않은 노트)
            float activeNoteHeadY = longNoteStarts.getOrDefault(key, -1.0f);
            if (activeNoteHeadY >= 0.0f) {
                float tailY = INITIAL_Y;
                float height = tailY - activeNoteHeadY;
                g.fillRect(laneX, activeNoteHeadY, laneWidth, height);
            }

            // 3. 키가 눌려있을 때 레인 배경을 밝게 렌더링 (시각적 피드백)
            if (keyInputState.get(key) != -1) {
                g.setColor(new Color(255, 255, 255, 60)); // 반투명 흰색
                g.fillRect(laneX, 0, laneWidth, gc.getHeight());
            }
            
            // 4. 레인 경계선 렌더링
            g.setColor(Color.darkGray);
            g.drawLine(laneX, 0, laneX, gc.getHeight());
        }

        // 히트라인 렌더링 (가이드용)
        g.setColor(Color.red);
        g.drawLine(0, INITIAL_Y, gc.getWidth(), INITIAL_Y);
    }

    public static void main(String[] args) {
        try {
            AppGameContainer app = new AppGameContainer(new Main("Slick2D Rhythm LongNote Test"));
            // 8개 레인 * 60px = 480 너비
            app.setDisplayMode(8 * 60, 900, false); 
            app.setTargetFrameRate(60);
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}