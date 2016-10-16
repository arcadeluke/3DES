package threedes.algo;

import threedes.app.config.Constant;
import threedes.util.ByteArrayUtil;

public class DESKeyGenerator {

    
    public static byte [][] createAllKeys(byte [] initKey)
    {
        //System.out.println("InitKey: " + Arrays.toString(initKey));
        
        byte [][] allKeys = new byte[Constant.TOTAL_KEY_NUMBER][];
        
        byte [] C0 = {0x00, 0x00, 0x00, 0x00};
        byte [] D0 = {0x00, 0x00, 0x00, 0x00};
        
        ByteArrayUtil.separateByteArray(initKey, C0, D0);
        
        byte [] CPrev = C0.clone();
        byte [] DPrev = D0.clone();
        
        //System.out.println("C0: " + Arrays.toString(C0));
        //System.out.println("D0: " + Arrays.toString(D0));
        
        for (int i = 0; i < Constant.TOTAL_KEY_NUMBER; ++i)
        {
            byte [] CNew = CPrev.clone();
            byte [] DNew = DPrev.clone();
            
            int shift = PermTables.sPTable[i];
            
            ByteArrayUtil.shiftArray(CNew, Constant.HALF_KEY_SIZE_IN_BIT, shift);
            ByteArrayUtil.shiftArray(DNew, Constant.HALF_KEY_SIZE_IN_BIT, shift);
            
            //System.out.println("i = " + (i + 1));
            //System.out.println("Ci: " + Arrays.toString(CNew));
            //System.out.println("Di: " + Arrays.toString(DNew));
            
            byte [] Key = ByteArrayUtil.joinByteArray(CNew, Constant.HALF_KEY_SIZE_IN_BIT, DNew, Constant.HALF_KEY_SIZE_IN_BIT);
            
            Key = ByteArrayUtil.applyPermTable(Key, PermTables.sPC2Table);
            
            //System.out.println("Ki: " + Arrays.toString(Key));
            
            allKeys[i] = Key;
            
            CPrev = CNew;
            DPrev = DNew;
        }
        
        return allKeys;
    }
    
    
    
}
