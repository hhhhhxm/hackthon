package com.kezaihui.faq.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kezaihui.faq.entity.FaqPair;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: lerry_li
 * @CreateDate: 2022/03/31
 * @Description
 */
@Repository
@Mapper
public interface FaqPairDao  extends BaseMapper<FaqPair> {

    List<FaqPair> listAll();
}
