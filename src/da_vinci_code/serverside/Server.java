package da_vinci_code.serverside;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONObject; 
import org.json.simple.parser.JSONParser; 
import org.json.simple.parser.ParseException;

public class Server {
	static ArrayList<GameManager> gameManager; // 각 방마다 gamemanager 존재
	static ServerSocket server;
	static Socket socket;

	public static void main(String[] args) {
		try {
			server = new ServerSocket(5001);
			while (true) {
				// 1. 소켓 생성(bind 생략 가능)
				// 2. 접속 수락
				socket = server.accept();
				System.out.println("접속 수락");

				// 3. 받기 전용 스레드 실행
				Receiver receiver = new Receiver(socket);
				receiver.start();
				// 4. 전송 전용 스레드 실행

				// 클라이언트에게 몇명의 방에 참가하고 싶은지 물어보기
				questRoomLimit();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void questRoomLimit() {
		// 클라이언트에게 몇명의 방에 참가하고 싶은지 물어보기
		Sender sender = new Sender(socket, "ROOM_LIMIT");
		sender.start();
	}

}

class Receiver extends Thread {
	private Socket socket = null;

	public Receiver(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			// 4. InputStream으로 보내온 메시지를 받는다.
			InputStream is = socket.getInputStream(); // 핵심
			BufferedInputStream bis = new BufferedInputStream(is);
			InputStreamReader reader = new InputStreamReader(bis, "UTF-8");
			char[] arr = new char[100];
			while (is != null) { // 스트림으로 반복문 제어
				// 5. 출력
				reader.read(arr);
				// ㅁㅁㅁ는 \0이고 배열의 공백을 의미한다.
				String msg = new String(arr).replace('\0', ' ');
				System.out.println("[상대] " + msg);

				// 긴 문장 후 짧은 문장이 들어올 경우 이전 값과 섞여 들어온다.
				// 초기화
				arr = new char[100];
			}

		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

}

class Sender extends Thread {
	private Socket socket = null;
	private String title;

	public Sender(Socket socket) {
		this.socket = socket;
	}

	public Sender(Socket socket, String title) {
		this.socket = socket;
		this.title = title;
	}

	@Override
	public void run() {
		try {
			OutputStream os = socket.getOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(os);
			OutputStreamWriter writer = new OutputStreamWriter(bos, "UTF-8");

			switch (title) {
			case "ROOM_LIMIT":
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("title", "ROOM_LIMIT");
				String json = jsonObj.toJSONString();
				writer.write(json);

				break;
			}
			

			writer.flush();
//            // 입력값을 받는 Scanner
//            Scanner scan = new Scanner(System.in);
//            // OutputStream으로 보내온 메시지를 전송한다.
//            OutputStream os = socket.getOutputStream();
//            BufferedOutputStream bos = new BufferedOutputStream(os);
//            OutputStreamWriter writer = new OutputStreamWriter(bos, "UTF-8");
//
//            while (os != null) {
//                String msg = scan.nextLine();
//                writer.write(msg);
//                writer.flush();
//
//            }

			// scan.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

}
