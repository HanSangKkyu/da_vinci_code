package sample;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Client {

    public static void main(String[] args) {
        try {
            // 1. 소켓 생성
            Socket socket = new Socket("localhost", 5001);// 2. 연결 요청
            // 3. 받기 전용 스레드 실행
            ClientReceiver receiver = new ClientReceiver(socket);
            receiver.start();
            // 4. 전송 전용 스레드 실행
            ClientSender sender = new ClientSender(socket);
            sender.start();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}

class ClientReceiver extends Thread {
    private Socket socket = null;

    public ClientReceiver(Socket socket) {
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
            while (is != null) { // 스트림으로 반복문 제어\
                // 5. 출력
                reader.read(arr);
                // ㅁㅁㅁ는 \0이고 배열의 공백을 의미한다.
                String msg = new String(arr).replace('\0', ' ');
                System.out.println("[상대] " + msg);
                JSONParser parser = new JSONParser();
                JSONObject jsonObj = (JSONObject) parser.parse(msg);
				String title = (String) jsonObj.get("title");
				System.out.println(title);
				JSONArray arr1 = (JSONArray)jsonObj.get("arr1");
				
				
				System.out.println(arr1.get(0).toString()+arr1.get(1)+arr1.get(2));

                // 긴 문장 후 짧은 문장이 들어올 경우 이전 값과 섞여 들어온다.
                // 초기화
                arr = new char[100];
                Server.global_str = "asdf";
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
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
			System.out.println("["+socket+" 가 보낸 msg] " + msg);
			arr = new char[10000];
			return msg;

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}
}

class ClientSender extends Thread {
    private Socket socket = null;

    public ClientSender(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // 입력값을 받는 Scanner
            Scanner scan = new Scanner(System.in);
            // OutputStream으로 보내온 메시지를 전송한다.
            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);
            OutputStreamWriter writer = new OutputStreamWriter(bos, "UTF-8");

            while (os != null) {
                String msg = scan.nextLine();
                System.out.println(Server.global_str);
                writer.write(msg);
                writer.flush();

            }

            // scan.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
