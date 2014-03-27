package s3streampipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class S3StreamPipe {
	
	Queue<String> lineBuf = new ConcurrentLinkedQueue<String> ();

	final String bucketName;
	
	S3StreamPipe(String bucketname) {
		this.bucketName = bucketname;
	}
	
	public static void main(String[] args) {
		String bucketName = args[0];
		S3StreamPipe pipe = new S3StreamPipe(bucketName);
		
		/* the main loop to stream input into the lineBuf buffer */
		while (true) {
			
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in))) {
				
				while (true) {
					String line = br.readLine();
					if (line != null) {
						System.out.println("fengji test: " + line);
						pipe.lineBuf.add(line);
					}
				}
				
			} catch (IOException e) {
				System.out.print("Unexpected IOException ");
				e.printStackTrace();
			}
		}
	}

}
