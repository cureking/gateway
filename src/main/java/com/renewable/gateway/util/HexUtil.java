package com.renewable.gateway.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
public class HexUtil {

//    public static void main(String[] args) {
//        byte []out = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
//                (byte)0x10, (byte)0x11, (byte)0x12, (byte)127, (byte)128, (byte)255 };
//
//
//        System.out.println( bytesToHexString((byte)10));
//        System.out.println( bytesToHexString(out) );
//        System.out.println( bytesToHexString(out, 0) );
//        System.out.println( bytesToHexString(out, 1) );
//
//        byte []b1 = hexStringToBytes("0123456789ABCDEF");
//        for( int i = 0; i < b1.length; i++ ) {
//            System.out.println("[" + i + "]:" + b1[i]);
//        }
//        System.out.println( bytesToHexString(b1) );
//
//        byte []b2 = bcdStringToBytes("1234567890123456");
//        for( int i = 0; i < b2.length; i++ ) {
//            System.out.println("[" + i + "]:" + b2[i]);
//        }
//        System.out.println( bytesToHexString(b2) );
//
////        byte []bcdBytes = {0, 1, 2, 3, 4, 5, 6, 7, 9, 111,
//////                10, 11, 21, 31, 41, 51, 61, 71, 81, 91, 99};
//        byte []bcdBytes = {10,00,25,28};
//        System.out.println("bcd2HexString:" + bcdToHexString(bcdBytes) );
//    }

    public static String bytesToHexString(byte src) {
        int v = src & 0xFF;
        String hv = Integer.toHexString(v);
        return hv;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            hv = hv.toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String bytesToHexString(byte[] src, int mode) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {

            if (i != 0 && i % 16 == 0) {
                stringBuilder.append("\r\n");
            }

            if (mode == 1) {
                stringBuilder.append("0x");
            }

            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);

            if (mode == 1 && i + 1 != src.length) {
                stringBuilder.append(',');
            }
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    public static String bcdToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            int v1 = v / 10;
            int v2 = v % 10;

            if (v >= 0 && v < 100) {
                stringBuilder.append(v1);
                stringBuilder.append(v2);
            }
        }
        return stringBuilder.toString();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("") || hexString.length() % 2 != 0) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte[] bcdStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("") || hexString.length() % 2 != 0) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) * 10 + charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    //custom
    public static int byte2hex2bcd2int(byte[] bytes) {
        String hexString = bytesToHexString(bytes);
        System.out.println(hexString);
        int result = 0;
        for (int i = 0; i < hexString.length(); i++) {
            int currentS = charToByte(hexString.charAt(i));
            if (currentS < 10) {
                result += currentS * Math.pow(10, hexString.length() - i - 1);
            } else {
                log.error("BCD码出现错误" + hexString);
            }
        }
        return result;
    }

    /**
     * 该方法也是返回int，但是可以避免byte的自动补位。如{8,0}=》800
     *
     * @param bytes
     * @return
     */
    public static int bcd2int(byte[] bytes) {
        String hexString = byte2string(bytes);

        hexString = hexString.replace("-","");  //去除负数影响，避免由传感器发送的signed数据造成的错误。    //todo 这样做的结果就是数据必然存在负数导致的错误，后期要么让硬件不要发送无意义的signed数据，要么在前面处理负数。

//        System.out.println("HexString:"+hexString);
        int result = 0;
        for (int i = 0; i < hexString.length(); i++) {
            int currentS = charToByte(hexString.charAt(i));
//            System.out.println("currentS:"+currentS);
            if (currentS < 10) {
                result += currentS * Math.pow(10, hexString.length() - i - 1);
            } else {
                log.error("BCD码出现错误" + hexString);
            }
        }
        return result;
    }

    private static String byte2string(byte[] bytes) {
        String str = "";
        for (int i = 0; i < bytes.length; i++) {
            byte aByte = bytes[i];
            str += (aByte / 16 + aByte % 16);
        }
        return (String) str;
    }

    public static void main(String[] args) {
        byte[] origin = {0, 0, 32, 8};
        byte[] test3 = {8, 0};
        System.out.println(HexUtil.bcd2int(origin));
//        System.out.println(byte2string(test3));
        //todo 2019.04.09 解决单数位拆分。
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
