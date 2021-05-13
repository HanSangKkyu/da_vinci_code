package da_vinci_code.serverside;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.json.simple.JSONObject;

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
