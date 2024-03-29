package com.mango.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mango.springbootinit.exception.BusinessException;
import com.mango.springbootinit.exception.ThrowUtils;
import com.mango.springbootinit.mapper.GeneratorMapper;
import com.mango.springbootinit.common.ErrorCode;
import com.mango.springbootinit.constant.CommonConstant;
import com.mango.springbootinit.model.dto.generator.GeneratorQueryRequest;
import com.mango.springbootinit.model.entity.Generator;
import com.mango.springbootinit.model.entity.User;
import com.mango.springbootinit.model.vo.GeneratorVO;
import com.mango.springbootinit.model.vo.UserVO;
import com.mango.springbootinit.service.GeneratorService;
import com.mango.springbootinit.service.UserService;
import com.mango.springbootinit.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mango-zyz
 * @description 针对表【generator(生成器)】的数据库操作Service实现
 * @createDate 2024-03-20 22:17:39
 */
@Service
public class GeneratorServiceImpl extends ServiceImpl<GeneratorMapper, Generator>
        implements GeneratorService {
    @Resource
    private UserService userService;


    @Override
    public void validGenerator(Generator generator, boolean add) {


        if (generator == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = generator.getName();
        String description = generator.getDescription();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name, description), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 256) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }


    }


    /**
     * 获取查询包装类
     *
     * @param generatorQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest) {

        QueryWrapper<Generator> queryWrapper = new QueryWrapper<>();
        if (generatorQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = generatorQueryRequest.getSearchText();
        String sortField = generatorQueryRequest.getSortField();
        String sortOrder = generatorQueryRequest.getSortOrder();

        Long id = generatorQueryRequest.getId();
        String name = generatorQueryRequest.getName();
        List<String> tagList = generatorQueryRequest.getTags();
        Long userId = generatorQueryRequest.getUserId();
        Long notId = generatorQueryRequest.getNotId();
        String description = generatorQueryRequest.getDescription();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("name", searchText).or().like("description", searchText);
        }

        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(description), "content", description);

        if (CollectionUtils.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        //构建一个查询条件，查询id字段不等于notId值的数据。
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;

    }


    /**
     * 获取生成器VO
     *
     * @param generator
     * @param request
     * @return
     */
    @Override
    public GeneratorVO getGeneratorVO(Generator generator, HttpServletRequest request) {
        GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
        Long generatorId = generatorVO.getId();
        //1.关联查询用户信息
        Long userId = generator.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        generatorVO.setUser(userVO);
        return generatorVO;
    }


    /**
     * 获取生成器VO分页
     *
     * @param generatorPage
     * @param request
     * @return
     */
    @Override
    public Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage, HttpServletRequest request) {
        if (generatorPage ==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<GeneratorVO> generatorVOPage = new Page<>();
        List<Generator> records = generatorPage.getRecords();
        List<GeneratorVO> collect = records.stream().map(item -> getGeneratorVO(item, request)).collect(Collectors.toList());
        BeanUtils.copyProperties(generatorPage, generatorVOPage);
        generatorVOPage.setRecords(collect);
        return generatorVOPage;
    }
}




