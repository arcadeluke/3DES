package threedes.app.config;

public class Constant {

    public static final int BITS_OF_BYTE = 8;
    public static final int BITS_OF_HALFBYTE = BITS_OF_BYTE / 2;
    
    public static final int BLOCK_SIZE_IN_BIT = 64;
    public static final int HALFBLOCK_SIZE_IN_BIT = BLOCK_SIZE_IN_BIT / 2;
    public static final int BLOCK_SIZE_IN_BYTE = BLOCK_SIZE_IN_BIT / BITS_OF_BYTE;
    
    public static final int KEY_SIZE_IN_BIT = 56;
    public static final int KEY_SIZE_IN_BYTE = KEY_SIZE_IN_BIT / BITS_OF_BYTE;
    public static final int THREEKEY_SIZE_IN_BIT = KEY_SIZE_IN_BIT * 3;
    public static final int HALF_KEY_SIZE_IN_BIT = KEY_SIZE_IN_BIT / 2;
    
    public static final int E_FUNC_SIZE_IN_BIT = 48;
    public static final int E_FUNC_SIZE_IN_BYTE = E_FUNC_SIZE_IN_BIT / BITS_OF_BYTE;
    
    public static final int TOTAL_KEY_NUMBER = 16;
    
    public static final int S_BOX_INPUT_BITS = 6;
    
    public static final int S_BOX_ROW_LENGTH = 16;
    public static final int S_BOX_COL_LENGTH = 4;
    
    
}
