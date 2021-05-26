package da_vinci_code.clientside;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GameManager {
	Socket socket; // 서버에게 데이터를 보낼 때 사용하는 소켓
	int room_id; // 각 방을 구분하기 위한 고유의 값
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
		return room_id;
	}

	public void setRoom_id(int room_id) {
		this.room_id = room_id;
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

	public void showLog() {
		// 플레이어들이 한 행동에 대해 보여준다.
	}

	public void showGameInfo() {
		System.out.println("현재 턴 : player " + nowTurnPlayerId);

		// 플레이어들의 타일 상태, 내가 가진 타일, 바닥에 깔린 타일 정보 출력
		int remainTileBlackNum = 0;
		int remainTileWhiteNum = 0;
		for (int i = 0; i < remainTile.size(); i++) {
			if (remainTile.get(i).getColor().equals("black")) {
				remainTileBlackNum++;
			} else if (remainTile.get(i).getColor().equals("white")) {
				remainTileWhiteNum++;
			}
		}
		System.out.println("■:" + remainTileBlackNum + ", □:" + remainTileWhiteNum);
		System.out.println("");
		for (int i = 0; i < player.size(); i++) {
			if (player.get(i).getId() == id) {
				continue;
			}
			ArrayList<Tile> tiles = player.get(i).getTile();
			System.out.printf("Player " + player.get(i).getId() + ":");
			for (int j = 0; j < tiles.size(); j++) {
				String colorPrint = "";
				if (tiles.get(j).getColor().equals("black")) {
					colorPrint = "■";
				} else if (tiles.get(j).getColor().equals("white")) {
					colorPrint = "□";
				}

				String numPrint = "";
				if (tiles.get(j).isOpen() == true) {
					numPrint = tiles.get(j).getNum() + "";
				} else if (tiles.get(j).isOpen() == false) {
					numPrint = "?";
				}

				System.out.printf(" " + colorPrint + numPrint);
			}
			System.out.println();
		}

		System.out.println();

		ArrayList<Tile> tiles = me.getTile();
		System.out.printf("my id(" + id + ") :");
		for (int j = 0; j < tiles.size(); j++) {
			String colorPrint = "";
			if (tiles.get(j).getColor().equals("black")) {
				colorPrint = "■";
			} else if (tiles.get(j).getColor().equals("white")) {
				colorPrint = "□";
			}

			System.out.printf(" " + colorPrint + tiles.get(j).getNum());
		}
		System.out.println();

	}

		public void Guess() {
		// 어떤 플레이어의 어떤 타일을 맞출 건지 물어본다.
		// 서버에게 맞출 타일 정보를 보낸다.
		Scanner scan = new Scanner(System.in);
		int target_id; // 타겟 플레이어 ID
		int tileorder; // 왼쪽부터 몇번째 타일을 맞출 것인지
		int guessNum; // 맞출 타일에 적힌 번호
		int count = 0;
		int tilesize = 0;
		int[] surviveplayer = {0, 0, 0, 0}; // 최대 플레이어 4명
		
		
		for (int i = 0; i < player.size(); i++) {
			tilesize = 0;
			if (player.get(i).getId() == id) { // 있는 플레이어인지 체크
				continue;
			}
			
			//다 오픈된 플레이어 인지 체크
			ArrayList<Tile> tiles = player.get(i).getTile();
			for (int j = 0; j < tiles.size(); j++) {
				if (tiles.get(j).isOpen() == true) {
					tilesize += 1;
				} 
			}
			if (tilesize == tiles.size()) {
				continue;
			}
			// --------------------
			surviveplayer[count] = i+1; // 살아남은 플레이어 확인
			count += 1;
		}
		
		while (true) {
			int check = 0;
			System.out.println("<다른 플레이어의 타일 맞추기>");
			System.out.printf("플레이어ID: ");
			target_id = Integer.parseInt(scan.nextLine());
			if(target_id < 1 || target_id > 4 ) { // 없는 플레이어를 입력했을 경우
				System.out.println("올바른 플레이어를 입력하십시오.");
				continue;
			}
			for (int i = 0; i < surviveplayer.length; i++) {
				if(target_id == surviveplayer[i]) {
					check = 1;
				}
			}
			if (check != 1) {
				System.out.println("타일이 다 뒤집혀진 플레이어이거나 없는 플레이어입니다.");
				continue;
			}
			
			ArrayList<Tile> tiles = player.get(target_id-1).getTile();
			System.out.printf("몇번째 타일을 맞추겠습니까? (왼쪽부터 0,1,2,..) : ");
			tileorder = Integer.parseInt(scan.nextLine());
			if(tileorder < 0 || tileorder >= tiles.size()) { // 없는 타일을 입력했을 경우
				System.out.println("올바른 타일 위치를 입력하십시오.");
				continue;
			}
			if (tiles.get(tileorder).isOpen() == true) {
				System.out.println("이미 알려진 타일입니다. 다시고르시오.");
				continue;
			}
			
			System.out.printf("그 타일은 몇입니까? : ");
			guessNum = Integer.parseInt(scan.nextLine());
			if(guessNum < 0 || guessNum > 11) { // 벗어나는 값을 입력했을 경우
				System.out.println("올바른 타일 숫자를 입력하십시오.");
				continue;
			}
	
			JSONObject jo = new JSONObject();
			jo.put("title", "GUESS");
			jo.put("room_id", room_id);
			jo.put("id", id);
			jo.put("target_id", target_id);
			jo.put("tileorder", tileorder);
			jo.put("guessNum", guessNum);
	
			send(socket, jo);
			break;
		}
	}

	public void continueOrStop() {
		// 계속 타일을 맞출 것인지 차례를 넘길 것인지 물어본다.
		JSONObject jo = new JSONObject();
		jo.put("title", "CONTINUE");
		jo.put("room_id", room_id);
		jo.put("id", id);

		while (true) {
			System.out.printf("1. 계속 타일을 맞춘다 2.차례를 넘긴다: ");
			Scanner scan = new Scanner(System.in);
			int sel = Integer.parseInt(scan.nextLine());
			if (sel == 1) {
				jo.put("isContinue", true);
			} else if (sel == 2) {
				jo.put("isContinue", false);
			}
			
			if (sel == 1 || sel == 2) {
				send(socket, jo);
				break;
			}
			else {
				System.out.println("1 또는 2를 입력해 주세요.");
			}
		}

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

			// 내 정보면 me에도 채우기
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

		room_id = ((Long) jo.get("room_id")).intValue();

		sortTile();
		showGameInfo();

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

		// me에 들어 있는 tile 정보도 정렬
		ArrayList<Tile> tiles = me.getTile();
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

	void gameEnd(int winner_id) {
		System.out.println("게임 끝");
		System.out.println("우승자: plyaer " + winner_id);
	}
}
