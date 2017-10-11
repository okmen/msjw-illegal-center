package cn.illegal.service.impl;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.illegal.bean.AppealInfoBack;
import cn.illegal.bean.AppealInfoBean;
import cn.illegal.bean.CarInfoBean;
import cn.illegal.bean.CustDataInfo;
import cn.illegal.bean.CustInfoBean;
import cn.illegal.bean.ElectronicReceiptBean;
import cn.illegal.bean.IllegalInfoBean;
import cn.illegal.bean.IllegalInfoClaim;
import cn.illegal.bean.IllegalInfoSheet;
import cn.illegal.bean.IllegalProcessPointBean;
import cn.illegal.bean.ParamRequestBean;
import cn.illegal.bean.RecordOfReportingNoParking;
import cn.illegal.bean.ReportingNoParking;
import cn.illegal.bean.ReservationDay;
import cn.illegal.bean.SubcribeBean;
import cn.illegal.bean.ResultReturnBean;
import cn.illegal.bean.ResultReturnBeanA;
import cn.illegal.cached.impl.IIllegalCachedImpl;
import cn.illegal.dao.IIllegalDao;
import cn.illegal.service.IIllegalService;
import cn.illegal.utils.ApiClientUtils;
import cn.illegal.utils.HttpClientUtil;
import cn.sdk.bean.BaseBean;
import cn.sdk.pingan.OpenApiClient;
import cn.sdk.pingan.OpenApiRsp;
import cn.sdk.util.Base64Encoder;
import cn.sdk.util.DateUtil;
import cn.sdk.util.DateUtil2;
import cn.sdk.util.MD5;
//import cn.sdk.util.HttpClientUtil;
import cn.sdk.util.MacUtil;
import cn.sdk.util.MsgCode;
import cn.sdk.util.RandomUtil;
import cn.sdk.webservice.WebServiceClient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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



	/**
	 * 验证用户是否注册或同步   0-未同步   1-已同步
	 * @throws Exception 
	 */
	public String isRegisterUser(String openId,String sourceOfCertification) throws Exception{
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		String url=illegalCache.getPartnerUrl()+"partnerService/isRegisterUser.do";
		String key="";
		String partnerCode="";
		if("C".equals(sourceOfCertification)){
			key=illegalCache.getPartnerKeyW();
			partnerCode=illegalCache.getPartnerCodeW();
		}else{
			key=illegalCache.getPartnerKeyZ();
			partnerCode=illegalCache.getPartnerCodeZ();
		}
		String partnerUserId=openId;
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
			if(result.getData()==null){
				logger.error("校验客户是否注册失败，ParamRequestBean= "+bean.toString()+"  result:"+result.getData().toString());
			}else{
				isReg=result.getData().get("isRegister").toString();
			}
		} catch (Exception e) {
			logger.error("校验客户是否注册失败，ParamRequestBean= "+bean.toString(), e);
			throw e;
		}
				
		return  isReg;
		
	}
	
	
	/**
	 * 已注册客户信息同步
	 */
	public String custRegInfoReceive(CustInfoBean custInfo, List<CarInfoBean> carInfo,String openId,String sourceOfCertification)throws Exception {
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		String url=illegalCache.getPartnerUrl()+"partnerService/custRegInfoReceive.do";
		String key="";
		String partnerCode="";
		if("C".equals(sourceOfCertification)){
			key=illegalCache.getPartnerKeyW();
			partnerCode=illegalCache.getPartnerCodeW();
		}else{
			key=illegalCache.getPartnerKeyZ();
			partnerCode=illegalCache.getPartnerCodeZ();
		}
		String partnerUserId=openId;
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
	 * @throws Exception 
	 */
	public  BaseBean  queryInfoByLicensePlateNo1(String licensePlateNo, String licensePlateType,
		String vehicleIdentifyNoLast4,String openId,String sourceOfCertification) throws Exception {
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		String url=illegalCache.getPartnerUrl()+"partnerService/trafficIllegalQuerySync.do";
		String key="";
		String partnerCode="";
		if("C".equals(sourceOfCertification)){
			key=illegalCache.getPartnerKeyW();
			partnerCode=illegalCache.getPartnerCodeW();
		}else{
			key=illegalCache.getPartnerKeyZ();
			partnerCode=illegalCache.getPartnerCodeZ();
		}
		String partnerUserId=openId;
		String macAlg=illegalCache.getPartnerMacAlg();
		String serionNo=RandomUtil.randomString(20);
		
		Map<String,String> data=new HashMap<String,String>();
		data.put("licensePlateNo",licensePlateNo);
		data.put("licensePlateType", licensePlateType);
		data.put("vehicleIdentifyNoLast4", vehicleIdentifyNoLast4);
		
		ResultReturnBean result=null;
		ParamRequestBean bean=null;
		BaseBean baseBean =new BaseBean();
		List<IllegalInfoBean> infos=null;
		try {
			bean=new ParamRequestBean(partnerCode,partnerUserId ,serionNo, timeStamp, macAlg, null, data);

			result= ApiClientUtils.requestApi(url,bean,data,key);
			
			if(result.getRespCode().equals("0000")&&result.getData()!=null){			
				infos=(List<IllegalInfoBean>) JSON.parseArray(result.getData().toString(), IllegalInfoBean.class); 
			}

			baseBean.setCode(result.getRespCode());
			baseBean.setData(infos);
			baseBean.setMsg(result.getRespMsg());
		} catch (Exception e) {
			logger.error("查询违法信息根据（根据车牌号）失败，ParamRequestBean= "+bean.toString(), e);
			throw e;
		}
		logger.debug("---"+result.getData());
		
		return baseBean;
	}
	
	/**
	 * 查询违法信息根据--根据车牌号
	 * @throws Exception 
	 */
	public  BaseBean  queryInfoByLicensePlateNo(String licensePlateNo, String licensePlateType,
		String vehicleIdentifyNoLast4,String openId,String sourceOfCertification) throws Exception {
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		String url=illegalCache.getPartnerUrl()+"partnerService/trafficIllegalQuerySync.do";
		String key="";
		String partnerCode="";
		if("C".equals(sourceOfCertification)){
			key=illegalCache.getPartnerKeyW();
			partnerCode=illegalCache.getPartnerCodeW();
		}else{
			key=illegalCache.getPartnerKeyZ();
			partnerCode=illegalCache.getPartnerCodeZ();
		}
		String partnerUserId=openId;
		String macAlg=illegalCache.getPartnerMacAlg();
		String serionNo=RandomUtil.randomString(20);
		
		Map<String,String> data=new HashMap<String,String>();
		data.put("licensePlateNo",licensePlateNo);
		data.put("licensePlateType", licensePlateType);
		data.put("vehicleIdentifyNoLast4", vehicleIdentifyNoLast4);
		
		ResultReturnBean result=null;
		ParamRequestBean bean=null;
		BaseBean baseBean =new BaseBean();
		List<IllegalInfoBean> infos = new ArrayList<IllegalInfoBean>();
		try {
			bean=new ParamRequestBean(partnerCode,partnerUserId ,serionNo, timeStamp, macAlg, null, data);

			result= ApiClientUtils.requestApi(url,bean,data,key);
			
			if(result.getRespCode().equals("0000")&&result.getData()!=null){			
				infos=(List<IllegalInfoBean>) JSON.parseArray(result.getData().toString(), IllegalInfoBean.class); 
			}

			baseBean.setCode(result.getRespCode());
			baseBean.setData(infos);
			baseBean.setMsg(result.getRespMsg());
		} catch (Exception e) {
			logger.error("查询违法信息根据（根据车牌号）失败，ParamRequestBean= "+bean.toString(), e);
			throw e;
		}
		
		logger.debug("---"+result.getData());
		
		if(infos.size() > 0){
			for(IllegalInfoBean illegalInfoBean : infos){
				List<String> illegalImgs = new ArrayList<String>();
				//查询违法图片
				if(StringUtils.isNotBlank(illegalInfoBean.getImgQueryCode())){
					//illegalImgs = illegalPictureQuery(illegalInfoBean.getImgQueryCode());
					//illegalInfoBean.setIllegalImgs(illegalImgs);
				}else{
					//illegalInfoBean.setIllegalImgs(illegalImgs);
				}
			}
		}
		
		//屏蔽微信违法图片显示
		/*if(sourceOfCertification.equals("Z")){
			for(IllegalInfoBean illegalInfoBean : infos){
				illegalInfoBean.setImgQueryCode("");
			}
		}else if(sourceOfCertification.equals("C")){
			for(IllegalInfoBean illegalInfoBean : infos){
				illegalInfoBean.setImgQueryCode("");
			}
		}*/
		
		/*for(IllegalInfoBean illegalInfoBean : infos){
			illegalInfoBean.setImgQueryCode("");
		}*/
		return baseBean;
	}


	/**
	 * 查询违法信息--根据驾驶证
	 * @throws Exception 
	 */
	public List<IllegalInfoBean> queryInfoByDrivingLicenceNo(String drivingLicenceNo, String recordNo,String openId,String sourceOfCertification) throws Exception {
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		String url=illegalCache.getPartnerUrl()+"partnerService/trafficDriverIllegalQuery.do";
		String key="";
		String partnerCode="";
		if("C".equals(sourceOfCertification)){
			key=illegalCache.getPartnerKeyW();
			partnerCode=illegalCache.getPartnerCodeW();
		}else{
			key=illegalCache.getPartnerKeyZ();
			partnerCode=illegalCache.getPartnerCodeZ();
		}
		String partnerUserId=openId;
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
	
		logger.debug("---"+result.getData());
		
		return infos;

	}


	/**
	 * 打单注册接口
	 * @return
	 * @throws Exception 
	 */
	public BaseBean  trafficIllegalClaimReg(CustInfoBean custInfo, CarInfoBean carInfo,String openId,String sourceOfCertification) throws Exception{
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		String url=illegalCache.getPartnerUrl()+"partnerService/trafficIllegalClaimReg.do";
		String key="";
		String partnerCode="";
		if("C".equals(sourceOfCertification)){
			key=illegalCache.getPartnerKeyW();
			partnerCode=illegalCache.getPartnerCodeW();
		}else{
			key=illegalCache.getPartnerKeyZ();
			partnerCode=illegalCache.getPartnerCodeZ();
		}
		String partnerUserId=openId;
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

		logger.debug("---"+result.getData());
		return  baseBean;
	}
	
	/**
	 * 打单前查询
	 * @throws Exception 
	 */
	@Override
	public BaseBean trafficIllegalClaimBefore(String licensePlateNo, String licensePlateType, String mobilephone,String openId,String sourceOfCertification) throws Exception {
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		String url=illegalCache.getPartnerUrl()+"partnerService/trafficIllegalQuery.do";
		String key="";
		String partnerCode="";
		if("C".equals(sourceOfCertification)){
			key=illegalCache.getPartnerKeyW();
			partnerCode=illegalCache.getPartnerCodeW();
		}else{
			key=illegalCache.getPartnerKeyZ();
			partnerCode=illegalCache.getPartnerCodeZ();
		}
		String partnerUserId=openId;
		String macAlg=illegalCache.getPartnerMacAlg();
		String serionNo=RandomUtil.randomString(20);
		BaseBean baseBean=new BaseBean();
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
				
			baseBean.setCode(result.getRespCode());
			baseBean.setMsg(result.getRespMsg());
			baseBean.setData(result.getData());

		} catch (Exception e) {
			logger.error("违法确认，打单前查询失败，ParamRequestBean= "+bean.toString(), e);
			throw e;
		}
			
		logger.debug("---"+result.getData());
			
		return baseBean;

	}
	
	
	/**
	 * 确认打单接口
	 * @throws Exception 
	 */
	@Override
	public IllegalInfoSheet trafficIllegalClaim(String illegalNo,String openId,String sourceOfCertification) throws Exception {
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		String url=illegalCache.getPartnerUrl()+"partnerService/trafficIllegalClaim.do";
		String key="";
		String partnerCode="";
		if("C".equals(sourceOfCertification)){
			key=illegalCache.getPartnerKeyW();
			partnerCode=illegalCache.getPartnerCodeW();
		}else{
			key=illegalCache.getPartnerKeyZ();
			partnerCode=illegalCache.getPartnerCodeZ();
		}
		String partnerUserId=openId;
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
			if(result.getRespCode().equals("0000")&&result.getData()!=null){	
				info=(IllegalInfoSheet) JSON.parseObject(result.getData().toString(), IllegalInfoSheet.class);
			}

		} catch (Exception e) {
			logger.error("违法确认，打单失败，ParamRequestBean= "+bean.toString(), e);
			throw e;
		}
	
		logger.debug("---"+result.getData());
			
		return info;
	}


	/**
	 * 违法缴费信息
	 * @throws Exception 
	 * @throws Exception 
	 */
	@Override
	public String toQueryPunishmentPage(String billNo, String licensePlateNo, String mobileNo,String openId,String sourceOfCertification) throws Exception{
		String url=illegalCache.getPartnerUrl()+"punishment/toQueryPunishmentPage.do";
		PostMethod post=null;
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());	
		String key="";
		String partnerCode="";
		if("C".equals(sourceOfCertification)){
			key=illegalCache.getPartnerKeyW();
			partnerCode=illegalCache.getPartnerCodeW();
		}else{
			key=illegalCache.getPartnerKeyZ();
			partnerCode=illegalCache.getPartnerCodeZ();
		}
		String partnerUserId=openId;
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
			throw e;
		} catch (IOException e) {
			logger.error("查询缴费信息失败  IO，ParamRequestBean= "+bean.toString(), e);
			throw e;
		}
			
		return redirectUrl;
	}


	/**
	 * 扫码查询信息
	 */
	public String callback(String traffData) throws Exception{
		String url ="";    
        try {     	
        	String source="http://gzh.stc.gov.cn/api/illegalHanding/qrCodeToQueryPage.html?traffData="+traffData;
    	    url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxc2b699cf2f919b58"+
    	    "&redirect_uri="+java.net.URLEncoder.encode(source,"utf-8")+
    	    "&response_type=code&scope=snsapi_base&state=123#wechat_redirect"; 
			
		} catch (Exception e) {
			logger.error("微信回调地址拼接失败 ！  traffData "+traffData, e);
			throw e;
		}
			
		return url;
	}
	
	
	/**
	 * 扫码查询信息
	 */
	public String qrCodeToQueryPage(String userName, String traffData, String mobileNo,String openId,String sourceOfCertification) throws Exception{
		String url=illegalCache.getPartnerUrl()+"partnerService/qrCodeToQueryPage.do";
		//String url="http://uat.stcpay.com/ysth-traffic-front/partnerService/qrCodeToQueryPage.do";
		PostMethod post=null;
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());	
		String key="";
		String partnerCode="";
		if("C".equals(sourceOfCertification)){
			key=illegalCache.getPartnerKeyW();
			partnerCode=illegalCache.getPartnerCodeW();
		}else{
			key=illegalCache.getPartnerKeyZ();
			partnerCode=illegalCache.getPartnerCodeZ();
		}
		String partnerUserId=openId;
		String macAlg=illegalCache.getPartnerMacAlg();
		String serionNo=RandomUtil.randomString(20);
		
		Map<String,String> data=new HashMap<String,String>();
		data.put("traffData",traffData);
		data.put("userName",userName);
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
			logger.error("失败  Http， ParamRequestBean= "+bean.toString(), e);		
			throw e;
		} catch (IOException e) {
			logger.error("失败  IO，ParamRequestBean= "+bean.toString(), e);
			throw e;
		}
			
		return redirectUrl;
	}
	
	
	/**
	 * 规费缴费信息
	 * @throws Exception 
	 */
	public String toPayPage(String billNo,String licensePlateNo, String mobileNo,String openId,String sourceOfCertification) throws Exception {
		String url=illegalCache.getPartnerUrl()+"fee/toQueryFeePage.do";
		PostMethod post=null;
		String timeStamp=DateUtil.formatDateTimeWithSec(new Date());	
		String key="";
		String partnerCode="";
		if("C".equals(sourceOfCertification)){
			key=illegalCache.getPartnerKeyW();
			partnerCode=illegalCache.getPartnerCodeW();
		}else{
			key=illegalCache.getPartnerKeyZ();
			partnerCode=illegalCache.getPartnerCodeZ();
		}
		String partnerUserId=openId;
		String macAlg=illegalCache.getPartnerMacAlg();
		String serionNo=RandomUtil.randomString(20);
		
		Map<String,String> data=new HashMap<String,String>();
		data.put("billNo",billNo);
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
			}
			

		} catch (HttpException e) {
			logger.error("查询规费信息失败  Http， ParamRequestBean= "+bean.toString(), e);		
			throw e;
		} catch (IOException e) {
			logger.error("查询规费信息失败  IO，ParamRequestBean= "+bean.toString(), e);
			throw e;
		}
			
		return redirectUrl;
	}
	
	/**
	 * 获取所以违法处理点
	 * @throws Exception 
	 */
	public List<IllegalProcessPointBean> getIllegalProcessingPoint() throws Exception{
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
			throw e;
		}
			 
		return result;
	}

	


	
	/**
	 * 预约排期信息读取
	 * @throws Exception 
	 */
	@Override
	public Map toGetSubscribeSorts(String cldbmid) throws Exception {
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
			 //返回的状态码
	        String code = head.get("code").toString();
	        String snm="";
	        if(code.equals("0")){
	        	snm = head.get("snm").toString();
	        	JSONObject body=(JSONObject) respStr.get("body");
		         
		        String items=body.get("item").toString();
		   
		        days=(List<ReservationDay>) JSON.parseArray(items, ReservationDay.class);
		        map.put("snm", snm);
		        map.put("data", days);
	        }else{
	        	snm=head.get("message").toString();
	        	map.put("message", snm);
	        }     
	        map.put("code", code);
	        //System.out.println(days.get(0).getYydate());
		} catch (Exception e) {
			logger.error("预约排期信息读取失败 ， XML= "+xml.toString(), e);
			throw e;
		}
		
		return map;
	}
	

	/**
	 * 预约接口
	 * @throws Exception 
	 */
	@Override
	public BaseBean toChangeSubscribe(String snm,String cldbmid,String cczb_id,CustInfoBean custInfo,CarInfoBean carInfo,String sourceType) throws Exception {
		Integer plateType=null;
		try {
			plateType=Integer.parseInt(carInfo.getLicensePlateType());
		} catch (Exception e) {
			plateType=2;
		}
		
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
		xml.append("<hpzl>"+plateType+"</hpzl>");
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
			throw e;
		}
		return bean;
	}


	/**
	 * 取消预约接口
	 * @throws Exception 
	 */
	@Override
	public BaseBean toCancleSubscribe(String subscribeNo) throws Exception {
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
			throw e;
		}
		return bean;
	}


	/**
	 * 获取预约信息列表
	 * @throws Exception 
	 */
	@Override
	public List<SubcribeBean> querySubscribe(String licensePlateNo, int licensePlateType, String mobilephone) throws Exception {
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
			throw e;
		}
		return subcribes;
	}


	/**
	 * 提交申诉
	 * @throws Exception 
	 */
	@Override
	public BaseBean trafficIllegalAppeal(AppealInfoBean info,String identityCard,String userCode,String sourceType) throws Exception {
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
			throw e;
		}	
		//Map<String, String> map = TransferThirdParty.commitDriverInformationSinglePrintApplicationInterface(applyType, userName, identityCard, mobilephone, sourceOfCertification, url, method, userId, userPwd, key);

		return bean;
	}

	/**
	 * 申诉反馈
	 * @throws Exception 
	 */
	@Override
	public List<AppealInfoBack> trafficIllegalAppealFeedback(String identityCard,String sourceType) throws Exception {
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
			throw e;
		}	
		return info;
	}

	/**
	 * 根据图片查询码查询违法图片
	 * @throws Exception 
	 */
	public List<String> illegalPictureQuery(String imgQueryCode,String sourceOfCertification) throws Exception {
		List<String> strings = new ArrayList<String>();
		String url = illegalCache.getPoliceUrl(); //webservice请求url
		String method = illegalCache.getPoliceMethod(); //webservice请求方法名称
		String userid = illegalCache.getPoliceUserid(); //webservice登录账号
		String userpwd = illegalCache.getPoliceUserpwd(); //webservice登录密码
		String key = illegalCache.getPoliceKey(); //秘钥
		String jkid="WFTPCX";
		StringBuffer xml=new StringBuffer();
		xml.append("<request><head>");
		xml.append("<tpcxm>"+imgQueryCode+"</tpcxm>");
		xml.append("<sqly>"+sourceOfCertification+"</sqly>");//用户来源，C-微信；Z-支付宝；A-移动APP	
		xml.append("</head></request>");
		try {
			JSONObject result=WebServiceClient.requestWebService(url, method, jkid, xml.toString(),userid,userpwd, key);
			JSONObject head = (JSONObject) result.get("head");
			if("0000".equals(head.getString("code"))){
				JSONObject body=(JSONObject) result.get("body");
				String wftp = body.getString("wftp");
				wftp = wftp.replaceAll("\n", "");
				strings.add(wftp);
			}	
		} catch (Exception e) {
			logger.error("illegalPictureQuery失败 ， XML= "+xml.toString() + "请求参数imgQueryCode是：" + imgQueryCode, e);
			//throw e;
			return strings;
		}	
		return strings;
	}
	

	/**
	 * 车辆临时停车违停申报
	 */
	public Map<String, String> reportingNoParking(ReportingNoParking reportingNoParking) throws Exception {
		String url = illegalCache.getPoliceUrl(); // webservice请求url
		String method = illegalCache.getPoliceMethod(); // webservice请求方法名称
		String userid = illegalCache.getPoliceUserid(); // webservice登录账号
		String userpwd = illegalCache.getPoliceUserpwd(); // webservice登录密码
		String key = illegalCache.getPoliceKey(); // 秘钥
		Map<String, String> map = new HashMap<>();
		String jkid = "WTSQ01";
		String xml = "<?xml version=\"1.0\" encoding=\"gb2312\" ?><REQUEST><HPHM>"
				+ reportingNoParking.getNumberPlateNumber() + "</HPHM><HPZL>" + reportingNoParking.getPlateType()
				+ "</HPZL><SFZMHM>" + reportingNoParking.getIDcard() + "</SFZMHM><TCDD>"
				+ reportingNoParking.getParkingSpot() + "</TCDD><TCZP>" + reportingNoParking.getScenePhoto() + "</TCZP><YHLY>"
				+ reportingNoParking.getSourceOfCertification() + "</YHLY><OPENID>"+reportingNoParking.getOpenId()
				+ "</OPENID></REQUEST>";
		try {
			JSONObject json = WebServiceClient.requestWebService(url, method, jkid, xml, userid, userpwd, key);
			String code = json.getString("CODE");
			String msg = json.getString("MSG");
			map.put("code", code);
			map.put("msg", msg);
			if ("0000".equals(code)) {
				json = json.getJSONObject("BODY");
				String cid = json.getString("CID");
				map.put("cid", cid);
			}
		} catch (Exception e) {
			logger.error("reportingNoParking 失败 ， xml = " + xml);
			throw e;
		}

		return map;
	}

	/**
	 * 单宗违停申报结果查询
	 */
	public Map<String, Object> singleQueryOfReportingNoParking(String orderNumber, String numberPlateNumber,
			String plateType, String sourceOfCertification) throws Exception {
		String url = illegalCache.getPoliceUrl(); // webservice请求url
		String method = illegalCache.getPoliceMethod(); // webservice请求方法名称
		String userid = illegalCache.getPoliceUserid(); // webservice登录账号
		String userpwd = illegalCache.getPoliceUserpwd(); // webservice登录密码
		String key = illegalCache.getPoliceKey(); // 秘钥
		String jkid = "WTSQ02";
		String xml = "<?xml version=\"1.0\" encoding=\"gb2312\" ?><REQUEST><XH>" + orderNumber + "</XH><HPHM>"
				+ numberPlateNumber + "</HPHM><HPZL>" + plateType + "</HPZL><YHLY>" + sourceOfCertification
				+ "</YHLY></REQUEST>";
		Map<String, Object> map = new HashMap<>();
		try {
			JSONObject json = WebServiceClient.changedWebService(url, method, jkid, xml, userid, userpwd, key);
			String code = json.getString("code");
			String msg = json.getString("msg");
			map.put("code", code);
			map.put("msg", msg);
			if ("0000".equals(code)) {
				RecordOfReportingNoParking recordOfReportingNoParking = new RecordOfReportingNoParking();
				json = json.getJSONObject("body");
				json = json.getJSONObject("ret");
				String description = json.getString("clms");
				String state = json.getString("clzt");
				String numberPlateNumber2 = json.getString("hphm");
				String plateType2 = json.getString("hpzl");
				recordOfReportingNoParking.setDescription(description);
				recordOfReportingNoParking.setState(state);
				recordOfReportingNoParking.setNumberPlateNumber(numberPlateNumber2);
				recordOfReportingNoParking.setPlateType(plateType2);
				map.put("data", recordOfReportingNoParking);
			}
		} catch (Exception e) {
			logger.error("singleQueryOfReportingNoParking 失败 ， xml = " + xml);
			throw e;
		}

		return map;
	}

	/**
	 * 查询申报记录列表查询
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, Object> recordOfReportingNoParking(String numberPlateNumber, String plateType,
			String sourceOfCertification) throws Exception {
		String url = illegalCache.getPoliceUrl(); // webservice请求url
		String method = illegalCache.getPoliceMethod(); // webservice请求方法名称
		String userid = illegalCache.getPoliceUserid(); // webservice登录账号
		String userpwd = illegalCache.getPoliceUserpwd(); // webservice登录密码
		String key = illegalCache.getPoliceKey(); // 秘钥
		String jkid = "WTSQ03";
		String xml = "<?xml version=\"1.0\" encoding=\"gb2312\" ?><REQUEST><HPHM>" + numberPlateNumber + "</HPHM><HPZL>"
				+ plateType + "</HPZL><YHLY>" + sourceOfCertification + "</YHLY></REQUEST>";
		Map<String, Object> map = new HashMap<>();
		try {
			JSONObject json = WebServiceClient.changedWebService(url, method, jkid, xml, userid, userpwd, key);
			String code = json.getString("code");
			String msg = json.getString("msg");
			map.put("code", code);
			map.put("msg", msg);
			if ("0000".equals(code)) {
				// 成功
				json = json.getJSONObject("body");
				List<RecordOfReportingNoParking> list = new ArrayList<>();
				if (json.toJSONString().contains("[")) {
					// 多条
					JSONArray jsonArray = json.getJSONArray("ret");
					Iterator iterator = jsonArray.iterator();
					while (iterator.hasNext()) {
						RecordOfReportingNoParking recordOfReportingNoParking = new RecordOfReportingNoParking();
						JSONObject jsonObject = (JSONObject) iterator.next();
						String numberPlateNumber2 = jsonObject.getString("hphm");
						String plateType2 = jsonObject.getString("hpzl");
						String parkingSpot = jsonObject.getString("tcdd");
						String parkingReason = jsonObject.getString("tcyy");
						String reportTime1 = jsonObject.getString("sqsj");
						Date date2 = DateUtil2.str2date(reportTime1);
						String reportTime = DateUtil2.date2str(date2);
						String sourceOfCertification2 = jsonObject.getString("sqly");
						String IDcard = jsonObject.getString("sqsfzmhm");
						String state = jsonObject.getString("clzt");
						String description = jsonObject.getString("clms");
						String orderNumber = jsonObject.getString("xh");
						recordOfReportingNoParking.setDescription(description);
						recordOfReportingNoParking.setIDcard(IDcard);
						recordOfReportingNoParking.setNumberPlateNumber(numberPlateNumber2);
						recordOfReportingNoParking.setParkingReason(parkingReason);
						recordOfReportingNoParking.setParkingSpot(parkingSpot);
						recordOfReportingNoParking.setPlateType(plateType2);
						recordOfReportingNoParking.setReportTime(reportTime);
						recordOfReportingNoParking.setSourceOfCertification(sourceOfCertification2);
						recordOfReportingNoParking.setState(state);
						recordOfReportingNoParking.setOrderNumber(orderNumber);
						list.add(recordOfReportingNoParking);
					}

				} else {
					RecordOfReportingNoParking recordOfReportingNoParking = new RecordOfReportingNoParking();
					JSONObject jsonObject = json.getJSONObject("ret");
					String numberPlateNumber2 = jsonObject.getString("hphm");
					String plateType2 = jsonObject.getString("hpzl");
					String parkingSpot = jsonObject.getString("tcdd");
					String parkingReason = jsonObject.getString("tcyy");
					String reportTime1 = jsonObject.getString("sqsj");
					Date date2 = DateUtil2.str2date(reportTime1);
					String reportTime = DateUtil2.date2str(date2);
					String sourceOfCertification2 = jsonObject.getString("sqly");
					String IDcard = jsonObject.getString("sqsfzmhm");
					String state = jsonObject.getString("clzt");
					String description = jsonObject.getString("clms");
					String orderNumber = jsonObject.getString("xh");
					recordOfReportingNoParking.setDescription(description);
					recordOfReportingNoParking.setIDcard(IDcard);
					recordOfReportingNoParking.setNumberPlateNumber(numberPlateNumber2);
					recordOfReportingNoParking.setParkingReason(parkingReason);
					recordOfReportingNoParking.setParkingSpot(parkingSpot);
					recordOfReportingNoParking.setPlateType(plateType2);
					recordOfReportingNoParking.setReportTime(reportTime);
					recordOfReportingNoParking.setSourceOfCertification(sourceOfCertification2);
					recordOfReportingNoParking.setState(state);
					recordOfReportingNoParking.setOrderNumber(orderNumber);
					list.add(recordOfReportingNoParking);
				}
				map.put("code", code);
				map.put("data", list);
			} else {
				// 失败
				map.put("code", code);
				map.put("msg", msg);
				map.put("data", null);
			}

		} catch (Exception e) {
			logger.error("recordOfReportingNoParking 失败 ， xml = " + xml);
			throw e;
		}

		return map;
	}

	
	/**
	 * 查询电子回单（规费、缴费）
	 * @throws Exception 
	 * @throws Exception 
	 * @param ideNo  驾驶证或身份证
	 * @param billNo 缴款编号
	 * @param licensePlateNo  车牌号
	 */
	@Override
	public BaseBean toQueryElectronicReceiptPage(String billNo, String licensePlateNo, String ideNo,String sourceOfCertification) throws Exception{
		String GATEWAY =illegalCache.getPartnerUrl()+"openapi/gateway.do";//"http://uat.stcpay.com/gov-traffic-front/openapi/gateway.do";
		String APPKEY="c7e05df070ab5933";//illegalCache.getPartnerKey();
		String APPID="";
		if("C".equals(sourceOfCertification)){
			//key=illegalCache.getPartnerKeyW();
			APPID=illegalCache.getPartnerCodeW();
		}else{
			//key=illegalCache.getPartnerKeyZ();
			APPID=illegalCache.getPartnerCodeZ();
		}
		boolean IS_DATA_ENCYPTY = false;//是否需要对data部分加密
		boolean IS_SIGN = true;//是否校验签名
		// 请求接口
        String method = "eicBillResultQuery";
				
        String data = "{"
            + "\"idNo\":\""+""+"\","
            + "\"licenseNum\":\""+licensePlateNo+"\","
            + "\"billNo\":\""+billNo+"\""
            + "}";
        OpenApiRsp rsp=null;
        List<ElectronicReceiptBean> result =null;
        BaseBean bean=new BaseBean();
        
        logger.info("请求data：" + data);
        try{
	        // 实例化OpenApi客户端
	        OpenApiClient client = new OpenApiClient(GATEWAY, APPID, APPKEY, IS_DATA_ENCYPTY, IS_SIGN);
	        // 发送请求
	        rsp = client.execute(method, data);
	        result =(List<ElectronicReceiptBean>) JSON.parseArray(rsp.getData(), ElectronicReceiptBean.class);
	        if(result==null||result.size()==0){
	        	bean.setCode(MsgCode.businessError);
		        bean.setMsg("未查询到相应的记录");
	        }else{
	        	bean.setCode(rsp.getReturnCode());
		        bean.setData(result);
		        bean.setMsg(rsp.getReturnMsg());
	        }
	        
        }catch (Exception e) {
			logger.error("查询电子回单错误！");
			e.printStackTrace();
			throw e;
		}			
		return bean;
	}



	/**
	 * 查询电子印章
	 * @throws Exception 
	 * @throws Exception 
	 * @param orderId  银行流水号
	 */
	public String szTrafficPoliceElecBillQry(String orderId) throws Exception {
        String url="https://my-uat1.orangebank.com.cn/khpayment/szTrafficPoliceElecBillQry.do";
        String key="szjjhd20170929SDB000000001";
        String md5Str="orderId="+orderId+"&md5Key="+key;
        logger.info("请求data：" + orderId);
        String resultStr=null;
        try{
	       String sign=MD5.MD5Encode(md5Str);
	       String method=url+"?orderId="+orderId+"&sign="+sign.toUpperCase();
	       logger.debug(method);
	        // 发送请求
	       resultStr=method;//HttpClientUtil.get(method);
	       
	      
	      /* FileModel resultStr=HttpRequest.sendPost4File(url, null);
	       InputStream in = resultStr.getInputStream();
	       byte[] bytes = new byte[in.available()];
           // 将文件中的内容读入到数组中
           in.read(bytes);
           
           String  strBase64 = Base64Encoder.encode(bytes);     //将字节流数组转换为字符串
           in.close();
           bean.setData(strBase64);*/
        }catch (Exception e) {
			logger.error("查询电子印章异常！");
			e.printStackTrace();
			throw e;
		}			
		return resultStr;
	}

}
