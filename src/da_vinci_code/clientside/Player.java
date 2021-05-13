package da_vinci_code.clientside;

import java.util.ArrayList;

public class Player {
	int id; // 로그인시 부여 받는 고유의 값 

	ArrayList<Tile> tile; // 플레이어가 보유한 타일 정보 

	boolean isAlive; // 해당 플레이어가 살아있는지 

	boolean isActive; // 턴을 받아서 입력을 할 수 있는 상태인지, 서버에 요청을 보낼 수 있는지
	
	public Player(int id, ArrayList<Tile> tile, boolean isAlive, boolean isActive) {
		super();
		this.id = id;
		this.tile = tile;
		this.isAlive = isAlive;
		this.isActive = isActive;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Tile> getTile() {
		return tile;
	}

	public void setTile(ArrayList<Tile> tile) {
		this.tile = tile;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
    

}
