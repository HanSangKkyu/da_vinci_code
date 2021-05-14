package da_vinci_code.serverside;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import da_vinci_code.serverside.*;

class Receiver extends Thread {
	private Socket socket = null;
	private Server server = null;

	public Receiver(Socket socket, Server server) {
		super();
		this.socket = socket;
		this.server = server;
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
			System.out.println("[" + socket + " 가 보낸 msg] \n" + msg);
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
				case "ROOM_LIMIT":
					// 클라이언트가 참가하고 싶은 방 제한인원을 받음
					int limit = ((Long) jsonObj.get("limit")).intValue();
					// 게임에참가자들을매칭한다.
					matchPlayer(limit);
					break;
				case "GUESS":
					// 플레이어가 타일 맞추기를 시도함
					int room_id = ((Long) jsonObj.get("room_id")).intValue();
					int id = ((Long) jsonObj.get("id")).intValue();
					int target_id = ((Long) jsonObj.get("target_id")).intValue();
					int tileorder = ((Long) jsonObj.get("tileorder")).intValue();
					int guessNum = ((Long) jsonObj.get("guessNum")).intValue();

					server.gameManager.get(roomIDToIdx(room_id)).checkGuessingTile(room_id, id, target_id, tileorder, guessNum);
					break;
				case "CONTINUE":
					int room_id1 = ((Long) jsonObj.get("room_id")).intValue();
					int id1 = ((Long) jsonObj.get("id")).intValue();
					boolean isContinue = (boolean) jsonObj.get("isContinue");
					server.gameManager.get(roomIDToIdx(room_id1)).continueOrStop(id1, isContinue);
					break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void matchPlayer(int limit) throws IOException {
		boolean flag = false;
		for (int i = 0; i < server.gameManager.size(); i++) {
			if (limit == server.gameManager.get(i).getLimit() && server.gameManager.get(i).getPlayer().size() < limit) {
				// 조건에 맞는 방이 있다면
				server.gameManager.get(i).addPlayer(++server.nextPlayerID, socket);
				flag = true;

				System.out.println(socket + "이 " + server.nextPlayerID + "를 부여받고 "
						+ server.gameManager.get(i).getRoom_id() + "방에 배정 됨 ");
				server.gameManager.get(i).sendID(server.nextPlayerID, socket, server.gameManager.get(i).getRoom_id()); // 방
																														// 배정
																														// 결과를
																														// 클라이언트에게
																														// 알린다.

				// 방에 모든 플레이어들이 들어왔는지 확인
				server.gameManager.get(i).checkRoomPlayerNum();
				break;
			}
		}

		if (!flag) {
			GameManager gameManager = new GameManager(++server.nextRoomID, limit);
			gameManager.addPlayer(++server.nextPlayerID, socket);
			gameManager.sendID(server.nextPlayerID, socket, server.nextRoomID); // 방 배정 결과를 클라이언트에게 알린다.
			server.gameManager.add(gameManager);

			System.out.println(socket + "이 " + server.nextPlayerID + "를 부여받고 " + server.nextRoomID + "방에 배정 됨 ");
		}

	}

	public int roomIDToIdx(int room_id) {
		// room_id를 인덱스로 변환해준다.
		for (int i = 0; i < server.gameManager.size(); i++) {
			if (server.gameManager.get(i).getRoom_id() == room_id) {
				return i;
			}
		}
		return -1;
	}

}