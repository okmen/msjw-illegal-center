package cn.illegal.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;


import cn.sdk.util.DESUtils;
import cn.sdk.util.HttpClientUtil;
import cn.sdk.util.MacUtil;
import net.sf.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class WebServiceTest {
    /**
    * ????
    * @param wsdl wsdl??
    * @param ns ????
    * @param method ???
    * @param params ??
    * @return
    * @throws Exception
    */
    public synchronized static String accessService(String wsdl, String ns,String method, Map<String, String> params, String result)
        throws Exception {
        //???? 
        String param = getParam(params);
        String soapResponseData = "";

        //??SOAP 
        StringBuffer soapRequestData = new StringBuffer("");
        soapRequestData.append(
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        soapRequestData.append("<soap:Body>");
        soapRequestData.append("<ns1:" + method + " xmlns:ns1=\"" + ns + "\">");
        soapRequestData.append(param);
        soapRequestData.append("</ns1:" + method + ">");
        soapRequestData.append("</soap:Body>" + "</soap:Envelope>");

        PostMethod postMethod = new PostMethod(wsdl);

        // ???Soap???????PostMethod? 
        byte[] b = null;
        InputStream is = null;

        try {
            b = soapRequestData.toString().getBytes("utf-8");
            is = new ByteArrayInputStream(b, 0, b.length);

            RequestEntity re = new InputStreamRequestEntity(is, b.length,
                    "text/xml; charset=UTF-8");
            postMethod.setRequestEntity(re);

            HttpClient httpClient = new HttpClient();
            int status = httpClient.executeMethod(postMethod);
            System.out.println("status:" + status);

            if (status == 200) {
                soapResponseData = getMesage(postMethod.getResponseBodyAsString(),
                        result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }

        return soapResponseData;
    }

    public static String getParam(Map<String, String> params) {
        String param = "";

        if (params != null) {
            Iterator<String> it = params.keySet().iterator();

            while (it.hasNext()) {
                String str = it.next();
                param += ("<" + str + ">");
                param += params.get(str);
                param += ("</" + str + ">");
            }
        }

        return param;
    }

    public static String getMesage(String soapAttachment, String result) {
        System.out.println("response:" + soapAttachment);

        if (result == null) {
            return null;
        }

        if ((soapAttachment != null) && (soapAttachment.length() > 0)) {
            int begin = soapAttachment.indexOf(result);
            begin = soapAttachment.indexOf(">", begin);

            int end = soapAttachment.indexOf("</" + result + ">");
            String str = soapAttachment.substring(begin + 1, end);
            str = str.replaceAll("<", "<");
            str = str.replaceAll(">", ">");

            return str;
        } else {
            return "";
        }
    }

    /**
    * @param args
    */
    /*public static void main(String[] args) {
        try {
        	String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><REQUEST><USERNAME>4312251991222112211</USERNAME><PWD>123456</PWD><YHLY>C</YHLY></REQUEST>";
            Map<String, String> param = new HashMap<String, String>();
            param.put("path", "WX02");
            param.put("admission_pwd", "WX02@168");

            String wsdl = "https://app.stc.gov.cn:8095/yywfcl/services/yywfcl";
            String ns = "https://app.stc.gov.cn:8095/yywfcl/services/yywfcl";
            String method = "getYypqByCldbmid";
            Map<String, String> postData = new HashMap<String, String>();
            postData.put("getYypqByCldbmidRequest","0");
            postData.put("mobileNo", "13800138000");
            postData.put("licensePlateNo", "粤A001");
            String response = accessService(wsdl, ns, method, postData, "result");
            System.out.println("response:" + response);
        	
        	
        	//给方法传递参数，并且调用方法
        	System.out.println("result is "+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

	  public static void main(String[] args) throws Exception {
	        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><REQUEST><USERNAME>4312251991222112211</USERNAME><PWD>123456</PWD><YHLY>C</YHLY></REQUEST>";
	
	        String kk = DESUtils.encrypt(xml, "94D863D9BE7FB032E6A19430CC892610");
	        String url = "http://code.stcpay.com:8088/ysth-traffic-front/partnerService/trafficIllegalQuery.do";
	        Map<String, String> show = new HashMap<String, String>();
	        show.put("mobileNo", "18601174358");
	        show.put("licensePlateNo", "粤B6F7M1");
	        show.put("licensePlateType", "2");
	        Map<String, Object> postData = new HashMap<String, Object>();
	        postData.put("partnerCode", "P003");
	        postData.put("partnerUserId", "123456");
	        postData.put("serialNo", "12345678900987654321");
	        postData.put("macAlg", "33");
	        postData.put("timeStamp", "20170412194200");
	        postData.put("mac", "102FCA8CF2C76927A463FCF8EABAE342DB5EF4FE363B920A");
	        postData.put("data", show);
	        
	       
	        //JSONObject show1=JSONObject.fromObject(show);
	        String mac= MacUtil.genMsgMac("20170412194200", "1234567890123456", "33", show.toString());
	        System.out.println(mac);
	        postData.put("mac", mac);
	        // String json="{\"billNo\": \"000000000001\",\"mobileNo\": \"13800138000\",\"licensePlateNo\": \"粤A001\",\"remark1\": null,\"remark2\": null,\"remark3\": null}";
	 
	        
	        JSONObject jsons=JSONObject.fromObject(postData);
	        System.out.println(jsons);
	
	        String respStr = HttpClientUtil.post(url,jsons.toString());
	        System.out.println(respStr);
	
	    }
}
