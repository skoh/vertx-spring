package com.nemustech.indoornow.user;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.nemustech.common.util.JsonUtil2;
import com.nemustech.indoornow.common.Config;
import com.nemustech.indoornow.user.User;
import com.nemustech.indoornow.user.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Config.class })

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceTest {
	@Autowired
	protected UserService service;

//	@Test
	public void t01_insert() throws Exception {
		User model = new User();
		model.setCmp_no("cmp_no");
		model.setName("name");

		Object result = service.insert(model);
		System.out.println("result: " + result);
	}

//	@Test
	public void t02_list() throws Exception {
		User model = new User();
//		model.setName("name");

		List<User> list = service.list(model);
		System.out.println("list: " + JsonUtil2.toStringPretty(list));
	}

//	@Test
	public void t03_update() throws Exception {
		User model = new User();
		model.setCondition("name = 'name'");
		model.setTitle("title");

		Object result = service.update(model);
		System.out.println("result: " + result);
	}

//	@Test
	public void t04_delete() throws Exception {
		User model = new User();
		model.setCondition("name = 'name'");

		Object result = service.delete(model);
		System.out.println("result: " + result);
	}
}