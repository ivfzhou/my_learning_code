package cn.ivfzhou.java.mybatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.ibatis.transaction.Transaction;

public class MyTransaction implements Transaction {

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public void commit() throws SQLException {
    }

    @Override
    public void rollback() throws SQLException {
    }

    @Override
    public void close() throws SQLException {
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return null;
    }

}
