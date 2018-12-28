package uci.ics.mondego.tldr.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class SourceFile implements Entities{
 
	private String currentCheckSum;
	private String prevCheckSum;
	
	private String filePath;
	private String fileName;
	
	public SourceFile(){
		
	}
	
	public SourceFile(String name){
		filePath = name;
		fileName = getNameFromAbsolutePath(filePath);
		currentCheckSum = calculateCheckSum();
		prevCheckSum = currentCheckSum;
	}
	
	public String getName(){
		return fileName;
	}
	
	public String getPath(){
		return filePath;
	}
	
	public String getCurrentCheckSum(){
		return calculateCheckSum();
	}
	
	public String getPreviousCheckSum(){
		return prevCheckSum;
	}
	
	public String calculateCheckSum(){
		String chsm = null;
		 try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			chsm = checksum(filePath, md);
			
			prevCheckSum = !chsm.equals(currentCheckSum) ? currentCheckSum : prevCheckSum;
			
			currentCheckSum = !chsm.equals(currentCheckSum)  ? chsm : currentCheckSum;
			
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 catch (IOException e){
			 e.printStackTrace();
		 } 
		 return chsm;
	}
	
	public boolean hasChanged(){
		boolean changed = false;
		String newCheckSum = calculateCheckSum();
		changed = !newCheckSum.equals(currentCheckSum);
		prevCheckSum = changed ? currentCheckSum : prevCheckSum;
		currentCheckSum = changed ? newCheckSum : currentCheckSum;
		return changed;
	}
	
	private static String checksum(String filepath, MessageDigest md) throws IOException {
		
		
		//System.out.println("here : "+filepath);
		InputStream fis = new FileInputStream(filepath);
        byte[] buffer = new byte[1024];
        int nread;
        
        while ((nread = fis.read(buffer)) != -1) {
            md.update(buffer, 0, nread);
        }
        
        StringBuilder result = new StringBuilder();
        
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        
        return result.toString();
    }
	
	private String getNameFromAbsolutePath(String path){
		return path.lastIndexOf('/') == -1 ? path : path.substring(path.lastIndexOf('/') + 1);
	}
	
}
