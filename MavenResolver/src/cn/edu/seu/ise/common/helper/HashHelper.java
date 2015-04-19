package cn.edu.seu.ise.common.helper;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class used to generate hash. 
 * 
 * @author Dong Qiu
 *
 */
public class HashHelper {
	
	/** Logger */
	private static Logger logger = LoggerFactory.getLogger(HashHelper.class.getName());
	
	/** Name of SHA-512 algorithm */
	private static String SHA_512_ALG = "SHA-512";
	
	/** Static message digest */
	private static MessageDigest md;
	
	/** Initial message digest */
	static {
		try {
			md = MessageDigest.getInstance(SHA_512_ALG);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Fail to initialize {} hash function. \n{}",SHA_512_ALG ,e);
		}
	}
	
	/**
	 * Calculates the hash for the content of file. 
	 * 
	 * @param file  the file to be calculated 
	 * @return  the hash value of the content
	 * @throws IOException 
	 */
	public static String getFileHash(@NonNull File file) throws IOException {
		checkNotNull(md);
		FileInputStream fin = new FileInputStream(file);
		byte data[] = new byte[(int) file.length()];
		fin.read(data);
		fin.close();
		return new BigInteger(1, md.digest(data)).toString(16);
	}
	
	/**
	 * Calculates the hash for the string. 
	 * 
	 * @param data  the string to be calculated
	 * @return  the hash of the string 
	 */
	public static String getHash(@NonNull String data) {
		checkNotNull(md);
		return new BigInteger(1, md.digest(data.getBytes())).toString(16);
	}
}
