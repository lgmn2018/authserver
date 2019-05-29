package com.lgmn.authserver.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lgmn.common.domain.LgmnUserInfo;
import com.lgmn.userservices.api.dto.*;
import com.lgmn.userservices.api.entity.*;
import com.lgmn.userservices.api.service.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 〈自定义UserDetailService〉
 *
 * @author Curise
 * @create 2018/12/13
 * @since 1.0.0
 */
//@Service(
//        version = "${demo.service.version}",
//        application = "${dubbo.application.id}",
//        protocol = "${dubbo.protocol.id}",
//        registry = "${dubbo.registry.id}"
//)
@Component
public class MyUserDetailService implements UserDetailsService {

    @Reference(version = "${demo.service.version}")
    private LgmnUserEntityService userEntityService;

    @Reference(version = "${demo.service.version}")
    private LgmnRoleEntityService roleEntityService;

    @Reference(version = "${demo.service.version}")
    private LgmnUserRoleEntityService userRoleEntityService;

    @Reference(version = "${demo.service.version}")
    private LgmnPermissionEntityService permissionEntityService;

    @Reference(version = "${demo.service.version}")
    private LgmnRolePermissionEntityService rolePermissionEntityService;

    @Override
    public UserDetails loadUserByUsername(String memberName) throws UsernameNotFoundException {
        LgmnUserEntity userEntity = getLgmnUserEntity(memberName);
        if (userEntity == null) {
            throw new UsernameNotFoundException("没有该用户");
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        // 可用性 :true:可用 false:不可用
        boolean enabled = true;
        // 过期性 :true:没过期 false:过期
        boolean accountNonExpired = true;
        // 有效性 :true:凭证有效 false:凭证无效
        boolean credentialsNonExpired = true;
        // 锁定性 :true:未锁定 false:已锁定
        boolean accountNonLocked = true;

        List<LgmnRoleEntity> roleList = getRoles(userEntity.getId());

        if (roleList != null) {
            for (LgmnRoleEntity role : roleList) {
                //角色必须是ROLE_开头，可以在数据库中设置
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getName());
                grantedAuthorities.add(grantedAuthority);

                List<LgmnPermissionEntity> permissionList = getPermissions(roleList);
                //获取权限
                if (permissionList != null) {
                    for (LgmnPermissionEntity permission : permissionList) {
                        GrantedAuthority authority = new SimpleGrantedAuthority(permission.getName());
                        grantedAuthorities.add(authority);
                    }
                }
            }
        }
        User user = new User(userEntity.getAccount(), userEntity.getPassword(),
                enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, grantedAuthorities);
        return user;
    }

    public LgmnUserEntity getLgmnUserEntity(String memberName) {
        LgmnUserDto userDto = new LgmnUserDto();
        userDto.setAccount(memberName);
        List<LgmnUserEntity> userEntitys = null;
        try {
            userEntitys = userEntityService.getListByDto(userDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (userEntitys.size() <= 0) {
           return null;
        }
        LgmnUserEntity lgmnUserEntity = userEntitys.get(0);
        return lgmnUserEntity;
    }

    public LgmnUserInfo getLgmnUserInfo(String memberName) {
        LgmnUserEntity lgmnUserEntity = getLgmnUserEntity(memberName);
        LgmnUserInfo lgmnUserInfo = new LgmnUserInfo();
        lgmnUserInfo.setId(lgmnUserEntity.getId());
        lgmnUserInfo.setAccount(lgmnUserEntity.getAccount());
        lgmnUserInfo.setAvatar(lgmnUserEntity.getAvatar());
        lgmnUserInfo.setNikeName(lgmnUserEntity.getNikeName());
        lgmnUserInfo.setUserType(lgmnUserEntity.getUserType());
        return lgmnUserInfo;
    }

    private List<LgmnRoleEntity> getRoles(String userId) {
        LgmnUserRoleDto userRoleDto = new LgmnUserRoleDto();
        userRoleDto.setUserId(userId);
        List<LgmnUserRoleEntity> userRoleEntityList = null;
        List<LgmnRoleEntity> roleEntityList = null;
        try {
            userRoleEntityList = userRoleEntityService.getListByDto(userRoleDto);

            List<String> roleIds = new ArrayList<>();
            for (LgmnUserRoleEntity userRoleEntity : userRoleEntityList) {
                roleIds.add(userRoleEntity.getRoleId());
            }

            LgmnRoleDto roleDto = new LgmnRoleDto();
            roleDto.setId(roleIds);
            roleEntityList = roleEntityService.getListByDto(roleDto);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return roleEntityList;
        }
    }

    private List<LgmnPermissionEntity> getPermissions(List<LgmnRoleEntity> roles) {
        LgmnRolePermissionDto rolePermissionDto = new LgmnRolePermissionDto();
        List<String> roleIds = new ArrayList<>();

        List<LgmnRolePermissionEntity> rolePermissionEntities = null;
        LgmnPermissionDto permissionDto = new LgmnPermissionDto();

        List<LgmnPermissionEntity> permissionEntities = null;

        for (LgmnRoleEntity roleEntity : roles) {
            roleIds.add(roleEntity.getId());
        }
        rolePermissionDto.setRoleId(roleIds);
        try {
            rolePermissionEntities = rolePermissionEntityService.getListByDto(rolePermissionDto);

            List<String> permissionIds = new ArrayList<>();

            for (LgmnRolePermissionEntity rolePermissionEntity : rolePermissionEntities) {
                permissionIds.add(rolePermissionEntity.getPermissionId());
            }

            permissionDto.setId(permissionIds);

            permissionEntities = permissionEntityService.getListByDto(permissionDto);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return permissionEntities;
        }
    }
}

