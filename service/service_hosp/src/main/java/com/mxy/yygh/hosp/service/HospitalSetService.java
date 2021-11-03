package com.mxy.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mxy.yygh.model.hosp.HospitalSet;

public interface HospitalSetService extends IService<HospitalSet> {

    String getSignKey(String hoscode);
}
