package da_vinci_code.clientside;

import java.net.Socket;
import java.util.ArrayList;

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
	
	void Main() {
		// 1.로그인 2.종료   선택(1,2): 
	}

	void RoomSelect() {
		// 플레이 인원을 입력하십시오(2~4): / 로그인과 게임 매칭을 위해 정보를 넘긴다 
	}

	void Wait() {
		// 다른 플레이어들과 매칭되는 것을 기다린다. 
	}

	void showLog() {
		// 플레이어들이 한 행동에 대해 보여준다. 
	}

	void showGameInfo() {
		// 플레이어들의 타일 상태, 내가 가진 타일, 바닥에 깔린 타일 정보 출력 
	}

	void Guess() {
		// 어떤 플레이어의 어떤 타일을 맞출 건지 물어본다./ 서버에게 맞출 타일 정보를 보낸다.  
	}

	void continueOrStop() {
		// 계속 타일을 맞출 것인지 차례를 넘길 것인지 물어본다. 
	}

	void exitOrStay() {
		// 타일이 모두 공개되었을 때 방에 남아있을지 메인으로 갈지 정한다. 
	}

	void updateGameInfo(ArrayList<Player> player, int nowTurnPlayerId, ArrayList<Tile> remainTile) {
		// 서버에 요청을 보내서 클라이언트 사이드에서 가지고 있는 GameManager의 필드값을 서버와 동기화 한다. 
	}

	void updateLogger(Logger logger) {
		// 로그를 업데이트 한다. 
	}

	void updateMyInfo(Player me) {
		// 나의 정보를 업데이트 한다. 
	}

	void updatePlayerEnter(Player player) {
		// 플레이어가 접속했다는 정보를 업데이트한다. 
	}
}
