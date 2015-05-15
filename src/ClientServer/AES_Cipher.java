package ClientServer;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@SuppressWarnings("unused")
public class AES_Cipher {

	public static void main(String args[]) throws Exception {
         String text = "Hello World";
         String key = "AXQ12745DRT94545"; // 128 bit key
         
         // Create key and cipher
         Key AESKey = new SecretKeySpec(key.getBytes(), "AES");
         Cipher cipher = Cipher.getInstance("AES");
         
         // Encryption
         cipher.init(Cipher.ENCRYPT_MODE, AESKey);
         byte[] encrypted = cipher.doFinal(text.getBytes());
         String echo_encrypted = new String(encrypted);
         
         // Decryption
         cipher.init(Cipher.DECRYPT_MODE, AESKey);
         String decrypted = new String(cipher.doFinal(encrypted));
         String echo_decrypted = new String(decrypted);
         
         // Display Original text, encryption and decryption
         System.out.println("Plain Text: " + new String(text));
         System.out.println("Encrypted Text: " + new String(echo_encrypted));
         System.out.println("Decrypted Text: " + new String(echo_decrypted));
    }

}
