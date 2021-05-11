package da_vinci_code.serverside;

import java.util.ArrayList;

public class GameManager {
	int Room_id; // 각 방를 구분하기 위한 고유의 값 
	int limit; // 2명, 3명, 4명 플레이 가능 방 
	ArrayList<Player> player; 
	int nowTurnPlayerId; // Player.id 
	ArrayList<Tile> remainTile; // 바닥에 있는 타일 
	Logger logger; 
	Tile lastTile; // 방금 플레이어에게 부여한 타일 정보 
	
	void initGameData() {
		
	}

	void addPlayer() {
		// 로그인한 클라이언트에게 id를 부여하고 player에 add  
	}

	void removePlayer() {
		// 로그아웃한 클라이언트를 player에서 remove 
	}

	void startGame() {
		
	}

	int getNextPlayerId() {
		// nowTurnPlayerId값에 현재 턴인 플레이어의 id를 리턴 
		return 0; // 임시
	}

	void sendLog() {
		
	}

	void checkGuessingTile(int room_id,int player_id, int tileorder, int num) {
		// 플레이어의 타일 맞추기가 성공했는지 확인 
	}

	void log() {
		// 로그 입력 
	}

	void checkGameEnd() {
		// 플레이어의 타일이 모두 뒤집혔는지 판단한다. 1명의 플레이어를 제외한 모든 플레이어들의 타일이 뒤집혔다면 모든 클라이언트들을 로그아웃 시킨다. 
	}

	void addTileToPlayer(int id, Tile tile) {
		
	}

	void removeTileFromRemainTile() {
		
	}

	void openTileFromPlayer(int id, int num) {
		// id를 가진 플레이어의 num번째 타일을 오픈한다. 
	}

	void sortTile() {
		// 플레이어들의 타일을 순서대로 정렬한다. 
	}

	void checkRoomPlayerNum() {
		// 방에 모든 플레이어가 입장했는지 검사한다. 
	}

	void sendGameInfo(ArrayList<Player> player, int nowTurnPlayerId, ArrayList<Tile> remainTile) {
		// 플레이어들에게 현재 게임 상태를 보낸다. 
	}
}
