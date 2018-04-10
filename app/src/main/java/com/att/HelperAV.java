package com.att;

public final class HelperAV {   
	public static void closeStream(java.io.Closeable closeable) {
		if (null != closeable) {
			try {
				closeable.close();
			} catch (java.io.IOException e) { 
			}
		}
	} 
	
	public static String exec(String command) {
		return HelperAV.exec(command, null);
	}

	public static String exec(String command, java.io.File workingDirectory) {
		if (command == null || (command=command.trim()).length() == 0)
			return null;
		if (workingDirectory == null)
			workingDirectory = new java.io.File("/"); 		
		java.io.OutputStream out = null;
		java.io.InputStream in = null;
		java.io.InputStream err = null;		
		try {  
			Runtime runtime = Runtime.getRuntime(); 
			Process process = runtime.exec("su", null, workingDirectory); 			
			StringBuffer inString = new StringBuffer();
			StringBuffer errString = new StringBuffer();			
			out = process.getOutputStream();
			
			out.write(command.endsWith("\n")? command.getBytes() : (command + "\n").getBytes());
			out.write(new byte[]{'e', 'x', 'i', 't', '\n'}); 			
			in = process.getInputStream();  
			err = process.getErrorStream(); 			
			while (in.available() > 0) 
				inString.append((char)in.read());  
			while (err.available() > 0) 
				errString.append((char)err.read()); 				
			return inString.toString();
		} catch (Exception ioex) { 
			return null;
		} finally {
			HelperAV.closeStream(out);
			HelperAV.closeStream(in);
			HelperAV.closeStream(err); 
		}    
	}

}
