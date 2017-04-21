package cn.illegal.service.impl;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.illegal.bean.AppealInfoBack;
import cn.illegal.bean.AppealInfoBean;
import cn.illegal.bean.CarInfoBean;
import cn.illegal.bean.CustDataInfo;
import cn.illegal.bean.CustInfoBean;
import cn.illegal.bean.DeviceBean;
import cn.illegal.bean.IllegalInfoBean;
import cn.illegal.bean.IllegalInfoClaim;
import cn.illegal.bean.IllegalInfoSheet;
import cn.illegal.bean.IllegalProcessPointBean;
import cn.illegal.bean.MessageBean;
import cn.illegal.bean.ParamRequestBean;
import cn.illegal.bean.ReservationDay;
import cn.illegal.bean.Sditem;
import cn.illegal.bean.SubcribeBean;
import cn.illegal.bean.ResultReturnBean;
import cn.illegal.bean.ResultReturnBeanA;
import cn.illegal.bean.Token;
import cn.illegal.bean.UserOpenidBean;
import cn.illegal.bean.UserRegInfo;
import cn.illegal.bean.WechatUserInfoBean;
import cn.illegal.cached.impl.IIllegalCachedImpl;
import cn.illegal.config.IConfig;
import cn.illegal.dao.IIllegalDao;
import cn.illegal.orm.DeviceORM;
import cn.illegal.orm.UsernameORM;
import cn.illegal.service.IIllegalService;
import cn.illegal.utils.ApiClientUtils;
import cn.illegal.utils.RandomUtils;
import cn.illegal.utils.TokenGenerater;
import cn.sdk.bean.BaseBean;
import cn.sdk.util.DateUtil;
import cn.sdk.util.HttpClientUtil;
import cn.sdk.util.MacUtil;
import cn.sdk.util.RandomUtil;
import cn.sdk.util.StringUtil;
import cn.sdk.webservice.WebServiceClient;
import net.sf.json.JSONArray;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Service("illegalService")
public class IIllegalServiceImpl implements IIllegalService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IIllegalDao illegalDao;

	@Autowired
	private IIllegalCachedImpl illegalCache;
	
	
	@Override
	public String getMsg(String msg) {
		String result = "-1";
		
		try {
			result = illegalDao.getMsg(msg);
		} catch (Exception e) {
			logger.error("插入wechatUserInfo失败，错误 ＝ ", e);
			throw e;
		}
		
		return result;
	}



	public String isRegisterUser(){
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		String url="http://code.stcpay.com:8088/ysth-traffic-front/partnerService/isRegisterUser.do";
		String key=illegalCache.getPartnerKey();
		String partnerCode=illegalCache.getPartnerCode();
		String partnerUserId=illegalCache.getPartnerUserId();
		String macAlg=illegalCache.getPartnerMacAlg();
		String serionNo=RandomUtil.randomString(20);
		ResultReturnBeanA result=null;
		ParamRequestBean bean=null;
		String isReg="0";
		
		CustDataInfo data=new CustDataInfo();
		//data.setCustInfo(custInfo);
		//data.setCarInfo(carInfo);
		try {
			bean=new ParamRequestBean(partnerCode,partnerUserId ,serionNo, timeStamp, macAlg, null, data);
			result= ApiClientUtils.requestApiA(url,bean,data,key);
			isReg=result.getData().get("isRegister").toString();
			System.out.println(result.getRespCode()+"--"+isReg);
		} catch (Exception e) {
			logger.error("校验客户是否注册失败，ParamRequestBean= "+bean.toString(), e);
			throw e;
		}
				
		return  isReg;
		
	}
	
	
	/**
	 * 已注册客户信息同步
	 */
	public String custRegInfoReceive(CustInfoBean custInfo, List<CarInfoBean> carInfo) {
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		String url="http://code.stcpay.com:8088/ysth-traffic-front/partnerService/custRegInfoReceive.do";
		String key=illegalCache.getPartnerKey();
		String partnerCode=illegalCache.getPartnerCode();
		String partnerUserId=illegalCache.getPartnerUserId();
		String macAlg=illegalCache.getPartnerMacAlg();
		String serionNo=RandomUtil.randomString(20);
		
		CustDataInfo data=new CustDataInfo();
		data.setCustInfo(custInfo);
		data.setCarInfo(carInfo);
		ResultReturnBean result=null;
		ParamRequestBean bean=null;
		try {
			bean=new ParamRequestBean(partnerCode,partnerUserId ,serionNo, timeStamp, macAlg, null, data);

			result= ApiClientUtils.requestApi(url,bean,data,key);
		} catch (Exception e) {
			logger.error("已注册客户信息同步失败，ParamRequestBean= "+bean.toString(), e);
			throw e;
		}
		
		
		return result.getRespCode();
	}

	
	/**
	 * 查询违法信息根据--根据车牌号
	 */
	public  List<IllegalInfoBean>  queryInfoByLicensePlateNo(String licensePlateNo, String licensePlateType,
		String vehicleIdentifyNoLast4) {
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		String url="http://code.stcpay.com:8088/ysth-traffic-front/partnerService/trafficIllegalQuerySync.do";
		String key=illegalCache.getPartnerKey();
		String partnerCode=illegalCache.getPartnerCode();
		String partnerUserId=illegalCache.getPartnerUserId();
		String macAlg=illegalCache.getPartnerMacAlg();
		String serionNo=RandomUtil.randomString(20);
		
		Map<String,String> data=new HashMap<String,String>();
		data.put("licensePlateNo",licensePlateNo);
		data.put("licensePlateType", licensePlateType);
		//data.put("vehicleIdentifyNoLast4", vehicleIdentifyNoLast4);
		
		ResultReturnBean result=null;
		ParamRequestBean bean=null;
		List<IllegalInfoBean> infos=null;
		try {
			bean=new ParamRequestBean(partnerCode,partnerUserId ,serionNo, timeStamp, macAlg, null, data);

			result= ApiClientUtils.requestApi(url,bean,data,key);
			
			if(result.getRespCode().equals("0000")&&result.getData()!=null){			
				infos=(List<IllegalInfoBean>) JSON.parseArray(result.getData().toString(), IllegalInfoBean.class); 
			}

		} catch (Exception e) {
			logger.error("查询违法信息根据（根据车牌号）失败，ParamRequestBean= "+bean.toString(), e);
			throw e;
		}
			
		System.out.println("---"+result.getData());
			
		return infos;
	}


	/**
	 * 查询违法信息--根据驾驶证
	 */
	public List<IllegalInfoBean> queryInfoByDrivingLicenceNo(String drivingLicenceNo, String recordNo) {
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		String url="http://code.stcpay.com:8088/ysth-traffic-front/partnerService/trafficDriverIllegalQuery.do";
		String key=illegalCache.getPartnerKey();
		String partnerCode=illegalCache.getPartnerCode();
		String partnerUserId=illegalCache.getPartnerUserId();
		String macAlg=illegalCache.getPartnerMacAlg();
		String serionNo=RandomUtil.randomString(20);
		
		Map<String,String> data=new HashMap<String,String>();
		data.put("drivingLicenceNo",drivingLicenceNo);
		data.put("vehicleIdentifyNoLast4", recordNo);
		//data.put("vehicleIdentifyNoLast4", vehicleIdentifyNoLast4);
		
		ResultReturnBean result=null;
		List<IllegalInfoBean> infos=null;
		ParamRequestBean bean=null;
		try {
			bean=new ParamRequestBean(partnerCode,partnerUserId ,serionNo, timeStamp, macAlg, null, data);

			result= ApiClientUtils.requestApi(url,bean,data,key);
				
			if(result.getRespCode().equals("0000")&&result.getData()!=null){			
				infos=(List<IllegalInfoBean>) JSON.parseArray(result.getData().toString(), IllegalInfoBean.class); 
			}

		} catch (Exception e) {
			logger.error("查询违法信息根据（根据驾驶证号）失败，ParamRequestBean= "+bean.toString(), e);
			throw e;
		}
	
		System.out.println("---"+result.getData());
		
		return infos;

	}


	/**
	 * 打单注册接口
	 * @return
	 */
	public BaseBean  trafficIllegalClaimReg(CustInfoBean custInfo, CarInfoBean carInfo){
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		String url="http://uat.stcpay.com/ysth-traffic-front/partnerService/trafficIllegalClaimReg.do";
		String key=illegalCache.getPartnerKey();
		String partnerCode=illegalCache.getPartnerCode();
		String partnerUserId=illegalCache.getPartnerUserId();
		String macAlg=illegalCache.getPartnerMacAlg();
		String serionNo=RandomUtil.randomString(20);
		
		Map<String,String> data=new HashMap<String,String>();
		data.put("mobileNo",custInfo.getMobileNo());		
		data.put("licensePlateNo",carInfo.getLicensePlateNo());
		data.put("licensePlateType",carInfo.getLicensePlateType());
		data.put("vehicleIdentifyNoLast4", carInfo.getVehicleIdentifyNoLast4());
		data.put("drivingLicenceNo",custInfo.getDrivingLicenceNo());
		data.put("custName",custInfo.getCustName());
		data.put("certificateNo",custInfo.getCertificateNo());
		data.put("certificateType", custInfo.getCertificateType());
		
		ResultReturnBeanA result=null;
		BaseBean baseBean =new BaseBean();
		ParamRequestBean bean=null;
		try {
			bean=new ParamRequestBean(partnerCode,partnerUserId ,serionNo, timeStamp, macAlg, null, data);

			result= ApiClientUtils.requestApiA(url,bean,data,key);
			baseBean.setCode(result.getRespCode());
			baseBean.setMsg(result.getRespMsg());
			baseBean.setData(result.getData());
		} catch (Exception e) {
			logger.error("打单注册失败，ParamRequestBean= "+bean.toString(), e);
			throw e;
		}

		System.out.println("---"+result.getData());
		return  baseBean;
	}
	
	/**
	 * 打单前查询
	 */
	@Override
	public List<IllegalInfoClaim> trafficIllegalClaimBefore(String licensePlateNo, String licensePlateType, String mobilephone) {
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		String url="http://uat.stcpay.com/ysth-traffic-front/partnerService/trafficIllegalQuery.do";
		String key=illegalCache.getPartnerKey();
		String partnerCode=illegalCache.getPartnerCode();
		String partnerUserId=illegalCache.getPartnerUserId();
		String macAlg=illegalCache.getPartnerMacAlg();
		String serionNo=RandomUtil.randomString(20);
		
		Map<String,String> data=new HashMap<String,String>();
		data.put("mobileNo", mobilephone);
		data.put("licensePlateNo",licensePlateNo);
		data.put("licensePlateType", licensePlateType); 
		List<IllegalInfoClaim> infos=null; 
		ResultReturnBean result=null;
		ParamRequestBean bean=null;
		try {
			bean=new ParamRequestBean(partnerCode,partnerUserId ,serionNo, timeStamp, macAlg, null, data);

			result= ApiClientUtils.requestApi(url,bean,data,key);
				
			if(result.getRespCode().equals("0000")&&result.getData()!=null){			
				infos=(List<IllegalInfoClaim>) JSON.parseArray(result.getData().toString(), IllegalInfoClaim.class); 
			}

		} catch (Exception e) {
			logger.error("违法确认，打单前查询失败，ParamRequestBean= "+bean.toString(), e);
			throw e;
		}
			
		System.out.println("---"+result.getData());
			
		return infos;

	}
	
	
	/**
	 * 确认打单接口
	 */
	@Override
	public IllegalInfoSheet trafficIllegalClaim(String illegalNo) {
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		String url="http://uat.stcpay.com/ysth-traffic-front/partnerService/trafficIllegalClaim.do";
		String key=illegalCache.getPartnerKey();
		String partnerCode=illegalCache.getPartnerCode();
		String partnerUserId=illegalCache.getPartnerUserId();
		String macAlg=illegalCache.getPartnerMacAlg();
		String serionNo=RandomUtil.randomString(20);
		
		Map<String,String> data=new HashMap<String,String>();
		data.put("illegalNo",illegalNo);
		ResultReturnBeanA result=null;
		IllegalInfoSheet info=null;
		ParamRequestBean bean=null;
		try {
			bean=new ParamRequestBean(partnerCode,partnerUserId ,serionNo, timeStamp, macAlg, null, data);

			result= ApiClientUtils.requestApiA(url,bean,data,key);
				
			info=(IllegalInfoSheet) JSON.parseObject(result.getData().toString(), IllegalInfoSheet.class); 

		} catch (Exception e) {
			logger.error("违法确认，打单前查询失败，ParamRequestBean= "+bean.toString(), e);
			throw e;
		}
	
		System.out.println("---"+result.getData());
			
		return info;
	}


	/**
	 * 违法缴费信息
	 * @throws Exception 
	 */
	@Override
	public String toQueryPunishmentPage(String billNo, String licensePlateNo, String mobileNo){
		String url="http://uat.stcpay.com/ysth-traffic-front/punishment/toQueryPunishmentPage.do";
		PostMethod post=null;
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());	
		String key="1234567890000000";//illegalCache.getPartnerKey();
		String partnerCode=illegalCache.getPartnerCode();
		String partnerUserId=illegalCache.getPartnerUserId();
		String macAlg=illegalCache.getPartnerMacAlg();
		String serionNo=RandomUtil.randomString(20);
		
		Map<String,String> data=new HashMap<String,String>();
		data.put("billNo",billNo);
		data.put("licensePlateNo",licensePlateNo);
		data.put("mobileNo",mobileNo);
		data.put("remark1", "1");	
		ParamRequestBean bean=null;

	    String redirectUrl="";
        try {     	
        	bean=new ParamRequestBean(partnerCode,partnerUserId ,serionNo, timeStamp, macAlg, null, data);
     		
     		JSONObject show1=(JSONObject) JSONObject.toJSON(data);
     	    String mac= MacUtil.genMsgMac(bean.getTimeStamp(), key, bean.getMacAlg(), show1.toString());
     	    bean.setMac(mac);     

     	    JSONObject jsons=(JSONObject) JSONObject.toJSON(bean);
     	    
        	URL _url = new URL(url);
            HostConfiguration config = new HostConfiguration();
            int port = _url.getPort() > 0 ? _url.getPort() : 80;
            config.setHost(_url.getHost(), port, _url.getProtocol());

            post = new PostMethod(url);
            post.setRequestEntity(new StringRequestEntity(jsons.toString(), "application/x-www-form-urlencoded", "UTF-8"));
			int result = HttpClientUtil.getHttpClient().executeMethod(config, post);
			if(result==302){
				Header header=post.getResponseHeader("location");
				redirectUrl=header.getValue();
				System.out.println(redirectUrl+"---");
			}
			
		} catch (HttpException e) {
			logger.error("查询缴费信息失败  Http， ParamRequestBean= "+bean.toString(), e);		
		} catch (IOException e) {
			logger.error("查询缴费信息失败  IO，ParamRequestBean= "+bean.toString(), e);
		}
			
		return redirectUrl;
	}


	
	/**
	 * 规费缴费信息
	 */
	public String toPayPage(String illegalNo,String licensePlateNo, String mobileNo) {
		String url="http://uat.stcpay.com/ysth-traffic-front/fee/toQueryFeePage.do";
		PostMethod post=null;
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());	
		String key="1234567890000000";//illegalCache.getPartnerKey();
		String partnerCode=illegalCache.getPartnerCode();
		String partnerUserId=illegalCache.getPartnerUserId();
		String macAlg=illegalCache.getPartnerMacAlg();
		String serionNo=RandomUtil.randomString(20);
		
		Map<String,String> data=new HashMap<String,String>();
		data.put("billNo",illegalNo);
		data.put("licensePlateNo",licensePlateNo);
		data.put("mobileNo",mobileNo);
		data.put("remark1", "1");
		//data.put("remark2", "");
		//data.put("remark3", "");
			
		ParamRequestBean bean=null;

	    String redirectUrl="";
        try {
        	bean=new ParamRequestBean(partnerCode,partnerUserId ,serionNo, timeStamp, macAlg, null, data);
    		
    		JSONObject show1=(JSONObject) JSONObject.toJSON(data);
    	    String mac= MacUtil.genMsgMac(bean.getTimeStamp(), key, bean.getMacAlg(), show1.toString());
    	    
    	   // boolean s=MacUtil.verifyMsgMac(timeStamp, key, bean.getMacAlg(), show1.toString(), mac);
    	    System.out.println(mac);
    	    bean.setMac(mac);     
    	    
    	    JSONObject jsons=(JSONObject) JSONObject.toJSON(bean);
    	    System.out.println(jsons);
    	    
        	URL _url = new URL(url);
            HostConfiguration config = new HostConfiguration();
            int port = _url.getPort() > 0 ? _url.getPort() : 80;
            config.setHost(_url.getHost(), port, _url.getProtocol());

            post = new PostMethod(url);
            post.setRequestEntity(new StringRequestEntity(jsons.toString(), "application/x-www-form-urlencoded", "UTF-8"));
			int result = HttpClientUtil.getHttpClient().executeMethod(config, post);
			if(result==302){
				Header header=post.getResponseHeader("location");
				redirectUrl=header.getValue();
				System.out.println(redirectUrl+"---");
			}
			

		} catch (HttpException e) {
			logger.error("查询规费信息失败  Http， ParamRequestBean= "+bean.toString(), e);		
		} catch (IOException e) {
			logger.error("查询规费信息失败  IO，ParamRequestBean= "+bean.toString(), e);
		}
			
		return redirectUrl;
	}
	
	/**
	 * 获取所以违法处理点
	 */
	public List<IllegalProcessPointBean> getIllegalProcessingPoint(){
		//String xml = "<request><userid>WX02</userid><userpwd>WX02@168</userpwd ><lrip>123.56.180.216</lrip><lrmac>00:16:3e:10:16:4d</lrmac></request>";
		StringBuffer xml=new StringBuffer();
		xml.append("<request>");
		xml.append("<userid>"+illegalCache.getSubcribeUserid()+"</userid>");
		xml.append("<userpwd>"+illegalCache.getSubcribeUserpwd()+"</userpwd >");
		xml.append("<lrip>"+illegalCache.getSubcribeLrip()+"</lrip>");
		xml.append("<lrmac>"+illegalCache.getSubcribeLrmac()+"</lrmac>");
		xml.append("</request>");
		
		String url = illegalCache.getSubcribeUrl();
		
		String method = "getCldbmAll";
		
		List<IllegalProcessPointBean> result=new ArrayList<>();
		JSONObject respStr;
		try {
			respStr = WebServiceClient.easyWebService(url, method, xml.toString());
			JSONObject head=(JSONObject) respStr.get("head");
	         //返回的message
	        // String msg = head.get("message").toString();
	         //返回的状态码
	         String code =head.get("code").toString();
	         
	         JSONObject body=(JSONObject) respStr.get("body");
	         
	         String items = body.get("item").toString();

	         result =(List<IllegalProcessPointBean>) JSON.parseArray(items, IllegalProcessPointBean.class);
	         
			 System.out.println("第一个处理点:"+result.get(0).getCldaddress());
		} catch (Exception e) {
			logger.error("获取所以违法处理点失败 ， XML= "+xml.toString(), e);		
			e.printStackTrace();
		}
			 
		return result;
	}

	


	
	/**
	 * 预约排期信息读取
	 */
	@Override
	public Map toGetSubscribeSorts(String cldbmid) {
		StringBuffer xml =new StringBuffer();
		xml.append("<request>");
		xml.append("<userid>"+illegalCache.getSubcribeUserid()+"</userid>");
		xml.append("<userpwd>"+illegalCache.getSubcribeUserpwd()+"</userpwd >");
		xml.append("<lrip>"+illegalCache.getSubcribeLrip()+"</lrip>");
		xml.append("<lrmac>"+illegalCache.getSubcribeLrmac()+"</lrmac>");
		xml.append("<cldbmid>"+cldbmid+"</cldbmid >");
		xml.append("</request>");
		String url = illegalCache.getSubcribeUrl();
		String method = "getYypqByCldbmid";
		List<ReservationDay> days=new ArrayList<>();
		Map<String,Object> map=new HashMap();
		try {
			JSONObject respStr = WebServiceClient.easyWebService(url, method, xml.toString());
			JSONObject head=(JSONObject) respStr.get("head");
	         //返回的message
	        String snm = head.get("snm").toString();
	         //返回的状态码
	        String code = head.get("code").toString();
	         
	        JSONObject body=(JSONObject) respStr.get("body");
	         
	        String items=body.get("item").toString();
	   
	        days=(List<ReservationDay>) JSON.parseArray(items, ReservationDay.class);
	        
	        map.put("snm", snm);
	        map.put("data", days);
	        System.out.println(days.get(0).getYydate());
		} catch (Exception e) {
			logger.error("预约排期信息读取失败 ， XML= "+xml.toString(), e);
			e.printStackTrace();
		}
		
		return map;
	}
	

	/**
	 * 预约接口
	 */
	@Override
	public BaseBean toChangeSubscribe(String snm,String cldbmid,String cczb_id,CustInfoBean custInfo,CarInfoBean carInfo,String sourceType) {
		StringBuffer xml =new StringBuffer();
		xml.append("<request>");
		xml.append("<userid>"+illegalCache.getSubcribeUserid()+"</userid>");
		xml.append("<userpwd>"+illegalCache.getSubcribeUserpwd()+"</userpwd >");
		xml.append("<lrip>"+illegalCache.getSubcribeLrip()+"</lrip>");
		xml.append("<lrmac>"+illegalCache.getSubcribeLrmac()+"</lrmac>");
		xml.append("<snm>"+snm+"</snm>");
		xml.append("<cldbmid>"+cldbmid+"</cldbmid >");
		xml.append("<cczb_id>"+cczb_id+"</cczb_id>");
		xml.append("<hphm>"+carInfo.getLicensePlateNo()+"</hphm>");
		xml.append("<hpzl>"+carInfo.getLicensePlateType()+"</hpzl>");
		xml.append("<jszh>"+custInfo.getDrivingLicenceNo()+"</jszh>");
		xml.append("<sjhm>"+custInfo.getMobileNo()+"</sjhm>");
		xml.append("<lyfs>"+sourceType+"</lyfs>");
		xml.append("</request>");
		String url = illegalCache.getSubcribeUrl();
		String method = "addYypqSjxr";
		BaseBean bean=new BaseBean();
		try {
			JSONObject respStr = WebServiceClient.easyWebService(url, method, xml.toString());
			JSONObject head=(JSONObject) respStr.get("head");
	         //返回的message
	        String msg = head.get("message").toString();
	         //返回的状态码
	        String code = head.get("code").toString();
	        
	        bean.setCode(code);
	        bean.setMsg(msg);
	        System.out.println(msg);
		} catch (Exception e) {
			logger.error("预约失败 ， XML= "+xml.toString(), e);
			e.printStackTrace();
		}
		return bean;
	}


	/**
	 * 取消预约接口
	 */
	@Override
	public BaseBean toCancleSubscribe(String subscribeNo) {
		StringBuffer xml =new StringBuffer();
		xml.append("<request>");
		xml.append("<userid>"+illegalCache.getSubcribeUserid()+"</userid>");
		xml.append("<userpwd>"+illegalCache.getSubcribeUserpwd()+"</userpwd >");
		xml.append("<lrip>"+illegalCache.getSubcribeLrip()+"</lrip>");
		xml.append("<lrmac>"+illegalCache.getSubcribeLrmac()+"</lrmac>");
		xml.append("<yylsh>"+subscribeNo+"</yylsh> ");
		xml.append("</request>");
		String url = illegalCache.getSubcribeUrl();
		String method = "setYyQxByLsh";
		String result="";
		BaseBean bean=new BaseBean();
		try {
			JSONObject respStr = WebServiceClient.easyWebService(url, method, xml.toString());
			JSONObject head=(JSONObject) respStr.get("head");
	         //返回的message
	        String msg = head.get("message").toString();
	         //返回的状态码
	        String code = head.get("code").toString();
	        bean.setCode(code);
	        bean.setMsg(msg);
		} catch (Exception e) {
			logger.error("取消预约失败 ， XML= "+xml.toString(), e);
			e.printStackTrace();
		}
		return bean;
	}


	/**
	 * 获取预约信息列表
	 */
	@Override
	public List<SubcribeBean> querySubscribe(String licensePlateNo, String licensePlateType, String mobilephone) {
		StringBuffer xml =new StringBuffer();
		xml.append("<request>");
		xml.append("<userid>"+illegalCache.getSubcribeUserid()+"</userid>");
		xml.append("<userpwd>"+illegalCache.getSubcribeUserpwd()+"</userpwd >");
		xml.append("<lrip>"+illegalCache.getSubcribeLrip()+"</lrip>");
		xml.append("<lrmac>"+illegalCache.getSubcribeLrmac()+"</lrmac>");
		xml.append("<hphm>"+licensePlateNo+"</hphm>");
		xml.append("<hpzl>"+licensePlateType+"</hpzl>");
		xml.append("<sjhm>"+mobilephone+"</sjhm>");
		xml.append("</request>");
		String url = illegalCache.getSubcribeUrl();
		String method = "getYycxByLsh";
		List<SubcribeBean> subcribes=new ArrayList<>();
		try {
			JSONObject respStr = WebServiceClient.easyWebService(url, method, xml.toString());
			JSONObject head=(JSONObject) respStr.get("head");
	         //返回的message
	        //String msg = head.get("message").toString();
	         //返回的状态码
	        String code = head.get("code").toString();
	        JSONObject body=(JSONObject) respStr.get("body");
	        if(body!=null){
		        String items=body.get("item").toString();
		 	    if(items.indexOf("[")!=-1){
		 	    	subcribes=(List<SubcribeBean>) JSON.parseArray(items, SubcribeBean.class); 
		 	    }else{
		 	    	SubcribeBean bean=JSON.parseObject(items, SubcribeBean.class);
		 	    	subcribes.add(bean);
		 	    }  
	        }
	        
		} catch (Exception e) {
			logger.error("获取预约信息列表失败 ， XML= "+xml.toString(), e);
			e.printStackTrace();
		}
		return subcribes;
	}


	/**
	 * 提交申诉
	 */
	@Override
	public BaseBean trafficIllegalAppeal(AppealInfoBean info,String identityCard,String userCode,String sourceType) {
		String url = illegalCache.getPoliceUrl(); //webservice请求url
		String method = illegalCache.getPoliceMethod(); //webservice请求方法名称
		String userid = illegalCache.getPoliceUserid(); //webservice登录账号
		String userpwd = illegalCache.getPoliceUserpwd(); //webservice登录密码
		String key = illegalCache.getPoliceKey(); //秘钥
		String jkid="HM1003";
		StringBuffer xml=new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><request>");
		xml.append("<ssrxm>"+info.getClaimant()+"</ssrxm>");
		xml.append("<lxdh>"+info.getClaimantPhone()+"</lxdh>");
		xml.append("<lxdz>"+info.getClaimantAddress()+"</lxdz>");
		xml.append("<ssch>"+info.getLicensePlateNo()+"</ssch>");
		xml.append("<ssnr>"+info.getAppealContent()+"</ssnr>");
		xml.append("<jkbh>"+info.getBillNo()+"</jkbh>");
		xml.append("<sslx>"+info.getAppealType()+"</sslx>");
		xml.append("<wfsj>"+info.getIllegalTime()+"</wfsj>");
		xml.append("<wfdd>"+info.getIllegalAddress()+"</wfdd>");
		xml.append("<zfdw>"+info.getAgency()+"</zfdw>");
		xml.append("<zjtp>"+info.getMaterialPicture()+"</zjtp>");
		xml.append("<ssly>"+sourceType+"</ssly>");//申述来源（ A移动APP C微信Z支付宝E邮政）
		xml.append("<sfzmhm>"+identityCard+"</sfzmhm>");
		xml.append("<xjyhid>"+userid+" </xjyhid>");
		xml.append("<sshpzl>2</sshpzl>");
		xml.append("</request>");
		String code="";
		BaseBean bean =new BaseBean();
		try {
			JSONObject result=WebServiceClient.requestWebService(url, method, jkid, xml.toString(),userid,userpwd, key);
			code=result.get("CODE").toString();
			String msg=result.get("MSG").toString();
			bean.setCode(code);
			bean.setMsg(msg);
		} catch (Exception e) {
			logger.error("申诉信息录入失败 ， XML= "+xml.toString(), e);
			e.printStackTrace();
		}	
		//Map<String, String> map = TransferThirdParty.commitDriverInformationSinglePrintApplicationInterface(applyType, userName, identityCard, mobilephone, sourceOfCertification, url, method, userId, userPwd, key);

		return bean;
	}

	/**
	 * 申诉反馈
	 */
	@Override
	public List<AppealInfoBack> trafficIllegalAppealFeedback(String identityCard,String sourceType) {
		List<AppealInfoBack> info=null;
		String url = illegalCache.getPoliceUrl(); //webservice请求url
		String method = illegalCache.getPoliceMethod(); //webservice请求方法名称
		String userid = illegalCache.getPoliceUserid(); //webservice登录账号
		String userpwd = illegalCache.getPoliceUserpwd(); //webservice登录密码
		String key = illegalCache.getPoliceKey(); //秘钥
		String jkid="sfrz_wfss_jgfk";
		StringBuffer xml=new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><REQUEST>");
		xml.append("<SFZMHM>"+identityCard+"</SFZMHM>");
		xml.append("<YHLY>"+sourceType+"</YHLY>");//用户来源，C-微信；Z-支付宝；A-移动APP	
		xml.append("</REQUEST>");

		try {
			JSONObject result=WebServiceClient.requestWebService(url, method, jkid, xml.toString(),userid,userpwd, key);
			JSONObject body=(JSONObject) result.get("BODY");
		    String items=body.get("ROW").toString();
		    
		    info=(List<AppealInfoBack>) JSON.parseArray(items, AppealInfoBack.class);
			//System.out.println(info.get(0).getLXDZ());
		} catch (Exception e) {
			logger.error("获取申诉反馈信息失败 ， XML= "+xml.toString(), e);
			e.printStackTrace();
		}	
		return info;
	}


	

}
