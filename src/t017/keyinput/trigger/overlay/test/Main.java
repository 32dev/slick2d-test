package t017.keyinput.trigger.overlay.test;

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

    // 모든 키 관련 맵은 이제 '실제 Slick2D 키 이름'을 키로 사용합니다.
    private Map<String, Integer> keyInputState = new HashMap<>();
    private Map<String, Float> longNoteStarts = new HashMap<>();
    private Map<String, List<LongNote>> finishedLongNotes = new HashMap<>();

    // 레인 순서를 정의하고 레인 인덱스를 얻기 위한 배열
    private final String[] LANE_KEYS = {
        "Q", "W", "E", "NUMPAD7", "NUMPAD8", "NUMPAD9", "SPACE", "RIGHT"
    };

    // 노트 이동 속도 (픽셀/프레임 근사치)
    private float noteSpeed = 5.0f;
    private final float INITIAL_Y = 900.0f; // 노트가 시작하는 화면 하단 Y 좌표
    private int laneWidth = 60; // 노트/레인의 너비
    private int totalLanes = LANE_KEYS.length;

    public Main(String title) {
        super(title);
        // 초기 상태 설정
        for (int i = 0; i < LANE_KEYS.length; i++) {
            String key = LANE_KEYS[i];
            keyInputState.put(key, -1); // 초기 상태는 안 눌림
        }
    }

    @Override
    public void keyPressed(int key, char c) {
        String keyName = Input.getKeyName(key);

        if (key == Input.KEY_ESCAPE) {
            System.out.println("Exiting game.");
            System.exit(0);
        }

        if (keyInputState.containsKey(keyName)) {
            int laneIndex = getLaneIndex(keyName);

            if (laneIndex != -1 && keyInputState.get(keyName) == -1) {
                // 1. 키를 눌렀을 때의 시각적 효과 (누름 상태)
                keyInputState.put(keyName, laneIndex);

                // 2. 롱노트 시작 로직
                if (longNoteStarts.getOrDefault(keyName, -1.0f) < 0.0f) {
                    longNoteStarts.put(keyName, INITIAL_Y);
                }
            }
        }
    }

    @Override
    public void keyReleased(int key, char c) {
        String keyName = Input.getKeyName(key);

        if (keyInputState.containsKey(keyName)) {
            // 1. 키가 떼어지면 -1 (안 눌린 상태)로 설정
            keyInputState.put(keyName, -1);

            // 2. 롱노트 종료 로직
            float currentHeadY = longNoteStarts.getOrDefault(keyName, -1.0f);
            if (currentHeadY >= 0.0f) {
                // 활성화된 롱노트를 finishedLongNotes에 추가
                finishedLongNotes.get(keyName).add(new LongNote(INITIAL_Y, currentHeadY));

                // 롱노트 비활성화
                longNoteStarts.put(keyName, -1.0f);
            }
        }
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        for (String key : LANE_KEYS) {
            longNoteStarts.put(key, -1.0f);
            finishedLongNotes.put(key, new ArrayList<>());
        }
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        float moveDistance = noteSpeed * (delta / 16.6666f);

        // 1. 활성화된 (눌리고 있는) 롱노트 이동 처리
        for (String key : LANE_KEYS) {
            float startY = longNoteStarts.get(key);
            if (startY >= 0.0f) {
                longNoteStarts.put(key, startY - moveDistance);
            }
        }

        // 2. 끝난 (떼어진) 롱노트 이동 처리 및 제거
        for (String key : LANE_KEYS) {
            List<LongNote> notes = finishedLongNotes.get(key);
            if (notes == null) continue;

            for (int i = notes.size() - 1; i >= 0; i--) {
                LongNote note = notes.get(i);
                note.startY -= moveDistance;
                note.endY -= moveDistance;
                
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
        
        // 배경을 회색으로 채우기
        g.setColor(Color.gray);
        g.fillRect(0, 0, gc.getWidth(), gc.getHeight());

        // 전체 레인에 대한 반복 처리
        for (String key : LANE_KEYS) {
            
            int laneIndex = getLaneIndex(key); 
            if (laneIndex == -1) continue;

            // -----------------------------------------------------------------
            // **[수정]** 와이드 노트 (SPACE, RIGHT) 처리
            // -----------------------------------------------------------------
            if (key.equals("SPACE") || key.equals("RIGHT")) {
                
                // SPACE: Q(0) ~ E(2) 레인에 걸치도록 (3개 레인 너비)
                int startLaneIndex = key.equals("SPACE") ? 0 : 3;
                int endLaneIndex = key.equals("SPACE") ? 2 : 5;
                
                // **RIGHT 키는 NUMPAD7(3) ~ NUMPAD9(5) 레인에 걸치도록 수정했습니다. 
                // 요청하신 NUM789에 겹치게는 레인 3, 4, 5입니다.**
                
                float laneX = startLaneIndex * laneWidth;
                float width = (endLaneIndex - startLaneIndex + 1) * laneWidth;

                g.setColor(Color.red); // **[수정]** 노트 색상: 빨간색

                // 1. 끝난 (떼어진) 롱노트 렌더링
                List<LongNote> notes = finishedLongNotes.get(key);
                if (notes != null) {
                    for (LongNote note : notes) {
                        float height = note.startY - note.endY;
                        g.fillRect(laneX, note.endY, width, height);
                    }
                }
                
                // 2. 활성화된 (눌리고 있는) 롱노트 렌더링
                float activeNoteHeadY = longNoteStarts.getOrDefault(key, -1.0f);
                if (activeNoteHeadY >= 0.0f) {
                    float tailY = INITIAL_Y;
                    float height = tailY - activeNoteHeadY;
                    g.fillRect(laneX, activeNoteHeadY, width, height);
                }

                // 3. 키가 눌려있을 때 레인 배경을 밝게 렌더링
                if (keyInputState.get(key) != -1) {
                    g.setColor(new Color(255, 0, 0, 60)); // 반투명 빨간색
                    g.fillRect(laneX, 0, width, gc.getHeight());
                }
                
            } else {
                // -----------------------------------------------------------------
                // 일반 노트 (Q, W, E, NUMPAD7, NUMPAD8, NUMPAD9) 처리
                // -----------------------------------------------------------------
                float laneX = laneIndex * laneWidth;

                g.setColor(Color.white); // 일반 노트 색상: 흰색

                // 1. 끝난 (떼어진) 롱노트 렌더링
                List<LongNote> notes = finishedLongNotes.get(key);
                if (notes != null) {
                    for (LongNote note : notes) {
                        float height = note.startY - note.endY;
                        g.fillRect(laneX, note.endY, laneWidth, height);
                    }
                }
                
                // 2. 활성화된 (눌리고 있는) 롱노트 렌더링
                float activeNoteHeadY = longNoteStarts.getOrDefault(key, -1.0f);
                if (activeNoteHeadY >= 0.0f) {
                    float tailY = INITIAL_Y;
                    float height = tailY - activeNoteHeadY;
                    g.fillRect(laneX, activeNoteHeadY, laneWidth, height);
                }

                // 3. 키가 눌려있을 때 레인 배경을 밝게 렌더링
                if (keyInputState.get(key) != -1) {
                    g.setColor(new Color(255, 255, 255, 60)); // 반투명 흰색
                    g.fillRect(laneX, 0, laneWidth, gc.getHeight());
                }
            }
            
            // 레인 경계선 렌더링 (모든 레인에 대해)
            float currentLaneX = laneIndex * laneWidth;
            g.setColor(Color.darkGray);
            g.drawLine(currentLaneX, 0, currentLaneX, gc.getHeight());
        }

        // 히트라인 렌더링 (가이드용)
        g.setColor(Color.red);
        g.drawLine(0, INITIAL_Y, gc.getWidth(), INITIAL_Y);
    }

    public static void main(String[] args) {
        try {
            AppGameContainer app = new AppGameContainer(new Main("Slick2D Rhythm LongNote Test"));
            // 8개 레인 * 60px = 480 너비
            app.setDisplayMode(480, 900, false); 
            app.setTargetFrameRate(60);
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}