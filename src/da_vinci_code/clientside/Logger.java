package da_vinci_code.clientside;

import java.util.ArrayList;

public class Logger {
	ArrayList<String> log;

	public ArrayList<String> getLog() {
		return log;
	}

	public void addLog(String text) {
		log.add(text);
	}
	
}
