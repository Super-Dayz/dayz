package com.mxy.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mxy.yygh.model.cmn.Dict;
import com.mxy.yygh.model.hosp.HospitalSet;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {

    List<Dict> findChildData(Long id);

    void exportDictData(HttpServletResponse response);

    void importDictData(MultipartFile file);
    //根据dictcode和value查询
    String getDictName(String dictCode, String value);

    List<Dict> findByDictCode(String dictCode);
}
