package com.smartwms.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartwms.system.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    User findByUsername(@Param("username") String username);

    @Select("""
            SELECT p.permission_code
            FROM sys_permission p
            INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
            INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id
            WHERE ur.user_id = #{userId} AND p.status = 1
            """)
    List<String> findPermissionsByUserId(@Param("userId") Long userId);
}
