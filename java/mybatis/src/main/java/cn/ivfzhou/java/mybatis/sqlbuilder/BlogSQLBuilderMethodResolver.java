package cn.ivfzhou.java.mybatis.sqlbuilder;

import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.apache.ibatis.jdbc.SQL;

public class BlogSQLBuilderMethodResolver implements ProviderMethodResolver {

    public static String selectBlog2() {
        return new SQL().
                SELECT("*").
                FROM("t_blog").
                toString();
    }

}
