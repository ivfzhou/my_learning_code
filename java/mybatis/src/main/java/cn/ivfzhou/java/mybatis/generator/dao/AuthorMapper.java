package cn.ivfzhou.java.mybatis.generator.dao;

import cn.ivfzhou.java.mybatis.generator.bean.Author;
import jakarta.annotation.Generated;

public interface AuthorMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_author")
    int deleteByPrimaryKey(Integer id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_author")
    int insert(Author row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_author")
    int insertSelective(Author row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_author")
    Author selectByPrimaryKey(Integer id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_author")
    int updateByPrimaryKeySelective(Author row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_author")
    int updateByPrimaryKey(Author row);
}