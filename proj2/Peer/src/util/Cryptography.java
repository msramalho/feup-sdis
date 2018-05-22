package src.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Cryptography {

	private static final String ALGO = "AES/CBC/PKCS5Padding";
	private static final int IV_SIZE = 16;
	
	public static byte[] encrypt(byte[] data, String key) throws Exception {

		//Generate IV
		byte[] iv = new byte[IV_SIZE];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);
		IvParameterSpec ivParam = new IvParameterSpec(iv);

		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, hashKey(key), ivParam);
		byte[] encrypted = c.doFinal(data, 0 , data.length);
		
		byte[] encryptedAndText = new byte[IV_SIZE + encrypted.length];
		System.arraycopy(iv, 0, encryptedAndText, 0, IV_SIZE);
		System.arraycopy(encrypted, 0, encryptedAndText, IV_SIZE, encrypted.length);
		
		return encryptedAndText;
	}
	
	public static byte[] encrypt(byte[] data, int length, String key) throws Exception {

		//Generate IV
		byte[] iv = new byte[IV_SIZE];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);
		IvParameterSpec ivParam = new IvParameterSpec(iv);

		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, hashKey(key), ivParam);
		byte[] encrypted = c.doFinal(data, 0, length);
		
		byte[] encryptedAndText = new byte[IV_SIZE + encrypted.length];
		System.arraycopy(iv, 0, encryptedAndText, 0, IV_SIZE);
		System.arraycopy(encrypted, 0, encryptedAndText, IV_SIZE, encrypted.length);
		
		return encryptedAndText;
	}

	public static byte[] decrypt(byte[] encryptedData, String key) throws Exception {
		
		//Extract IV
		byte[] iv = new byte[IV_SIZE];
		System.arraycopy(encryptedData, 0, iv, 0, iv.length);
		IvParameterSpec ivParam = new IvParameterSpec(iv);
		
		//Extract encrypted part
		int encryptedSize = encryptedData.length - IV_SIZE;
		byte[] encryptedBytes = new byte[encryptedSize];
		System.arraycopy(encryptedData, IV_SIZE, encryptedBytes, 0, encryptedSize);
		
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, hashKey(key), ivParam);
		return c.doFinal(encryptedBytes, 0, encryptedBytes.length);
	}
	
	public static byte[] decrypt(byte[] encryptedData, int length, String key) throws Exception {
	
		//Extract IV
		byte[] iv = new byte[IV_SIZE];
		System.arraycopy(encryptedData, 0, iv, 0, iv.length);
		IvParameterSpec ivParam = new IvParameterSpec(iv);
		
		//Extract encrypted part
		int encryptedSize = encryptedData.length - IV_SIZE;
		byte[] encryptedBytes = new byte[encryptedSize];
		System.arraycopy(encryptedData, IV_SIZE, encryptedBytes, 0, encryptedSize);
		
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, hashKey(key), ivParam);
		return c.doFinal(encryptedBytes, 0, length);
	}

	private static Key hashKey(String key) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.update(key.getBytes(StandardCharsets.UTF_8));
		byte[] keyBytes = new byte[16];
		System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
		return new SecretKeySpec(keyBytes, "AES");
	}

	
}
