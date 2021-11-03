package com.mxy.yygh.hosp.controller.api;

import com.mxy.yygh.common.exception.YyghException;
import com.mxy.yygh.common.helper.HttpRequestHelper;
import com.mxy.yygh.common.result.Result;
import com.mxy.yygh.common.result.ResultCodeEnum;
import com.mxy.yygh.common.utils.MD5;
import com.mxy.yygh.hosp.service.DepartmentService;
import com.mxy.yygh.hosp.service.HospitalService;
import com.mxy.yygh.hosp.service.HospitalSetService;
import com.mxy.yygh.hosp.service.ScheduleService;
import com.mxy.yygh.model.hosp.Department;
import com.mxy.yygh.model.hosp.Hospital;
import com.mxy.yygh.model.hosp.Schedule;
import com.mxy.yygh.vo.hosp.DepartmentQueryVo;
import com.mxy.yygh.vo.hosp.DepartmentVo;
import com.mxy.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiController {
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private HospitalSetService hospitalSetService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ScheduleService scheduleService;

    //上传医院接口
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request){
        //获取传递医院的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //获取医院系统传来的签名
        String hospSign = (String)paramMap.get("sign");
        //根据传递过来的编码，查询数据库，查询签名
        String hoscode = (String) paramMap.get("hoscode");
        String signkey=hospitalSetService.getSignKey(hoscode);
        //把查询的签名进行md5加密
        String signKeyMd5 = MD5.encrypt(signkey);
        //判断签名是否一致
        if(!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //传输过程中将+转换为' '，
        String logoData=(String) paramMap.get("logoData");
        logoData=logoData.replaceAll(" ","+");
        paramMap.put("logoData",logoData);
        //调用service方法
        hospitalService.save(paramMap);
        return Result.ok();

    }
    //查询医院接口
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request){
        //获取传递医院的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //获取医院编号
        String hoscode=(String) paramMap.get("hoscode");
        //获取医院系统传来的签名
        String hospSign = (String)paramMap.get("sign");
        //根据传递过来的编码，查询数据库，查询签名

        String signkey=hospitalSetService.getSignKey(hoscode);
        //把查询的签名进行md5加密
        String signKeyMd5 = MD5.encrypt(signkey);
        //判断签名是否一致
        if(!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //调用service方法编号查询
        Hospital hospital=hospitalService.getByHoscode(hoscode);
         return Result.ok(hospital);
    }
    //上传科室接口
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        //获取传递科室的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //获取医院编号
        String hoscode=(String) paramMap.get("hoscode");
        //获取医院系统传来的签名
        String hospSign = (String)paramMap.get("sign");
        //根据传递过来的编码，查询数据库，查询签名

        String signkey=hospitalSetService.getSignKey(hoscode);
        //把查询的签名进行md5加密
        String signKeyMd5 = MD5.encrypt(signkey);
        //判断签名是否一致
        if(!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //调用service方法
        departmentService.save(paramMap);
        return Result.ok();

    }
    //查询科室接口
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request){
        //获取传递科室的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //获取医院编号
        String hoscode=(String) paramMap.get("hoscode");
        //当前页和每页记录数
        int page= StringUtils.isEmpty(paramMap.get("page"))?1:Integer.parseInt((String) paramMap.get("page"));
        int limit= StringUtils.isEmpty(paramMap.get("limit"))?1:Integer.parseInt((String) paramMap.get("limit"));

        //条件校验
        DepartmentQueryVo departmentVo=new DepartmentQueryVo();
        departmentVo.setHoscode(hoscode);
        //service方法
        Page<Department> pageModel=departmentService.findPageDepartment(page,limit,departmentVo);
        return Result.ok(pageModel);
    }
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request){
        //获取传递科室的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //获取医院编号
        String hoscode=(String) paramMap.get("hoscode");
        //获取科室编号
        String depcode=(String) paramMap.get("depcode");
        //校验
        departmentService.remove(hoscode,depcode);
        return Result.ok();
    }
    //上传排班
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        //获取传递科室的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //TODO 校验
        scheduleService.save(paramMap);
        return Result.ok();
    }
    //查询排班
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request){
        //获取传递科室的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //获取医院编号
        String hoscode=(String) paramMap.get("hoscode");
        //科室编号
        String depcode=(String) paramMap.get("depcode");
        //当前页和每页记录数
        int page= StringUtils.isEmpty(paramMap.get("page"))?1:Integer.parseInt((String) paramMap.get("page"));
        int limit= StringUtils.isEmpty(paramMap.get("limit"))?1:Integer.parseInt((String) paramMap.get("limit"));

        //条件校验
        ScheduleQueryVo scheduleQueryVo =new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);
        //service方法
        Page<Schedule> pageModel=scheduleService.findPageDepartment(page,limit,scheduleQueryVo);
        return Result.ok(pageModel);
    }
    //删除排班
    @PostMapping("schedule/remove")
    public Result remove(HttpServletRequest request){
        //获取传递科室的信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //获取医院编号和排班编号
        String hoscode=(String) paramMap.get("hoscode");
        String hosScheduleId=(String) paramMap.get("hosScheduleId");
        //签名校验
        scheduleService.remove(hoscode,hosScheduleId);
        return Result.ok();
    }
}
