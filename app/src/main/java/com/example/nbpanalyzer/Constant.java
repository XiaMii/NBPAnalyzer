package com.example.nbpanalyzer;

/**
 * 常量类
 */
public class Constant {

    public static final String DATABASE_NAME = "info.db";  // 数据库名称
    public static final int DATABASE_VERSION = 1;          //数据库版本
    public static final String TABLE_NAME = "Nbp_data";     //数据库表名
    /**
     * id、user、date、sysPressure、disPressure、nbpPulse 以下是数据库表中的字段
     */
    public static  final String ID = "id";                //id主键
    public static  final String USER = "user";            //用户
    public static final String DATE = "date";             //日期
    public static final String TIME = "time";             //时间
    public static final String SYS = "sysPressure";       //收缩压
    public static final String DIS = "disPressure";       //舒张压
    public static final String PR = "nbpPulse";           //脉率

    /**字符串类型的密钥对
     *  一对测试密钥   1:
     *  pubKey_String ---公钥
     *  pubKey_String ---私钥
     */
    public static final String pubKey_String = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCVRiDkEKXy/KBTe+UmkA+feq1zGWIgBxkgbz7aBJGb5+eMKKoiDRoEHzlGndwFKm4mQWNftuMOfNcogzYpGKSEfC7sqfBPDHsGPZixMWzL3J10zkMTWo6MDIXKKqMG1Pgeq1wENfJjcYSU/enYSZkg3rFTOaBSFId+rrPjPo7Y4wIDAQAB";
    public static final String priKey_String = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJVGIOQQpfL8oFN75SaQD596rXMZYiAHGSBvPtoEkZvn54woqiINGgQfOUad3AUqbiZBY1+24w581yiDNikYpIR8Luyp8E8MewY9mLExbMvcnXTOQxNajowMhcoqowbU+B6rXAQ18mNxhJT96dhJmSDesVM5oFIUh36us+M+jtjjAgMBAAECgYABtnxKIabF0wBD9Pf8KUsEmXPEDlaB55LyPFSMS+Ef2NlfUlgha+UQhwsxND6CEKqS5c0uG/se/2+4l0jXz+CTYBEh+USYB3gxcMKEo5XDFOGaM2Ncbc7FAKJIkYYN2DHmr4voSM5YkVibw5Lerw0kKdYyr0Xd0kmqTok3JLiLgQJBAOGZ1ao9oqWUzCKnpuTmXre8pZLmpWPhm6S1FU0vHjI0pZh/jusc8UXSRPnx1gLsgXq0ux30j968x/DmkESwxX8CQQCpY1+2p1aX2EzYO3UoTbBUTg7lCsopVNVf41xriek7XF1YyXOwEOSokp2SDQcRoKJ2PyPc2FJ/f54pigdsW0adAkAM8JTnydc9ZhZ7WmBhOrFuGnzoux/7ZaJWxSguoCg8OvbQk2hwJd3U4mWgbHWY/1XB4wHkivWBkhRpxd+6gOUjAkBH9qscS52zZzbGiwQsOk1Wk88qKdpXku4QDeUe3vmSuZwC85tNyu+KWrfM6/H74DYFbK/MzK7H8iz80uJye5jVAkAEqEB/LwlpXljFAxTID/SLZBb+bCIoV/kvg+2145F+CSSUjEWRhG/+OH0cQfqomfg36WrvHl0g/Xw06fg31HgK";

    /**
     *  测试密钥  2:
     *  pubKey_String ---公钥
     *  pubKey_String ---私钥
     */
    public static final String pubKey1 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCokTtKtoiIT8/4dmC3qd0l6m5LWSKkZTeWHbTi7yZ0zHc3Y9PEdHu9LtdzVMJ6z+6kD09bY0lK31gVBBGDqC0pUum9ObqY6HRLHcoHqAT90bpL7+B1ufbLt3S8lJlhTXY9TL4i9i5mI43FrHKxR+c8OAJs0gILzd1x7K+KnQ1pBQIDAQAB";

    public static final String priKey1 = "MIIBNgIBADANBgkqhkiG9w0BAQEFAASCASAwggEcAgEAAoGBAKiRO0q2iIhPz/h2YLep3SXqbktZIqRlN5YdtOLvJnTMdzdj08R0e70u13NUwnrP7qQPT1tjSUrfWBUEEYOoLSlS6b05upjodEsdygeoBP3Rukvv4HW59su3dLyUmWFNdj1MviL2LmYjjcWscrFH5zw4AmzSAgvN3XHsr4qdDWkFAgEAAoGAaRXg+LrCcvgOlr51nQnwK+rxx1dSGVpgRN1QHwkn2Dh/ObCqHBbh7RZ+ig+VDisCgpRozHghAOQrbS6UHJeDTvRWQhvjSkPUKtzT5sVXdpFrQRhOBP2ucxGubZtYNUitm3ilObsT1YZf0OB+Ozs19FJidCgHS88tPsB/W+ukowECAQACAQACAQACAQACAQA=";

}
