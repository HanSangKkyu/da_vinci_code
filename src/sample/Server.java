package sample;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class Server {
    static String global_str = "";

    public static void main(String[] args) {

        try {

            ServerSocket server = new ServerSocket(5001);
            while (true) {
                // 1. 소켓 생성(bind 생략 가능)
                // 2. 접속 수락
                Socket socket = server.accept();
                System.out.println("접속 수락");
                // 3. 받기 전용 스레드 실행
                ServerReceiver receiver = new ServerReceiver(socket);
                receiver.start();
                // 4. 전송 전용 스레드 실행
                ServerSender sender = new ServerSender(socket);
                sender.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

class ServerReceiver extends Thread {
    private Socket socket = null;

    public ServerReceiver(Socket socket) {
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
                Server.global_str = "asdf";
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}

class ServerSender extends Thread {
    private Socket socket = null;

    public ServerSender(Socket socket) {
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
