package com.yanceysong.im.codec.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @ClassName Base64URL
 * @Description
 * @date 2023/5/15 10:54
 * @Author yanceysong
 * @Version 1.0
 */
public class Base64UrlUtil {
    public static byte[] base64EncodeUrl(byte[] input) {
        byte[] base64 = Base64.getEncoder().encodeToString(input).getBytes(StandardCharsets.UTF_8);
        return convertEncode(base64);
    }

    public static byte[] base64EncodeUrlNotReplace(byte[] input) {
        byte[] base64 = Base64.getEncoder().encodeToString(input).getBytes(StandardCharsets.UTF_8);
        return convertEncode(base64);
    }

    public static byte[] base64DecodeUrlNotReplace(byte[] input) throws IOException {
        return Base64.getDecoder().decode(new String(convertDecode(input.clone()), StandardCharsets.UTF_8));
    }

    public static byte[] base64DecodeUrl(byte[] input) throws IOException {
        return Base64.getDecoder().decode(new String(convertDecode(input.clone()), StandardCharsets.UTF_8));
    }

    /**
     * 加密时候base64转换
     *
     * @param base64 base64编码
     * @return 转换后的字节数组
     */
    private static byte[] convertEncode(byte[] base64) {
        for (int i = 0; i < base64.length; ++i) {
            switch (base64[i]) {
                case '+':
                    base64[i] = '*';
                    break;
                case '/':
                    base64[i] = '-';
                    break;
                case '=':
                    base64[i] = '_';
                    break;
                default:
                    break;
            }
        }
        return base64;
    }

    /**
     * 解时候base64转换
     *
     * @param base64 base64编码
     * @return 转换后的字节数组
     */
    private static byte[] convertDecode(byte[] base64) {
        for (int i = 0; i < base64.length; ++i) {
            switch (base64[i]) {
                case '*':
                    base64[i] = '+';
                    break;
                case '-':
                    base64[i] = '/';
                    break;
                case '_':
                    base64[i] = '=';
                    break;
                default:
                    break;
            }
        }
        return base64;
    }
}
