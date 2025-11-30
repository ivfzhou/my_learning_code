package cn.ivfzhou.java.mybatis.databaseidprovider;

import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.ibatis.mapping.DatabaseIdProvider;

public class MyDatabaseIDProvider implements DatabaseIdProvider {

    private Properties props;

    @Override
    public void setProperties(Properties p) {
        System.out.println("MyDatabaseIDProvider.setProperties：" + p);
        this.props = p;
    }

    @Override
    public String getDatabaseId(DataSource dataSource) throws SQLException {
        var productName = dataSource.getConnection().getMetaData().getDatabaseProductName();
        System.out.println("MyDatabaseIDProvider.getDatabaseId：productName=" + productName);
        return props.getProperty(productName);
    }

}
