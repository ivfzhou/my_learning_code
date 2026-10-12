package cn.ivfzhou.java.mybatis.generator.dao;

import cn.ivfzhou.java.mybatis.generator.bean.Blog;
import jakarta.annotation.Generated;

public interface BlogMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_blog")
    int deleteByPrimaryKey(Integer id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_blog")
    int insert(Blog row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_blog")
    int insertSelective(Blog row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_blog")
    Blog selectByPrimaryKey(Integer id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_blog")
    int updateByPrimaryKeySelective(Blog row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_blog")
    int updateByPrimaryKey(Blog row);
}