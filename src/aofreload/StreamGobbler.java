package aofreload;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class StreamGobbler extends Thread
 {
	InputStream is;
	String type;
	String fileName;

	StreamGobbler(String fileName, InputStream is, String type) {
		this.fileName = fileName;
		this.is = is;
		this.type = type;
	}

	public void run() {
		try {
			
			//true = append file
    		FileWriter fileWritter = new FileWriter(fileName, true);
    	    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
    	        

    	        
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				bufferWritter.write(line);
				bufferWritter.write("\n");
				//System.out.println(type + ">" + line);
			}
			
	        bufferWritter.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
