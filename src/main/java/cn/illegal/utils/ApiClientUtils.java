package cn.illegal.utils;

import org.apache.log4j.Logger;

import cn.illegal.bean.ParamRequestBean;
import cn.illegal.bean.ResultReturnBean;
import cn.illegal.bean.ResultReturnBeanA;
import cn.sdk.exception.HttpPingAnException;
import cn.sdk.util.HttpClientUtil;
import cn.sdk.util.MacUtil;
import cn.sdk.util.MsgCode;

import net.sf.json.JSONObject;

public class ApiClientUtils {

	private ApiClientUtils(){
		
	}
	public static final Logger logger= Logger.getLogger(ApiClientUtils.class);
	
	//使用volatile关键字保其可见性  
    volatile private static ApiClientUtils instance = null;
	
	 public static ApiClientUtils getInstance() {  
	        try {    
	            if(instance != null){//懒汉式   
	                  
	            }else{  
	                //创建实例之前可能会有一些准备性的耗时工作   
	                Thread.sleep(300);  
	                synchronized (ApiClientUtils.class) {  
	                    if(instance == null){
	                    	//二次检查  
	                        instance = new ApiClientUtils();  
	                    }  
	                }  
	            }   
	        } catch (InterruptedException e) {   
	            e.printStackTrace();  
	        }  
	        return instance;  
	    }
	
	 
	 public static ResultReturnBean requestApi(String url,ParamRequestBean paramBean, Object data,String key)throws Exception{
		 ResultReturnBean result=null;
	     try {
		    	JSONObject show1=JSONObject.fromObject(data);
		        String mac= MacUtil.genMsgMac(paramBean.getTimeStamp(), key, paramBean.getMacAlg(), show1.toString());
		        paramBean.setMac(mac);     
		        JSONObject jsons=JSONObject.fromObject(paramBean);
		        logger.info("Json"+jsons);
		
		        long startTime = System.currentTimeMillis();
		        String respStr = HttpClientUtil.post(url,jsons.toString());
	            long endTime = System.currentTimeMillis();
	            long times = (endTime - startTime) / 1000;
	            if(times > 5){
	            	logger.info(url + "接口执行耗时:" + times + " 秒");
	            }
		       
	            logger.info("ReturnJson:"+respStr);
		        result=(ResultReturnBean) JSONObject.toBean(JSONObject.fromObject(respStr),ResultReturnBean.class);	
			} catch (Exception e) {
				logger.error("平安接口调用错误,url=" + url,e);
	            throw new HttpPingAnException(Integer.valueOf(MsgCode.httpPingAnCallError), MsgCode.httpPingAnCallMsg);
			}       	 
		  return  result;
	 }
	 
	 public static ResultReturnBeanA requestApiA(String url,ParamRequestBean paramBean, Object data,String key) throws Exception{		        
		 ResultReturnBeanA result=null;
	     try {
		    	JSONObject show1=JSONObject.fromObject(data);
		        String mac= MacUtil.genMsgMac(paramBean.getTimeStamp(), key, paramBean.getMacAlg(), show1.toString());
		        paramBean.setMac(mac);     
		        JSONObject jsons=JSONObject.fromObject(paramBean);
		        logger.info("Json"+jsons);
		
		        long startTime = System.currentTimeMillis();
		        String respStr = HttpClientUtil.post(url,jsons.toString());
	            long endTime = System.currentTimeMillis();
	            long times = (endTime - startTime) / 1000;
	            if(times > 5){
	            	logger.info(url + "接口执行耗时:" + times + " 秒");
	            }
		       
	            logger.info("ReturnJson:"+respStr);
		        result=(ResultReturnBeanA) JSONObject.toBean(JSONObject.fromObject(respStr),ResultReturnBeanA.class);	
			} catch (Exception e) {
				logger.error("平安接口调用错误,url=" + url,e);
	            throw new HttpPingAnException(Integer.valueOf(MsgCode.httpPingAnCallError), MsgCode.httpPingAnCallMsg);
			}       	 
		  return  result;  
		 }
	 
/*	 public static void main(String[] args) {
		 String key="1234567890123456";
		 String macAlg="33";
		 String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		 String url="http://code.stcpay.com:8088/ysth-traffic-front/partnerService/custRegInfoReceive.do";
		 Map<String, Object> show = new HashMap<String, Object>();
	     show.put("mobileNo", "18601174358");
	     show.put("licensePlateNo", "粤B6F7M1");
	     show.put("licensePlateType", "2");
	     
	     Map<String, String> custInfo = new HashMap<String, String>();
	     custInfo.put("custName", "王玉璞");
	     custInfo.put("certificateNo", "622822198502074110");
	     custInfo.put("certificateType", "01");
	     custInfo.put("mobileNo", "18601174358");
	     custInfo.put("drivingLicenceNo", "622822198502074110");
	     
	     
	     List list=new ArrayList<>();
	     Map<String, String> carInfo = new HashMap<String, String>();
	     carInfo.put("vehicleIdentifyNoLast4", "9094");
	     carInfo.put("licensePlateNo", "粤B6F7M1");
	     carInfo.put("licensePlateType", "2");
	     show.put("custInfo", custInfo);
	     list.add(carInfo);
	     show.put("carInfo", list);
	   
	    // String result=ApiClientUtils.requestApi(url, key, macAlg, timeStamp, show);
	}*/
}
