package aofreload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AOFReloader {
	private static String s3curl;
	final public static String SERVER = "http://10.145.131.78:9070/";

	public static void main(String[] args) {
		FileInputStream configInput;
		try {
			configInput = new FileInputStream("resource/config.properties");
			Properties prop = new Properties();
			prop.load(configInput);
			
			s3curl = prop.getProperty("s3curl") + " --id=vblob ";

			String bucketName = args[0];
			String sessionId = args[1];
			String AOFfile = args[2];

			String listRes = listBucket(bucketName, sessionId);

			List<String> files = findFiles(listRes);

			for (String file : files) {
				fetchAndAppend(bucketName, file, AOFfile);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	private static String listBucket(String bucket, String sessionId) {
		String command = s3curl + " --id=vblob -- http://10.145.131.78:9070/" + bucket
				+ "?prefix=AOF-" + sessionId;
		
		try {
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			StringBuilder res = new StringBuilder();
			String line;
			while ( (line = br.readLine()) != null) {
			     res.append(line);
			}
			return res.toString();
		} catch (Exception e) {
			System.out.print("Unexpected exception: ");
			e.printStackTrace();
		}		
		return null;
	}
	
	private static List<String> findFiles(String listResult) {
		List<String> list = new ArrayList<String> ();
		for(int start = 0, end = 0; start < listResult.length(); start = end) {
			int pos = listResult.indexOf("<Key>", start);
			if (pos == -1) {
				break;
			}
			pos += 5; // skip "<Key>"
			end = listResult.indexOf("</Key>", pos);
			
			list.add(listResult.substring(pos, end));
			
			end += 6; //skip "</Key>"
		} 
		
		return list;
	}
	
	private static void fetchAndAppend(String bucketName, String obj, String AOFfile) {
		String command = s3curl + " --id=vblob -- http://10.145.131.78:9070/" + bucketName + "/" + obj;
		
		File file =new File(AOFfile);
		 
		
		try {
			//if file does not exists, then create it
			if(!file.exists()) {
				file.createNewFile();
			}
			
			Process p = Runtime.getRuntime().exec(command);
			StreamGobbler redirect = new StreamGobbler(AOFfile, p.getInputStream(), "OUTPUT");
			redirect.start();
			p.waitFor();

		} catch (Exception e) {
			System.out.print("Unexpected exception: ");
			e.printStackTrace();
		}
		
	}
}
