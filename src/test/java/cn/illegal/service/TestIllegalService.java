package cn.illegal.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.illegal.bean.AppealInfoBack;
import cn.illegal.bean.AppealInfoBean;
import cn.illegal.bean.CarInfoBean;
import cn.illegal.bean.CustInfoBean;
import cn.illegal.bean.ElectronicReceiptBean;
import cn.illegal.bean.IllegalInfoBean;

import cn.illegal.bean.ReportingNoParking;

import cn.sdk.bean.BaseBean;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:junit-test.xml" })
public class TestIllegalService {

    @Autowired
    @Qualifier("illegalService")
    private IIllegalService illegalService;
    
    private String openId="123456";
    
    
	@Test
	public void testQueryOfreportingNoParking() {
		try {
			illegalService.recordOfReportingNoParking("粤B6F7M1", "02", "C");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testsingleQueryOfreportingNoParking() {
		try {
			illegalService.singleQueryOfReportingNoParking("10611", "粤B6F7M1", "02", "C");
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
			rp.setNumberPlateNumber("粤B6F7M1");
			rp.setParkingReason("111");
			rp.setParkingSpot("111");
			rp.setPlateType("02");
			rp.setScenePhoto("111");
			rp.setScenePhoto1("22");
			rp.setScenePhoto2("33");
			rp.setScenePhoto3("44");
			rp.setSourceOfCertification("C");
			rp.setStopNoticePhoto("gg");
			illegalService.reportingNoParking(rp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    @Test
    public void illegalPictureQuery() {
    	try {
			illegalService.illegalPictureQuery("1A84ED1DECF55BA7330BB05302BDE6F2","C");
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
	   CarInfoBean carinfo=new CarInfoBean("粤B8Q55T",  "02", "5563");
	   CustInfoBean custinfo=new CustInfoBean("张羽帆", "445222199209020034", "01", "15920050177",  "445222199209020034");

	   List<CarInfoBean> list=new ArrayList<>();
	   list.add(carinfo);
	   String code=illegalService.custRegInfoReceive(custinfo, list,openId);
	   System.out.println("返回結果："+code);
    }

   /**
	 * 查询违法信息根据--根据车牌号
 * @throws Exception 
	 */
   @Test
   public void queryInfoByLicensePlateNo() throws Exception{

	   List<IllegalInfoBean> illegalInfoBeans =  illegalService.queryInfoByLicensePlateNo("粤B8Q55T","01","","oPyqQjheTh8nCsdpQD8WukZv9Uxk");
	   System.out.println(illegalInfoBeans);
   }
   
   /**
	 * 查询违法信息--根据驾驶证
 * @throws Exception 
	 */
   @Test
   public void queryInfoByDrivingLicenceNo() throws Exception{
	   illegalService.queryInfoByDrivingLicenceNo("622822198502074110","440200642519",openId);
   }
   
   
   @Test
   public void  trafficIllegalClaimReg() throws Exception{
	   CarInfoBean carinfo=new CarInfoBean("粤B6A42E",  "02", "5563");
	   CustInfoBean custinfo=new CustInfoBean("张羽帆", "445222199209020034", "01", "15920050177",  "445222199209020034");
	   illegalService.trafficIllegalClaimReg(custinfo, carinfo,openId);
   }
   /**
	 * 打单前查询 
 * @throws Exception 
	 */
   @Test
   public void trafficIllegalClaimBefore() throws Exception{
	   BaseBean bean= illegalService.trafficIllegalClaimBefore("粤B5ZN11","02","13501592159","oPyqQjheTh8nCsdpQD8WukZv9Uxk");
	   System.out.println(bean.getData().toString());
	   
   }
  
   /**
	 * 查询缴费信息
 * @throws Exception 
	 */
   @Test
   public void toQueryPunishmentPage() throws Exception{
	 // String ss= illegalService.toQueryPunishmentPage("931701009747","粤B8A3N2","18601174358",openId);
	  String ss= illegalService.toPayPage("4403010922403405","粤B8A3N2","18601174358",openId);
	  
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
 * @throws Exception 
	 */
   @Test
   public void toGetSubscribeSorts() throws Exception{
	   //illegalService.toGetSubscribeSorts("440319000000");
	   illegalService.isRegisterUser(openId);
   }
    
   /**
    * 预约
 * @throws Exception 
    */
   @Test
   public void toChangeSubscribe() throws Exception{
	   CarInfoBean carinfo=new CarInfoBean("粤B6F7M1",  "002", "9094");
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
   
   @Test
   public void toQueryElectronicReceiptPage() throws Exception{
	   
	  List<ElectronicReceiptBean> list = illegalService.toQueryElectronicReceiptPage("", "粤B8Q55T", "", openId);
	  
	  for (ElectronicReceiptBean electronicReceiptBean : list) {
		System.out.println(electronicReceiptBean.toString());
	  }
   }
}