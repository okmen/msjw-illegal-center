package cn.illegal.dao;
import java.util.List;

import cn.illegal.bean.UserOpenidBean;
import cn.illegal.bean.UserRegInfo;
import cn.illegal.bean.WechatUserInfoBean;
import cn.illegal.orm.DeviceORM;
import cn.illegal.orm.UsernameORM;

public interface IIllegalDao {
	
	
	/**
	 * 通过id获取用户微信信息
	 * 
	 * @author 黄泽铭
	 * @param id
	 * @return
	 */
	WechatUserInfoBean getWechatUserInfoById(int id);
	
	List<WechatUserInfoBean> getAllWechatUserInfoBeanList();
	

	/**
	 * 插入微信用户信息
	 * 
	 * @author 黄泽铭
	 * @param wechatUserInfo
	 * @return 成功则返回纪录id，失败返回0
	 */
	int insertWechatUserInfo(WechatUserInfoBean wechatUserInfo);
	
	
	/**
	 * 添加新用户
	 * 
	 * @param userRegInfo
	 * @return
	 */
	public long addNewUser(UserRegInfo userRegInfo);

	/**
	 * 获取username最大值
	 * 
	 * @return
	 */
	public long getMaxUsername();
	
	/**
	 * 获取当前用户名表最大的id
	 * @author nishixiang
	 * @return
	 */
	public UsernameORM createUsername();
	
	/**
	 * 添加信息到表cm_user_openid
	 * @author shengfenglai
	 * @return long
	 */
	public long addBindOpenid(UserOpenidBean userOpenidBean);
	
	/**
	 * 更新绑定微信的状态
	 * @author shengfenglai
	 * @return long
	 */
	public long updateBindOpenidStatus(UserOpenidBean userOpenidBean);
	
	/**
	 * 通过openid拿到userId
	 * @param openid
	 * @return userId
	 */
	public Long getUserIdByOpenid(String openid);
	
	
    /**
     * 通过userId拿到openid
     * 
     * @param userId
     * @return
     */
    public String getOpenidByUserId(long userId);
    
    
    /**
     * 获取Device
     * @param deviceUuid 设备号
     * @param osType 系统类型
     * @return
     */
    public DeviceORM getDevice(String deviceUuid,int osType);
    
    /**
     * 记录设备号
     * @param deviceUuid 设备号
     * @param osType 系统类型
     * @param userId 用户id
     */
    public void addDevice(String deviceUuid,int osType,long userId,long addTime);
    
    /**
     * 更新device
     * @param deviceUuid 设备号
     * @param osType 系统类型
     * @param userId 用户id
     * @return
     */
    public boolean updateDevice(String deviceUuid,int osType,long userId);
    

}
