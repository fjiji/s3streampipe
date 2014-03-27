package s3streampipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class S3StreamPipe extends TimerTask {
	
	private final Queue<String> lineBuf = new ConcurrentLinkedQueue<String> ();
	
	public void addLine(String str) {
		lineBuf.add(str);
	}
	
	private final FileDumper dumper;
	
	S3StreamPipe(FileDumper dumper) {
		this.dumper = dumper;
	}
	
	public void run() {
		//System.out.println(">>>test fengji!!!");
		String str;
		StringBuilder builder = new StringBuilder();
		while ((str = lineBuf.poll()) != null) {
			builder.append(str);
			builder.append("\n");
		}
		String content = builder.toString();
		if (content.length() != 0) {
			System.out.println("fengji test: " + content);
			dumper.dump(content.getBytes());
		}
		//System.out.println("<<<test fengji!!!");
	}
	
	public static void main(String[] args) {
		String bucketName = args[0];
		FileDumper dumper = new FileDumper(bucketName);
		S3StreamPipe pipe = new S3StreamPipe(dumper);
		
		Timer timer = new Timer();
		timer.schedule(pipe, 2000, 2000); // 2 seconds delay and 2 second period
		
		/* the main loop to stream input into the lineBuf buffer */
		while (true) {
			
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in))) {
				
				while (true) {
					String line = br.readLine();
					if (line != null) {
						
						pipe.addLine(line);
					}
				}
				
			} catch (IOException e) {
				System.out.print("Unexpected IOException ");
				e.printStackTrace();
			}
		}
	}

}
