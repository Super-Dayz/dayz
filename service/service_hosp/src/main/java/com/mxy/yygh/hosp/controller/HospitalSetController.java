package com.mxy.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mxy.yygh.common.exception.YyghException;
import com.mxy.yygh.common.result.Result;
import com.mxy.yygh.common.utils.MD5;
import com.mxy.yygh.hosp.service.HospitalSetService;
import com.mxy.yygh.model.hosp.HospitalSet;
import com.mxy.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
//@CrossOrigin
public class HospitalSetController {
    //注入Service
    @Autowired
    private HospitalSetService hospitalSetService;

    //1.查询医院设置表所有信息
    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("findAll")
    public Result findAllHospitalSet(){
        //调用service方法

        List<HospitalSet> list=hospitalSetService.list();
        return Result.ok(list);

    }
    //2.删除医院设置
    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable Long id){
        boolean flag=hospitalSetService.removeById(id);
        if(flag) {
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    //3.条件查询带分页
    @PostMapping(value = ("findPage/{current}/{limit}"))
    public Result findPageHospSet(@PathVariable long current, @PathVariable long limit, @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
        //创建一个Page对象，传递当前页，每页记录数
        Page<HospitalSet> page=new Page<>(current,limit);
        //构建条件
        QueryWrapper<HospitalSet> wrapper=new QueryWrapper<>();
        String hosname= hospitalSetQueryVo.getHosname();
        String hoscode=hospitalSetQueryVo.getHoscode();
        if(!StringUtils.isEmpty(hosname)){
            wrapper.like("hosname",hospitalSetQueryVo.getHosname());
        }
        if(!StringUtils.isEmpty(hoscode)){
            wrapper.eq("hoscode",hospitalSetQueryVo.getHoscode());
        }
        //调用方法实现分页查询
        Page<HospitalSet> pageHospitalSet=hospitalSetService.page(page,wrapper);

        return Result.ok(pageHospitalSet);
    }
    //4.添加医院设置
    @PostMapping(value = "saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        //设置状态 1使用 0不能使用
        hospitalSet.setStatus(1);
        //签名密钥
        Random random=new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+ random.nextInt(1000)));
        boolean save=hospitalSetService.save(hospitalSet);
        if(save){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    //5.根据id获取医院设置
    @GetMapping(value = "getHospSet/{id}")
    public Result getHospSet(@PathVariable  Long id){
//        try {
//            int i=1/0;
//        }catch (Exception e){
//            throw new YyghException("失败",201);
//        }
        HospitalSet hospitalSet= hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }
    //6.修改医院设置
    @PostMapping(value = "updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        boolean flag= hospitalSetService.updateById(hospitalSet);
        if(flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    //7.批量删除医院设置
    @DeleteMapping(value = "batchRemove")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> idList){
        hospitalSetService.removeByIds(idList);
        return Result.ok();
    }

    //8.医院设置锁定和解锁
    @PutMapping(value = "lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,@PathVariable Integer status){
        //根据id查询医院的设置信息
        HospitalSet hospitalSet= hospitalSetService.getById(id);
        //设置状态
        hospitalSet.setStatus(status);
        //调用方法
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();

    }
    //9.发送签名密钥
    @PutMapping(value = "sendKey/{id}")
    public Result lockHospitalSet(@PathVariable Long id){
        HospitalSet hospitalSet= hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //发送短信
        return Result.ok();


    }
}
