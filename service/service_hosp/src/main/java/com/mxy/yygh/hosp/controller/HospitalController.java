package com.mxy.yygh.hosp.controller;

import com.mxy.yygh.common.result.Result;
import com.mxy.yygh.hosp.service.HospitalService;
import com.mxy.yygh.model.hosp.Hospital;
import com.mxy.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/hospital")
//@CrossOrigin
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;

    //医院列表
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page, @PathVariable Integer limit, HospitalQueryVo hospitalQueryVo){
      Page<Hospital> pageModel= hospitalService.selectHospPage(page,limit,hospitalQueryVo);

      return Result.ok(pageModel);
    }
    //更新医院上传状态
    @ApiOperation(value = "更新医院上线状态")
    @GetMapping(value = "updateHospStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable String id,@PathVariable Integer status){
        hospitalService.updateStatus(id,status);
        return Result.ok();
    }
    //医院详细信息
    @ApiOperation(value = "医院详细信息")
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable String id){

        Map<String,Object> map=hospitalService.getHospById(id);

        return Result.ok(map);

    }
}
