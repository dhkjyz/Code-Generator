package com.mango.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mango.springbootinit.model.dto.generator.GeneratorQueryRequest;
import com.mango.springbootinit.model.vo.GeneratorVO;
import com.mango.springbootinit.model.entity.Generator;

import javax.servlet.http.HttpServletRequest;

/**
* @author mango-zyz
* @description 针对表【generator(生成器)】的数据库操作Service
* @createDate 2024-03-20 22:17:39
*/
public interface GeneratorService extends IService<Generator> {

    /**
     * 校验
     *
     * @param post
     * @param add
     */
    void validGenerator(Generator post, boolean add);


    /**
     * 获取查询条件
     *
     * @param generatorQueryRequest
     * @return
     */
    QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest);

    /**
     * 获取帖子封装
     *
     * @param generator
     * @param request
     * @return
     */
    GeneratorVO getGeneratorVO(Generator generator, HttpServletRequest request);

    /**
     * 分页获取生成器封装
     *
     * @param generatorPage
     * @param request
     * @return
     */
    Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage, HttpServletRequest request);

      //一个转化。模仿原则 ，Page<Generator>这个是怎么生成

}
