package cn.illegal.cached;

public interface ICacheKey {
	public int exprieTime = 15 * 24 * 3600;        // redis过期时间全部为15天
	public static final int USER_INFO_REDIS_CACHE_TIME = 15 * 24 * 3600;  //用户信息 redis过期时间全部为15天
	public int maxExpireTime = 30 * 24 * 3600; 
	public static String cachePrefix = "USER_ACCOUNT_";
	public static String USER_INFO_REDIS_KEY = cachePrefix + "userInfo_";
	public static String USER_REDIS_KEY = cachePrefix + "user_";
	public static String USER_WECHAT_INFO_REDIS_KEY = cachePrefix + "wechat_";
}
