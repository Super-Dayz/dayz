package com.mxy.yygh.cmn.controller;

import com.mxy.yygh.cmn.service.DictService;
import com.mxy.yygh.common.result.Result;
import com.mxy.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = "数据字典接口")
@RestController
@RequestMapping(value = "/admin/cmn/dict")
//@CrossOrigin
public class DictController {
    @Autowired
    private DictService dictService;

    //根据数据id查询子数据
    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id){
        List<Dict> list=dictService.findChildData(id);
        return Result.ok(list);
    }
    //导入数据接口
    @ApiOperation(value="导入")
    @PostMapping(value = "importData")
    public Result importDict(MultipartFile file){
        dictService.importDictData(file);
        return Result.ok();

    }
    //导出数据接口
    @ApiOperation(value="导出")
    @GetMapping(value = "exportData")
    public void exportDict(HttpServletResponse response) {
        dictService.exportDictData(response);
    }

    //根据dictcode和value查询
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,@PathVariable String value){
        String dictName=dictService.getDictName(dictCode,value);
        return dictName;
    }
    //根据value查询
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value){
        String dictName=dictService.getDictName("",value);
        return dictName;
    }
    //根据dictCode获取下级节点
    @ApiOperation(value = "根据dictCode获取下级节点")
    @GetMapping(value = "findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode){
        List<Dict> list=dictService.findByDictCode(dictCode);
        return Result.ok(list);

    }
}
