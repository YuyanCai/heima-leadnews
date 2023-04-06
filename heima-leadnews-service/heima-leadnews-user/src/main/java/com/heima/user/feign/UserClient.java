package com.heima.user.feign;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.apis.user.IUserRealNameClient;
import com.heima.apis.wemedia.IChannelClient;
import com.heima.apis.wemedia.IUserClient;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.ApUserReqDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.mapper.ApUserRealnameMapper;
import com.heima.user.pojos.ApUserRealname;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: xiaocai
 * @since: 2023/04/04/22:58
 */
@RestController
public class UserClient implements IUserRealNameClient {

    @Autowired
    private ApUserRealnameMapper userRealnameMapper;

    @Override
    @PostMapping("/api/v1/auth/list")
    public PageResponseResult listUser(ApUserReqDto dto) {

//        2.分页条件查询
        IPage page = new Page(dto.getPage(), dto.getSize());

        QueryWrapper<ApUserRealname> wrapper = new QueryWrapper<>();

        if (!StringUtils.isEmpty(dto.getStatus())) {
            wrapper.like("status", dto.getStatus());
        }
        wrapper.orderByDesc("created_time");

        IPage iPage = userRealnameMapper.selectPage(page, wrapper);
        int total = iPage.getRecords().size();

        //3.结果返回
        PageResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), total);
        responseResult.setData(iPage.getRecords());

        return responseResult;
    }

    @Override
    @PostMapping("/api/v1/auth/authFail")
    @Transactional
    public ResponseResult auditUserFail(ApUserReqDto dto) {
//        1.获得拒绝的理由
        String msg = dto.getMsg();
//        2.修改状态为审核失败，status置位2
        ApUserRealname apUserRealname = new ApUserRealname();
        Short status = 2;
        apUserRealname.setStatus(status);
        apUserRealname.setReason(msg);
        //3.指定修改的是哪一个用户
        QueryWrapper<ApUserRealname> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(dto.getId())) {
            wrapper.eq("id", dto.getId());
        }
        int update = userRealnameMapper.update(apUserRealname, wrapper);
        if (update < 1) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Autowired
    private IUserClient userClient;

    @Autowired
    private ApUserMapper apUserMapper;

    @Override
    @PostMapping("/api/v1/auth/authPass")
    @Transactional
    public ResponseResult auditUserSuccess(ApUserReqDto dto) {
        /**
         *        审核成功：
         *         1、修改用户状态为审核通过
         *         2、在自媒体端创建用户
         */
        //1、修改用户状态为审核通过
        ApUserRealname apUserRealname = new ApUserRealname();
        Short status = 9;
        apUserRealname.setStatus(status);
        QueryWrapper<ApUserRealname> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(dto.getId())){
            wrapper.eq("id",dto.getId());
        }
        userRealnameMapper.update(apUserRealname,wrapper);

        //2、在自媒体端创建用户
//        1.在app端通过前端传来的id查找用户详情
        ApUserRealname apUserRealname1 = userRealnameMapper.selectById(dto.getId());
//        2.根据用户详情中的用户id查询用户的基本信息
        ApUser apUser = apUserMapper.selectById(apUserRealname1.getUserId());
//        3.创建自媒体端对象，属性对拷，对密码加盐处理
        WmUser wmUser = new WmUser();
        BeanUtils.copyProperties(apUser, wmUser);

        String salt = apUser.getSalt();
        String pswd = wmUser.getPassword();
        pswd = DigestUtils.md5DigestAsHex((pswd + salt).getBytes());
        wmUser.setPassword(pswd);
        ResponseResult responseResult = userClient.saveWmUser(wmUser);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
