package org.oh.user.impl;

import org.oh.user.User;
import org.oh.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nemustech.common.mapper.CommonMapper;
import com.nemustech.common.service.impl.CommonServiceImpl;
import org.oh.user.UserService;

@Service
public class UserServiceImpl extends CommonServiceImpl<User> implements UserService {
	@Autowired
	protected UserMapper mapper;

	@Override
	public CommonMapper<User> getMapper() {
		return mapper;
	}
}