package threedes.app.app;

import threedes.algo.DESEncrypt;
import threedes.algo.PasswordHasher;
import threedes.app.config.Configuration;
import threedes.app.config.CryptConfig;
import threedes.app.config.CryptMode;
import threedes.app.config.ExecOption;
import threedes.app.config.GenKeyConfig;
import threedes.util.FileIO;
import threedes.util.TextBinaryConverter;

public class Main {
    
    private static Configuration parseArgument(String [] args)
    {
        if (args.length != 3 && args.length != 5)
        {
            return null;
        }
        
        String option = args[0];
        
        if ("genkey".equals(option.toLowerCase()))
        {
            return new GenKeyConfig("", args[2], "", args[1], ExecOption.GENKEY, CryptMode.CBC);
        }
        else
        {
            ExecOption execOption = ExecOption.NONE;
            CryptMode cryptMode = CryptMode.CBC;
            
            if ("encrypt".equals(option.toLowerCase()))
            {
                execOption = ExecOption.ENCRYPT;
            }
            else if ("decrypt".equals(option.toLowerCase()))
            {
                execOption = ExecOption.DECRYPT;
            }
            else
            {
                return null;
            }
            
            if ("ECB".equals(args[4].toUpperCase()))
            {
                cryptMode = CryptMode.ECB;
            }
            else if ("CBC".equals(args[4].toUpperCase()))
            {
                cryptMode = CryptMode.CBC;
            }
            else if ("CTR".equals(args[4].toUpperCase()))
            {
                cryptMode = CryptMode.CTR;
            }
            
            return new CryptConfig(args[1], args[3], args[2], "", execOption, cryptMode);
        }
    }

    public static void main(String[] args) {
        // genkey "password" ./out_hash.txt
        // decrypt ./cipher.txt ./out_hash.txt ./restore_plain.txt ECB
        // encrypt ./plain.txt ./out_hash.txt ./cipher.txt ECB
        // decrypt ./cipher_cbc.txt ./out_hash.txt ./restore_plain_cbc.txt CBC
        // encrypt ./plain.txt ./out_hash.txt ./cipher_cbc.txt CBC
        // decrypt ./cipher_ctr.txt ./out_hash.txt ./restore_plain_ctr.txt CTR
        // encrypt ./plain.txt ./out_hash.txt ./cipher_ctr.txt CTR
        
        Configuration config = parseArgument(args);
        
        if (config.getExecOption() == ExecOption.GENKEY)
        {
            long currentTime = System.currentTimeMillis();
            FileIO.writeTextFile(((GenKeyConfig)config).getOutputFilePath(), PasswordHasher.generateSHA256(config));
            long genKeyDoneTime = System.currentTimeMillis();
            
            float cryptDurationInSec = ((float)(genKeyDoneTime - currentTime)/1000.0f);
            System.out.println("Key written. GenKey: " + cryptDurationInSec + " secs.");
        }
        else if (config.getExecOption() == ExecOption.ENCRYPT)
        {
            long currentTime = System.currentTimeMillis();
            byte [] cipher = DESEncrypt.ThreeDES_Encrypt((CryptConfig)config);
            long cryptDoneTime = System.currentTimeMillis();
            FileIO.writeBinaryFile(((CryptConfig)config).getOutputFilePath(), cipher);
            long fileDoneTime = System.currentTimeMillis();
            
            float cryptDurationInSec = ((float)(cryptDoneTime - currentTime)/1000.0f);
            float fileDurationInSec = ((float)(fileDoneTime - cryptDoneTime)/1000.0f);
            System.out.println("Cipher written. Crypt: " + cryptDurationInSec + " secs, writeDisk: " + fileDurationInSec + " secs." );
        }
        else if (config.getExecOption() == ExecOption.DECRYPT)
        {
            long currentTime = System.currentTimeMillis();
            byte [] plain = DESEncrypt.ThreeDES_Decrypt((CryptConfig)config);
            long cryptDoneTime = System.currentTimeMillis();
            FileIO.writeBinaryFile(((CryptConfig)config).getOutputFilePath(), plain);
            long fileDoneTime = System.currentTimeMillis();
            
            float cryptDurationInSec = ((float)(cryptDoneTime - currentTime)/1000.0f);
            float fileDurationInSec = ((float)(fileDoneTime - cryptDoneTime)/1000.0f);
            System.out.println("Plain written. Crypt: " + cryptDurationInSec + " secs, writeDisk: " + fileDurationInSec + " secs." );
        }
        else
        {
            System.out.println("OOPS!!");
        }
        

    }

}
