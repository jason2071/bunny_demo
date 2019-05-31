package com.example.bunny.util;

public class Crc16Utils {

    public static int calculate_crc(byte[] bytes) {
        int i;
        int crc_value = 0;
        for (byte aByte : bytes) {
            for (i = 0x80; i != 0; i >>= 1) {
                if ((crc_value & 0x8000) != 0) {
                    crc_value = (crc_value << 1) ^ 0x8005;
                } else {
                    crc_value = crc_value << 1;
                }
                if ((aByte & i) != 0) {
                    crc_value ^= 0x8005;
                }
            }
        }
        return crc_value;
    }

    public static int calculateInt(int[] number) {
        int i;
        int crcValue = 0;
        for (int num : number) {
            for (i = 0x80; i != 0; i >>= 1) {
                if ((crcValue & 0x8000) != 0) {
                    crcValue = (crcValue << 1) ^ 0x8005;
                } else {
                    crcValue = crcValue << 1;
                }
                if ((num & i) != 0) {
                    crcValue ^= 0x8005;
                }
            }
        }
        return crcValue;
    }
}
