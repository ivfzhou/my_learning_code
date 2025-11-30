package cn.ivfzhou.java.mybatis;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import cn.ivfzhou.java.mybatis.bean.Blog;
import cn.ivfzhou.java.mybatis.bean.Type;
import cn.ivfzhou.java.mybatis.mapper.BlogMapper;

public final class TestMyBatis {

    public static void main(String[] args) throws IOException {
        var resource = "mybatis-config.xml";
        var inputStream = Resources.getResourceAsStream(resource);
        var sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        // var sqlSessionFactory2 = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream(resource), "myenv");
        try (var session = sqlSessionFactory.openSession(true)) {
            switch (6) {
                case 1:
                    Blog blog = session.selectOne("cn.ivfzhou.java.mybatis.mapper.BlogMapper.selectBlog1", 4);
                    System.out.println(JSON.toJSON(blog));
                    break;
                case 2:
                    List<Blog> blogs = session.selectList("cn.ivfzhou.java.mybatis.mapper.BlogMapper.selectBlog2");
                    System.out.println(JSON.toJSON(blogs));
                    break;
                case 3:
                    blog = session.selectOne("cn.ivfzhou.java.mybatis.mapper.BlogMapper.selectBlog3", 1);
                    System.out.println(JSON.toJSON(blog));
                    break;
                case 4:
                    blog = new Blog(0);
                    blog.setGrade(88.8888);
                    blog.setType(Type.SOCIAL);
                    var res = session.insert("cn.ivfzhou.java.mybatis.mapper.BlogMapper.insertBlog", blog);
                    System.out.println("res=" + res + "，blog.id=" + blog.getId());
                    break;
                case 5:
                    var mapper = session.getMapper(BlogMapper.class);
                    blogs = mapper.selectBlog2();
                    System.out.println(JSON.toJSON(blogs));
                    break;
            }
        }
    }

}
