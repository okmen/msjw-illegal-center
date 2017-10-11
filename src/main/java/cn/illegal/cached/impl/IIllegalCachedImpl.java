package cn.illegal.cached.impl;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.illegal.bean.Token;
import cn.illegal.bean.UserRegInfo;
import cn.illegal.bean.WechatUserInfoBean;
import cn.illegal.cached.IIllegalCached;
import cn.illegal.config.IConfig;
import cn.sdk.cache.ICacheManger;
import cn.sdk.serialization.ISerializeManager;



@Service
public class IIllegalCachedImpl implements IIllegalCached{
	protected Logger log = Logger.getLogger(this.getClass());
	
    
    @Value("${refreshTokenTime}")
    private int refreshTokenTime;
    
    @Value("${encyptAccessTokenTime}")
    private int encyptAccessTokenTime;
    
    @Value("${accessTokentime}")
    private int accessTokenTime;
    
    
    //平安接口--合作方代码微信
    @Value("${partnerCodeW}")
    private String  partnerCodeW;
  //平安接口--合作方代码支付宝
    @Value("${partnerCodeZ}")
    private String  partnerCodeZ;
    @Value("${partnerUrl}")
    private String  partnerUrl;
    //平安接口--合作方唯一标识
    @Value("${partnerUserId}")
    private String partnerUserId;
    //平安接口--秘钥微信
    @Value("${partnerKeyW}")
    private String partnerKeyW;
  //平安接口--秘钥微信
    @Value("${partnerKeyZ}")
    private String partnerKeyZ;
    //平安接口--签名算法
    @Value("${partnerMacAlg}")
    private String partnerMacAlg;
    
    //违法预约接口--ip
    @Value("${subcribeLrip}")
    private String subcribeLrip;
    //违法预约接口--mac
    @Value("${subcribeLrmac}")
    private String subcribeLrmac;
    //违法预约接口--用户名
    @Value("${subcribeUserid}")
    private String subcribeUserid;
    //违法预约接口--密码
    @Value("${subcribeUserpwd}")
    private String subcribeUserpwd;
  //违法预约接口--密码
    @Value("${subcribeUrl}")
    private String subcribeUrl;
    
    
    //警司通接口--请求地址
    @Value("${policeUrl}")
    private String policeUrl;
    //警司通接口--方法
    @Value("${policeMethod}")
    private String policeMethod;
    //警司通接口--秘钥
    @Value("${policeKey}")
    private String policeKey;
    //警司通接口--用户名
    @Value("${policeUserid}")
    private String policeUserid;
    //警司通接口--密码
    @Value("${policeUserpwd}")
    private String policeUserpwd;
    
    
	@Autowired
	@Qualifier("jedisCacheManagerImpl")
	private ICacheManger<String> cacheManger;
	
	@Autowired
	@Qualifier("jedisCacheManagerImpl")
	private ICacheManger<Object> objectcacheManger;
	
	
	
	
	@Autowired
	private ISerializeManager< Map<String, String> > serializeManager;
	
    public static final String arrayToString(byte[] bytes)
    {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < bytes.length; i++)
        {
            buff.append(bytes[i] + " ");
        }
        return buff.toString();
    }
    
    
    @Override
    public boolean setWechatUserInfoBean(long id, WechatUserInfoBean wechatUserInfoBean){
    	String userRedisKey = USER_WECHAT_INFO_REDIS_KEY + id;
		return objectcacheManger.setByKryo(userRedisKey, wechatUserInfoBean, exprieTime);
    }
	
    @Override
	public WechatUserInfoBean getWechatUserInfoBean(long id){
    	String userRedisKey = USER_WECHAT_INFO_REDIS_KEY + id;
    	return (WechatUserInfoBean) objectcacheManger.getByKryo(userRedisKey, WechatUserInfoBean.class);
	}
	
	

	@Override
	public boolean setUser(long userId, UserRegInfo user) {
		String userRedisKey = USER_REDIS_KEY + userId;
		return objectcacheManger.setByKryo(userRedisKey, user, exprieTime);
	}

	@Override
	public UserRegInfo getUser(long userId) {
		String userRedisKey = USER_REDIS_KEY + userId;
		return (UserRegInfo) objectcacheManger.getByKryo(userRedisKey, UserRegInfo.class);
	}
	
	public Token insertToken(Token token) {
	    cacheManger.set(IConfig.USER_ACCOUNT_ACCESS_TOKEN_REDIS + token.getUserId(), token.getAccessToken(), accessTokenTime);
        cacheManger.set(IConfig.USER_ACCOUNT_REFRESH_TOKEN_REDIS + token.getUserId(), token.getRefreshToken(), refreshTokenTime);
        return token;
    }
    
    public String deleteToken(String userId) {
        cacheManger.del(IConfig.USER_ACCOUNT_ACCESS_TOKEN_REDIS + userId);
        cacheManger.del(IConfig.USER_ACCOUNT_REFRESH_TOKEN_REDIS + userId);
        return "success";
    }   
    
    public Token getToken(String userId)
    {   
        Token token = new Token();
        String accessToken = cacheManger.get(IConfig.USER_ACCOUNT_ACCESS_TOKEN_REDIS + userId);
        String refreshToken = cacheManger.get(IConfig.USER_ACCOUNT_REFRESH_TOKEN_REDIS + userId);
        token.setUserId(userId);
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        return token;
    }
    
    public Token updateAllToken(String userId)
    {
        Token token = getToken(userId);
        cacheManger.set(IConfig.USER_ACCOUNT_ACCESS_TOKEN_REDIS + token.getUserId(), token.getAccessToken(), accessTokenTime);
        cacheManger.set(IConfig.USER_ACCOUNT_REFRESH_TOKEN_REDIS + token.getUserId(), token.getRefreshToken(), refreshTokenTime);      
        return token;
    }
    
    public void updateAccessToken(String userId, String accessToken)
    {
        cacheManger.set(IConfig.USER_ACCOUNT_ACCESS_TOKEN_REDIS + userId, accessToken, accessTokenTime);
    }
    public void updateRefreshToken(String userId, String refreshToken)
    {
        cacheManger.set(IConfig.USER_ACCOUNT_REFRESH_TOKEN_REDIS + userId, refreshToken, refreshTokenTime);        
    }
    
    @Override
    public void insertEncyptAccessToken(String encyptAccessToken, String AccessToken) {
        if(StringUtils.isNotBlank(encyptAccessToken) && StringUtils.isNotBlank(AccessToken)){
            cacheManger.set(String.format(IConfig.ENCYPT_ACCESS_TOKEN_REDIS_KEY, encyptAccessToken), AccessToken, encyptAccessTokenTime);          
        }
    }

    @Override
    public String getAccessTokenFromEncypt(String encyptAccessToken) {
        if(StringUtils.isNotBlank(encyptAccessToken)){
            return cacheManger.get(String.format(IConfig.ENCYPT_ACCESS_TOKEN_REDIS_KEY, encyptAccessToken), encyptAccessTokenTime);            
        }else{
            return null;
        }
    }




	public String getPartnerUserId() {
		return partnerUserId;
	}


	public void setPartnerUserId(String partnerUserId) {
		this.partnerUserId = partnerUserId;
	}



	public String getPartnerMacAlg() {
		return partnerMacAlg;
	}


	public void setPartnerMacAlg(String partnerMacAlg) {
		this.partnerMacAlg = partnerMacAlg;
	}


	public String getSubcribeLrip() {
		return subcribeLrip;
	}


	public void setSubcribeLrip(String subcribeLrip) {
		this.subcribeLrip = subcribeLrip;
	}


	public String getSubcribeLrmac() {
		return subcribeLrmac;
	}


	public void setSubcribeLrmac(String subcribeLrmac) {
		this.subcribeLrmac = subcribeLrmac;
	}


	public String getSubcribeUserid() {
		return subcribeUserid;
	}


	public void setSubcribeUserid(String subcribeUserid) {
		this.subcribeUserid = subcribeUserid;
	}


	public String getSubcribeUserpwd() {
		return subcribeUserpwd;
	}


	public void setSubcribeUserpwd(String subcribeUserpwd) {
		this.subcribeUserpwd = subcribeUserpwd;
	}


	public String getPoliceUrl() {
		return policeUrl;
	}


	public void setPoliceUrl(String policeUrl) {
		this.policeUrl = policeUrl;
	}


	public String getPoliceMethod() {
		return policeMethod;
	}


	public void setPoliceMethod(String policeMethod) {
		this.policeMethod = policeMethod;
	}


	public String getPoliceKey() {
		return policeKey;
	}


	public void setPoliceKey(String policeKey) {
		this.policeKey = policeKey;
	}


	public String getPoliceUserid() {
		return policeUserid;
	}


	public void setPoliceUserid(String policeUserid) {
		this.policeUserid = policeUserid;
	}


	public String getPoliceUserpwd() {
		return policeUserpwd;
	}


	public void setPoliceUserpwd(String policeUserpwd) {
		this.policeUserpwd = policeUserpwd;
	}


	public String getSubcribeUrl() {
		return subcribeUrl;
	}


	public void setSubcribeUrl(String subcribeUrl) {
		this.subcribeUrl = subcribeUrl;
	}


	public String getPartnerUrl() {
		return partnerUrl;
	}


	public void setPartnerUrl(String partnerUrl) {
		this.partnerUrl = partnerUrl;
	}


	public String getPartnerCodeW() {
		return partnerCodeW;
	}


	public void setPartnerCodeW(String partnerCodeW) {
		this.partnerCodeW = partnerCodeW;
	}


	public String getPartnerCodeZ() {
		return partnerCodeZ;
	}


	public void setPartnerCodeZ(String partnerCodeZ) {
		this.partnerCodeZ = partnerCodeZ;
	}


	public String getPartnerKeyW() {
		return partnerKeyW;
	}


	public void setPartnerKeyW(String partnerKeyW) {
		this.partnerKeyW = partnerKeyW;
	}


	public String getPartnerKeyZ() {
		return partnerKeyZ;
	}


	public void setPartnerKeyZ(String partnerKeyZ) {
		this.partnerKeyZ = partnerKeyZ;
	}



}
