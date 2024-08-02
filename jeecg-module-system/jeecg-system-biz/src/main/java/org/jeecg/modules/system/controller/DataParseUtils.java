package org.jeecg.modules.system.controller;

public class DataParseUtils {


    /**
     * �з��ţ�int ռ 2 ���ֽ�
     */
    public static int convertTwoSignInt(byte b1, byte b2) { // signed
        return (b2 << 8) | (b1 & 0xFF);
    }

    /**
     * �з���, int ռ 4 ���ֽ�
     */
    public static int convertFourSignInt(byte b1, byte b2, byte b3, byte b4) {
        return (b4 << 24) | (b3 & 0xFF) << 16 | (b2 & 0xFF) << 8 | (b1 & 0xFF);
    }

    /**
     * �޷��ţ�int ռ 2 ���ֽ�
     */
    public static int convertTwoUnsignInt(byte b1, byte b2)      // unsigned
    {
        return (b2 & 0xFF) << 8 | (b1 & 0xFF);
    }

    /**
     * �޷���, int ռ 4 ���ֽ�
     */
    public static long convertFoutUnsignLong(byte b1, byte b2, byte b3, byte b4) {
        return (long) (b4 & 0xFF) << 24 | (b3 & 0xFF) << 16 | (b2 & 0xFF) << 8 | (b1 & 0xFF);
    }
}
