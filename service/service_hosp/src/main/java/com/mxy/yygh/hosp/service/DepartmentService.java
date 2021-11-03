package com.mxy.yygh.hosp.service;

import com.mxy.yygh.model.hosp.Department;
import com.mxy.yygh.vo.hosp.DepartmentQueryVo;
import com.mxy.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    void save(Map<String, Object> paramMap);

    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentVo);

    void remove(String hoscode, String depcode);

    List<DepartmentVo> findDeptTree(String hoscode);
    //根据科室编号和医院编号查询科室名称
    String getDepName(String hoscode, String depcode);
}
