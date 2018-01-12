package GUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class test {

	public static void main(String[] args) {
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://5.29.193.52:3306/oop_course_ariel" + "?autoReconnect=true&useSSL=false", "oop1", "Lambda1();");
			PreparedStatement pst = con.prepareStatement("SELECT * FROM ex4_db");
			ResultSet rs = pst.executeQuery();
			//	        while (rs.next()){
			rs.next();	
			for (int i = 1; i < rs.getMetaData().getColumnCount(); i++) {
				System.out.print(rs.getString(i) + "%");
			}
			//	        }
//			rs.next();
//			System.out.println(rs.getString(2));
		}
		catch (SQLException e) {
			System.out.println("first: "+e);
		}
	}

}
