// 사용안함    
//package da_vinci_code.clientside;
//
//import java.io.BufferedOutputStream;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.net.Socket;
//
//import org.json.simple.JSONObject;
//
//public class Sender extends Thread {
//	private Socket socket = null;
//	private String title;
//	private int room_limit;
//
//	public Sender(Socket socket) {
//		this.socket = socket;
//	}
//
//	public Sender(Socket socket, String title, int room_limit) {
//		this.socket = socket;
//		this.title = title;
//		this.room_limit = room_limit;
//	}
//
//	@Override
//	public void run() {
//		try {
//			OutputStream os = socket.getOutputStream();
//			BufferedOutputStream bos = new BufferedOutputStream(os);
//			OutputStreamWriter writer = new OutputStreamWriter(bos, "UTF-8");
//
//			switch (title) {
//			case "ROOM_LIMIT":
//				JSONObject jsonObj = new JSONObject();
//				jsonObj.put("title", "ROOM_LIMIT");
//				jsonObj.put("limit", room_limit);
//				String json = jsonObj.toJSONString();
//				writer.write(json);
//
//				break;
//			}
//
//			writer.flush();
//
//			// scan.close();
//		} catch (Exception e) {
//			System.out.println(e.toString());
//		}
//	}
//
//}