package cn.illegal.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.illegal.bean.AppealInfoBean;
import cn.illegal.bean.CarInfoBean;
import cn.illegal.bean.CustInfoBean;
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
    
    
    /**
	 * 注册用户信息同步
	 */
   @Test
   public void custRegInfoReceive() {
	   CarInfoBean carinfo=new CarInfoBean("粤B6A42E",  "02", "5563");
	   CustInfoBean custinfo=new CustInfoBean("张羽帆", "445222199209020034", "01", "15920050177",  "445222199209020034");

	   List<CarInfoBean> list=new ArrayList<>();
	   list.add(carinfo);
	   String code=illegalService.custRegInfoReceive(custinfo, list);
	   System.out.println("返回結果："+code);
    }

   /**
	 * 查询违法信息根据--根据车牌号
	 */
   @Test
   public void queryInfoByLicensePlateNo(){
	   illegalService.queryInfoByLicensePlateNo("粤B6A42E","02","9094");
   }
   
   /**
	 * 查询违法信息--根据驾驶证
	 */
   @Test
   public void queryInfoByDrivingLicenceNo(){
	   illegalService.queryInfoByDrivingLicenceNo("622822198502074110","440200642519");
   }
   
   
   @Test
   public void  trafficIllegalClaimReg(){
	   CarInfoBean carinfo=new CarInfoBean("粤B6A42E",  "02", "5563");
	   CustInfoBean custinfo=new CustInfoBean("张羽帆", "445222199209020034", "01", "15920050177",  "445222199209020034");
	   illegalService.trafficIllegalClaimReg(custinfo, carinfo);
   }
   /**
	 * 打单前查询 
	 */
   @Test
   public void trafficIllegalClaimBefore(){
	   illegalService.trafficIllegalClaimBefore("粤B6A42E","02","15920050177");
   }
  
   /**
	 * 查询缴费信息
 * @throws Exception 
	 */
   @Test
   public void toQueryPunishmentPage() throws Exception{
	  String ss= illegalService.toQueryPunishmentPage("931701009747","粤B8A3N2","18601174358");
	  //String ss= illegalService.toPayPage("4403010922403405","粤B8A3N2","18601174358");
	  
	  System.out.println(ss+"--test");
   }
   
   /**
    * 获取违法处理点
 * @throws Exception 
    */
   @Test
   public void getIllegalProcessingPoint() throws Exception{
	   illegalService.getIllegalProcessingPoint();
   }
   
   /**
	 * 预约排期信息读取
	 */
   @Test
   public void toGetSubscribeSorts(){
	   //illegalService.toGetSubscribeSorts("440319000000");
	   illegalService.isRegisterUser();
   }
    
   /**
    * 预约
 * @throws Exception 
    */
   @Test
   public void toChangeSubscribe() throws Exception{
	   CarInfoBean carinfo=new CarInfoBean("粤B6F7M1",  "2", "9094");
	   CustInfoBean custinfo=new CustInfoBean("王玉璞", "622822198502074110", "01", "18601174358",  "622822198502074110");
	   illegalService.toChangeSubscribe("CgQxRtU5pO", "440319000000", "140053", custinfo, carinfo, "003");
   }
   
   /**
    * 取消预约
 * @throws Exception 
    */
   @Test
   public void toCancleSubscribe() throws Exception{
	   illegalService.toCancleSubscribe("1170414100961");
   }
   
   /**
    * 预约查询
 * @throws Exception 
    */
   @Test
   public void querySubscribe() throws Exception{
	   illegalService.querySubscribe("粤B6F7M1",2,"18601174358");
   }
   
   @Test
   public void trafficIllegalAppeal() throws Exception{
	   AppealInfoBean bean=new AppealInfoBean("000000002", "粤B6F7M1","2", "2017-04-11 14:20:24", "深南大道2", "测试用2！", "交警队", "小王", "白石洲", "18601174358", "2", "测试！", "xxx");
	   illegalService.trafficIllegalAppeal(bean, "622822198502074110", "", "C");
   }
   
   @Test
   public void trafficIllegalAppealFeedback() throws Exception{
	   illegalService.trafficIllegalAppealFeedback("622822198502074110", "C");
   }
}