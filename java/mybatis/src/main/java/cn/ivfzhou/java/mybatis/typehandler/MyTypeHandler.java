package cn.ivfzhou.java.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import cn.ivfzhou.java.mybatis.bean.Type;

/*
 * 继承实现类型处理器，xml 配置包扫描。会覆盖已有的处理器。
 * */
@MappedJdbcTypes(value = {JdbcType.INTEGER}, includeNullJdbcType = true)
@MappedTypes(Type.class)
public class MyTypeHandler extends BaseTypeHandler<Type> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Type parameter, JdbcType jdbcType) throws SQLException {
        System.out.println("MyTypeHandler.setNonNullParameter：i=" + i + "parameter=" + parameter + "jdbcType=" + jdbcType);
        ps.setInt(i, parameter.ordinal());
    }

    @Override
    public Type getNullableResult(ResultSet rs, String columnName) throws SQLException {
        System.out.println("MyTypeHandler.getNullableResult：columnName=" + columnName);
        return Type.values()[rs.getInt(columnName)];
    }

    @Override
    public Type getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        System.out.println("MyTypeHandler.getNullableResult：columnIndex=" + columnIndex);
        return Type.values()[rs.getInt(columnIndex)];
    }

    @Override
    public Type getNullableResult(CallableStatement cs, int columnIndex) {
        return null;
    }

}
