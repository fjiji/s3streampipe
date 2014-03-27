package s3streampipe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDumper {
	final public static String S3CURL = "/home/fji/Downloads/s3-curl/s3curl.pl --id=vblob ";
	final public static String SERVER = "http://10.145.131.78:9070/";
	
	final String bucketName;
	
	FileDumper(String bucketName) {
		this.bucketName = bucketName;
	}
	
	void dump(byte[] content) {
		String fileName = generateFileName();
		String path = "/tmp/" + fileName;
		writeToLocalFile(path, content);
		
		//invoke s3curl
		StringBuilder commandBuilder = new StringBuilder();
		commandBuilder.append(S3CURL);
		commandBuilder.append(" --put ");
		commandBuilder.append(path);
		commandBuilder.append(" -- ");
		commandBuilder.append(SERVER);
		commandBuilder.append(bucketName).append("/");
		commandBuilder.append(fileName);
		
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
		return (bucketName + Long.toString(current));
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
