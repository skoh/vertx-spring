package org.oh.user;

import org.springframework.stereotype.Repository;

import com.nemustech.common.mapper.CommonMapper;

@Repository
public interface UserMapper extends CommonMapper<User> {
}