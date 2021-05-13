package da_vinci_code.clientside;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Client {
	static GameManager gameManager;
	static Socket socket;

	public static void main(String[] args) {

		Main();

	}

	public static OutputStreamWriter getWriter() {
		// 서버에게 메세지를 보낼 때 사용하는 wirter를 얻는
		OutputStreamWriter writer = null;
		try {
			OutputStream os = socket.getOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(os);
			writer = new OutputStreamWriter(bos, "UTF-8");
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return writer;
	}

	public static void Main() {
		// 1.로그인 2.종료 선택(1,2):
		while (true) {
			Scanner scan = new Scanner(System.in);

			System.out.println("1.로그인 2.종료   : ");
			int select = Integer.parseInt(scan.nextLine());
			if (select == 1) {
				try {
					// 1. 소켓 생성
					socket = new Socket("localhost", 5001);// 2. 연결 요청

					// gameManager 생성
					gameManager = new GameManager(socket);
					roomSelect();

					// 3. 받기 전용 스레드 실행
					Receiver receiver = new Receiver(socket, gameManager);
					receiver.start();

				} catch (Exception e) {
					System.out.println(e.toString());
				}
				break;
			} else if (select == 2) {
				break;
			}

		}

	}

	public static void roomSelect() {
		// 플레이 인원을 입력하십시오(2~4): / 게임 매칭을 위해 정보를 넘긴다
		while (true) {
			Scanner scan = new Scanner(System.in);

			System.out.println("플레이 인원을 입력하십시오(2~4): ");
			int limit = Integer.parseInt(scan.nextLine());
			if (limit >= 2 && limit <= 4) {
				gameManager.setLimit(limit);

				try {
					OutputStreamWriter writer = getWriter();

					JSONObject jsonObj = new JSONObject();
					jsonObj.put("title", "ROOM_LIMIT");
					jsonObj.put("limit", limit);
					String json = jsonObj.toJSONString();

					writer.write(json);
					writer.flush();

				} catch (Exception e) {
					System.out.println(e.toString());
				}

				Wait();
				break;
			}

		}
	}

	public static void Wait() {
		// 다른 플레이어들과 매칭되는 것을 기다린다.
		System.out.println("다른 플레이어를 기다리는 중");
	}
}
