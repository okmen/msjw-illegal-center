package cn.illegal.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.illegal.bean.ParamRequestBean;
import cn.illegal.bean.ResultReturnBean;
import cn.illegal.bean.ResultReturnBeanA;
import cn.sdk.bean.BaseBean;
import cn.sdk.exception.HttpPingAnException;
import cn.illegal.utils.HttpClientUtil;
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
	
	 
	 public static ResultReturnBean requestApi(String url,ParamRequestBean paramBean, Object data,String key,String baseUrl)throws Exception{
		 ResultReturnBean result=null;
		 //请求返回结果
		 String respStr="";
		 String jsonParamStr="";
	     try {
		    	JSONObject show1=JSONObject.fromObject(data);
		        String mac=getMac(paramBean.getTimeStamp(), key, paramBean.getMacAlg(), show1.toString(),baseUrl);
		        paramBean.setMac(mac);  
		        JSONObject jsons=JSONObject.fromObject(paramBean);
		        jsonParamStr=jsons.toString();
		        long startTime = System.currentTimeMillis();
		        respStr = HttpClientUtil.post(url,jsonParamStr);
	            long endTime = System.currentTimeMillis();
	            long times = endTime - startTime;
	            if(times > 14900){
	            	logger.error(url + "接口执行耗时:" + times + " 毫秒"+"。返回结果："+respStr); 
	            	throw new HttpPingAnException(Integer.valueOf(MsgCode.httpPingAnCallError), MsgCode.httpPingAnCallMsg);
	            }
		        result=(ResultReturnBean) JSONObject.toBean(JSONObject.fromObject(respStr),ResultReturnBean.class);	
			} catch (Exception e) {
				logger.error("平安接口调用错误,url=" + url+";请求参数："+jsonParamStr+";返回结果："+respStr+"。resultJson="+result,e);
	            throw new HttpPingAnException(Integer.valueOf(MsgCode.httpPingAnCallError), MsgCode.httpPingAnCallMsg);
			}       	 
		  return  result;
	 }
	 
	 public static ResultReturnBeanA requestApiA(String url,ParamRequestBean paramBean, Object data,String key,String baseUrl) throws Exception{		        
		 ResultReturnBeanA result=null;
		 //返回请求结果
		 String respStr="";
		 String jsonParamStr="";
	     try {
		    	JSONObject show1=JSONObject.fromObject(data);
		        String mac= getMac(paramBean.getTimeStamp(), key, paramBean.getMacAlg(), show1.toString(),baseUrl);//MacUtil.genMsgMac(paramBean.getTimeStamp(), key, paramBean.getMacAlg(), show1.toString());
		        paramBean.setMac(mac);     
		        JSONObject jsons=JSONObject.fromObject(paramBean);
		        jsonParamStr=jsons.toString(); 
		        long startTime = System.currentTimeMillis();
		        respStr = HttpClientUtil.post(url,jsonParamStr);
	            long endTime = System.currentTimeMillis();
	            long times = endTime - startTime;
	            if(times > 14900){
	            	logger.error(url + "接口执行耗时:" + times + " 毫秒"+"。请求参数："+jsonParamStr+"返回结果："+respStr); 
	            	throw new HttpPingAnException(Integer.valueOf(MsgCode.httpPingAnCallError), MsgCode.httpPingAnCallMsg);
	            }
		        result=(ResultReturnBeanA) JSONObject.toBean(JSONObject.fromObject(respStr),ResultReturnBeanA.class);	
			} catch (Exception e) {
				logger.error("平安接口调用错误,url=" + url+";请求参数："+jsonParamStr+";返回结果："+respStr+"。resultJson="+result,e);
				throw new HttpPingAnException(Integer.valueOf(MsgCode.httpPingAnCallError), MsgCode.httpPingAnCallMsg);
			}       	 
		  return  result;  
		 }
	 
	 
	 public static String getMac(String timeStamp, String macKey, String hashAlg, String msg,String url) throws Exception{		        
		 String respStr="";
		 BaseBean result=new BaseBean();
	     try {
	    	    Map<String,String> data=new HashMap<String,String>();
	    		data.put("timestamp",timeStamp);
	    		data.put("key",macKey);
	    		data.put("hashAlg",hashAlg);
	    		data.put("data",msg);
	    		respStr =HttpClientUtil.post(url,data,null);
	    		result=(BaseBean) JSONObject.toBean(JSONObject.fromObject(respStr),BaseBean.class);	
//	    		logger.info("timeStamp:"+timeStamp+",key:"+macKey+",macAlg:"+hashAlg+",msg:"+msg+",mac="+result.getMsg());
	    	 
			} catch (Exception e) {
				logger.error("获取mac失败,url=" + url+";请求参数：[timestamp:"+timeStamp+",key:"+macKey+",hashAlg:"+hashAlg+",data:"+msg+"]",e);
	            throw e;
			}       	 
		  return  result.getMsg();  
		 }
	 
	 
	 public static void main(String[] args) throws Exception {
	
		String mac= getMac("20171020134651", "c7e05df070ab5933","33","{\"billNo\":\"011171012B20460\",\"mobileNo\":\"13802235270\",\"remark1\":\"1\"}","");

		System.out.println(mac);
	}

	 //timeStamp:20171020134651,key:c7e05df070ab5933,macAlg:33,msg:{"billNo":"011171012B20460","mobileNo":"13802235270","remark1":"1"},mac=5C4C199185459EE189276372D621992DB6C51BAF0934DBD1E5E689F710D558713661B96A3AA4525043857E286A394021

}
