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
	static int nextRoomID; // 다음에 방이 만들어지면 부여할 room id값 
	static int nextPlayerID; // 다음에 플레이어가 새로 생기면 부여할 id값
	
	public Server() {
		this.gameManager = new ArrayList<GameManager>();
		this.nextRoomID = 0;
		this.nextPlayerID = 0;
	}

	public static void main(String[] args) {

		Server serverObj = new Server();
		
		try {
			ServerSocket server = new ServerSocket(5001);
			while (true) {
				// 1. 소켓 생성(bind 생략 가능)
				// 2. 접속 수락
				Socket socket = server.accept();
				System.out.println("접속 수락 " + socket);

				// 3. 받기 전용 스레드 실행
				Receiver receiver = new Receiver(socket, serverObj);
				receiver.start();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}


