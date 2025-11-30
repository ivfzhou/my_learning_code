package cn.ivfzhou.java.mybatis.bean;

import java.io.Serializable;

public class Comment implements Serializable {

    private int id;

    private String comment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", Comment='" + comment + '\'' +
                '}';
    }
}
