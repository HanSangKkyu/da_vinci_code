package da_vinci_code.clientside;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GameManager {
	Socket socket; // 서버에게 데이터를 보낼 때 사용하는 소켓
	int Room_id; // 각 방을 구분하기 위한 고유의 값
	int id; // 나의 고유 id
	int limit; // 내가 선택한 게임의 인원 수 2명, 3명, 4명
	Player me; // 나에 대한 게임 정보(가지고 있는 타일, 내 턴인지, 생존여부)
	ArrayList<Player> player; // 내가 속한 방에 함께 있는 플레이어
	int nowTurnPlayerId; // 현재 턴을 가지고 있는 플레이어의 id
	ArrayList<Tile> remainTile; // 바닥에 깔린 타일
	Logger logger; // 로그

	public GameManager(Socket socket) {
		this.socket = socket;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getRoom_id() {
		return Room_id;
	}

	public void setRoom_id(int room_id) {
		Room_id = room_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public Player getMe() {
		return me;
	}

	public void setMe(Player me) {
		this.me = me;
	}

	public ArrayList<Player> getPlayer() {
		return player;
	}

	public void setPlayer(ArrayList<Player> player) {
		this.player = player;
	}

	public int getNowTurnPlayerId() {
		return nowTurnPlayerId;
	}

	public void setNowTurnPlayerId(int nowTurnPlayerId) {
		this.nowTurnPlayerId = nowTurnPlayerId;
	}

	public ArrayList<Tile> getRemainTile() {
		return remainTile;
	}

	public void setRemainTile(ArrayList<Tile> remainTile) {
		this.remainTile = remainTile;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void showLog() {
		// 플레이어들이 한 행동에 대해 보여준다.
	}

	public void showGameInfo() {
		// 플레이어들의 타일 상태, 내가 가진 타일, 바닥에 깔린 타일 정보 출력
	}

	public void Guess() {
		// 어떤 플레이어의 어떤 타일을 맞출 건지 물어본다./ 서버에게 맞출 타일 정보를 보낸다.
	}

	public void continueOrStop() {
		// 계속 타일을 맞출 것인지 차례를 넘길 것인지 물어본다.
	}

	public void exitOrStay() {
		// 타일이 모두 공개되었을 때 방에 남아있을지 메인으로 갈지 정한다.
	}

	public void updateGameInfo(JSONObject jo) {
		// 서버에 요청을 보내서 클라이언트 사이드에서 가지고 있는 GameManager의 필드값을 서버와 동기화 한다.
		String title = (String) jo.get("title");

		int nowTurnPlayerId = ((Long) jo.get("nowTurnPlayerId")).intValue();

		player = new ArrayList<Player>();
		JSONArray pja = (JSONArray) jo.get("player");
		for (int i = 0; i < pja.size(); i++) {
			JSONObject pjo = (JSONObject) pja.get(i);

			int t_id = ((Long) pjo.get("id")).intValue();
			boolean t_isAlive = (boolean) pjo.get("isAlive");
			ArrayList<Tile> t_tile = new ArrayList<Tile>();

			JSONArray tja = (JSONArray) pjo.get("tile");
			for (int j = 0; j < tja.size(); j++) {
				JSONObject tjo = (JSONObject) tja.get(j);
				String t_color = (String) tjo.get("color"); // “black” or “white”
				int t_num = ((Long) tjo.get("num")).intValue(); // 0~11
				boolean t_isOpen = (boolean) tjo.get("isOpen");

				t_tile.add(new Tile(t_color, t_num, t_isOpen));
			}

			// 내정보면 me에도 채우기
			if (t_id == id) {
				me = new Player(t_id, t_tile, t_isAlive, false);
			}

			// 자기 차례면
			if (t_id == nowTurnPlayerId) {
				player.add(new Player(t_id, t_tile, t_isAlive, true));
			} else {
				player.add(new Player(t_id, t_tile, t_isAlive, false));
			}

		}

		JSONArray rtja = (JSONArray) jo.get("remainTile");
		remainTile = new ArrayList<Tile>();
		for (int i = 0; i < rtja.size(); i++) {
			JSONObject rtjo = (JSONObject) rtja.get(i);

			String t_color = (String) rtjo.get("color");
			int t_num = ((Long) rtjo.get("num")).intValue();
			boolean t_isOpen = (boolean) rtjo.get("isOpen");

			remainTile.add(new Tile(t_color, t_num, t_isOpen));
		}

		JSONArray lja = (JSONArray) jo.get("logger");
		logger = new Logger();
		for (int i = 0; i < lja.size(); i++) {
			String t_text = (String) lja.get(i);
			logger.addLog(t_text);

		}

	}

	public void updateLogger(Logger logger) {
		// 로그를 업데이트 한다.
	}

	public void updateMyInfo(Player me) {
		// 나의 정보를 업데이트 한다.
	}

	public void updatePlayerEnter(Player player) {
		// 플레이어가 접속했다는 정보를 업데이트한다.
	}
}
