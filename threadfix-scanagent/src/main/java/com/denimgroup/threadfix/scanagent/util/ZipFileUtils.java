package com.denimgroup.threadfix.scanagent.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileUtils {
	/**
	 * Prevent instantiation
	 */
	private ZipFileUtils() { }
	
	/**
	 * 	This method was copied/adapted from:
	 * 	http://www.java2s.com/Tutorial/Java/0180__File/unzipFileIntoDirectory.htm
	 * 
	 * TODO - This is kinda gross. Clean it up.
	 * 
	 * @param zipFilename Location of the ZIP file to unpack
	 * @param targetPath Base path where the ZIP file shoudl be unpacked
	 */
	public static void unzipFile(String zipFilename, String targetPath)
	throws java.io.IOException {
		
		ZipFile zipFile = new ZipFile(zipFilename);
		File jiniHomeParentDir = new File(targetPath);
		
		Enumeration<ZipEntry> files = (Enumeration<ZipEntry>) zipFile.entries();
		
		File f = null;
	    FileOutputStream fos = null;
	    
	    while (files.hasMoreElements()) {
	      try {
	        ZipEntry entry =  files.nextElement();
	        InputStream eis = zipFile.getInputStream(entry);
	        byte[] buffer = new byte[1024];
	        int bytesRead = 0;
	  
	        f = new File(jiniHomeParentDir.getAbsolutePath() + File.separator + entry.getName());
	        
	        if (entry.isDirectory()) {
	          f.mkdirs();
	          continue;
	        } else {
	          f.getParentFile().mkdirs();
	          f.createNewFile();
	        }
	        
	        fos = new FileOutputStream(f);
	  
	        while ((bytesRead = eis.read(buffer)) != -1) {
	          fos.write(buffer, 0, bytesRead);
	        }
	      } catch (IOException e) {
	        e.printStackTrace();
	        continue;
	      } finally {
	        if (fos != null) {
	          try {
	            fos.close();
	          } catch (IOException e) {
	            // ignore
	          }
	        }
	      }
	    }
	}
}