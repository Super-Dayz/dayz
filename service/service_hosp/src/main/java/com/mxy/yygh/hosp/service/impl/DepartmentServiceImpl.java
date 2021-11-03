package com.mxy.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mxy.yygh.hosp.repository.DepartmentRepository;
import com.mxy.yygh.hosp.service.DepartmentService;
import com.mxy.yygh.model.hosp.Department;
import com.mxy.yygh.vo.hosp.DepartmentQueryVo;
import com.mxy.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;
    //上传科室接口
    @Override
    public void save(Map<String, Object> paramMap) {
        //paramMap转department对象
        String paramMapString = JSONObject.toJSONString(paramMap);
        Department department= JSONObject.parseObject(paramMapString,Department.class);
        //根据医院编号和科室编号查询
        Department departmentExist=departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());
        //判断
        if(departmentExist!=null){
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        }else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentVo) {
        //创建pageable对象
        //0第一页
        Pageable pageable= PageRequest.of(page-1,limit);
        //创建example对象
        Department department=new Department();
        BeanUtils.copyProperties(departmentVo,department);
        department.setIsDeleted(0);

        ExampleMatcher matcher=ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Department> eXample=Example.of(department,matcher);
        Page<Department> all=departmentRepository.findAll(eXample,pageable);
        return all;
    }
    //删除医院接口
    @Override
    public void remove(String hoscode, String depcode) {
        //根据编号查询信息
        Department department=departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode,depcode);
        if(department!=null){
            departmentRepository.deleteById(department.getId());
        }
    }

    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //创建list集合，用于最终数据封装
        List<DepartmentVo> result = new ArrayList<>();

        //根据医院编号，查询医院所有科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example example = Example.of(departmentQuery);
        //所有科室列表 departmentList
        List<Department> departmentList = departmentRepository.findAll(example);

        //根据大科室编号  bigcode 分组，获取每个大科室里面下级子科室
        Map<String, List<Department>> deparmentMap =
                departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        //遍历map集合 deparmentMap
        for(Map.Entry<String,List<Department>> entry : deparmentMap.entrySet()) {
            //大科室编号
            String bigcode = entry.getKey();
            //大科室编号对应的全局数据
            List<Department> deparment1List = entry.getValue();
            //封装大科室
            DepartmentVo departmentVo1 = new DepartmentVo();
            departmentVo1.setDepcode(bigcode);
            departmentVo1.setDepname(deparment1List.get(0).getBigname());

            //封装小科室
            List<DepartmentVo> children = new ArrayList<>();
            for(Department department: deparment1List) {
                DepartmentVo departmentVo2 =  new DepartmentVo();
                departmentVo2.setDepcode(department.getDepcode());
                departmentVo2.setDepname(department.getDepname());
                //封装到list集合
                children.add(departmentVo2);
            }
            //把小科室list集合放到大科室children里面
            departmentVo1.setChildren(children);
            //放到最终result里面
            result.add(departmentVo1);
        }
        //返回
        return result;
    }
    //根据科室编号和医院编号查询科室名称
    @Override
    public String getDepName(String hoscode, String depcode) {
        Department departmentByHoscodeAndDepcode = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(departmentByHoscodeAndDepcode!=null){
            return departmentByHoscodeAndDepcode.getDepname();
        }
        return null;
    }


}
