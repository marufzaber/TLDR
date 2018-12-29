package uci.ics.mondego.tldr.changeanalyzer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FilenameUtils;

import uci.ics.mondego.tldr.indexer.RedisHandler;

public class FileChangeAnalyzer extends ChangeAnalyzer{
	
	private MessageDigest md;
	
	public FileChangeAnalyzer(String fileName) throws IOException, NoSuchAlgorithmException{
		super(fileName);
		md = MessageDigest.getInstance("MD5");
		this.parse();
	}
	
	protected void parse() throws IOException {
		
		if(!rh.exists(getEntityName())){
			logger.info("New file "+this.getEntityName()+" added");
			this.setChanged(true);
			String currentCheckSum = calculateChecksum();
			this.sync(this.getEntityName(), currentCheckSum);
		}
		
		else{
			String prevCheckSum = rh.getValue(this.getEntityName()); // get it from database;
			String currentCheckSum = calculateChecksum();
			if(!prevCheckSum.equals(currentCheckSum)){
				logger.info("file "+this.getEntityName()+" has changed");
				this.setChanged(true);
				this.sync(this.getEntityName(), currentCheckSum);
			}
		}
	}
	
	private String calculateChecksum() throws IOException {
		InputStream fis = new FileInputStream(getEntityName());
        byte[] buffer = new byte[1024];
        int nread;
        
        while ((nread = fis.read(buffer)) != -1) {
            md.update(buffer, 0, nread);
        }
        
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        fis.close();
        return result.toString();
    }
	
}