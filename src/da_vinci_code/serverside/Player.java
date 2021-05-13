package da_vinci_code.serverside;

import java.util.ArrayList;

public class Player {
	int id; // 로그인시 부여 받는 고유의 값 
	ArrayList<Tile> tile; // 플레이어가 보유한 타일 정보 
	boolean isAlive; // 해당 플레이어가 살아있는지
	
	public Player(int id) {
		super();
		this.id = id;
		
		this.isAlive = true;
		this.tile = new ArrayList<Tile>();
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

}
