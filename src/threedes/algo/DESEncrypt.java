package threedes.algo;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import threedes.app.config.BitwiseOperations;
import threedes.app.config.Constant;
import threedes.app.config.CryptConfig;
import threedes.app.config.CryptMode;
import threedes.app.config.ExecOption;
import threedes.util.ByteArrayUtil;
import threedes.util.FileIO;
import threedes.util.TextBinaryConverter;

public class DESEncrypt {

    public static byte [] ThreeDES_Encrypt(CryptConfig config)
    {
        byte [] plainText = FileIO.readFileToByteArray(config.getInputFilePath());
        
        String passwordString = FileIO.readFileToString(config.getKeyFilePath(), null);
        byte [] initKeys = TextBinaryConverter.stringToByteArray(passwordString);
        
        // three init keys
        byte [][] initKeyArray = ByteArrayUtil.separateByteArrayToBlock(initKeys, Constant.ORIGIN_THREEKEY_SIZE_IN_BYTE);
        
        assert(initKeyArray.length == 3);
        
        CryptMode mode = config.getCryptMode();
        
        byte [] iv = null;
        
        if (mode == CryptMode.CBC)
        {
            iv = generateInitialVector(Constant.BLOCK_SIZE_IN_BYTE);
        }
        else if (mode == CryptMode.CTR)
        {
            iv = generateInitialVector(Constant.BLOCK_SIZE_IN_BYTE / 2);
        }
        
        int extraByte = plainText.length % Constant.BLOCK_SIZE_IN_BYTE;
        
        plainText = ByteArrayUtil.appendByteArray(plainText, iPaddingTable[extraByte]);
        
        byte [] result = null;
        
        if (mode == CryptMode.CTR)
        {
            result = DES_Crypt(initKeyArray[2], DES_Crypt(initKeyArray[1], DES_Crypt(initKeyArray[0], plainText, ExecOption.ENCRYPT, mode, iv), ExecOption.ENCRYPT, mode, iv), ExecOption.ENCRYPT, mode, iv);
        }
        else
        {
            result = DES_Crypt(initKeyArray[2], DES_Crypt(initKeyArray[1], DES_Crypt(initKeyArray[0], plainText, ExecOption.ENCRYPT, mode, iv), ExecOption.DECRYPT, mode, iv), ExecOption.ENCRYPT, mode, iv);
        }
        
        return ByteArrayUtil.appendByteArray(result, iv);
    }
    
    public static byte [] ThreeDES_Decrypt(CryptConfig config)
    {
        byte [] cipherText = FileIO.readFileToByteArray(config.getInputFilePath());
        
        String passwordString = FileIO.readFileToString(config.getKeyFilePath(), null);
        byte [] initKeys = TextBinaryConverter.stringToByteArray(passwordString);
        
        // three init keys
        byte [][] initKeyArray = ByteArrayUtil.separateByteArrayToBlock(initKeys, Constant.ORIGIN_THREEKEY_SIZE_IN_BYTE);
        
        assert(initKeyArray.length == 3);
        
        CryptMode mode = config.getCryptMode();
        
        byte [] iv = null;
        
        if (mode == CryptMode.CBC)
        {
            iv = ByteArrayUtil.subByteArray(cipherText, cipherText.length - Constant.BLOCK_SIZE_IN_BYTE, Constant.BLOCK_SIZE_IN_BYTE);
            cipherText = ByteArrayUtil.subByteArray(cipherText, 0, cipherText.length - Constant.BLOCK_SIZE_IN_BYTE);
        }
        else if (mode == CryptMode.CTR)
        {
            iv = ByteArrayUtil.subByteArray(cipherText, cipherText.length - Constant.BLOCK_SIZE_IN_BYTE / 2, Constant.BLOCK_SIZE_IN_BYTE / 2);
            cipherText = ByteArrayUtil.subByteArray(cipherText, 0, cipherText.length - Constant.BLOCK_SIZE_IN_BYTE / 2);
        }
        
        byte [] result = null;
        
        if (mode == CryptMode.CTR)
        {
            result = DES_Crypt(initKeyArray[0], DES_Crypt(initKeyArray[1], DES_Crypt(initKeyArray[2], cipherText, ExecOption.ENCRYPT, mode, iv), ExecOption.ENCRYPT, mode, iv), ExecOption.ENCRYPT, mode, iv);
        }
        else
        {
            result = DES_Crypt(initKeyArray[0], DES_Crypt(initKeyArray[1], DES_Crypt(initKeyArray[2], cipherText, ExecOption.DECRYPT, mode, iv), ExecOption.ENCRYPT, mode, iv), ExecOption.DECRYPT, mode, iv);
        }
        
        int lastByte = result[result.length - 1];
        
        return ByteArrayUtil.subByteArray(result, 0, result.length - lastByte);
    }
    
    public static byte[] DES_Crypt(byte [] initKey, byte [] message, ExecOption option, CryptMode mode, byte [] originIV)
    {        
        byte [] iv = null;
        byte [][] blockedMessage = ByteArrayUtil.separateByteArrayToBlock(message, Constant.BLOCK_SIZE_IN_BYTE);
        byte [][] allKeys = DESKeyGenerator.createAllKeys(initKey);
        byte [][] blockedCryptedMessage = new byte[blockedMessage.length][];
        
        if (mode == CryptMode.CBC || mode == CryptMode.CTR)
        {
            iv = originIV.clone();
            resetCounter();
        }
        
        for(int i = 0; i < blockedMessage.length; ++i)
        {
            byte [] messageAboutCrypt = null;

            if (mode == CryptMode.ECB)
            {
                messageAboutCrypt = blockedMessage[i];
            }
            else if (mode == CryptMode.CBC)
            {
                if (option == ExecOption.ENCRYPT)
                {
                    messageAboutCrypt = ByteArrayUtil.bitwiseByteArray(iv, blockedMessage[i], BitwiseOperations.XOR);
                }
                else if (option == ExecOption.DECRYPT)
                {
                    messageAboutCrypt = blockedMessage[i];
                    if (i != 0)
                    {
                        iv = blockedMessage[i-1].clone();
                    }
                }
            }
            else if (mode == CryptMode.CTR)
            {
                byte [] counter = counterGenerator();
                messageAboutCrypt = ByteArrayUtil.appendByteArray(iv, counter);
            }

            byte [] messageDoneCrypt = blockCryptBox(messageAboutCrypt, allKeys, option);
            
            byte [] finalResult = null;
            
            
            if (mode == CryptMode.ECB)
            {
                finalResult = messageDoneCrypt;
            }
            else if (mode == CryptMode.CBC)
            {
                if (option == ExecOption.ENCRYPT)
                {
                    iv = messageDoneCrypt.clone();
                    finalResult = messageDoneCrypt;
                }
                else if (option == ExecOption.DECRYPT)
                {
                    finalResult = ByteArrayUtil.bitwiseByteArray(iv, messageDoneCrypt, BitwiseOperations.XOR);
                }
            }
            else if (mode == CryptMode.CTR)
            {
                finalResult = ByteArrayUtil.bitwiseByteArray(blockedMessage[i], messageDoneCrypt, BitwiseOperations.XOR);;
            }

            blockedCryptedMessage[i] = finalResult;
        }

        return ByteArrayUtil.mergeBlockToByteArray(blockedCryptedMessage);
    }
    
    private static byte [] blockCryptBox(byte [] message, byte [][] allKeys, ExecOption option)
    {
        byte [] permIPArray = ByteArrayUtil.applyPermTable(message, PermTables.sIPTable);
        
        byte [] L0 = new byte[permIPArray.length / 2];
        byte [] R0 = new byte[permIPArray.length / 2];
        
        ByteArrayUtil.separateByteArray(permIPArray, L0, R0);
        
        byte [] LPrev = L0.clone();
        byte [] RPrev = R0.clone();
        
        byte [] LNew = null;
        byte [] RNew = null;
        
        for (int j = 0; j < Constant.TOTAL_KEY_NUMBER; ++j)
        {
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
        }
        
        byte [] mergeResult = ByteArrayUtil.joinByteArray(RNew, Constant.HALFBLOCK_SIZE_IN_BIT, LNew, Constant.HALFBLOCK_SIZE_IN_BIT);
        
        byte [] finalResult = ByteArrayUtil.applyPermTable(mergeResult, PermTables.sIPInvertTable);
        
        return finalResult;
    }
    
    private static byte [] f_function(byte [] R, byte [] K)
    {
        if (R == null || K == null)
        {
            return null;
        }
        
        byte [] ERNew = ByteArrayUtil.applyPermTable(R, PermTables.sExpansionTable);
        byte [] sboxVal = ByteArrayUtil.bitwiseByteArray(ERNew, K, BitwiseOperations.XOR);
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
        
        byte [] retVal = ByteArrayUtil.applyPermTable(sboxOutVal, PermTables.sSBoxPermTable);
        
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
        
        return (byte) result;
    }
    
    private static byte [] generateInitialVector(int nBytes)
    {
        SecureRandom random = null;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte iv[] = new byte[nBytes];
        random.nextBytes(iv);

        return iv;
    }
    
    private static int sCounter = 0;
    
    private static byte [] counterGenerator()
    {
        byte [] counter = {0x00, 0x00, 0x00, 0x00};
        counter[0] = (byte) ((sCounter & 0xff000000) >>> (3 * Constant.BITS_OF_BYTE));
        counter[1] = (byte) ((sCounter & 0x00ff0000) >>> (2 * Constant.BITS_OF_BYTE));
        counter[2] = (byte) ((sCounter & 0x0000ff00) >>> (1 * Constant.BITS_OF_BYTE));
        counter[3] = (byte) (sCounter & 0x000000ff);
        ++sCounter;
        return counter;
    }
    
    private static void resetCounter()
    {
        sCounter = 0;
    }
    
    private static byte [][] iPaddingTable = 
        {
            {0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08},
            {0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07},
            {0x06, 0x06, 0x06, 0x06, 0x06, 0x06},
            {0x05, 0x05, 0x05, 0x05, 0x05},
            {0x04, 0x04, 0x04, 0x04},
            {0x03, 0x03, 0x03},
            {0x02, 0x02},
            {0x01}
        };
}
