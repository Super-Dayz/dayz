package com.mxy.easyexcel;

import com.alibaba.excel.EasyExcel;

public class TestRead {
    public static void main(String[] args) {
        //读取文件路径
        String fileName="/Users/dayz/test/01.xlsx";
        //调用方法读取
        EasyExcel.read(fileName,UserData.class,new ExcelListen()).sheet().doRead();
    }
}
