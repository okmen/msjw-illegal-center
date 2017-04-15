package cn.illegal.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.illegal.bean.ParamRequestBean;
import cn.illegal.bean.ResultReturnBean;
import cn.sdk.util.DateUtil;
import cn.sdk.util.HttpClientUtil;
import cn.sdk.util.MacUtil;
import net.sf.json.JSONObject;

public class ApiClientUtils {

	private ApiClientUtils(){
		
	}
	
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
	
	 
	 public static ResultReturnBean requestApi(String url,ParamRequestBean paramBean, Object data,String key){
	    /*    Map<String, Object> postData = new HashMap<String, Object>();
	        postData.put("partnerCode", "P003");
	        postData.put("partnerUserId", "123456");
	        postData.put("serialNo", "12345678900987654321");
	        postData.put("macAlg", "33");
	        postData.put("timeStamp", timeStamp);
	        postData.put("data", data);*/
	        
	       
	        JSONObject show1=JSONObject.fromObject(data);
	        String mac= MacUtil.genMsgMac(paramBean.getTimeStamp(), key, paramBean.getMacAlg(), show1.toString());
	        System.out.println(mac);
	        paramBean.setMac(mac);     
	        JSONObject jsons=JSONObject.fromObject(paramBean);
	        System.out.println(jsons);
	
	        String respStr = HttpClientUtil.post(url,jsons.toString());
	        System.out.println("Json:"+respStr);
	        ResultReturnBean result=(ResultReturnBean) JSONObject.toBean(JSONObject.fromObject(respStr),ResultReturnBean.class);		 
		    return  result;
	 }
	 
	 
	 public static void main(String[] args) {
		 String key="1234567890123456";
		 String macAlg="33";
		 String timeStamp=DateUtil.formatDateTimeWithSec(new Date());
		 String url="http://code.stcpay.com:8088/ysth-traffic-front/partnerService/custRegInfoReceive.do";
		 Map<String, Object> show = new HashMap<String, Object>();
	     /*show.put("mobileNo", "18601174358");
	     show.put("licensePlateNo", "粤B6F7M1");
	     show.put("licensePlateType", "2");*/
	     
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
	}
}
