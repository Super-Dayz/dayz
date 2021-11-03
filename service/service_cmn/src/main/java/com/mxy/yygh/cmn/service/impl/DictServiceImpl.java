package com.mxy.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mxy.yygh.cmn.listen.DictListListen;
import com.mxy.yygh.cmn.mapper.DictMapper;
import com.mxy.yygh.cmn.service.DictService;
import com.mxy.yygh.model.cmn.Dict;

import com.mxy.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    //根据数据id查询子数据列表
    @Override
    //@Cacheable(value = "dict",keyGenerator = "keyGenerator")
    public List<Dict> findChildData(Long id) {
        QueryWrapper<Dict> wrapper=new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        List<Dict> dictList=baseMapper.selectList(wrapper);
        for(Dict dict:dictList){
            Long dictId=dict.getId();
            boolean isChild=this.isChildren(dictId);
            dict.setHasChildren(isChild);
        }
        return dictList;
    }

    @Override
    public void exportDictData(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
// 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName ="dict";
            response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

            List<Dict> dictList = baseMapper.selectList(null);
            List<DictEeVo> dictVoList = new ArrayList<>(dictList.size());
            for(Dict dict : dictList) {
                DictEeVo dictVo = new DictEeVo();
                BeanUtils.copyProperties(dict,dictVo);
                dictVoList.add(dictVo);
            }

            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("数据字典").doWrite(dictVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //导入数据字典
    @Override
    @CacheEvict(value = "dict",allEntries = true)
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListListen(baseMapper)).sheet().doRead();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //根据dictcode和value查询
    @Override
    public String getDictName(String dictCode, String value) {
        //如果dictCode为空，直接根据value查询
        if(StringUtils.isEmpty(dictCode)){
            //直接value查询
            QueryWrapper<Dict> wrapper=new QueryWrapper<>();
            wrapper.eq("value",value);
            Dict dict=baseMapper.selectOne(wrapper);
            return dict.getName();
        }else {
            QueryWrapper<Dict> wrapper=new QueryWrapper<>();
            wrapper.eq("dict_code",dictCode);
            Dict codedict=baseMapper.selectOne(wrapper);
            Long parent_id = codedict.getId();
            //根据parent_id和value查询
            Dict findDict = baseMapper.selectOne(new QueryWrapper<Dict>().eq("parent_id", parent_id).eq("value", value));
            return findDict.getName();
        }
    }
    private Dict getDictByDictCode(String dictCode){
        QueryWrapper<Dict> wrapper=new QueryWrapper<>();
        wrapper.eq("dict_code",dictCode);
        Dict dict=baseMapper.selectOne(wrapper);
        return dict;
    }
    //根据dictCode获取下级节点
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        //根据dictcode获取对应的ID
        Dict dictByDictCode = this.getDictByDictCode(dictCode);
        //根据id获取子节点
        List<Dict> childData = this.findChildData(dictByDictCode.getId());
        return childData;
    }


    //判断id下面是否有子节点
    private boolean isChildren(Long id){
        QueryWrapper<Dict> wrapper=new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Integer count=baseMapper.selectCount(wrapper);
        return count>0;
    }
}
