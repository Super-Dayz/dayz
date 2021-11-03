package com.mxy.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mxy.yygh.common.helper.HttpRequestHelper;
import com.mxy.yygh.common.result.Result;
import com.mxy.yygh.hosp.repository.ScheduleRepository;
import com.mxy.yygh.hosp.service.DepartmentService;
import com.mxy.yygh.hosp.service.HospitalService;
import com.mxy.yygh.hosp.service.ScheduleService;
import com.mxy.yygh.model.hosp.Department;
import com.mxy.yygh.model.hosp.Schedule;
import com.mxy.yygh.vo.hosp.BookingScheduleRuleVo;
import com.mxy.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;
    //上传排班
    @Override
    public void save(Map<String, Object> paramMap) {
        //paramMap转department对象
        String paramMapString = JSONObject.toJSONString(paramMap);
        Schedule schedule= JSONObject.parseObject(paramMapString,Schedule.class);
        //根据医院编号和排班编号查询
        Schedule scheduleExist=scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(),schedule.getHosScheduleId());
        //判断
        if(scheduleExist!=null){
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(scheduleExist);
        }else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> findPageDepartment(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        //创建pageable对象
        //0第一页
        Pageable pageable= PageRequest.of(page-1,limit);
        //创建example对象
        Schedule schedule=new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo,schedule);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);
        ExampleMatcher matcher=ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Schedule> eXample=Example.of(schedule,matcher);
        Page<Schedule> all=scheduleRepository.findAll(eXample,pageable);
        return all;
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        //根据编号查询
        Schedule scheduleByHoscodeAndHosScheduleId = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if(scheduleByHoscodeAndHosScheduleId!=null){
            scheduleRepository.deleteById(scheduleByHoscodeAndHosScheduleId.getId());
        }


    }


    //根据医院编号 和 科室编号 ，查询排班规则数据
    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {
        //1 根据医院编号 和 科室编号 查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        //2 根据工作日workDate期进行分组
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),//匹配条件
                Aggregation.group("workDate")//分组字段
                        .first("workDate").as("workDate")
                        //3 统计号源数量
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                //排序
                Aggregation.sort(Sort.Direction.DESC,"workDate"),
                //4 实现分页
                Aggregation.skip((page-1)*limit),
                Aggregation.limit(limit)
        );
        //调用方法，最终执行
        AggregationResults<BookingScheduleRuleVo> aggResults =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResults.getMappedResults();

        //分组查询的总记录数
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggResults =
                mongoTemplate.aggregate(totalAgg,
                        Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResults.getMappedResults().size();

        //把日期对应星期获取
        for(BookingScheduleRuleVo bookingScheduleRuleVo:bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        //设置最终数据，进行返回
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList",bookingScheduleRuleVoList);
        result.put("total",total);

        //获取医院名称
        String hosName = hospitalService.getHospName(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hosName);
        result.put("baseMap",baseMap);

        return result;
    }
    //根据医院编号、科室编号和工作日期查询排班详细信息
    @Override
    public List<Schedule> getDetailScheduleDetail(String hoscode, String depcode, String workDate) {
        //根据参数查询mongodb
        List<Schedule> scheduleList=scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());
        //把得到list集合遍历，设置对应的值
        scheduleList.stream().forEach(item->{
            this.packageSchedule(item);
        });
        
        return scheduleList;
    }
    //封装排班详情其他值
    private  void packageSchedule(Schedule item) {

        //设置医院名称
        item.getParam().put("hosname",hospitalService.getHospName(item.getHoscode()));
        //设置科室名称
        item.getParam().put("depname",departmentService.getDepName(item.getHoscode(),item.getDepcode()));
        //设置对应的日期
        item.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(item.getWorkDate())));
    }

    /**
     * 根据日期获取周几数据
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }



}
