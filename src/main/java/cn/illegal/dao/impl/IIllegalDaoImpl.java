package cn.illegal.dao.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.illegal.bean.UserOpenidBean;
import cn.illegal.bean.UserRegInfo;
import cn.illegal.bean.WechatUserInfoBean;
import cn.illegal.dao.IIllegalDao;
import cn.illegal.dao.mapper.IllegalMapper;
import cn.illegal.orm.DeviceORM;
import cn.illegal.orm.UsernameORM;
import cn.illegal.orm.WechatUserInfoORM;

@Repository
public class IIllegalDaoImpl implements IIllegalDao {

	protected Logger log = Logger.getLogger(this.getClass());
	@Autowired
	private IllegalMapper userMapper;
	
	@Override
	public String getMsg(String msg) {
		// TODO Auto-generated method stub
		return msg+"--yes";
	}
	
	


}
