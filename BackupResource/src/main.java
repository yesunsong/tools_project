//import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 备份Resource资源
 * 
 * @author yesunsong
 *
 */
public class main {

	private static String table_name="COMPANY";
	private static String field_name_1="time";
	private static String field_name_2="path";
	
	public static void main(String[] args) {
		System.out.println("test");
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			// 连接数据库
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			// 创建表
			stmt =  c.createStatement();
			String sql = "CREATE TABLE "+table_name+" " +
			"(ID INT PRIVATE KEY  NOT NULL,"+
			field_name_1+"        TEXT NOT NULL,"+
			field_name_2+" 		  TEXT  NOT NULL"+
			")";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
