package cn.ivfzhou.java.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.FetchType;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;

import cn.ivfzhou.java.mybatis.bean.Author;
import cn.ivfzhou.java.mybatis.bean.Blog;
import cn.ivfzhou.java.mybatis.bean.Comment;
import cn.ivfzhou.java.mybatis.languagedriver.MyLanguageDriver;
import cn.ivfzhou.java.mybatis.sqlbuilder.BlogSQLBuilder;

// @CacheNamespaceRef(OtherMapper.class)
public interface BlogMapper {

    // @Select("select * from t_blog")
    // @SelectProvider(BlogSQLBuilderMethodResolver.class)
    @SelectProvider(value = BlogSQLBuilder.class, method = "selectBlog2")
    @ConstructorArgs(@Arg(id = true, column = "id", name = "id"))
    @Results(id = "blogMap1", value = {
            @Result(property = "title", column = "title"),
            @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class),
            @Result(property = "author", column = "author_id", one = @One(select = "cn.ivfzhou.java.mybatis.mapper.BlogMapper.selectAuthor")),
            @Result(property = "coauthor", column = "coauthor_id", one = @One(select = "cn.ivfzhou.java.mybatis.mapper.BlogMapper.selectAuthor")),
            @Result(property = "comments", column = "id", many = @Many(select = "cn.ivfzhou.java.mybatis.mapper.BlogMapper.selectComments", fetchType = FetchType.LAZY))
    })
    List<Blog> selectBlog2();

    Blog selectBlog3();

    @Select("select * from t_author where id = #{id}")
    Author selectAuthor(int id);

    @Select("select * from t_comment where blog_id = #{blog_id}")
    List<Comment> selectComments(int blogID);

    @MapKey("id")
    Map<Integer, Blog> selectBlogs();

    @Update("""
            <script>
            update Author
              <set>
                <if test='username != null'>username=#{username},</if>
                <if test='password != null'>password=#{password},</if>
                <if test='email != null'>email=#{email},</if>
                <if test='bio != null'>bio=#{bio}</if>
              </set>
            where id=#{id}
            </script>
            """
    )
    @Lang(MyLanguageDriver.class)
    void example();

}
