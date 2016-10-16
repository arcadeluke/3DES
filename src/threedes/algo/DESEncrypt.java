package threedes.algo;

import threedes.app.config.BitwiseOperations;
import threedes.app.config.Constant;
import threedes.app.config.CryptConfig;
import threedes.app.config.ExecOption;
import threedes.util.ByteArrayUtil;
import threedes.util.FileIO;
import threedes.util.TextBinaryConverter;

public class DESEncrypt {

	public static byte [] ThreeDES_Encrypt(CryptConfig config)
	{
		//byte [] plainText = {0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef};
		byte [] plainText = FileIO.readFileToByteArray(config.getInputFilePath());
		String passwordString = FileIO.readFileToString(config.getKeyFilePath(), null);
		//System.out.println(passwordString);
		byte [] initKeys = TextBinaryConverter.stringToByteArray(passwordString);
		
		// three init keys
		byte [][] initKeyArray = ByteArrayUtil.separateByteArrayToBlock(initKeys, Constant.KEY_SIZE_IN_BYTE);
		
		assert(initKeyArray.length == 3);
		
		return DES_Crypt(initKeyArray[2], DES_Crypt(initKeyArray[1], DES_Crypt(initKeyArray[0], plainText, ExecOption.ENCRYPT), ExecOption.DECRYPT), ExecOption.ENCRYPT);
	}
	
	public static byte [] ThreeDES_Decrypt(CryptConfig config)
    {
        //byte [] cipherText = {0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef};
        byte [] cipherText = FileIO.readFileToByteArray(config.getInputFilePath());
	    //byte [] cipherText = FileIO.readFileToByteArray("./cipher.txt");
        String passwordString = FileIO.readFileToString(config.getKeyFilePath(), null);
        //System.out.println(passwordString);
        byte [] initKeys = TextBinaryConverter.stringToByteArray(passwordString);
        
        // three init keys
        byte [][] initKeyArray = ByteArrayUtil.separateByteArrayToBlock(initKeys, Constant.KEY_SIZE_IN_BYTE);
        
        assert(initKeyArray.length == 3);
        
        return DES_Crypt(initKeyArray[0], DES_Crypt(initKeyArray[1], DES_Crypt(initKeyArray[2], cipherText, ExecOption.DECRYPT), ExecOption.ENCRYPT), ExecOption.DECRYPT);
    }
	
	public static byte[] DES_Crypt(byte [] initKey, byte [] message, ExecOption option)
	{
	    //System.out.println("PT: " + ByteArrayUtil.convertByteArrayInBinaryString(message));
	    
		byte [][] blockedMessage = ByteArrayUtil.separateByteArrayToBlock(message, Constant.BLOCK_SIZE_IN_BYTE);
		byte [][] allKeys = DESKeyGenerator.createAllKeys(initKey);
		
		byte [][] blockedCryptedMessage = new byte[blockedMessage.length][];
		
		for(int i = 0; i < blockedMessage.length; ++i)
		{
			byte [] permIPArray = ByteArrayUtil.applyPermTable(blockedMessage[i], PermTables.sIPTable);
			
			byte [] L0 = new byte[permIPArray.length / 2];
			byte [] R0 = new byte[permIPArray.length / 2];
			
			ByteArrayUtil.separateByteArray(permIPArray, L0, R0);
			
			//System.out.println("BP: " + ByteArrayUtil.convertByteArrayInBinaryString(blockedMessage[i]));
			//System.out.println("PR: " + ByteArrayUtil.convertByteArrayInBinaryString(permIPArray));
			//System.out.println("L0: " + ByteArrayUtil.convertByteArrayInBinaryString(L0));
			//System.out.println("R0: " + ByteArrayUtil.convertByteArrayInBinaryString(R0));
			
			byte [] LPrev = L0.clone();
            byte [] RPrev = R0.clone();
            
            byte [] LNew = null;
            byte [] RNew = null;
			
			for (int j = 0; j < Constant.TOTAL_KEY_NUMBER; ++j)
			{
			    //System.out.println("j = " + (j+1));
			    
			    // Ln = Rn-1
			    // Rn = Ln-1 xor f(Rn-1, Kn)
			    
			    int keyIndex = j;
			    
			    if (option == ExecOption.DECRYPT)
			    {
			        keyIndex = Constant.TOTAL_KEY_NUMBER - j - 1;
			    }
			    
			    LNew = RPrev;
			    RNew = ByteArrayUtil.bitwiseByteArray(LPrev, f_function(RPrev, allKeys[keyIndex]), BitwiseOperations.XOR);
			    
			    LPrev = LNew;
			    RPrev = RNew;
			    
			    //System.out.println("Lj: " + ByteArrayUtil.convertByteArrayInBinaryString(LNew));
	            //System.out.println("Rj: " + ByteArrayUtil.convertByteArrayInBinaryString(RNew));
			}
			
			byte [] mergeResult = ByteArrayUtil.joinByteArray(RNew, Constant.HALFBLOCK_SIZE_IN_BIT, LNew, Constant.HALFBLOCK_SIZE_IN_BIT);
			
			byte [] finalResult = ByteArrayUtil.applyPermTable(mergeResult, PermTables.sIPInvertTable);
			
			blockedCryptedMessage[i] = finalResult;
		}
		
		return ByteArrayUtil.mergeBlockToByteArray(blockedCryptedMessage);
	}
	
	private static byte [] f_function(byte [] R, byte [] K)
	{
		if (R == null || K == null)
		{
			return null;
		}
		
		byte [] ERNew = ByteArrayUtil.applyPermTable(R, PermTables.sExpansionTable);
		byte [] sboxVal = ByteArrayUtil.bitwiseByteArray(ERNew, K, BitwiseOperations.XOR);
		
		//System.out.println("    ERNew: " + ByteArrayUtil.convertByteArrayInBinaryString(ERNew));
		//System.out.println("    SBox: " + ByteArrayUtil.convertByteArrayInBinaryString(sboxVal));
		
		byte [] sboxOutVal = new byte[R.length];
		
		// shall be 48 bits / 6 bits = 8
		int numSBox = sboxVal.length * Constant.BITS_OF_BYTE / Constant.S_BOX_INPUT_BITS;
		
		for (int i = 0; i < numSBox; i += 2)
		{
			byte b1 = ByteArrayUtil.fetchByteFromArray(sboxVal, i * Constant.S_BOX_INPUT_BITS, Constant.S_BOX_INPUT_BITS);
			byte b2 = ByteArrayUtil.fetchByteFromArray(sboxVal, (i + 1) * Constant.S_BOX_INPUT_BITS, Constant.S_BOX_INPUT_BITS);
			
			b1 = s_box(b1, i);
			b2 = s_box(b2, i + 1);
			
			byte twoBlock = (byte) ((b1 << Constant.BITS_OF_HALFBYTE) | b2);
			sboxOutVal[i / 2] = twoBlock;
		}
		
		//System.out.println("    SBoxF: " + ByteArrayUtil.convertByteArrayInBinaryString(sboxOutVal));
		
		byte [] retVal = ByteArrayUtil.applyPermTable(sboxOutVal, PermTables.sSBoxPermTable);
		//System.out.println("    SBoxR: " + ByteArrayUtil.convertByteArrayInBinaryString(retVal));
		
		return retVal;
	}
	
	private static byte s_box(byte origin, int s)
	{
		if ( s < 0 || s >= 8)
		{
			return 0x00;
		}
		
		// convert binary 00X0000Y to 000000XY
		int row = ((origin & 0x20) >>> 4) | (origin & 0x01);
		
		// convert binary 000ABCD0 to 0000ABCD
		int column = ((origin & 0x1e) >>> 1);
		
		int result = PermTables.sSTables[s][row * Constant.S_BOX_ROW_LENGTH + column];
		
		//System.out.println("    origin/s/row/column/result: " + origin + " " + s + " " + row + " " + column + " " + result);
		
		return (byte) result;
	}
}