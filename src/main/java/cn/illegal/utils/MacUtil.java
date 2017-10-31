package cn.illegal.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class MacUtil {
    
    public static String CHARARRAY = "0123456789ABCDEF";
    public static String CHARSET = "UTF-8";

    /**
     * hash算法定义
     *
     * 2016年8月26日
     * 
     * @author zhengpin
     */
    public enum HashAlgorithm {

        /**
         * SHA1签名算法
         */
        SHA1("30", "SHA-1", true),
        /**
         * SHA256签名算法
         */
        SHA256("31", "SHA-256", true),
        /**
         * SHA512签名算法
         */
        SHA512("32", "SHA-512", true),
        /**
         * MD5签名算法
         */
        MD5("33", "MD5", true),
        /**
         * SHA1签名算法，不对密钥做安全加密
         */
        SHA1_SIMPLE("50", "SHA-1", false),
        /**
         * SHA256签名算法，不对密钥做安全加密
         */
        SHA256_SIMPLE("51", "SHA-256", false),
        /**
         * SHA512签名算法，不对密钥做安全加密
         */
        SHA512_SIMPLE("52", "SHA-512", false),
        /**
         * MD5签名算法，不对密钥做安全加密
         */
        MD5_SIMPLE("53", "MD5", false);

        private String code;// 算法编码
        private String algorithm;// 算法
        private boolean isEncryptKey;// 是否对密钥做安全加密

        private HashAlgorithm(String code, String algorithm, boolean isEncryptKey) {
            this.code = code;
            this.algorithm = algorithm;
            this.isEncryptKey = isEncryptKey;
        }

        public String getCode() {
            return code;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public boolean isEncryptKey() {
            return isEncryptKey;
        }
        
        /**
         * 检查签名算法值是否存在
         *
         * @param code
         * @return
         */
        public static boolean isExists(String code){
            for (HashAlgorithm hashAlg : values()) {
                if (hashAlg.getCode().equals(code)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 通过编码获取对应算法信息
         *
         * @param code
         * @return
         */
        public static HashAlgorithm getByCode(String code) {
            for (HashAlgorithm hashAlg : values()) {
                if (hashAlg.getCode().equals(code)) {
                    return hashAlg;
                }
            }
            return MD5;// 不存在时使用MD5
        }

    }

    /**
     * 方法说明：计算数字签名
     * 
     * @param timeStamp
     *            时间戳，格式YYYYMMDDhhmmss共14位
     * @param macKey
     *            双方约定的密钥
     * @param hashAlg
     *            哈希算法编码
     * @param msg
     *            待签名原信息
     * @return
     */
    public static String genMsgMac(String timeStamp, String macKey, String hashAlg, String msg) {
        String macHexValue = null;
        try {
            // 通过编码获取对应算法
            HashAlgorithm hashAlgorith = HashAlgorithm.getByCode(hashAlg);
            // 计算消息摘要
            byte[] signSrc = hashEncrypt(msg, hashAlgorith.getAlgorithm());
            // 将消息摘要转为十六进制字符串，作为加密原文
            String content = bytes2Hex(signSrc);
            // 用约定密钥拼接时间戳再拼接80的字符串，得到的结果作为加密消息摘要的密钥
            String password = macKey + timeStamp + "80";
            // 用最终的密钥对消息摘要进行aes加密，得到的结果转为十六进制字符串就为数字签名值
            macHexValue = bytes2Hex(aesEncrypt(content, password, hashAlgorith.isEncryptKey()));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("计算数字签名时发生异常！");
        }
        return macHexValue;
    }

    /**
     * 方法说明：验证签名
     * 
     * @param timeStamp
     *            时间戳，与原报文一致的值，格式YYYYMMDDhhmmss共14位
     * @param macKey
     *            双方约定的密钥
     * @param hashAlg
     *            哈希算法
     * @param msg
     *            接收到的明文信息
     * @param msgMac
     *            数字签名值
     * @return
     * @throws Exception
     */
    public static boolean verifyMsgMac(String timeStamp, String macKey, String hashAlg, String msg,
            String msgMac) {
        // 用约定密钥拼接时间戳再拼接80的字符串，得到的结果作为解密消息摘要的密钥
        String password = macKey + timeStamp + "80";
        try {
            // 通过编码获取对应算法
            HashAlgorithm hashAlgorith = HashAlgorithm.getByCode(hashAlg);
            // 将数字签名从十六进制转字节数组，再根据上一步计算的密钥解密得到消息摘要
            String messageDigest1 = new String(aesDecrypt(Hex2bytes(msgMac), password, hashAlgorith.isEncryptKey()), CHARSET);
            String messageDigest2 = bytes2Hex(hashEncrypt(msg, hashAlgorith.getAlgorithm()));
            if (messageDigest1.compareTo(messageDigest2) == 0) {
                return true;
            }
            return false;
        } catch (BadPaddingException e) {
            e.printStackTrace();
            System.out.println("验签名发生BadPaddingException异常，可能是密钥错误或者被数据被修改过导致，判断为验签名不通过");
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("验签名发生异常，判断为验签名不通过");
            return false;
        }
    }
    
    /**
     * 方法说明：AES加密
     * 
     * @param content
     *            加密原文
     * @param password
     *            加密密钥
     * @return
     * @throws Exception
     */
    private static byte[] aesEncrypt(String content, String password, boolean isEncryptKey)
            throws Exception {
        SecretKeySpec key = null;
        if (isEncryptKey) {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes(CHARSET));
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            key = new SecretKeySpec(enCodeFormat, "AES");
        } else {
            // 对密钥做MD5并取前16位值作为密钥
            password = bytes2Hex(hashEncrypt(password, "MD5")).substring(0, 16);
            key = new SecretKeySpec(password.getBytes(), "AES");
        }
        Cipher cipher = Cipher.getInstance("AES");// 创建密码器
        byte[] byteContent = content.getBytes(CHARSET);
        cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
        byte[] result = cipher.doFinal(byteContent);
        return result; // 加密
    }

    /**
     * 方法说明：AES解密
     * 
     * @param content
     *            字节数组格式的密文
     * @param password
     *            解密密钥
     * @return
     * @throws Exception
     */
    private static byte[] aesDecrypt(byte[] content, String password, boolean isEncryptKey)
            throws Exception {
        SecretKeySpec key = null;
        if (isEncryptKey) {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes(CHARSET));
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            key = new SecretKeySpec(enCodeFormat, "AES");
        } else {
            // 对密钥做MD5并取前32位值作为密钥
            password = bytes2Hex(hashEncrypt(password, "MD5")).substring(0, 16);
            key = new SecretKeySpec(password.getBytes(), "AES");
        }
        Cipher cipher = Cipher.getInstance("AES");// 创建密码器
        cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
        byte[] result = cipher.doFinal(content);
        return result; // 加密
    }

    /**
     * 方法说明：用指定的哈希算法计算摘要值
     * 
     * @param strSrc
     *            原信息
     * @param encName
     *            哈希算法
     * @return
     */
    private static byte[] hashEncrypt(String strSrc, String encName) {
        MessageDigest md = null;
        try {
            byte[] bt = strSrc.getBytes(CHARSET);
            if (encName == null || encName.equals("")) {
                encName = "MD5";
            }
            md = MessageDigest.getInstance(encName);
            md.update(bt);
        } catch (UnsupportedEncodingException e1) {
            System.out.println("不支持的编码.");
            e1.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e2) {
            System.out.println("非法的消息摘要算法.");
            e2.printStackTrace();
            return null;
        }
        return md.digest();
    }

    /**
     * 方法说明：十六进制转字节数组
     * 
     * @param hexString
     * @return
     * @throws Exception
     */
    private static byte[] Hex2bytes(String hexString) throws Exception {
        if (hexString.length() % 2 != 0) {
            Exception c = new ArrayIndexOutOfBoundsException();
            throw c;
        }
        String src = hexString.toUpperCase();
        int length = src.length();
        byte[] dst = new byte[length / 2];
        char[] hexChars = src.toCharArray();
        for (int i = 0; i < length; i++) {
            if (i % 2 == 0) {
                dst[i / 2] = (byte) (CHARARRAY.indexOf(hexChars[i]));
            } else {
                dst[i / 2] = (byte) ((dst[i / 2]) << 4 | (CHARARRAY.indexOf(hexChars[i])));
            }
        }
        return dst;
    }

    /**
     * 方法说明：字节数组转十六进制
     * 
     * @param src
     * @return
     */
    private static String bytes2Hex(byte[] src) {
        StringBuffer dst = new StringBuffer();
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String temp = Integer.toHexString(v);
            if (temp.length() == 2) {
                dst.append(temp);
            } else {
                dst.append("0" + temp);
            }
        }
        return dst.toString().toUpperCase();
    }

    public static void main(String[] args) {
		//CustDataInfo data=new CustDataInfo();
		//JsonConfig jsonConfig=new JsonConfig();
		//JSONObject show1=JSONObject.fromObject(data, jsonConfig);
		
		//String mac= MacUtil.genMsgMac("20171018153015", "c7e05df070ab5933","33",show1.toString());
		//System.out.println(show1.toString());
		//System.out.println(mac);
		String mac1= genMsgMac("20171020114127", "4c2a98ea6138512e","33",":{\"billNo\":\"057901340413\",\"licensePlateNo\":\"粤B9C50B\",\"remark1\":\"1\"}");
		System.out.println(mac1);
		
		String xx = genMsgMac("20171020114256", "4c2a98ea6138512e", "33", "{\"licensePlateNo\":\"粤KE7418\",\"licensePlateType\":\"02\",\"vehicleIdentifyNoLast4\":\"2786\"}");
		System.out.println(xx);
	}
    //timeStamp:20171020103249,key:4c2a98ea6138512e,macAlg:33,msg:{"licensePlateNo":"粤BS463S","licensePlateType":"02","vehicleIdentifyNoLast4":"8674"},mac=EB4382328D2CE49135FA0BB039ACA10F9B665EA019C8196E0137DE3DBDF9625070ADDB09C07345D7AC48DFA704EE0D32
    //timeStamp:20171020114256,key:4c2a98ea6138512e,macAlg:33,msg:{"licensePlateNo":"粤KE7418","licensePlateType":"02","vehicleIdentifyNoLast4":"2786"},mac=05B33101C7E22FF888A9094C57E457DCCB31D6DF32877A34271E068172FE61B4EAEE19BC92C6A1C612FFBA0A5EE22568




  

}
