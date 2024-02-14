package org.oh.group.impl;

import org.oh.group.Group;
import org.oh.group.GroupMapper;
import org.oh.group.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nemustech.common.mapper.CommonMapper;
import com.nemustech.common.service.impl.CommonServiceImpl;

@Service
public class GroupServiceImpl extends CommonServiceImpl<Group> implements GroupService {
	@Autowired
	protected GroupMapper mapper;

	@Override
	public CommonMapper<Group> getMapper() {
		return mapper;
	}
}