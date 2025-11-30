package cn.ivfzhou.java.mybatis.transaction;

import java.util.Properties;
import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.apache.ibatis.datasource.DataSourceFactory;

public class MyDataSourceFactory implements DataSourceFactory {

    private DataSource dataSource;

    @Override
    public void setProperties(Properties props) {
        System.out.println("MyDataSourceFactory.setProperties：" + props);
        try {
            dataSource = DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

}
