package utils;

/**
 * Created by IntelliJ IDEA.
 * User: Jean-Baptiste
 * Date: 4 d�c. 2010
 * Time: 23:30:40
 * To change this template use File | Settings | File Templates.
 */

import java.util.*;
import java.io.*;
import java.security.*;
public class CypherUtil {
  public static String md5Hex (String message) {
      return cypherHex("MD5", message);
  }
  public static String sha256Hex (String message) {
      return cypherHex("SHA-256", message);
  }

  static String hex(byte[] array) {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < array.length; ++i) {
	  sb.append(Integer.toHexString((array[i]
	      & 0xFF) | 0x100).substring(1,3));
      }
      return sb.toString();
  }
  static String cypherHex (String algorithm, String message) {
      try {
	  MessageDigest md =
	      MessageDigest.getInstance(algorithm);
	  return hex (md.digest(message.getBytes("CP1252")));
      } catch (NoSuchAlgorithmException e) {
      } catch (UnsupportedEncodingException e) {
      }
      return null;
  }
}

