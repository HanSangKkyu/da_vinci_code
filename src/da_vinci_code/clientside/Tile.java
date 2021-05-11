package da_vinci_code.clientside;

public class Tile {
	String color; // “black” or “white” 
	int num; // 0~11 
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
	boolean isOpen; // 해당 타일이 다른 플레이어들에게 밝혀진 상태인지 
}
