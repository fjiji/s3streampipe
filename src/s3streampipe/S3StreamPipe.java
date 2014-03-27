package s3streampipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

public class S3StreamPipe {
	
	Queue<String> lineBuf = new LinkedList<String> ();

	final String bucketName;
	
	S3StreamPipe(String bucketname) {
		this.bucketName = bucketname;
	}
	
	public static void main(String[] args) {
		assert(args.length > 1);
		String bucketName = args[1];
		S3StreamPipe pipe = new S3StreamPipe(bucketName);
		
		/* the main loop to stream input into the lineBuf buffer */
		while (true) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in))) {
				String line = br.readLine();
				
				pipe.lineBuf.add(line);

			} catch (IOException e) {
				System.out.print("Unexpected IOException ");
				e.printStackTrace();
			}
		}
	}

}
