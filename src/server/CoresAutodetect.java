package server;

public class CoresAutodetect {
	
	public static int detect() {
		Runtime runtime = Runtime.getRuntime();
        
        return runtime.availableProcessors();
	}

}
