package com.mxy.easyexcel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class TestWrite {
    public static void main(String[] args) {
        List<UserData> list=new ArrayList<>();
        for(int i=0;i<10;i++){
            UserData data=new UserData();
            data.setIud(i);
            data.setUsername("luck"+i);
            list.add(data);
        }
        //设置excel文件的路径和名称
        String fileName="/Users/dayz/test/01.xlsx";

        //调用方法
        EasyExcel.write(fileName,UserData.class).sheet("用户信息")
                .doWrite(list);
    }
}
