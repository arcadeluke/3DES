package threedes.app.config;

public abstract class Configuration {
	
	protected Configuration(String iInputFilePath, String iOutputFilePath, String iKeyFilePath, String iPassword,
			ExecOption iExecOption, CryptMode iCryptMode) 
	{
		this.iInputFilePath = iInputFilePath;
		this.iOutputFilePath = iOutputFilePath;
		this.iKeyFilePath = iKeyFilePath;
		this.iPassword = iPassword;
		this.iExecOption = iExecOption;
		this.iCryptMode = iCryptMode;
	}
	
	public ExecOption getExecOption() 
	{
		return iExecOption;
	}

	protected String iInputFilePath;
	protected String iOutputFilePath;
	protected String iKeyFilePath;
	protected String iPassword;
	protected ExecOption iExecOption;
	protected CryptMode iCryptMode;
}
