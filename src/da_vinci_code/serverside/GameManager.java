package da_vinci_code.serverside;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

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
		this.lastTile = null;
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


	public static OutputStreamWriter getWriter(Socket socket) {
		// 서버에게 메세지를 보낼 때 사용하는 wirter를 얻는
		OutputStreamWriter writer = null;
		try {
			OutputStream os = socket.getOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(os);
			writer = new OutputStreamWriter(bos, "UTF-8");
		}catch(Exception e) {
			System.out.println(e.toString());
		}
		
		return writer;
	}
	
	void initGameData() {
		// 하나의 게임에 대한 모든 정보를 초기화 한다.

		// 첫 차례 지정
		this.nowTurnPlayerId = player.get(0).getId(); // Player.id

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
		if(player.size() == 4){
			// 플레이어 4 명 타일 3개 씩
			tilePosion = 3;
		}else{
			// 플레이어 2, 3 명 타일 4개 씩
			tilePosion = 4;

		}
		for(int i=0;i<player.size();i++){
			for(int j=0;j<tilePosion;j++){
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

	void startGame() {
		// 게임을 시작한다.
		// 현재 턴인 플레이어에게 바닥에 있는 랜덤한 블록을 준다.
		addTileToPlayerFromRemainTile(nowTurnPlayerId);
		sendGameInfo();


		
		
	}

	int getNextPlayerId() {
		// nowTurnPlayerId값에 현재 턴인 플레이어의 id를 리턴
		return 0; // 임시
	}

	void sendLog() {

	}

	void checkGuessingTile(int room_id, int player_id, int tileorder, int num) {
		// 플레이어의 타일 맞추기가 성공했는지 확인
	}

	void log() {
		// 로그 입력
	}

	void checkGameEnd() {
		// 플레이어의 타일이 모두 뒤집혔는지 판단한다. 1명의 플레이어를 제외한 모든 플레이어들의 타일이 뒤집혔다면 모든 클라이언트들을 로그아웃
		// 시킨다.
	}

	void addTileToPlayerFromRemainTile(int id){
		int randNum = (int)(Math.random() * remainTile.size());
		Tile tmpTile = new Tile(remainTile.get(randNum));

		player.get(idToIndex(nowTurnPlayerId)).getTile().add(tmpTile);

		remainTile.remove(randNum);
	}
	
	int idToIndex(int id){
		for(int i = 0;i< player.size();i++) {
			if(player.get(i).getId() == id) {
				return i;
			}
		}
		return -1;
	}

	void openTileFromPlayer(int id, int num) {
		// id를 가진 플레이어의 num번째 타일을 오픈한다.
	}

	void sortTile() {
		// 플레이어들의 타일을 순서대로 정렬한다.
	}

	void checkRoomPlayerNum() {
		// 방에 모든 플레이어가 입장했는지 검사한다.
		if (player.size() == limit) {
			initGameData();
			startGame();
		}
	}

	void sendGameInfo() {
		// 플레이어들에게 현재 게임 상태를 보낸다.
		try{
			OutputStreamWriter writer = getWriter(player.get(idToIndex(nowTurnPlayerId)).getSocket());
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("title", "GAME_INFO");
			JSONArray ja = new JSONArray();
			for(int i =0;i<player.size();i++){
				JSONObject pjo = new JSONObject();
				pjo.put("id", player.get(i).getId());
				pjo.put("isAlive", player.get(i).isAlive());
				pjo.put("socket", player.get(i).getSocket());


				JSONArray tja = new JSONArray();
				for(int j= 0;j<player.get(i).getTile().size();j++){
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
			for(int i =0;i<remainTile.size();i++){
				JSONObject rtjo = new JSONObject();
				rtjo.put("color", remainTile.get(i).getColor());
				rtjo.put("num", remainTile.get(i).getNum());
				rtjo.put("isOpen", remainTile.get(i).isOpen());

				rtja.add(rtjo);
			}
			jsonObj.put("remainTile", rtja);
			
			JSONArray lja = new JSONArray();
			for(int i =0;i<logger.getLog().size();i++){
				lja.add(logger.getLog().get(i));
			}
			jsonObj.put("logger", lja);
			
			JSONObject ltjo = new JSONObject();
			ltjo.put("color", lastTile.getColor());
			ltjo.put("num", lastTile.getNum());
			ltjo.put("isOpen", lastTile.isOpen());
			jsonObj.put("lastTile", ltjo);
			
			String json = jsonObj.toJSONString();
			
			writer.write(json);
			writer.flush();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
