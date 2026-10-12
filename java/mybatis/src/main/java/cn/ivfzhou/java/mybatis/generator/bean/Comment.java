package cn.ivfzhou.java.mybatis.generator.bean;

import jakarta.annotation.Generated;
import java.io.Serializable;

public class Comment implements Serializable {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_comment.id")
    private Integer id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_comment.blog_id")
    private Integer blogId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_comment.comment")
    private String comment;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_comment")
    private static final long serialVersionUID = 1L;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_comment.id")
    public Integer getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_comment.id")
    public void setId(Integer id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_comment.blog_id")
    public Integer getBlogId() {
        return blogId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_comment.blog_id")
    public void setBlogId(Integer blogId) {
        this.blogId = blogId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_comment.comment")
    public String getComment() {
        return comment;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_comment.comment")
    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    @Override
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_comment")
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
        Comment other = (Comment) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getBlogId() == null ? other.getBlogId() == null : this.getBlogId().equals(other.getBlogId()))
            && (this.getComment() == null ? other.getComment() == null : this.getComment().equals(other.getComment()));
    }

    @Override
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_comment")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getBlogId() == null) ? 0 : getBlogId().hashCode());
        result = prime * result + ((getComment() == null) ? 0 : getComment().hashCode());
        return result;
    }

    @Override
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_comment")
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", blogId=").append(blogId);
        sb.append(", comment=").append(comment);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}