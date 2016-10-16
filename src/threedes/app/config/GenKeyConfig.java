package threedes.app.config;

public class GenKeyConfig extends Configuration {

	public GenKeyConfig(String iInputFilePath, String iOutputFilePath, String iKeyFilePath, String iPassword,
			ExecOption iExecOption, CryptMode iCryptMode) 
	{
		super(iInputFilePath, iOutputFilePath, iKeyFilePath, iPassword, iExecOption, iCryptMode);

	}
	
	public String getPassword()
	{
		return iPassword;
	}
	
	public String getOutputFilePath()
	{
		return iOutputFilePath;
	}

}
