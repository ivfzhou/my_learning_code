package cn.ivfzhou.java.mybatis.bean;

import org.apache.ibatis.annotations.Param;

public class BlogExtended extends Blog {

    public BlogExtended(@Param("id") int id) {
        super(id);
    }

}
