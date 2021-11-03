package com.mxy.yygh.hosp.service;

import com.mxy.yygh.model.hosp.Hospital;
import com.mxy.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    //上传医院接口
    void save(Map<String, Object> paramMap);
    //根据医院编号查询
    Hospital getByHoscode(String hoscode);
    //医院列表（条件查询分页）
    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);
    //更新医院上传状态
    void updateStatus(String id, Integer status);

    Map<String,Object> getHospById(String id);

    String getHospName(String hoscode);
    //根据医院名称查询
    List<Hospital> findByHosname(String hosname);

    //医院预约挂号详情
    Map<String, Object> item(String hoscode);
}
