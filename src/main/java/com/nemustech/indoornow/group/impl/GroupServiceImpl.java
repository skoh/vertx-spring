package com.nemustech.indoornow.group.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nemustech.common.mapper.CommonMapper;
import com.nemustech.common.service.impl.CommonServiceImpl;
import com.nemustech.indoornow.group.Group;
import com.nemustech.indoornow.group.GroupMapper;
import com.nemustech.indoornow.group.GroupService;

@Service
public class GroupServiceImpl extends CommonServiceImpl<Group> implements GroupService {
	@Autowired
	protected GroupMapper mapper;

	@Override
	public CommonMapper<Group> getMapper() {
		return mapper;
	}
}