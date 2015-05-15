package ClientServer;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES_Cipher {

	private String key = "AXQ12745DRT94545"; // 128 bit key

	// Create key
	private Key AESKey = new SecretKeySpec(key.getBytes(), "AES");

	String encrypt(String text) throws Exception {		// Encryption
		
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, AESKey);
		byte[] encrypted = cipher.doFinal(text.getBytes());
		return new String(encrypted);
	}

	String decrypt(String text) throws Exception {		// Decryption

		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, AESKey);
		String decrypted = new String(cipher.doFinal(text.getBytes()));
		return new  String(decrypted);
	}


}