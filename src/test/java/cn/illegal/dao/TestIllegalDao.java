package cn.illegal.dao;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.illegal.bean.UserOpenidBean;
import cn.illegal.dao.IIllegalDao;
import cn.illegal.dao.mapper.IllegalMapper;
import cn.illegal.orm.DeviceORM;
import cn.illegal.orm.UsernameORM;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:junit-test.xml" })
public class TestIllegalDao {

	@Autowired
	private IIllegalDao illegalDao;

	@Autowired
	private IllegalMapper userMapper;

	// 测试新增用户
	@Test
	public void testAddUser() {

//		UserRegInfo ss2 = userDao.getUserByUserId(2);
//		Date date = new Date();
//		String username = "jerrycai " + date.getTime();
//		long result = userDao.addUser(date.getTime(), username, 1, 2);
//		assertEquals(1, result);
//		Map<String, Object> map = new HashMap<>();
//		map.put("username", username);
//		Map<String, String> dbuserinfo = userMapper.getUserByUsername(map);
//		assertEquals(dbuserinfo.get("mobilephone"), date.getTime());
	}
	

}
