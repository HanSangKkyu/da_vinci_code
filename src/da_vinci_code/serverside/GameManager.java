package da_vinci_code.serverside;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GameManager {
	int room_id; // 각 방를 구분하기 위한 고유의 값
	int limit; // 2명, 3명, 4명 플레이 가능 방
	ArrayList<Player> player;
	int nowTurnPlayerId; // Player.id
	ArrayList<Tile> remainTile; // 바닥에 있는 타일
	Logger logger;
	Tile lastTile; // 방금 플레이어에게 부여한 타일 정보

	public GameManager(int room_id, int limit) {
		super();
		this.room_id = room_id;
		this.limit = limit;

		this.player = new ArrayList<Player>();
		this.nowTurnPlayerId = 0;
		this.remainTile = new ArrayList<Tile>();
		this.logger = new Logger();
		this.lastTile = new Tile();
	}

	public int getRoom_id() {
		return room_id;
	}

	public void setRoom_id(int room_id) {
		this.room_id = room_id;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
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

	public Tile getLastTile() {
		return lastTile;
	}

	public void setLastTile(Tile lastTile) {
		this.lastTile = lastTile;
	}

	public static void send(Socket socket, JSONObject jsonObj) {
		// 서버에게 메세지를 보낼 때 사용하는 wirter를 얻는
		OutputStreamWriter writer = null;
		try {
			OutputStream os = socket.getOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(os);
			writer = new OutputStreamWriter(bos, "UTF-8");

			String json = jsonObj.toJSONString();
			writer.write(json);
			writer.flush();
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return;
	}

	void initGameData() {
		// 하나의 게임에 대한 모든 정보를 초기화 한다.

		// 첫 차례 지정
		this.nowTurnPlayerId = player.get(0).getId(); // Player.id
		System.out.println("first player is " + this.nowTurnPlayerId);

		// 바닥에 남은 타일 초기
		for (int i = 0; i < 12; i++) {
			// 검은 타일 초기화
			remainTile.add(new Tile("black", i, false));
			remainTile.add(new Tile("white", i, false));
		}

		// 플레이어들의 타일, 생존 정보를 초기화 한다.
		for (int i = 0; i < player.size(); i++) {
			player.get(i).setTile(new ArrayList<Tile>());
			player.get(i).setAlive(true);
		}

		// 플레이어에게 타일을 나눠준다.
		int tilePosion = 0;
		if (player.size() == 4) {
			// 플레이어 4 명 타일 3개 씩
			tilePosion = 3;
		} else {
			// 플레이어 2, 3 명 타일 4개 씩
			tilePosion = 4;
		}

		for (int i = 0; i < player.size(); i++) {
			for (int j = 0; j < tilePosion; j++) {
				addTileToPlayerFromRemainTile(player.get(i).getId());
			}
		}

	}

	void addPlayer(int player_id, Socket socket) {
		// 로그인한 클라이언트에게 id를 부여하고 player에 add
		player.add(new Player(player_id, socket));
	}

	void removePlayer() {
		// 로그아웃한 클라이언트를 player에서 remove
	}

	void startGame() throws IOException {
		// 게임을 시작한다.
		// 현재 턴인 플레이어에게 바닥에 있는 랜덤한 블록을 준다.
		startTurn();
	}

	void startTurn() throws IOException {
		// 현재 턴을 가진 플레이어가 턴을 시작한다.
		addTileToPlayerFromRemainTile(nowTurnPlayerId);
		sendGameInfo();
		requestGuess(nowTurnPlayerId);
	}

	int getNextPlayerId() {
		// nowTurnPlayerId값에 다음턴이 될 플레이어의 id를 저장한다
		int nowIdx = idToIndex(nowTurnPlayerId);
		int nextIdx = nowIdx;

		while (true) {
			if (player.get((++nextIdx) % player.size()).isAlive() == true) {
				nowTurnPlayerId = player.get((nextIdx) % player.size()).getId();
				break;
			}
		}

		return nowTurnPlayerId;
	}

	void sendLog(String s) { 
		//입력받은 로그를 모든 플레이어에게 전송한다.
		for (int i = 0; i < player.size(); i++) {
			try {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("title", "LOGGER");
				jsonObj.put("log", s);
				send(player.get(i).getSocket(),jsonObj);
			}
			 catch (Exception e) {
					System.out.println(e.toString());
			}
		}
	}

	void checkGuessingTile(int room_id, int id, int target_id, int tileorder, int guessNum) throws IOException {
		sortTile();
		System.out.println(
				player.get(idToIndex(target_id)).getTile().get(tileorder).getNum() + " " + guessNum + " " + tileorder);
		// 플레이어의 타일 맞추기가 성공했는지 확인
		if (player.get(idToIndex(target_id)).getTile().get(tileorder).getNum() == guessNum) {
			System.out.println(id + " " + target_id + " 맞춤");
			// isOpen을 true로 바꾼다.
			openTileFromPlayer(target_id, tileorder);
			
			if (isGameEnd()) {
				// 한명의 플레이어를 제외한 나머지 플레이어의 타일이 모두 뒤집혔는지 판단한다.
				String log = "게임이 끝났습니다.";
				System.out.println(log);
				
				
				// 우승자의 id를 찾는다.
				int winner_id = 0;
				for (int i = 0; i < player.size(); i++) {
					if (player.get(i).isAlive() == true) {
						winner_id = player.get(i).getId();
						break;
					}
				}

				// 플레이어들에게 알린다.
				JSONObject jo = new JSONObject();
				jo.put("title", "GAME_END");
				jo.put("winner_id", winner_id);
				for (int i = 0; i < player.size(); i++) {
					send(player.get(i).getSocket(), jo);
				}
			} else {
				// 계속 시도할지 물어본다.
				JSONObject jo = new JSONObject();
				jo.put("title", "CONTINUE");
				
				//지목당한 플레이어의 x번째 타일을 맞췄다는 로그 출력
				String log = target_id+" 플레이어의 "+tileorder+"번째 타일의 번호는 "+guessNum+"입니다.";
				sendLog(log);
				
				send(player.get(idToIndex(id)).getSocket(), jo);
			}

		} else {
			
			// 방금 뽑은 타일을 보여준다.
			for (int i = 0; i < player.get(idToIndex(id)).getTile().size(); i++) {
				if (!remainTile.isEmpty()) {
					if (player.get(idToIndex(id)).getTile().get(i).getNum() == lastTile.getNum()
							&& player.get(idToIndex(id)).getTile().get(i).getColor() == lastTile.getColor()) {
						openTileFromPlayer(id, i);
						break;
					}
				} 
				else {
					int randNum = (int) (Math.random() * player.get(idToIndex(id)).getTile().size());
					openTileFromPlayer(id, (int)(Math.random() * randNum));
				}
			}
			
			// 틀렸다고 로그 출력.
			String log = id + " 플레이어기" + " " + target_id + " 플레이어의 타일을" + " 맞추지 못했습니다.";
			System.out.println(log);
			sendLog(log);
			
			// 다음 턴으로 넘어간다.
			getNextPlayerId();
			startTurn();
		}
	}

	void log() {
		// 로그 입력
	}

	boolean isGameEnd() {
		// 플레이어의 타일이 모두 뒤집혔는지 판단한다. 1명의 플레이어를 제외한 모든 플레이어들의 타일이 뒤집혔다면 모든 클라이언트들을 로그아웃
		// 시킨다.

		// 모든 타일이 뒤집힌 플레이어의 isAlive를 false로 만든다
		for (int i = 0; i < player.size(); i++) {
			if (player.get(i).isAlive() == false) {
				continue;
			}

			int cntOpen = 0;
			for (int j = 0; j < player.get(i).getTile().size(); j++) {
				if (player.get(i).getTile().get(j).isOpen() == true) {
					cntOpen++;
				}
			}
			if (cntOpen == player.get(i).getTile().size()) {
				// 모든 타일이 뒤집혔다면 isAlive를 false로 둔다.
				player.get(i).setAlive(false);
			}
		}

		// 최종 우승자가 나왔는지 판단한다.
		int alivePlayerNum = 0;
		for (int i = 0; i < player.size(); i++) {
			if (player.get(i).isAlive() == true) {
				alivePlayerNum++;
			}
		}

		if (alivePlayerNum == 1) {
			return true;
		} else {
			return false;
		}

	}

	void addTileToPlayerFromRemainTile(int id) {
		if(!remainTile.isEmpty()) {
			int randNum = (int) (Math.random() * remainTile.size());
			Tile tmpTile = new Tile(remainTile.get(randNum));

			player.get(idToIndex(id)).getTile().add(tmpTile);

			lastTile = new Tile(tmpTile);

			remainTile.remove(randNum);
		}
	}

	int idToIndex(int id) {
		for (int i = 0; i < player.size(); i++) {
			if (player.get(i).getId() == id) {
				return i;
			}
		}
		return -1;
	}

	void openTileFromPlayer(int id, int num) {
		// id를 가진 플레이어의 num번째 타일을 오픈한다.
		player.get(idToIndex(id)).getTile().get(num).setOpen(true);
	}

	void sortTile() {
		// 플레이어들의 타일을 순서대로 정렬한다.
		// 두 번째 인자로 Comparator 객체를 익명객체로 만들어서 넘깁니다.
		for (int i = 0; i < player.size(); i++) {
			ArrayList<Tile> tiles = player.get(i).getTile();

			for (int j = 0; j < tiles.size(); j++) {
				for (int k = 0; k < tiles.size() - 1 - j; k++) {
					if (tiles.get(k).getNum() > tiles.get(k + 1).getNum()) {
						Tile tmp = new Tile(tiles.get(k));

						tiles.get(k).setColor(tiles.get(k + 1).getColor());
						tiles.get(k).setNum(tiles.get(k + 1).getNum());
						tiles.get(k).setOpen(tiles.get(k + 1).isOpen());

						tiles.get(k + 1).setColor(tmp.getColor());
						tiles.get(k + 1).setNum(tmp.getNum());
						tiles.get(k + 1).setOpen(tmp.isOpen());
					} else if (tiles.get(k).getNum() == tiles.get(k + 1).getNum()) {
						if (tiles.get(k).getColor() == "white") {
							// 같으면 숫자면 검정색이 더 작은 값이다
							Tile tmp = new Tile(tiles.get(k));

							tiles.get(k).setColor(tiles.get(k + 1).getColor());
							tiles.get(k).setNum(tiles.get(k + 1).getNum());
							tiles.get(k).setOpen(tiles.get(k + 1).isOpen());

							tiles.get(k + 1).setColor(tmp.getColor());
							tiles.get(k + 1).setNum(tmp.getNum());
							tiles.get(k + 1).setOpen(tmp.isOpen());
						}

					}
				}
			}
		}

	}

	void checkRoomPlayerNum() throws IOException {
		// 방에 모든 플레이어가 입장했는지 검사한다.
		if (player.size() == limit) {
			System.out.println(room_id + "에 " + limit + "명이 있다.");
			initGameData();
			startGame();
		}
	}

	void sendGameInfo() {
		// 플레이어들에게 현재 게임 상태를 보낸다.
		for (int t = 0; t < player.size(); t++) {
			try {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("title", "GAME_INFO");
				JSONArray ja = new JSONArray();
				for (int i = 0; i < player.size(); i++) {
					JSONObject pjo = new JSONObject();
					pjo.put("id", player.get(i).getId());
					pjo.put("isAlive", player.get(i).isAlive());

					JSONArray tja = new JSONArray();
					for (int j = 0; j < player.get(i).getTile().size(); j++) {
						JSONObject tjo = new JSONObject();
						tjo.put("color", player.get(i).getTile().get(j).getColor());
						tjo.put("num", player.get(i).getTile().get(j).getNum());
						tjo.put("isOpen", player.get(i).getTile().get(j).isOpen());
						tja.add(tjo);
					}
					pjo.put("tile", tja);
					ja.add(pjo);
				}

				jsonObj.put("player", ja);
				jsonObj.put("nowTurnPlayerId", nowTurnPlayerId);

				JSONArray rtja = new JSONArray();
				for (int i = 0; i < remainTile.size(); i++) {
					JSONObject rtjo = new JSONObject();
					rtjo.put("color", remainTile.get(i).getColor());
					rtjo.put("num", remainTile.get(i).getNum());
					rtjo.put("isOpen", remainTile.get(i).isOpen());
					
					rtja.add(rtjo);
				}
				jsonObj.put("remainTile", rtja);

				JSONArray lja = new JSONArray();
				for (int i = 0; i < logger.getLog().size(); i++) {
					lja.add(logger.getLog().get(i));
				}
				jsonObj.put("logger", lja);

				JSONObject ltjo = new JSONObject();
				ltjo.put("color", lastTile.getColor());
				ltjo.put("num", lastTile.getNum());
				ltjo.put("isOpen", lastTile.isOpen());
				jsonObj.put("lastTile", ltjo);

				jsonObj.put("room_id", room_id);

				send(player.get(t).getSocket(), jsonObj);

			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}

	}

	void sendID(int id, Socket socket, int roomNum) throws IOException {
		JSONObject jo = new JSONObject();
		jo.put("title", "SEND_ID");
		jo.put("id", id);
		jo.put("room_id", roomNum);

		send(socket, jo);
	}

	void requestGuess(int id) throws IOException {
		// 플레이어에게 맞추기 단계를 시작하라고 요청한다.
		JSONObject jo = new JSONObject();
		jo.put("title", "REQUEST_GUESS");
		jo.put("id", id);

		send(player.get(idToIndex(id)).getSocket(), jo);
	}

	void continueOrStop(int id, boolean isContinue) throws IOException {
		System.out.println("isContinue " + isContinue);
		// 플레이어가 계속 맞추겠다고 했는지 다음 턴으로 넘기겠다고 했는지 받아온다.
		if (isContinue == true) {
			//현재 차례 로그 출력
			String log = id + " 플레이어가 계속 진행합니다. ";
			System.out.println(log);
			sendLog(log);
			
			sendGameInfo();
			requestGuess(id);
		} else {
			//다음 차례 로그 출력
			String log = id + " 플레이어가 다음 플레이어에게 차례를 넘깁니다. ";
			System.out.println(log);
			sendLog(log);
			
			// 다음 차례로 넘기기 + 로그 출력
			getNextPlayerId();
			log = "다음 차례는 " + nowTurnPlayerId + " 플레이어입니다.";
			sendLog(log);
			startTurn();
		}
	}
}
