package com.nemustech.indoornow.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nemustech.common.mapper.CommonMapper;
import com.nemustech.common.service.impl.CommonServiceImpl;
import com.nemustech.indoornow.user.User;
import com.nemustech.indoornow.user.UserMapper;
import com.nemustech.indoornow.user.UserService;

@Service
public class UserServiceImpl extends CommonServiceImpl<User> implements UserService {
	@Autowired
	protected UserMapper mapper;

	@Override
	public CommonMapper<User> getMapper() {
		return mapper;
	}
}