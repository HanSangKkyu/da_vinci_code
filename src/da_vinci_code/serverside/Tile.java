package da_vinci_code.serverside;

public class Tile {
	String color; // “black” or “white” 
	int num; // 0~11 
	boolean isOpen; // 해당 타일이 다른 플레이어들에게 밝혀진 상태인지 
	
	public Tile(String color, int num, boolean isOpen) {
		super();
		this.color = color;
		this.num = num;
		this.isOpen = isOpen;
	}
	
	public Tile(Tile tile) {
		super();
		this.color = tile.getColor();
		this.num = tile.getNum();
		this.isOpen= tile.isOpen();
	}
	
	public Tile() {
		super();
	}

	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public boolean isOpen() {
		return isOpen;
	}
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
}
