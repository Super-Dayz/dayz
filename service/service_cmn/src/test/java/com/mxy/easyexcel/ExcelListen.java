package com.mxy.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

public class ExcelListen extends AnalysisEventListener<UserData> {
    //一行一行读取excel内容，从第二行开始
    @Override
    public void invoke(UserData userData, AnalysisContext analysisContext) {
        System.out.println(userData);
    }
    //读取后会执行
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("表头信息"+headMap);
    }
}
