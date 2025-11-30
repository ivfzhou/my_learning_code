package cn.ivfzhou.java.mybatis.sqlbuilder;

import org.apache.ibatis.jdbc.SQL;

public class BlogSQLBuilder {

    public static String selectBlog2() {
        String sql = new SQL().
                SELECT("*").
                FROM("t_blog").
                toString();

        // 匿名内部类风格。
        sql = new SQL() {
            {
                SELECT("*");
                FROM("t_blog");
            }
        }.toString();
        return sql;
    }

}
