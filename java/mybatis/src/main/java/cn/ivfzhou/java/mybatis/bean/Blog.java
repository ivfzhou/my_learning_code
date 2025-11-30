package cn.ivfzhou.java.mybatis.bean;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.type.Alias;

// 别名。同 xml 中的 typeAlias。
@Alias("blog")
public class Blog implements Serializable {

    private int id;

    private String title;

    private double grade;

    private Type type;

    private Author author;

    private Author coauthor;

    private List<Comment> comments;

    public Blog(@Param("id") int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Author getCoauthor() {
        return coauthor;
    }

    public void setCoauthor(Author coauthor) {
        this.coauthor = coauthor;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", grade=" + grade +
                ", type=" + type +
                ", author=" + author +
                ", coauthor=" + coauthor +
                ", comments=" + comments +
                '}';
    }
}
