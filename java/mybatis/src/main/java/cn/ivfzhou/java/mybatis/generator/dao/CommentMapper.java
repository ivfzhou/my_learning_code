package cn.ivfzhou.java.mybatis.generator.dao;

import cn.ivfzhou.java.mybatis.generator.bean.Comment;
import jakarta.annotation.Generated;

public interface CommentMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_comment")
    int deleteByPrimaryKey(Integer id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_comment")
    int insert(Comment row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_comment")
    int insertSelective(Comment row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_comment")
    Comment selectByPrimaryKey(Integer id);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_comment")
    int updateByPrimaryKeySelective(Comment row);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_comment")
    int updateByPrimaryKey(Comment row);
}