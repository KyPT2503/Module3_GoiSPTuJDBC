package transaction;

import java.sql.*;

public class Solution {
    private static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "root", "250399");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /*insert new user and user's permission, using transaction to commit if success, to rollback if fail*/
    public static void main(String[] args) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("insert into mydatabase.user (id, name, country, email) value (110,'andy','USA','andy@gmail.com')");
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("insert into mydatabase.user_permission (user_id, permission_id) value (110,1)");
            boolean isAffected1 = preparedStatement.executeUpdate() > 0;
            preparedStatement = connection.prepareStatement("insert into mydatabase.user_permission (user_id, permission_id) value (110,2)");
            boolean isAffected2 = preparedStatement.executeUpdate() > 0;
            preparedStatement = connection.prepareStatement("insert into mydatabase.user_permission (user_id, permission_id) value (110,5)");
            boolean isAffected3 = preparedStatement.executeUpdate() > 0;
            if (isAffected1 && isAffected2 && isAffected3) {
                connection.commit();
                System.out.println("Finished. Successfully.");
            } else {
                connection.rollback();
                System.out.println("Finished. Failed.");
            }
        }catch(SQLIntegrityConstraintViolationException e){
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            System.out.println("Finished. Failed.");
        }
        catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            System.out.println("Finished. Failed.");
            e.printStackTrace();
        } finally {
            try {
                connection.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
