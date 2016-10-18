package threedes.util;

import java.util.Arrays;

import threedes.app.config.BitwiseOperations;
import threedes.app.config.Constant;

public class ByteArrayUtil {

    private static byte [][] sBitValues = 
        {
            {
                0x7f, (byte) 0xbf, (byte) 0xdf, (byte) 0xef, (byte) 0xf7, (byte) 0xfb, (byte) 0xfd, (byte) 0xfe
            }, 
            {
                (byte) 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01
            }
        };
    
    public static void setBitOfArray(byte [] array, int index, int bit)
    {
        if (index >= array.length * Constant.BITS_OF_BYTE)
        {
            return;
        }
        
        if (bit == 0)
        {
            array[index / Constant.BITS_OF_BYTE] &= sBitValues[0][index % Constant.BITS_OF_BYTE];
        }
        else
        {
            array[index / Constant.BITS_OF_BYTE] |= sBitValues[1][index % Constant.BITS_OF_BYTE];
        }
    }
    
    public static int getBitOfArray(byte [] array, int index)
    {
        if (index >= array.length * Constant.BITS_OF_BYTE)
        {
            return -1;
        }
        
        return (array[index / Constant.BITS_OF_BYTE] & sBitValues[1][index % Constant.BITS_OF_BYTE]) != 0 ? 1 : 0;
    }
    
    public static void separateByteArray(byte [] originArray, byte [] firstHalf, byte [] secondHalf)
    {
        if (originArray == null || firstHalf == null || secondHalf == null)
        {
            return;
        }
        
        boolean evenLength = originArray.length % 2 == 0;
        
        if (evenLength && (firstHalf.length < originArray.length / 2 || secondHalf.length < originArray.length / 2))
        {
            return;
        }
        
        if (!evenLength && (firstHalf.length < 1 + originArray.length / 2 || secondHalf.length < 1 + originArray.length / 2))
        {
            return;
        }
        
        if (evenLength)
        {
            for (int i = 0; i < originArray.length / 2; ++i)
            {
                firstHalf[i] = originArray[i];
                secondHalf[i] = originArray[i + originArray.length / 2];
            }
        }
        else
        {
            for (int i = 0; i < originArray.length * Constant.BITS_OF_HALFBYTE; ++i)
            {
                setBitOfArray(firstHalf, i, getBitOfArray(originArray, i));
                setBitOfArray(secondHalf, i, getBitOfArray(originArray, i + originArray.length * Constant.BITS_OF_HALFBYTE));
            }
        }
    }
    
    public static byte [] joinByteArray(byte [] array1, int bit1, byte [] array2, int bit2)
    {
        int newArraySize = (bit1 + bit2) % Constant.BITS_OF_BYTE == 0 ? (bit1 + bit2) / Constant.BITS_OF_BYTE : 1 + (bit1 + bit2) / Constant.BITS_OF_BYTE;
        
        byte [] retArray = new byte[newArraySize];
        
        for (int i = 0; i < (bit1 + bit2); ++i)
        {
            if (i < bit1)
            {
                setBitOfArray(retArray, i, getBitOfArray(array1, i));
            }
            else
            {
                setBitOfArray(retArray, i, getBitOfArray(array2, i - bit1));
            }
        }
        
        return retArray;
    }
    
    // n > 0 left,  n < 0 right
    public static void shiftArray(byte [] array, int totalBit, int n)
    {
        if (array == null || totalBit <= 0 || totalBit > array.length * Constant.BITS_OF_BYTE)
        {
            return;
        }
        
        boolean isLeftShift = (n >= 0);
        
        if (!isLeftShift)
        {
            n *= -1;
        }
        
        for(int i = 0; i < n; ++i)
        {
            if (isLeftShift)
            {
                int firstBit = getBitOfArray(array, 0);
                
                for(int j = 0; j < totalBit - 1; ++j)
                {
                    setBitOfArray(array, j, getBitOfArray(array, j + 1));
                }
                setBitOfArray(array, totalBit - 1, firstBit);
            }
            else
            {
                int lastBit = getBitOfArray(array, totalBit - 1);
                
                for(int j = totalBit - 1; j > 0; --j)
                {
                    setBitOfArray(array, j, getBitOfArray(array, j - 1));
                }
                setBitOfArray(array, 0, lastBit);
            }
        }    
    }
    
    public static byte [] applyPermTable(byte [] origin, int [] table)
    {
        if (origin == null)
        {
            return null;
        }
        
        byte [] permedArray = new byte[table.length / Constant.BITS_OF_BYTE];
        
        for(int i = 0; i < table.length; ++i)
        {
            setBitOfArray(permedArray, i, ByteArrayUtil.getBitOfArray(origin, table[i] - 1));
        }
        
        return permedArray;
    }
    
    public static byte[][] separateByteArrayToBlock(byte [] origin, int blockSizeInByte)
    {
        if (origin == null || blockSizeInByte <= 0)
        {
            return null;
        }
        
        int blockNumbers = (origin.length % blockSizeInByte == 0) ? origin.length / blockSizeInByte : 1 + origin.length / blockSizeInByte;
        
        byte [][] retArray = new byte[blockNumbers][];
        
        for(int i = 0; i < retArray.length; ++i)
        {
            retArray[i] = new byte[blockSizeInByte];
        }
        
        
        for(int i = 0; i < origin.length; ++i)
        {
            retArray[i / blockSizeInByte][i % blockSizeInByte] = origin[i];
        }
        
        return retArray;
    }
    
    public static byte [] mergeBlockToByteArray(byte [][] origin)
    {
        if (origin == null || origin.length == 0)
        {
            return null;
        }
        
        int blockSize = origin[0].length;
        
        byte [] retVal = new byte[blockSize * origin.length];
        
        for(int i = 0; i < retVal.length; ++i)
        {
            retVal[i] = origin[i / blockSize][i % blockSize];
        }
        
        return retVal;
    }
    
    public static byte [] bitwiseByteArray(byte [] array1, byte [] array2, BitwiseOperations operator)
    {
        if (array1 == null || array2 == null || array1.length != array2.length)
        {
            return null;
        }
        
        byte [] retArray = new byte[array1.length];
        
        for (int i = 0; i < array1.length; ++i)
        {
            byte result = 0x00;
            
            switch (operator)
            {
            case AND:
                result = (byte) (array1[i] & array2[i]);
                break;
            case OR:
                result = (byte) (array1[i] | array2[i]);
                break;
            case XOR:
                result = (byte) (array1[i] ^ array2[i]);
                break;
            case NAND:
                result = (byte) ~(array1[i] & array2[i]);
                break;
            case NOR:
                result = (byte) ~(array1[i] | array2[i]);
                break;
            default:
                result = 0x00;
                break;
            }
            
            retArray[i] = result;
        }
        
        return retArray;
    }
    
    
    public static byte fetchByteFromArray(byte [] origin, int startInBit, int lengthInBit)
    {
        if (origin == null || lengthInBit <= 0 || lengthInBit > 8)
        {
            return 0x00;
        }
        
        byte [] retValue = new byte[1];
        
        retValue[0] = 0x00;
        int offset = Constant.BITS_OF_BYTE - lengthInBit;
        
        for(int i = lengthInBit - 1; i >= 0; --i)
        {
            setBitOfArray(retValue, i + offset, getBitOfArray(origin, startInBit + i));
        }
        
        return retValue[0];
    }
    
    public static byte [] fetchBitsFromArray(byte [] origin, int startInBit, int lengthInBit)
    {
        if (origin == null || lengthInBit <= 0 || startInBit >= origin.length * Constant.BITS_OF_BYTE)
        {
            return null;
        }
        
        int retSize = (lengthInBit % Constant.BITS_OF_BYTE == 0) ? lengthInBit / Constant.BITS_OF_BYTE : 1 + lengthInBit / Constant.BITS_OF_BYTE; 
        byte [] retValue = new byte [retSize];
        
        for(int i = 0; i < lengthInBit; ++i)
        {
            setBitOfArray(retValue, i, getBitOfArray(origin, startInBit + i));
        }
        
        return retValue;
    }
    
    public static String convertByteArrayInBinaryString(byte [] array)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i)
        {
            sb.append(String.format("%8s", Integer.toBinaryString(array[i] & 0xFF)).replace(' ', '0') + " ");
        }
        return sb.toString();
    }
    
    public static byte [] appendByteArray(byte [] array1, byte [] array2)
    {
        if (array1 == null)
        {
            return null;
        }
        if (array2 == null)
        {
            return array1;
        }
        
        byte [] retArray = new byte[array1.length + array2.length];
        
        for (int i = 0; i < array1.length; ++i)
        {
            retArray[i] = array1[i];
        }
        
        for(int i = 0; i < array2.length; ++i)
        {
            retArray[i + array1.length] = array2[i];
        }
        
        return retArray;
    }
    
    public static byte [] subByteArray(byte [] array, int startIndex, int nByte)
    {
        if (array == null || startIndex + nByte - 1 > array.length)
        {
            return null;
        }
        
        if (nByte == array.length && startIndex == 0)
        {
            return array.clone();
        }
        
        byte [] retArray = new byte[nByte];
        
        for (int i = 0; i < retArray.length; ++i)
        {
            retArray[i] = array[startIndex + i];
        }
        
        return retArray;
    }
    
    public static void main(String [] args)
    {
        byte [] array = {(byte) 0x80, 0x00, (byte) 0x87, 0x20, 0x00};
        byte [] array2 = {0x10, 0x20, (byte) 0x87, 0x01, 0x04, 0x7f};
        
        setBitOfArray(array, 14, 1);
        setBitOfArray(array, 13, 1);
        
        System.out.println(Arrays.toString(array));
        
        setBitOfArray(array, 14, 0);
        setBitOfArray(array, 12, 1);
        
        System.out.println(Arrays.toString(array));
        
        System.out.println(getBitOfArray(array, 0));
        System.out.println(getBitOfArray(array, 9));
        System.out.println(getBitOfArray(array, 12));
        System.out.println(getBitOfArray(array, 13));
        System.out.println(getBitOfArray(array, 14));
        
        byte [] first = {0x00, 0x00, 0x00};
        byte [] second = {0x00, 0x00, 0x00};
        
        separateByteArray(array, first, second);
        
        System.out.println(Arrays.toString(first));
        System.out.println(Arrays.toString(second));
        
        byte [] recover = joinByteArray(first, 10, second, 24);
        
        System.out.println(Arrays.toString(recover));
        
        separateByteArray(array2, first, second);
        
        System.out.println(Arrays.toString(first));
        System.out.println(Arrays.toString(second));
        
        shiftArray(array, 40, 2);
        System.out.println(Arrays.toString(array));
        
        shiftArray(array, 40, -2);
        System.out.println(Arrays.toString(array));
        
    }
    
}
