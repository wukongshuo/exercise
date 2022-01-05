package com.xxw.util.base.util;

public class ByteBufTest {




    public static String byteArrayToHexString(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            buffer.append(byteToHexString(b[i]));
            if (i != b.length - 1) {
                buffer.append(" ");
            }
        }
        return buffer.toString();
    }

    public static String byteToHexString(byte b) {
        return String.format("%02X ", b);
    }

    public static void main(String[] args) {
        Byte b = 1;
        String s = byteToHexString(b);
        System.out.println(s);
    }
}
