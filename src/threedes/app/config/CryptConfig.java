package threedes.app.config;

public class CryptConfig extends Configuration {

    public CryptConfig(String iInputFilePath, String iOutputFilePath, String iKeyFilePath, String iPassword,
            ExecOption iExecOption, CryptMode iCryptMode) 
    {
        super(iInputFilePath, iOutputFilePath, iKeyFilePath, iPassword, iExecOption, iCryptMode);
    }
    
    public String getInputFilePath()
    {
        return iInputFilePath;
    }
    
    public String getOutputFilePath()
    {
        return iOutputFilePath;
    }
    
    public String getKeyFilePath()
    {
        return iKeyFilePath;
    }
    
    public CryptMode getCryptMode() 
    {
        return iCryptMode;
    }
}
