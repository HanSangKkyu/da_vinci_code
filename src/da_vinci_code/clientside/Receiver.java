package da_vinci_code.clientside;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
			System.out.println("[서버로 부터 받은 msg] \n" + msg);
			arr = new char[10000];

			return msg;

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String msg = getMsg();

				while (true) {
					// 여러 InputStream이 한번에 들어왔을 때 컨트롤
					int indexofNum = msg.indexOf("}{");
					if (indexofNum != -1) {
						process(msg.substring(0, indexofNum + 1));
						msg = msg.substring(indexofNum + 1, msg.length());
					} else {
						process(msg);
						break;
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void process(String msg) throws ParseException {
		// 서버로부터 받은 메시지의 TITLE에 따라 작업을 수행한다.
		System.out.println("[인식된 msg] \n" + msg);

		JSONParser parser = new JSONParser();
		JSONObject jsonObj;

		// 결과 처리
		jsonObj = (JSONObject) parser.parse(msg);
		String title = (String) jsonObj.get("title");
		System.out.println(title);
		switch (title) {
		case "GAME_INFO":
			gameManager.updateGameInfo(jsonObj);
			break;
		case "SEND_ID":
			int id = ((Long) jsonObj.get("id")).intValue();
			gameManager.setId(id);
			int room_id = ((Long) jsonObj.get("room_id")).intValue();
			gameManager.setRoom_id(room_id);
			System.out.println("id: " + id + ", room_id: " + room_id + "에 배정되었습니다.");
			break;
		case "REQUEST_GUESS":
			int id1 = ((Long) jsonObj.get("id")).intValue();
			gameManager.Guess();
			break;
		case "CONTINUE": // 타일을 맞추고 계속 맞출지 물어본다.
			gameManager.continueOrStop();
			break;
		case "EXIT":
			gameManager.exitOrStay();
			break;
		case "GAME_END": // 타일을 모두 맞춘 우승자가 나왔을 경우.
			int winner_id = ((Long) jsonObj.get("winner_id")).intValue();
			gameManager.gameEnd(winner_id);
			break;
		case "LOGGER": // 로그 출력하기 
			String s = (String) jsonObj.get("log");
			System.out.println(s);
		}

	}

}
