package com.example.bunny.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public class StringUtil {
    private static final String PLATFORM_DEFAULT_ENCODING = Charset.defaultCharset().name();
    public static final String SHIFT_JIS = "SJIS";
    public static final String GB18030 = "GB18030";
    private static final String EUC_JP = "EUC_JP";
    private static final String UTF8 = "UTF8";
    private static final String ISO88591 = "ISO8859_1";
    private static final boolean ASSUME_SHIFT_JIS;

    public StringUtil() {
    }

    public static byte[] getGBK(String str) {
        try {
            return str.getBytes("GBK");
        } catch (UnsupportedEncodingException var2) {
            return null;
        }
    }

    public static String fromGBK(byte[] strData) {
        try {
            return new String(strData, "GBK");
        } catch (UnsupportedEncodingException var2) {
            return null;
        }
    }

    public static String fromUTF8(byte[] strData) {
        try {
            return new String(strData, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            return null;
        }
    }

    public static String guessEncoding(byte[] bytes) {
        int length = bytes.length;
        boolean canBeISO88591 = true;
        boolean canBeShiftJIS = true;
        boolean canBeUTF8 = true;
        boolean canBeGB18030 = true;
        int gb18030Bytes = 0;
        int utf8BytesLeft = 0;
        int utf2BytesChars = 0;
        int utf3BytesChars = 0;
        int utf4BytesChars = 0;
        int sjisBytesLeft = 0;
        int sjisKatakanaChars = 0;
        int sjisCurKatakanaWordLength = 0;
        int sjisCurDoubleBytesWordLength = 0;
        int sjisMaxKatakanaWordLength = 0;
        int sjisMaxDoubleBytesWordLength = 0;
        int isoHighOther = 0;
        boolean utf8bom = bytes.length > 3 && bytes[0] == -17 && bytes[1] == -69 && bytes[2] == -65;

        for(int i = 0; i < length && (canBeISO88591 || canBeShiftJIS || canBeUTF8 || canBeGB18030); ++i) {
            int value = bytes[i] & 255;
            if (canBeUTF8) {
                if (utf8BytesLeft > 0) {
                    if ((value & 128) == 0) {
                        canBeUTF8 = false;
                    } else {
                        --utf8BytesLeft;
                    }
                } else if ((value & 128) != 0) {
                    if ((value & 64) == 0) {
                        canBeUTF8 = false;
                    } else {
                        ++utf8BytesLeft;
                        if ((value & 32) == 0) {
                            ++utf2BytesChars;
                        } else {
                            ++utf8BytesLeft;
                            if ((value & 16) == 0) {
                                ++utf3BytesChars;
                            } else {
                                ++utf8BytesLeft;
                                if ((value & 8) == 0) {
                                    ++utf4BytesChars;
                                } else {
                                    canBeUTF8 = false;
                                }
                            }
                        }
                    }
                }
            }

            if (canBeISO88591) {
                if (value > 127 && value < 160) {
                    canBeISO88591 = false;
                } else if (value > 159 && (value < 192 || value == 215 || value == 247)) {
                    ++isoHighOther;
                }
            }

            if (canBeGB18030) {
                if (gb18030Bytes > 0) {
                    switch(gb18030Bytes) {
                        case 2:
                            if (value >= 64 && value <= 126 || value >= 128 && value <= 254) {
                                gb18030Bytes = 0;
                            } else if (value >= 48 && value <= 57) {
                                gb18030Bytes = 3;
                            } else {
                                canBeGB18030 = false;
                            }
                            break;
                        case 3:
                            if (value >= 129 && value <= 4067) {
                                gb18030Bytes = 4;
                            } else {
                                canBeGB18030 = false;
                            }
                            break;
                        case 4:
                            if (value >= 48 && value <= 57) {
                                gb18030Bytes = 0;
                            } else {
                                canBeGB18030 = false;
                            }
                            break;
                        default:
                            canBeGB18030 = false;
                    }
                } else if (value < 0 || value > 127) {
                    if (value >= 129 && value <= 254) {
                        gb18030Bytes = 2;
                    } else {
                        canBeGB18030 = false;
                    }
                }
            }

            if (canBeShiftJIS) {
                if (sjisBytesLeft > 0) {
                    if (64 <= value && value <= 126 || 128 <= value && value <= 252) {
                        int code = (bytes[i - 1] & 255) << 8 | bytes[i] & 255;
                        if (33197 <= code && code <= 33207 || 33216 <= code && code <= 33223 || 33231 <= code && code <= 33241 || 33257 <= code && code <= 33263 || 33272 <= code && code <= 33276 || 33344 <= code && code <= 33358 || 33369 <= code && code <= 33375 || 33402 <= code && code <= 33408 || 33435 <= code && code <= 33438 || 33522 <= code && code <= 33532 || 33687 <= code && code <= 33694 || 33719 <= code && code <= 33726 || 33751 <= code && code <= 33788 || 33889 <= code && code <= 33903 || 33938 <= code && code <= 33950 || 33983 <= code && code <= 34044 || 34112 <= code && code <= 34974 || 39027 <= code && code <= 39070 || 60325 <= code && code <= 60412 || 60224 <= code && code <= 61436) {
                            canBeShiftJIS = false;
                        } else {
                            sjisBytesLeft = 0;
                            sjisCurKatakanaWordLength = 0;
                            ++sjisCurDoubleBytesWordLength;
                            if (sjisCurDoubleBytesWordLength > sjisMaxDoubleBytesWordLength) {
                                sjisMaxDoubleBytesWordLength = sjisCurDoubleBytesWordLength;
                            }
                        }
                    } else {
                        canBeShiftJIS = false;
                    }
                } else if ((value < 0 || value > 31) && value != 127 && (32 > value || value > 126) && (161 > value || value > 223)) {
                    if ((129 > value || value > 159) && (224 > value || value > 239)) {
                        canBeShiftJIS = false;
                    } else {
                        sjisBytesLeft = 1;
                    }
                } else if (161 <= value && value <= 223) {
                    ++sjisKatakanaChars;
                    sjisCurDoubleBytesWordLength = 0;
                    ++sjisCurKatakanaWordLength;
                    if (sjisCurKatakanaWordLength > sjisMaxKatakanaWordLength) {
                        sjisMaxKatakanaWordLength = sjisCurKatakanaWordLength;
                    }
                }
            }
        }

        if (canBeUTF8 && utf8BytesLeft > 0) {
            canBeUTF8 = false;
        }

        if (canBeShiftJIS && sjisBytesLeft > 0) {
            canBeShiftJIS = false;
        }

        if (canBeGB18030 && gb18030Bytes > 0) {
            canBeGB18030 = false;
        }

        if (canBeUTF8 && (utf8bom || utf2BytesChars + utf3BytesChars + utf4BytesChars > 0)) {
            return "UTF8";
        } else if (canBeGB18030) {
            if (canBeShiftJIS) {
                return sjisMaxKatakanaWordLength > sjisMaxDoubleBytesWordLength ? "GB18030" : "SJIS";
            } else {
                return "GB18030";
            }
        } else if (canBeShiftJIS && (ASSUME_SHIFT_JIS || sjisMaxKatakanaWordLength >= 3 || sjisMaxDoubleBytesWordLength >= 3)) {
            return "SJIS";
        } else if (canBeISO88591 && canBeShiftJIS) {
            return (sjisMaxKatakanaWordLength != 2 || sjisKatakanaChars != 2) && isoHighOther * 10 < length ? "ISO8859_1" : "SJIS";
        } else if (canBeISO88591) {
            return "ISO8859_1";
        } else if (canBeShiftJIS) {
            return "SJIS";
        } else {
            return canBeUTF8 ? "UTF8" : PLATFORM_DEFAULT_ENCODING;
        }
    }

    public static boolean isNumber(String string) {
        Pattern pattern = Pattern.compile("[-]?[0-9]*");
        return pattern.matcher(string).matches();
    }

    public static String getReadableAmount(String amount) {
        String formatedAmount = "";
        if (amount.length() > 1 && amount.charAt(0) == '-') {
            formatedAmount = formatedAmount + "-";
            amount = amount.substring(1, amount.length());
        }

        if (!isNumber(amount)) {
            throw new NumberFormatException();
        } else {
            if (amount.length() > 2) {
                formatedAmount = formatedAmount + amount.substring(0, amount.length() - 2);
                formatedAmount = formatedAmount + ".";
                formatedAmount = formatedAmount + amount.substring(amount.length() - 2);
            } else if (amount.length() == 2) {
                formatedAmount = formatedAmount + "0.";
                formatedAmount = formatedAmount + amount;
            } else if (amount.length() == 1) {
                formatedAmount = formatedAmount + "0.0";
                formatedAmount = formatedAmount + amount;
            } else {
                formatedAmount = "0.00";
            }

            return formatedAmount;
        }
    }

    public static String getDigits(String data) {
        StringBuffer sb = new StringBuffer();
        if (isNumber(data)) {
            return data;
        } else {
            for(int i = 0; i < data.length(); ++i) {
                char c = data.charAt(i);
                if (c <= '9' && c >= '0') {
                    sb.append(c);
                }
            }

            return sb.toString();
        }
    }

    public static String mask(String value, String mask) {
        int markStart = -1;

        int markEnd;
        for(markEnd = 0; markEnd < mask.length(); ++markEnd) {
            if (mask.charAt(markEnd) == '*') {
                markStart = markEnd;
                break;
            }

            if (mask.charAt(markEnd) != 'x' && mask.charAt(markEnd) != 'X') {
                markStart = markEnd;
                break;
            }
        }

        if (markStart != -1 && markStart < value.length()) {
            markEnd = markStart + 1;

            for(int i = mask.length() - 1; i > markStart + 1; --i) {
                if (mask.charAt(i) == '*') {
                    markEnd = i + 1;
                    break;
                }

                if (mask.charAt(i) != 'x' && mask.charAt(i) != 'X') {
                    markEnd = i + 1;
                    break;
                }
            }

            if (mask.length() - markEnd + markStart >= value.length()) {
                return value;
            } else {
                StringBuilder str = new StringBuilder();
                str.append(value.substring(0, markStart));
                markEnd += value.length() - mask.length();

                for(int i = markStart; i < markEnd; ++i) {
                    str.append("*");
                }

                if (markEnd < value.length()) {
                    str.append(value.substring(markEnd));
                }

                return str.toString();
            }
        } else {
            return value;
        }
    }

    public static String formatString(String value, String formatString, boolean isLeftDirection) {
        StringBuilder result = new StringBuilder();
        int i;
        int n;
        char cur;
        if (isLeftDirection) {
            i = 0;

            for(n = 0; i < formatString.length() && n < value.length(); ++i) {
                cur = formatString.charAt(i);
                if (cur != 'x' && cur != 'X') {
                    result.append(cur);
                } else {
                    result.append(value.charAt(n));
                    ++n;
                }
            }
        } else {
            i = formatString.length() - 1;

            for(n = value.length() - 1; i >= 0 && n >= 0; --i) {
                cur = formatString.charAt(i);
                if (cur != 'x' && cur != 'X') {
                    result.insert(0, cur);
                } else {
                    result.insert(0, value.charAt(n));
                    --n;
                }
            }
        }

        return result.toString();
    }

    public static String addLeftPadding(String str, char padding, int len) {
        int paddingLen = len - str.length();
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < paddingLen; ++i) {
            builder.append(padding);
        }

        builder.append(str);
        return builder.toString();
    }

    public static String addRightPadding(String str, char padding, int len) {
        int paddingLen = len - str.length();
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < paddingLen; ++i) {
            builder.append(padding);
        }

        builder.insert(0, str);
        return builder.toString();
    }

    public static String subString(String src, int offset, int length) {
        if (src == null) {
            return null;
        } else if (offset >= src.length()) {
            return "";
        } else if (length < 0) {
            return src.substring(offset);
        } else {
            int finalLen = length;
            if (offset + length > src.length()) {
                finalLen = src.length() - offset;
            }

            return src.substring(offset, offset + finalLen);
        }
    }

    public static String getSubChineseString(String src, int start, int engLen) {
        if (start > 0) {
            String offsetString = getSubChineseString(src, 0, start);
            return getSubChineseString(src.substring(offsetString.length()), 0, engLen);
        } else {
            int len = src.length();
            String subString = src.substring(start, start + len);
            if (getStringLength(subString) <= engLen) {
                return subString;
            } else {
                subString = src.substring(start, start + len / 2);
                if (getStringLength(subString) == engLen || getStringLength(subString) + 1 == engLen && src.length() > subString.length() && getStringLength(src.substring(subString.length(), subString.length() + 1)) > 1) {
                    return subString;
                } else if (getStringLength(subString) > engLen) {
                    return getSubChineseString(subString, 0, engLen);
                } else {
                    String cutOutString = src.substring(start + len / 2, start + len);
                    return subString + getSubChineseString(cutOutString, 0, engLen - getStringLength(subString));
                }
            }
        }
    }

    public static int getStringLength(String str) {
        String strTemp = str.replaceAll("[^\\x00-\\xff]", "**");
        int length = strTemp.length();
        return length;
    }

    public static boolean isIp(String ip) {
        String regex = "^((25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$";
        return ip != null && !ip.isEmpty() && Pattern.compile(regex).matcher(ip).matches();
    }

    public static boolean isPort(String port) {
        String regex = "^([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5])$";
        return port != null && !port.isEmpty() && Pattern.compile(regex).matcher(port).matches();
    }

    public static boolean isHttpsUrl(String url) {
        String regex = "^[hH][tT]{2}[pP][sS]://[\\S]*";
        return url != null && !url.isEmpty() && Pattern.compile(regex).matcher(url).matches();
    }

    public static boolean isHttpUrl(String url) {
        String regex = "^[hH][tT]{2}[pP]://[\\S]*";
        return url != null && !url.isEmpty() && Pattern.compile(regex).matcher(url).matches();
    }

    public static String getHiddenCardNo(String cardNo) {
        try {
            return cardNo.substring(0, 6) + "*********".substring(19 - cardNo.length()) + cardNo.substring(cardNo.length() - 4);
        } catch (Exception var2) {
            var2.printStackTrace();
            return "";
        }
    }

    static {
        ASSUME_SHIFT_JIS = "SJIS".equalsIgnoreCase(PLATFORM_DEFAULT_ENCODING) || "EUC_JP".equalsIgnoreCase(PLATFORM_DEFAULT_ENCODING);
    }
}
