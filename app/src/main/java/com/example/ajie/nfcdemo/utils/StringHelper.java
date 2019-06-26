package com.example.ajie.nfcdemo.utils;

public class StringHelper {
    public static String Bytes2HexString(byte[] bytes) {
        return Bytes2HexString(bytes, bytes.length);
    }

    public static String Bytes2HexString(byte[] bytes, int size) {
        String ret = "";

        for(int i = 0; i < size; ++i) {
            String hex = Integer.toHexString(bytes[i] & 255);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }

            ret = ret + hex.toUpperCase();
        }

        return ret;
    }
}
