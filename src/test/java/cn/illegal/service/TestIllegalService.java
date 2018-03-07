package cn.illegal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;

import cn.illegal.bean.AppealInfoBean;
import cn.illegal.bean.CarInfoBean;
import cn.illegal.bean.CustInfoBean;
import cn.illegal.bean.IllegalInfoSheet;
import cn.illegal.bean.ReportingNoParking;

import cn.sdk.bean.BaseBean;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:junit-test.xml" })
public class TestIllegalService {

    @Autowired
    @Qualifier("illegalService")
    private IIllegalService illegalService;
    
    private String openId="123456";
    /**
     * 接收消息
     * @throws Exception
     */
    @Test
    public void testrm() throws Exception{
//    	String eventKey = "qrscene_F01";
//    	String i = eventKey.substring(eventKey.indexOf("F"));
//    	System.out.println(i);
    	illegalService.receiveMessage("F01", "event", "subscribe", "C","123456");
    }
    /**
     * 
     * 申诉结果评价
     * @throws Exception
     */
    @Test
    public void testevaluateResult() throws Exception{
    	BaseBean evaluateResult = illegalService.evaluateResult("3", "31068906", "2", "C");
    	System.out.println(evaluateResult.toJson());
    }
    /**
     * 申诉结果评价查询
     * @throws Exception
     */
    @Test
    public void testqueryEvaluateResult() throws Exception{
    	BaseBean queryEvaluateResult = illegalService.queryEvaluateResult("12345", "C");
    	System.out.println(queryEvaluateResult.toJson());
    }
	@Test
	public void testQueryOfreportingNoParking() {
		try {
			illegalService.recordOfReportingNoParking("粤S9XS12", "02", "C");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testsingleQueryOfreportingNoParking() {
		try {

			illegalService.singleQueryOfReportingNoParking("1077", "粤B6A42E", "02", "A");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 车辆临时停车违停申报
	 */
	@Test
	public void testreportingNoParking() {
		try {
			ReportingNoParking rp = new ReportingNoParking();
			rp.setIDcard("440301199002101119");
			rp.setNumberPlateNumber("粤B701NR");
			rp.setParkingSpot("111");
			rp.setPlateType("02");
			rp.setScenePhoto("33");
			rp.setSourceOfCertification("A");
			rp.setOpenId("123");
			Map<String, String> reportingNoParking = illegalService.reportingNoParking(rp);
			System.out.println(reportingNoParking);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    @Test
    public void illegalPictureQuery() {
    	try {
			List<String> illegalPictureQuery = illegalService.illegalPictureQuery("E112CD652B94C49DFF2E2FFE5252C6E3","C");
			System.out.println(illegalPictureQuery);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
	 * 注册用户信息同步
     * @throws Exception 
	 */
   @Test
   public void custRegInfoReceive() throws Exception {
	  CarInfoBean carinfo=new CarInfoBean("粤B6A42E",  "02", "5563");
	   CustInfoBean custinfo=new CustInfoBean("张羽帆", "445222199209020034", "01", "15920050177",  "445222199209020034");

	   List<CarInfoBean> list=new ArrayList<>();
	   list.add(carinfo);
	   String code=illegalService.custRegInfoReceive(custinfo, list,"oPyqQjheTh8nCsdpQD8WukZv9Uxk","Z");
	   //String code=illegalService.isRegisterUser(openId, "Z");
	   System.out.println("返回結果："+code);
    }

   @Test
   public void isRegisterUser() throws Exception{
	   
	   String code=illegalService.isRegisterUser("oPyqQjheTh8nCsdpQD8WukZv9Uxk", "A");
	   System.out.println("返回結果："+code);
   }
   
   /**
	 * 查询违法信息根据--根据车牌号
 * @throws Exception 
	 */
   @Test
   public void queryInfoByLicensePlateNo() throws Exception{

	   BaseBean illegalInfoBeans =  illegalService.queryInfoByLicensePlateNo("粤B138XL","02","4918","oPyqQjheTh8nCsdpQD8WukZv9Uxk","A");
//	   BaseBean illegalInfoBeans =  illegalService.queryInfoByLicensePlateNo1("粤Z0010港","02","1892",openId,"C");
	   System.out.println(JSON.toJSONString(illegalInfoBeans));

   }
   
   /**
	 * 查询违法信息--根据驾驶证
 * @throws Exception 
	 */
   @Test
   public void queryInfoByDrivingLicenceNo() throws Exception{
	   illegalService.queryInfoByDrivingLicenceNo("511222198201056390","440301446938",openId,"A");
   }
   
   
   @Test
   /**
    * 打单前注册
    * @throws Exception
    */
   public void  trafficIllegalClaimReg() throws Exception{
	   CarInfoBean carinfo=new CarInfoBean("粤B7A5M8",  "02", "4058");
	   CustInfoBean custinfo=new CustInfoBean("谭映月", "445222199209020034", "01", "18565860552",  "445222199209020034");
	   illegalService.trafficIllegalClaimReg(custinfo, carinfo,openId,"A");
   }
   
   
   /**
	 * 打单前查询 
 * @throws Exception 
	 */
   @Test
   public void trafficIllegalClaimBefore() throws Exception{
	   BaseBean bean= illegalService.trafficIllegalClaimBefore("粤BM58H8","02","13798545363","oPyqQjheTh8nCsdpQD8WukZv9Uxk","C");
	   System.out.println(bean.getData().toString());
	   //IllegalInfoSheet a=illegalService.trafficIllegalClaim("4403077901326770", "oPyqQjheTh8nCsdpQD8WukZv9Uxk", "A");
	   //System.out.println(a.toString());
	   //{"licensePlateNo":"粤B7A5M8","licensePlateType":"02","mobileNo":"18565860552"},
   }
  
   /**
	 * 查询缴费信息
 * @throws Exception 
	 */
   @Test
   public void toQueryPunishmentPage() throws Exception{
	 //String ss= illegalService.toQueryPunishmentPage("4403047901832152","粤BQ5F36","15920050177",openId,"Z");
	  String ss= illegalService.toPayPage("011170801B16030","123","123",openId,"A");
	 //IllegalInfoSheet sheet=illegalService.trafficIllegalClaim("4403047901832152",openId,"Z");
	  System.out.println(ss+"--test");
   }
   
   /**
    * 获取违法处理点
 * @throws Exception 
    */
   @Test
   public void getIllegalProcessingPoint() throws Exception{
	   illegalService.getIllegalProcessingPoint("A");
   }
   
   /**
	 * 预约排期信息读取
 * @throws Exception 
	 */
   @Test
   public void toGetSubscribeSorts() throws Exception{
	   illegalService.toGetSubscribeSorts("440319000000","A");
	   //illegalService.isRegisterUser(openId,"A");
   }
    
   /**
    * 预约
 * @throws Exception 
    */
   @Test
   public void toChangeSubscribe() throws Exception{
	   CarInfoBean carinfo=new CarInfoBean("粤B6F7M1",  "002", "9094");
	   CustInfoBean custinfo=new CustInfoBean("王玉璞", "622822198502074110", "01", "18601174358",  "622822198502074110");
	   illegalService.toChangeSubscribe("CgQxRtU5pO", "440319000000", "155091", custinfo, carinfo, "A");
   }
   
   /**
    * 取消预约
 * @throws Exception 
    */
   @Test
   public void toCancleSubscribe() throws Exception{
	   illegalService.toCancleSubscribe("1170414100961","");
   }
   
   /**
    * 预约查询
 * @throws Exception 
    */
   @Test
   public void querySubscribe() throws Exception{
	   illegalService.querySubscribe("粤B6F7M1",2,"18601174358","A");
   }
   
   @Test
   public void trafficIllegalAppeal() throws Exception{
	   AppealInfoBean bean=new AppealInfoBean("000000002", "粤B6F7M1","2", "2017-04-11 14:20:24", "深南大道2", "测试用2！", "交警队", "小王", "白石洲", "18601174358", "2", "测试！", "xxx");
	   BaseBean trafficIllegalAppeal = illegalService.trafficIllegalAppeal(bean, "622822198502074110", "", "C");
	   System.out.println(trafficIllegalAppeal);
   }
   
   @Test
   public void trafficIllegalAppealFeedback() throws Exception{
	   illegalService.trafficIllegalAppealFeedback("445222197912152216", "C");
//	   illegalService.trafficIllegalAppealFeedback("622822198502074110", "A");
   }
   
   @Test
   public void toQueryElectronicReceiptPage() throws Exception{
	   
//	  BaseBean list = illegalService.toQueryElectronicReceiptPage("4403010922368645", "粤BBG041", "","C","01");
	  BaseBean list = illegalService.toQueryElectronicReceiptPage("", "粤BU8296", "","C","01");
	  
	  System.out.println(list.toJson());
   }
   
   @Test
   public void szTrafficPoliceElecBillQry() throws Exception{
	   
	  String list = illegalService.szTrafficPoliceElecBillQry("SDB00000012017070712838356");
	  
	  System.out.println(list);
   }
   
}