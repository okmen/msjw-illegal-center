package cn.illegal.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class HttpClientUtil {
	private static transient final Logger log = Logger.getLogger(HttpClientUtil.class);

	private static MultiThreadedHttpConnectionManager httpClientManager = new MultiThreadedHttpConnectionManager();

	static {
		httpClientManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = httpClientManager.getParams();
		params.setStaleCheckingEnabled(true);
		params.setMaxTotalConnections(200);
		params.setDefaultMaxConnectionsPerHost(80);
		params.setConnectionTimeout(15000);
		params.setSoTimeout(15000);
	}

	public static HttpClient getHttpClient() {
		return new HttpClient(httpClientManager);
	}

	/**
	 * 发送http get请求
	 * 
	 * @param url
	 *            完整的url路径
	 * @param header
	 * 			  header
	 * @return
	 * @throws Exception
	 */
	public static String getResponseHeaderByName(String url,Map<String,String> header,String headerName) {
		GetMethod method = null;
		try {
			HostConfiguration config = new HostConfiguration();
			URL _url = new URL(url);
			int port = _url.getPort() > 0 ? _url.getPort() : 80;
			config.setHost(_url.getHost(), port, _url.getProtocol());
			method = new GetMethod(url);
			if(header!=null){
				for(String key:header.keySet()){
					method.addRequestHeader(key, header.get(key));
				}
			}
			getHttpClient().executeMethod(config, method);
			int statusCode = method.getStatusCode();
			if (statusCode != 200) {
				log.error("statusCode:" + statusCode + ". url:" + url);
			}else{
				return method.getResponseHeader(headerName).getValue();
			}
			//String responseResult = method.getResponseBodyAsString();
			//return responseResult;
		} catch (Exception ex) {
			log.error("error:" + ex.getMessage());
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		return null;
	}
	
	/**
	 * 发送http get请求
	 * 
	 * @param url
	 *            完整的url路径
	 * @return
	 * @throws Exception
	 */
	public static String get(String url) {
		GetMethod method = null;
		try {
			HostConfiguration config = new HostConfiguration();
			URL _url = new URL(url);
			int port = _url.getPort() > 0 ? _url.getPort() : 80;
			config.setHost(_url.getHost(), port, _url.getProtocol());
			method = new GetMethod(url);
			getHttpClient().executeMethod(config, method);
			int statusCode = method.getStatusCode();
			if (statusCode != 200) {
				log.error("statusCode:" + statusCode + ". url:" + url);
			}
			String responseResult = new String(method.getResponseBody(), "utf-8");
			return responseResult;
		} catch (Exception e) {
			log.error("error:" + e);
			e.printStackTrace();
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		return null;
	}

	/**
	 * 发送http post请求
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String post(String url, Map<String,String> postData, String productName) {
		if (url == null || url == null) {
			return null;
		}
		PostMethod post=null;;
		try {
			NameValuePair[] paramPair = null;
			if (postData != null) {
				paramPair = new NameValuePair[postData.size()];
				int i=0;
				for(String key:postData.keySet()){
					NameValuePair nameValuePair=new NameValuePair();
					nameValuePair.setName(key);
					nameValuePair.setValue(postData.get(key));
					paramPair[i]=nameValuePair;
					i++;
				}
			}

			URL _url = new URL(url);
			HostConfiguration config = new HostConfiguration();
			int port = _url.getPort() > 0 ? _url.getPort() : 80;
			config.setHost(_url.getHost(), port, _url.getProtocol());

			post = new PostMethod(url);
			post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			if(StringUtils.isNotEmpty(productName)){
				post.setRequestHeader("product", productName); // setRequestHeader设置必须在setRequestBody之前
				
			}
			if (paramPair != null && paramPair.length > 0) {
				post.setRequestBody(paramPair);
			}

			int result = getHttpClient().executeMethod(config, post);

//			if (log.isDebugEnabled()) {
//				log.debug("HttpClient.executeMethod returns result = [" + result + "]");
//			}

			if (result != 200) {
				log.error("wrong HttpClient.executeMethod post method !");
			}
			String responseResult = post.getResponseBodyAsString();
			return responseResult;
		} catch (Exception exp) {
			log.error("error:" + exp.getMessage());
		}finally {
			if (post != null) {
				post.releaseConnection();
			}
		}
		return null;
	}
	
	public static String post(String url, String data) {
       
        PostMethod post=null;
        
        try {

            URL _url = new URL(url);
            HostConfiguration config = new HostConfiguration();
            int port = _url.getPort() > 0 ? _url.getPort() : 80;
            config.setHost(_url.getHost(), port, _url.getProtocol());

            post = new PostMethod(url);
            post.setRequestEntity(new StringRequestEntity(data, "application/x-www-form-urlencoded", "UTF-8"));

            int result = getHttpClient().executeMethod(config, post);

//            if (log.isDebugEnabled()) {
//                log.debug("HttpClient.executeMethod returns result = [" + result + "]");
//            }

            if (result != 200) {
                log.error("wrong HttpClient.executeMethod post method !");
            }
            String responseResult = post.getResponseBodyAsString();
            return responseResult;
        } catch (Exception e) {
            log.error("error:" + e.getMessage());
        }finally {
            if (post != null) {
                post.releaseConnection();
            }
        }
        
        return null;
    }

	/**
	 * 返回输入流
	 * 
	 * @param is
	 *            InputStream输入流
	 * @return
	 */
	public static String getResponseAsString(HttpMethodBase method) {

		StringBuilder sb = new StringBuilder();
		BufferedReader in;
		try {
			InputStream is = method.getResponseBodyAsStream();
			in = new BufferedReader(new InputStreamReader(is,"GBK"));
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			log.error("error:" + e.getMessage());
		} catch (IOException ioe) {
			log.error("error:" + ioe.getMessage());
		}
		return null;
	}

}
