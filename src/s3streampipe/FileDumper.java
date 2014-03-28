package s3streampipe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDumper {
	private String s3curl_cmd;
	final public static String SERVER = "http://10.145.131.78:9070/";
	
	final String bucketName;
	final String sessionId;
	
	FileDumper(String bucketName, String sessionId, String s3curl) {
		this.bucketName = bucketName;
		this.sessionId = sessionId;
		this.s3curl_cmd = s3curl;
	}
	
	void dump(byte[] content) {
		String fileName = generateFileName();
		String path = "/tmp/" + fileName;
		writeToLocalFile(path, content);
		
		//invoke s3curl
		StringBuilder commandBuilder = new StringBuilder();
		commandBuilder.append(s3curl_cmd);
		commandBuilder.append(" --id=vblob  --put ");
		commandBuilder.append(path);
		commandBuilder.append(" -- ");
		commandBuilder.append(SERVER);
		commandBuilder.append(bucketName).append("/");
		commandBuilder.append(fileName);
		System.out.println("going to exec: " + commandBuilder.toString());
		
		try {
			Process p = Runtime.getRuntime().exec(commandBuilder.toString());
			p.waitFor();
		} catch (Exception e) {
			System.out.print("Unexpected exception: ");
			e.printStackTrace();
		}
	}
	
	private String generateFileName() {
		long current = System.currentTimeMillis();
		return ("AOF" + "-" + sessionId + "-" + Long.toString(current));
	}
	
	private void writeToLocalFile(String filename, byte[] content) {
		File file = new File(filename);
 
		try (FileOutputStream fop = new FileOutputStream(file)) {
			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			fop.write(content);
			fop.flush();
			fop.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
