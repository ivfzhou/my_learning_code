package cn.ivfzhou.java.mybatis.transaction;

import java.sql.Connection;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;

public class MyTransactionManager implements TransactionFactory {

    @Override
    public void setProperties(Properties props) {
        System.out.println("MyTransactionManager.setProperties：" + props);
        TransactionFactory.super.setProperties(props);
    }

    @Override
    public Transaction newTransaction(Connection conn) {
        return new MyTransaction();
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new MyTransaction();
    }

}
