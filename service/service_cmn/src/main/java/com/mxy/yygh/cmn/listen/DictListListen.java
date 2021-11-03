package com.mxy.yygh.cmn.listen;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.mxy.yygh.cmn.mapper.DictMapper;
import com.mxy.yygh.model.cmn.Dict;
import com.mxy.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

public class DictListListen extends AnalysisEventListener<DictEeVo> {
    private DictMapper dictMapper;

    public DictListListen(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    //一行一行读取
    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        //调用方法添加数据库
        Dict dict=new Dict();
        BeanUtils.copyProperties(dictEeVo,dict);
        dictMapper.insert(dict);

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
