package com.mxy.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mxy.yygh.cmn.client.DictFeignClient;
import com.mxy.yygh.hosp.repository.HospitalReposity;
import com.mxy.yygh.hosp.service.HospitalService;
import com.mxy.yygh.model.hosp.Hospital;
import com.mxy.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalReposity hospitalReposity;
    @Autowired
    private DictFeignClient dictFeignClient;
    @Override
    public void save(Map<String, Object> paramMap) {
        //把参数map集合转为对象Hospital
        String mapString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);
        //判断是否存在数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalByHoscode = hospitalReposity.getHospitalByHoscode(hoscode);
        Hospital hospital1Exist= hospitalByHoscode;

        //如果存在，进行添加
        if(hospital1Exist!=null){
            hospital.setStatus(hospital1Exist.getStatus());
            hospital.setCreateTime(hospital1Exist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalReposity.save(hospital);
        }else {
            //如果不存在，进行添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalReposity.save(hospital);
        }
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospitalByHoscode = hospitalReposity.getHospitalByHoscode(hoscode);
        return hospitalByHoscode;
    }
    //医院列表（条件查询分页）
    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //创建pageable对象
        Pageable pageable= PageRequest.of(page-1,limit);
        //创建条件匹配器
        ExampleMatcher matcher=ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //HospitalQueryVo转hospital对象
        Hospital hospital=new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        //创建对象
        Example<Hospital> example=Example.of(hospital,matcher);
        //调用方法实现查询
        Page<Hospital> pages = hospitalReposity.findAll(example, pageable);
        //获取查询list集合，遍历进行医院等级封装
        pages.getContent().stream().forEach(item ->{
            this.setHospitalHosType(item);
        });
        return pages;
    }
    //更新医院上传状态
    @Override
    public void updateStatus(String id, Integer status) {
        //根据id查询医院信息
        Hospital hospital = hospitalReposity.findById(id).get();
        //设置修改值
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalReposity.save(hospital);
    }

    @Override
    public Map<String,Object> getHospById(String id) {
        Map<String,Object> result=new HashMap<>();
        Hospital hospital = this.setHospitalHosType(hospitalReposity.findById(id).get());
        //医院基本信息
        result.put("hospital",hospital);
        result.put("bookingRule",hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }
    //获取医院名称
    @Override
    public String getHospName(String hoscode) {
        Hospital hospitalByHoscode = hospitalReposity.getHospitalByHoscode(hoscode);
        if(hospitalByHoscode!=null){
            return hospitalByHoscode.getHosname();
        }

        return null;
    }
    //根据医院名称查询
    @Override
    public List<Hospital> findByHosname(String hosname) {

        return hospitalReposity.findHospitalByHosnameLike(hosname);
    }
    //医院预约挂号详情
    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String, Object> result=new HashMap<>();
        //医院详情
        Hospital hospital=this.setHospitalHosType(this.getByHoscode(hoscode));
        result.put("hospital",hospital);
        //预约规则
        result.put("bookingRule",hospital.getBookingRule());
        //不需要返回
        hospital.setBookingRule(null);
        return result;
    }


    private Hospital setHospitalHosType(Hospital hospital) {
        //根据dictCode和value获取医院等级名称
        String hostype = dictFeignClient.getName("Hostype", hospital.getHostype());
        //查询省、市、地区
        String provinceString= dictFeignClient.getName(hospital.getProvinceCode());
        String cityString= dictFeignClient.getName(hospital.getCityCode());
        String districtString= dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("fullAddress",provinceString+cityString+districtString);
        hospital.getParam().put("hostypeString",hostype);
        return hospital;
    }
}
