package threedes.algo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import threedes.app.config.Configuration;
import threedes.app.config.Constant;
import threedes.app.config.ExecOption;
import threedes.app.config.GenKeyConfig;
import threedes.util.TextBinaryConverter;

public class PasswordHasher {

    public static String generateSHA256(Configuration config)
    {
        if (config == null || config.getExecOption() != ExecOption.GENKEY)
        {
            return null;
        }
        
        GenKeyConfig genKeyConfig = (GenKeyConfig)config;
        
        MessageDigest digest;
        String result = "";
        
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(genKeyConfig.getPassword().getBytes(StandardCharsets.UTF_8));
            
            byte[] threeKey = new byte[Constant.THREEKEY_SIZE_IN_BIT / Constant.BITS_OF_BYTE];  // 168 / 8 = 21
            
            for (int i = 0; i < threeKey.length; ++i)
            {
                threeKey[i] = hash[i];
            }
            result = TextBinaryConverter.byteArrayToString(threeKey);
            
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        
        return result;
    }
    
}
