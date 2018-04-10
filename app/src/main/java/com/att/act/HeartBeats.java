package com.att.act;

public class HeartBeats {


	//流水号
	public static String getrivercount(int num){



		String ss=Integer.toHexString(num);

		ss="0000".substring(0, 4 - ss.length()) + ss;

		return ss;
	}

	//16进制转ascii
	public static String toStringHex1(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
						i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "ASCII");
			s="0000".substring(0, 4 - s.length()) + s;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	public static String StringToAsciiString(String content) {
		String result = "";
		int max = content.length();
		for (int i = 0; i < max; i++) {
			char c = content.charAt(i);
			String b = Integer.toHexString(c);
			result = result + b;
		}
		result="0000".substring(0, 4 - result.length()) + result;
		return result;
	}













}
