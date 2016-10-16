You will be implementing the 3DES encryption algorithm using keying option 1 with ECB, CBC, and CTR modes as options.   

Your program should perform the following tasks and take in the following inputs at the command line

./3des genkey password outputFile   

./3des encrypt inputFile keyFile outputFile mode

./3des decrypt inputFile keyFile outputFile mode

Where genKey creates the  3DES encryption keys from the password and stores it in the file input as the outputFile, encrypt encrypts the contents of inputFile with the key in keyFile and stores the result in outputFile, and decrypt takes the data in inputFile and decrypts it using the key in keyFile and stores the result in outputFile.  Do not use existing crypto libraries to perform any of the functions other than to hash the password and derive your keys from the hash.  Truncate the output so that it is the appropriate key size if you use an algorithm that outputs a larger value.

Block ciphers require you to pad your input, so if the data you are encrypting is not evenly divisible by 64 bits, then you will add padding bits.  We will use PKCS#5 padding.  This involves all the padding bytes being the value of the number of padding bytes you are adding.  You may assume that you only need to pad in whole bytes.  For example, if I am encrypting one byte, FF, then my padded version would be FF 07 07 07 07 07 07 07.  If I was padding two bytes of data, 06 06, then my padded version would be 06 06 06 06 06 06 06 06.

For CBC mode you will have to include an initialization vector.  This value is supposed to be unique in the sense that it is never the same value twice for the same DES key.  You may use the standard rand() function with the time as a seed for this assignment to generate the IV.  The IV should be the size of a block.  You may either generate it as the first 64 bits of your encrypted file or write it to a separate file (as long as your DES decrypt function knows where to look).  The same idea holds for the nonce/IV in CTR mode.