package cn.edu.seu.ise.common.helper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * A helper class used to covert primitives to hex format. 
 * 
 * @author Dong Qiu
 *
 */
public class UnicodeHelper  {

	/**
	 * Converts byte to the hex format. 
	 * 
	 * @param b  the byte
	 * @return  the corresponding hex format
	 */
   static public String byteToHex(byte b) {
      // Returns hex String representation of byte b
	  char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
			   '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
      char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
      return new String(array);
   }

   /**
    * Converts char to the hex format. 
    * 
    * @param c  the char
    * @return  the corresponding hex format
    */
   static public String charToHex(char c) {
      // Returns hex String representation of char c
      byte hi = (byte) (c >>> 8);
      byte lo = (byte) (c & 0xff);
      return byteToHex(hi) + byteToHex(lo);
   }
   
   /**
    * Converts char array to the hex format. 
    * 
    * @param chars  the char array
    * @return  the corresponding hex format
    * 
    * @throws UnsupportedEncodingException
    */
   static public String charsToHex(char[] chars) throws UnsupportedEncodingException {
	   byte[] bytes = new String(chars).getBytes(Charset.forName("UTF-8"));
	   
	   StringBuilder builder = new StringBuilder();
	   if(chars == null || chars.length == 0) {
		   return builder.toString();
	   }
	   for(byte b : bytes) {
		   builder.append("0x").append(byteToHex(b));
	   }
	   return builder.toString();
   }
}
