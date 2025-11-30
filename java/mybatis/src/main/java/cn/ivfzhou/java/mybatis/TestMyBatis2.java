package cn.ivfzhou.java.mybatis;

import java.util.HashMap;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;

import cn.ivfzhou.java.mybatis.bean.Blog;

public final class TestMyBatis2 {

    /*
     * 不使用 xml 配置获取 SQLSessionFactory。
     * */
    public static void main(String[] args) throws Exception {
        var hashMap = new HashMap<>();
        hashMap.put("username", "ivfzhou");
        hashMap.put("password", "123456");
        hashMap.put("url", "jdbc:mysql://ivfzhoudockermysql:3306/db_blog");
        var dataSource = DruidDataSourceFactory.createDataSource(hashMap);
        var transactionFactory = new JdbcTransactionFactory();
        var environment = new Environment("development", transactionFactory, dataSource);
        var configuration = new Configuration(environment);
        configuration.addMapper(BlogMapper.class);
        configuration.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);
        var sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        try (var session = sqlSessionFactory.openSession()) {
            var mapper = session.getMapper(BlogMapper.class);
            var blog = mapper.selectByID(1);
            System.out.println(blog);
        }
    }

    public interface BlogMapper {
        @Select("SELECT * FROM `t_blog` WHERE `id`=#{id}")
        Blog selectByID(int id);
    }

}
