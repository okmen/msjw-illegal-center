package cn.illegal.utils;
import java.util.HashMap;
import java.util.Map;

import cn.illegal.config.IConfig;
import cn.sdk.util.Base64;
import cn.sdk.util.HMACSHA1;
import cn.sdk.util.MD5;
public class TokenGenerater {
	private static String authSecret;
	public String getAuthSecret() {
		return authSecret;
	}
	public void setAuthSecret(String authSecret) {
		TokenGenerater.authSecret = authSecret;
	}
	/**
	 * 生成accessToken
	 * @param userId
	 * @return
	 */
	public static String generateAccessToken(String userId)
	{		
		return generateKeyBase(userId, IConfig.ACCESS_TOKEN_KEY);
	}
	/**
	 * 生成refreshToken
	 * @param userId
	 * @return
	 */
	public static String generateRefreshToken(String userId)
	{
		return generateKeyBase(userId, IConfig.REFRESH_TOKEN_KEY);
	}
	/**
	 * 生成与authserver交互的token
	 * @param userId
	 * @param timeReturn
	 * @return
	 */
	public static Map<String, String> generateAuthServerToken(String userId)
	{
		long time = System.currentTimeMillis() / 1000;
		String token = Base64.encode(HMACSHA1.getSignature(userId+time, authSecret));
		HashMap<String, String> hMap = new HashMap<String, String>();
		hMap.put("time", Long.toString(time));
		hMap.put("signature", token);
		return hMap;
	}
	/**
	 * 生成accessToken和refreshToken的基础实现
	 * @param userId
	 * @param key
	 * @return
	 */
	private static String generateKeyBase(String userId, String key)
	{
		long time = System.currentTimeMillis();
		int randomNum = (int)Math.round(Math.random() * (999999 - 100000) + 100000);
		String token = MD5.MD5Encode(userId+time+key+randomNum);
		return token;
	}
}
