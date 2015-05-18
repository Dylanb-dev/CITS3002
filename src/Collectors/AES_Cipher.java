package Collectors;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES_Cipher {

	private static String key = "AXQ12745DRT94545"; // 128 bit key

	// Create key
	private static Key AESKey = new SecretKeySpec(key.getBytes(), "AES");

	
	public static String encrypt(String text) throws Exception {		// Encryption
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, AESKey);
		byte[] encrypted = cipher.doFinal(text.getBytes());
		return new String(encrypted);
	}

	public static String decrypt(String text) throws Exception {		// Decryption

		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, AESKey);
		String decrypted = new String(cipher.doFinal(text.getBytes()));
		return new  String(decrypted);
	}
	public static void main(String args[]) throws Exception {
        String text = "Hello World";
        System.out.println(text);
        String encypted = encrypt(text);
        System.out.println(encypted);
        String decypted = decrypt(encypted);
        System.out.println(decypted);

        
	}
}