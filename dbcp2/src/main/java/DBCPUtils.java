import org.apache.commons.dbcp2.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;

/**
 * Create by qxz on 2018/2/2
 * Description:
 */
public class DBCPUtils {

    public static void main(String[] args) throws SQLException {
        Connection conn = DBCPUtils.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id = 40");
        ResultSet rs = ps.executeQuery();
        System.out.println();
        while (rs.next()) {
            Object id = rs.getObject(1);
            Object userName = rs.getObject(2);
            Object passWord = rs.getObject(3);
            Object user_sex = rs.getObject(4);
            System.out.println("id: " + id + "\nname: " + userName + "\npassword: " + passWord + "\nuser_sex: " + user_sex);
        }
    }
    private static DataSource ds;//定义一个连接池对象
    static{
        try {
            Properties pro = new Properties();
            pro.load(DBCPUtils.class.getClassLoader().getResourceAsStream("dbcp.properties"));
            ds = BasicDataSourceFactory.createDataSource(pro);//得到一个连接池对象
        } catch (Exception e) {
            throw new ExceptionInInitializerError("初始化连接错误，请检查配置文件！");
        }
    }
    //从池中获取一个连接
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void closeAll(ResultSet rs, Statement stmt, Connection conn){
        if(rs!=null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(stmt!=null){
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(conn!=null){
            try {
                conn.close();//关闭
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
