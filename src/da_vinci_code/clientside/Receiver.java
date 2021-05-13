package da_vinci_code.clientside;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Receiver extends Thread {
	private Socket socket = null;
	private GameManager gameManager = null;

	public Receiver(Socket socket, GameManager gameManager) {
		this.socket = socket;
		this.gameManager = gameManager;
	}

	public String getMsg() {
		// 클라이언트가 보낸 메시지를 문자열로 반환한다
		// InputStream으로 보내온 메시지를 받는다.
		try {
			InputStream is = socket.getInputStream(); // 핵심
			BufferedInputStream bis = new BufferedInputStream(is);
			InputStreamReader reader = new InputStreamReader(bis, "UTF-8");
			char[] arr = new char[10000];
			reader.read(arr);
			String msg = new String(arr).replace('\0', ' ');
			System.out.println("[서버로 부터 받은 msg] " + msg);
			arr = new char[10000];
			return msg;

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}

	@Override
	public void run() {
		JSONParser parser = new JSONParser();
		JSONObject jsonObj;
		while (true) {
			try {
				jsonObj = (JSONObject) parser.parse(getMsg());
				String title = (String) jsonObj.get("title");
				System.out.println(title);
				switch (title) {
				case "GAME_INFO":
					gameManager.updateGameInfo(jsonObj);
					break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
