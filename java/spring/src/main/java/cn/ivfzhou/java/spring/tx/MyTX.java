package cn.ivfzhou.java.spring.tx;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.ivfzhou.java.spring.bean.User;

@Component("myTX")
public class MyTX {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public MyTX(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void query() {
        var users = jdbcTemplate.query("select * from t_user", (rs, rowNum) -> {
            var user = new User();
            user.setAge(rs.getInt("age"));
            user.setName(rs.getString("name"));
            return user;
        });

        System.out.println(JSON.toJSONString(users));
    }

    @Transactional(transactionManager = "transactionManager")
    public void insert() {
        var res = jdbcTemplate.update("insert into t_user (age, name) values (?, ?);", 20, "ww");
        System.out.println(res);
        throw new RuntimeException("回滚");
    }

}
