package com.mxy.yygh.hosp.controller.api;

import com.mxy.yygh.common.result.Result;
import com.mxy.yygh.hosp.service.DepartmentService;
import com.mxy.yygh.hosp.service.HospitalService;
import com.mxy.yygh.model.hosp.Hospital;
import com.mxy.yygh.vo.hosp.DepartmentVo;
import com.mxy.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
@Api(value = "医院")
@RestController
@RequestMapping(value = "/api/hosp/hospital")
public class HospApiController {
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;

    @ApiOperation( value = "查询医院列表")
    @GetMapping(value = "findHospList/{page}/{limit}")
    public Result findHospList(@PathVariable Integer page,
                               @PathVariable Integer limit,
                               HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitals = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitals);

    }
    //根据医院名称查询
    @ApiOperation("根据医院名称查询")
    @GetMapping("findByHosName/{hosname}")
    public Result findByHosname(@PathVariable String hosname){
            List<Hospital> list=hospitalService.findByHosname(hosname);
            return Result.ok(list);

    }

    @ApiOperation("根据医院编号获取科室")
    @GetMapping("department/{hoscode}")
    public Result index(@PathVariable String hoscode){

        List<DepartmentVo> deptTree = departmentService.findDeptTree(hoscode);
        return Result.ok(deptTree);
    }

    @ApiOperation(value = "医院预约挂号详情")
    @GetMapping("findHospDetail/{hoscode}")
    public Result item(@PathVariable String hoscode) {
        Map<String,Object> map=hospitalService.item(hoscode);
        return Result.ok(map);
    }


}
