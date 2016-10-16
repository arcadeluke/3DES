package threedes.util;

import threedes.app.config.Constant;

public class TextBinaryConverter {

	public static String byteArrayToString(byte [] byteArray)
	{
		if (byteArray == null || byteArray.length == 0)
		{
			return "";
		}
		
		StringBuffer ret = new StringBuffer();
		
		for (byte b : byteArray)
		{
			int firstHalf = (b & 0xf0) >>> Constant.BITS_OF_HALFBYTE;
			int secondHalf = (b & 0x0f);
			ret.append(sHexNum.charAt(firstHalf));
			ret.append(sHexNum.charAt(secondHalf));
		}
		
		return ret.toString();
	}
	
	public static byte [] stringToByteArray(String str)
	{
		if (str == null || (str.length() % 2 != 0))
		{
			return null;
		}
		
		byte [] retArray = new byte[str.length() / 2];
		
		for (int i = 0; i + 1 < str.length(); i += 2)
		{
			char c1 = str.toUpperCase().charAt(i);
			char c2 = str.toUpperCase().charAt(i+1);
			int index1 = sHexNum.indexOf(c1);
			int index2 = sHexNum.indexOf(c2);
			
			byte b = 0;
			b |= (index1 << Constant.BITS_OF_HALFBYTE);
			b |= index2;
			
			retArray[i/2] = b;
		}
		
		return retArray;
	}
	
	final protected static String sHexNum = "0123456789ABCDEF";
	
}
