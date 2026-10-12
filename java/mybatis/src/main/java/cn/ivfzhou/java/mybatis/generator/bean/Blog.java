package cn.ivfzhou.java.mybatis.generator.bean;

import jakarta.annotation.Generated;
import java.io.Serializable;

public class Blog implements Serializable {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.id")
    private Integer id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.title")
    private String title;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.grade")
    private Double grade;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.type")
    private Integer type;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.author_id")
    private Integer authorId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.coauthor_id")
    private Integer coauthorId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.has_coauthor")
    private Boolean hasCoauthor;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_blog")
    private static final long serialVersionUID = 1L;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.id")
    public Integer getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.id")
    public void setId(Integer id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.title")
    public String getTitle() {
        return title;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.title")
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.grade")
    public Double getGrade() {
        return grade;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.grade")
    public void setGrade(Double grade) {
        this.grade = grade;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.type")
    public Integer getType() {
        return type;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.type")
    public void setType(Integer type) {
        this.type = type;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.author_id")
    public Integer getAuthorId() {
        return authorId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.author_id")
    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.coauthor_id")
    public Integer getCoauthorId() {
        return coauthorId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.coauthor_id")
    public void setCoauthorId(Integer coauthorId) {
        this.coauthorId = coauthorId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.has_coauthor")
    public Boolean getHasCoauthor() {
        return hasCoauthor;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_blog.has_coauthor")
    public void setHasCoauthor(Boolean hasCoauthor) {
        this.hasCoauthor = hasCoauthor;
    }

    @Override
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_blog")
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Blog other = (Blog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getGrade() == null ? other.getGrade() == null : this.getGrade().equals(other.getGrade()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getAuthorId() == null ? other.getAuthorId() == null : this.getAuthorId().equals(other.getAuthorId()))
            && (this.getCoauthorId() == null ? other.getCoauthorId() == null : this.getCoauthorId().equals(other.getCoauthorId()))
            && (this.getHasCoauthor() == null ? other.getHasCoauthor() == null : this.getHasCoauthor().equals(other.getHasCoauthor()));
    }

    @Override
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_blog")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getGrade() == null) ? 0 : getGrade().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getAuthorId() == null) ? 0 : getAuthorId().hashCode());
        result = prime * result + ((getCoauthorId() == null) ? 0 : getCoauthorId().hashCode());
        result = prime * result + ((getHasCoauthor() == null) ? 0 : getHasCoauthor().hashCode());
        return result;
    }

    @Override
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_blog")
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", title=").append(title);
        sb.append(", grade=").append(grade);
        sb.append(", type=").append(type);
        sb.append(", authorId=").append(authorId);
        sb.append(", coauthorId=").append(coauthorId);
        sb.append(", hasCoauthor=").append(hasCoauthor);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}