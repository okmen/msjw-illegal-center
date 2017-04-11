package cn.illegal.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.illegal.bean.DeviceBean;
import cn.illegal.bean.Token;
import cn.illegal.bean.UserOpenidBean;
import cn.illegal.bean.UserRegInfo;
import cn.illegal.bean.WechatUserInfoBean;
import cn.illegal.service.IIllegalService;
import cn.sdk.util.AESNewUtils;
import cn.sdk.util.DESUtils;
import cn.sdk.util.HttpClientUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:junit-test.xml" })
public class TestIllegalService {

    @Autowired
    @Qualifier("illegalService")
    private IIllegalService illegalService;
    
    

	
    @Test
    public void testInsertWechatInfo() {
		WechatUserInfoBean bean = new WechatUserInfoBean();
		bean.setOpenId("test");
		bean.setNickname("testssdsd");
		bean.setHeadImgUrl("test");
		bean.setUpdateTime(new Date());
		int id = illegalService.insertWechatUserInfo(bean);
		Assert.assertTrue(id > 0);
	}
	
	/**
	 * 测试getWechatUserInfoById
	 */
	@Test
	public void testGetWechatUserInfoById() {
		WechatUserInfoBean result = null;
		result = illegalService.getWechatUserInfoById(1);
		Assert.assertTrue(result != null);
	}
	
	/**
	 * 
	 */
	@Test
	public void testAllWechatUserInfoBeanList() {
		WechatUserInfoBean result = null;
		List<WechatUserInfoBean> list = illegalService.getAllWechatUserInfoBeanList();
		System.out.println(list.size());
		Assert.assertTrue(list != null);
	}

//    // 获取用户信息
//    @Test
//    public void testGetUserinfo() {
//
//        UserRegInfo userRegInfo = new UserRegInfo();
//        userRegInfo.setMobilePhone("13177813777");
//        userRegInfo.setGrade(0);
//        userRegInfo.setGrowth(0);
//        userRegInfo.setStatus(0);
//        userRegInfo.setPassword("");
//        userRegInfo.setImg("");
//        userRegInfo.setCreatOn(System.currentTimeMillis() / 1000);
//        userRegInfo.setCouponMoney(0);
//        userRegInfo.setArea("222");
//        UserRegInfo info = illegalService.addNewUser(userRegInfo);
//        Assert.assertNotNull(info);
//        /*
//         * assertEquals(userInfo.containsKey("user_id"), true);
//         * assertEquals(userInfo.get("user_id"), "100001");
//         */
//
//    }
//
//    @Test
//    public void testsynchronized() throws InterruptedException {
//        long mobile = 13977822901L;
//        long time1 = System.currentTimeMillis();
//        for (int i = 0; i < 5; i++) {
//            mobile++;
//            Thread demo1 = new Thread(testGetUserinfo2(String.valueOf(mobile)));
//            demo1.start();
//        }
//        long time2 = System.currentTimeMillis();
//        System.out.println(time2 - time1);
//    }
//
//    public Runnable testGetUserinfo2(String mobile) throws InterruptedException {
//        UserRegInfo userRegInfo = new UserRegInfo();
//        userRegInfo.setMobilePhone(mobile);
//        userRegInfo.setGrade(0);
//        userRegInfo.setGrowth(0);
//        userRegInfo.setStatus(0);
//        userRegInfo.setPassword("");
//        userRegInfo.setImg("");
//        userRegInfo.setCreatOn(System.currentTimeMillis() / 1000);
//        userRegInfo.setCouponMoney(0);
//        userRegInfo.setArea("222");
//        UserRegInfo info = illegalService.addNewUser(userRegInfo);
//        Thread.sleep(2000);
//        return null;
//    }
//
//    @Test
//    public void testAccessToken() throws Exception {
//        long userId = 1;
//        String xxString = DESUtils.encrypt("1280797", "authorlsptime20141225");
//        System.out.println(xxString);
//        Token token = illegalService.getAccessToken(userId);
//        String accessToken =  token.getAccessToken();
////        String accessToken = "fdb853e911899cf3d3dd2c22e2543edb";
//        System.out.println(accessToken);
//        String refreshToken = token.getRefreshToken();
//        System.out.println("refreshToken = "+refreshToken);
////        String realToken = illegalService.getAccessTokenByUserId(userId);
////        System.out.println(realToken);
//        //Q1LDfgB2YXV65PU4uuxelF5CQTBq6rfy2YWcuWRQMKluQgmSgOVxvbPyFWCWBY6MfbEOWupR8kLf====
//        String jiami = AESNewUtils.encrypt(accessToken,accessToken);
//        System.out.println(accessToken);
//        System.out.println(jiami);
//        String jiemi = AESNewUtils.decrypt("xvkgkqN8f5ZvlawTKvuV0Zp47F0Htj4huzBekaCVkKlGP9SCH1tgBXy/cz1s6I", "1d704567d7eccac648f332258ce000e3");
//        
//        System.out.println(jiemi);
////        System.out.println(refreshToken);
////        System.out.println( illegalService.isAccessTokenValidate(accessToken, userId+""));
////        String newToken = illegalService.getAccessTokenByRefreshToken(userId+"", refreshToken).get("accessToken");
////        System.out.println(newToken);
//    }
//    
//    
//    @Test 
//    public void testAddBindOpenid() {
//        UserOpenidBean userOpenidBean = new UserOpenidBean();
//        userOpenidBean.setOpenid("qqqqqq");
//        userOpenidBean.setUserId(121223);
//        userOpenidBean.setBindTime(new Date().getTime()/1000);
//        userOpenidBean.setStatus(1);
//        long flag = illegalService.addBindOpenid(userOpenidBean);
//        Assert.assertEquals(flag, 1);
//    }
//    
//    @Test 
//    public void testCancelBindOpenid() {
//        UserOpenidBean userOpenidBean = new UserOpenidBean();
//        userOpenidBean.setUserId(121223);
//        userOpenidBean.setUnBindTime(new Date().getTime()/1000);
//        userOpenidBean.setStatus(2);
//        long flag = illegalService.cancelBindOpenid(userOpenidBean);
//        Assert.assertEquals(flag, 1);
//    }
//    
//    
//    @Test
//    public void testGetUserIdByOpenid() {
////        Token token =  illegalService.getAccessToken(1);
////        String urlString = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxd4c590c3a2dad288&secret=ad5d9e1fd3161923ba4078885a75a6cd";
////        final String jsonResult = HttpClientUtil.get(urlString);
////        final JSONObject jsonObject = JSON.parseObject(jsonResult);
////        String accessT =  ObjectUtils.defaultIfNull(jsonObject.get("access_token"), StringUtils.EMPTY).toString();
////        System.out.println(accessT);
////        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessT;
////        System.out.println(url);
////        String data = "{" + "\"touser\":" + "\"" + "oAj5WtyhEvr0FilsuXw6WiUt2QQw" + "\"" + "," 
////                   + "\"template_id\":" + "\"" + "oi6UX8x6nHH7ndXrtoqIVs7VFI6GO7MX6EvpyML0I9M" + "\"" + "," 
////                   + "\"url\":" + "\"" + "http://www.choumei.cn/" + "\"" + "," 
////                   + "\"topcolor\":" + "\"" + "#FF0000" + "\"" + "," 
////                   + "\"data\":" + "{" 
////                   + "\"first\":" + "{\"" + "value\":" + "\"" + "现金券已到账！美发不办卡，全城800家品牌店，去哪都是VIP！，使用臭美美发App，享超低折扣价！" + "\"" + "," + "\"" + "color\":" + "\"" + "#173177" + "\"" + "}," 
////                   + "\"keyword1\":" + "{\"" + "value\":" + "\"" + "你成功领取了臭美现金券" + "\"" + "," + "\"" + "color\":" + "\"" + "#173177" + "\"" + "}," 
////                   + "\"keyword2\":" + "{\"" + "value\":" + "\"" + "10元" + "\"" + "," + "\"" + "color\":" + "\"" + "#173177" + "\"" + "}," 
////                   + "\"remark\":" + "{\"" + "value\":" + "\"" + "现金券可直接抵用，消费评价后还可以拿更多红包哦！" + "\"" + "," + "\"" + "color\":" + "\"" + "#173177" + "\"" + "}}}";
////       String postRet = HttpClientUtil.post(url, data);
////       final JSONObject retObject = JSON.parseObject(postRet);
////       String errcode =  ObjectUtils.defaultIfNull(retObject.get("errcode"), StringUtils.EMPTY).toString();
////       if ("0".equals(errcode)) {
////           System.out.println("true");
////       }
////       System.out.println(errcode);
////       System.out.println(retObject.toJSONString());
////        postGetLaiseeMsgToUser("现金券已到账！美发不办卡，全城800家品牌店，去哪都是VIP！，使用臭美美发App，享超低折扣价！", "你成功领取了臭美现金券", 
////                10.1 + "元", "现金券可直接抵用，消费评价后还可以拿更多红包哦！", "oAj5WtyhEvr0FilsuXw6WiUt2QQw", "http://www.choumei.cn/");
//        postGetLaiseeMsgToPostUser("小葡萄领走了您分享的去哪儿红包", "领取金额：1元", "领取时间：2014-10-22 14:00", "为避免您被打扰，只向您推送前3条的领取记录", "oAj5WtyhEvr0FilsuXw6WiUt2QQw", "http://www.choumei.cn/");
//        
//    }
//    
//    private boolean postGetLaiseeMsgToUser(String first, String keyword1, String keyword2, String remark, String openId, String chouMeiUrl) {
//        boolean ret = false;
//        String urlString = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + "wxd4c590c3a2dad288" + "&secret=" + "ad5d9e1fd3161923ba4078885a75a6cd";
//        final String jsonResult = HttpClientUtil.get(urlString);
//        final JSONObject jsonObject = JSON.parseObject(jsonResult);
//        String accessT =  ObjectUtils.defaultIfNull(jsonObject.get("access_token"), StringUtils.EMPTY).toString();
//        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessT;
//        String data = "{" + "\"touser\":" + "\"" + openId + "\"" + "," 
//                   + "\"template_id\":" + "\"" + "oi6UX8x6nHH7ndXrtoqIVs7VFI6GO7MX6EvpyML0I9M" + "\"" + "," 
//                   + "\"url\":" + "\"" + chouMeiUrl + "\"" + "," 
//                   + "\"topcolor\":" + "\"" + "#FF0000" + "\"" + "," 
//                   + "\"data\":" + "{" 
//                   + "\"first\":" + "{\"" + "value\":" + "\"" + first + "\"" + "," + "\"" + "color\":" + "\"" + "#173177" + "\"" + "}," 
//                   + "\"keyword1\":" + "{\"" + "value\":" + "\"" + keyword1 + "\"" + "," + "\"" + "color\":" + "\"" + "#173177" + "\"" + "}," 
//                   + "\"keyword2\":" + "{\"" + "value\":" + "\"" + keyword2 + "\"" + "," + "\"" + "color\":" + "\"" + "#173177" + "\"" + "}," 
//                   + "\"remark\":" + "{\"" + "value\":" + "\"" + remark + "\"" + "," + "\"" + "color\":" + "\"" + "#173177" + "\"" + "}}}";
//       String postRet = HttpClientUtil.post(url, data);
//       final JSONObject retObject = JSON.parseObject(postRet);
//       String errcode =  ObjectUtils.defaultIfNull(retObject.get("errcode"), StringUtils.EMPTY).toString();
//       if ("0".equals(errcode)) {
//           ret = true;
//       }
//       return ret;
//    }
//    
//    
//    private boolean postGetLaiseeMsgToPostUser(String first, String keyword1, String keyword2, String remark, String openId, String chouMeiUrl) {
//        boolean ret = false;
//        String urlString = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + "wxd4c590c3a2dad288" + "&secret=" + "ad5d9e1fd3161923ba4078885a75a6cd";
//        final String jsonResult = HttpClientUtil.get(urlString);
//        final JSONObject jsonObject = JSON.parseObject(jsonResult);
//        String accessT =  ObjectUtils.defaultIfNull(jsonObject.get("access_token"), StringUtils.EMPTY).toString();
//        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessT;
//        String data = "{" + "\"touser\":" + "\"" + openId + "\"" + "," 
//                   + "\"template_id\":" + "\"" + "v9TJDXoNqpSqac-7DZdvvOU4fB2e8glrdPhB6KLTiuE" + "\"" + "," 
//                   + "\"url\":" + "\"" + chouMeiUrl + "\"" + "," 
//                   + "\"topcolor\":" + "\"" + "#FF0000" + "\"" + "," 
//                   + "\"data\":" + "{" 
//                   + "\"first\":" + "{\"" + "value\":" + "\"" + first + "\"" + "," + "\"" + "color\":" + "\"" + "#173177" + "\"" + "}," 
//                   + "\"keyword1\":" + "{\"" + "value\":" + "\"" + keyword1 + "\"" + "," + "\"" + "color\":" + "\"" + "#173177" + "\"" + "}," 
//                   + "\"keyword2\":" + "{\"" + "value\":" + "\"" + keyword2 + "\"" + "," + "\"" + "color\":" + "\"" + "#173177" + "\"" + "}," 
//                   + "\"remark\":" + "{\"" + "value\":" + "\"" + remark + "\"" + "," + "\"" + "color\":" + "\"" + "#173177" + "\"" + "}}}";
//       String postRet = HttpClientUtil.post(url, data);
//       final JSONObject retObject = JSON.parseObject(postRet);
//       String errcode =  ObjectUtils.defaultIfNull(retObject.get("errcode"), StringUtils.EMPTY).toString();
//       if ("0".equals(errcode)) {
//           ret = true;
//       }
//       return ret;
//    }
//    
//    @Test 
//    public void testGetOpenidByUserId() {
//        String openid = illegalService.getOpenidByUserId(878669);
//        //String openid = illegalService.getOpenidByUserId(0);
//        //String openid  = illegalService.getOpenidByUserId(-1);
//        System.out.println(openid);
//        Assert.assertNotNull(openid);
//    }
//
//    
//    @Test 
//    public void testGetDevice() {
//        DeviceBean deviceBean = illegalService.getDevice("w345wrgqery42562346arqgf", 1);
//        System.out.println(deviceBean.getUserId());
//        Assert.assertNotNull(deviceBean);
//    }
//    
//    
//    @Test
//    public void testaddDevice() {
//        //illegalService.addDevice("r124wrerjng42562346advdx", -1, 456321);
//        illegalService.addDevice("r124wrerjng42562346advdx", 2, 456321);
//    }
//    
//    @Test
//    public void testUpdateDevice() {
//        boolean flag = illegalService.updateDevice("r124wrerjng42562346advdx", 2, 789987);
//        System.out.println(flag);
//        Assert.assertTrue(flag);
//    }
    
}
