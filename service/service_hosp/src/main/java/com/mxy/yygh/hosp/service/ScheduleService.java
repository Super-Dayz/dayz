package com.mxy.yygh.hosp.service;

import com.mxy.yygh.model.hosp.Schedule;
import com.mxy.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> paramMap);

    Page<Schedule> findPageDepartment(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    void remove(String hoscode, String hosScheduleId);

    Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);
    //根据医院编号、科室编号和工作日期查询排班详细信息
    List<Schedule> getDetailScheduleDetail(String hoscode, String depcode, String workDate);
}
