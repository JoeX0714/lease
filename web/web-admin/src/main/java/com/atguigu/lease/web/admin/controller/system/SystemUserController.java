package com.atguigu.lease.web.admin.controller.system;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.SystemUser;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.admin.service.SystemUserService;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserItemVo;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "后台用户信息管理")
@RestController
@RequestMapping("/admin/system/user")
public class SystemUserController {

    @Autowired
    private SystemUserService systemUserService;

    @Operation(summary = "根据条件分页查询后台用户列表")
    @GetMapping("page")
    public Result<IPage<SystemUserItemVo>> page(@RequestParam long current, @RequestParam long size, SystemUserQueryVo queryVo) {
        Page<SystemUserItemVo> systemUserItemVoPage = new Page<>(current, size);
        IPage<SystemUserItemVo> systemUserItemVo=systemUserService.pageSystemUser(systemUserItemVoPage,queryVo);
        return Result.ok(systemUserItemVo);
    }

    @Operation(summary = "根据ID查询后台用户信息")
    @GetMapping("getById")
    public Result<SystemUserItemVo> getById(@RequestParam Long id) {
        SystemUserItemVo systemUserItemVo=systemUserService.getSystemUserById(id);
        return Result.ok(systemUserItemVo);
    }

    @Operation(summary = "保存或更新后台用户信息")//要处理密码，不能把密码明文存储在数据库中
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody SystemUser systemUser) {
        String password = systemUser.getPassword();
        if (password != null && !password.isEmpty()) {
            systemUser.setPassword(DigestUtils.md5Hex(password));
        } else {
            systemUser.setPassword(null);
        }//使用Mybatis-Plus提供的更新方法时，若实体中的字段为`null`，默认情况下，最终生成的update语句中，不会包含该字段。
        systemUserService.saveOrUpdate(systemUser);
        return Result.ok();
    }

    @Operation(summary = "判断后台用户名是否可用")
    @GetMapping("isUserNameAvailable")
    public Result<Boolean> isUsernameExists(@RequestParam String username) {
        LambdaQueryWrapper<SystemUser> systemUserQueryWrapper = new LambdaQueryWrapper<>();
        systemUserQueryWrapper.eq(SystemUser::getUsername, username);
        long count = systemUserService.count(systemUserQueryWrapper);
        return Result.ok(count==0);//count==0说明用户名不存在，该用户名可用
    }

    @DeleteMapping("deleteById")
    @Operation(summary = "根据ID删除后台用户信息")
    public Result removeById(@RequestParam Long id) {
        systemUserService.removeById(id);
        return Result.ok();
    }

    @Operation(summary = "根据ID修改后台用户状态")
    @PostMapping("updateStatusByUserId")
    public Result updateStatusByUserId(@RequestParam Long id, @RequestParam BaseStatus status) {
        if (systemUserService.getById(id) == null) {
            return Result.fail();
        }
        LambdaUpdateWrapper<SystemUser> systemUserUpdateWrapper = new LambdaUpdateWrapper<>();
        systemUserUpdateWrapper.eq(SystemUser::getId, id);
        systemUserUpdateWrapper.set(SystemUser::getStatus, status);
        systemUserService.update(systemUserUpdateWrapper);
        return Result.ok();
    }
}
