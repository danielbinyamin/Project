package program;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import GUI.mainWindowUI;


public abstract class dirListener implements Runnable {

	private static boolean exitFlag;
	private String wiggleDir;



	public dirListener(String wiggleDir) {
		this.wiggleDir = new String(wiggleDir);
		exitFlag = true;
	}

	private static boolean dirWatch(String dir) throws IOException, InterruptedException {

		Path path = Paths.get(dir+"\\");
		WatchService watchService = path.getFileSystem().newWatchService();

		path.register(watchService,
				StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_MODIFY,
				StandardWatchEventKinds.ENTRY_DELETE);

		while(exitFlag) {
			WatchKey watchKey = null;
			while (exitFlag && watchKey == null) { //poll watchkey's until detect change
				watchKey = watchService.poll();
			}
			if (watchKey == null) { //return false if exitFlag is false
				return false;
			}
			
			//Processes change that was detected by watchService
			for (WatchEvent<?> event : watchKey.pollEvents()) {
				WatchEvent.Kind<?> k = event.kind();
				if(k==StandardWatchEventKinds.ENTRY_CREATE) 
					return true;			
				else if(k==StandardWatchEventKinds.ENTRY_MODIFY) 
					return true;
				else if(k==StandardWatchEventKinds.ENTRY_DELETE) 
					return true;
				else if(!exitFlag) {
					break;
				}
			}

			//clean and reset the watchKey
			if(!watchKey.reset()) {
				watchKey.cancel();
				watchService.close();
			}
		}
		return false;
	}


	public abstract void action();

	@Override
	public void run() {

		try {

			boolean bChangeDetected = dirWatch(wiggleDir);
			while(exitFlag) {
				if(bChangeDetected) 
					action();
				bChangeDetected=dirWatch(wiggleDir);
			}
		}
		catch (IOException e) {
			System.out.println("Error at dirWatch thread");
			e.printStackTrace();
		} catch (InterruptedException e) {
			//do nothing
		}
	}
	
	public void stopListen() {
		this.exitFlag = false;
	}

	public String getWiggleDir() {
		return wiggleDir;
	}
	
	

}
