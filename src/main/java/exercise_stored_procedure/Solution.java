package exercise_stored_procedure;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/*drop procedure if exists usp_updateUser;
create procedure usp_updateUser(id_ int, name_ varchar(255), country_ varchar(255), email_ varchar(255))
begin
    update mydatabase.user set name=name_, country=country_, email=email_ where id = id_;
end;

drop procedure if exists usp_removeUser;
create procedure usp_removeUser(id_ int)
begin
    delete from mydatabase.user where id = id_;
end;*/
public class Solution {
    public static void main(String[] args) {
        List<User> users = getAllUser();
        System.out.println(users);

        updateUser(1, new User(1, "ky yeu thu", "Gia dinh ta <3", "ky&thu@gmail.com"));

        users = getAllUser();
        System.out.println(users);

        removeUser(7);

        users = getAllUser();
        System.out.println(users);
    }

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

    private static List<User> getAllUser() {
        List<User> users = new ArrayList<>();
        Connection connection = null;
        CallableStatement callableStatement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            callableStatement = connection.prepareCall("{call usp_getAllUser()}");
            resultSet = callableStatement.executeQuery();
            getUsersFromResultSet(users, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if (callableStatement != null) {
                try {
                    callableStatement.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return users;
    }

    private static void getUsersFromResultSet(List<User> users, ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String country = resultSet.getString("country");
            String email = resultSet.getString("email");
            users.add(new User(id, name, country, email));
        }
    }

    private static void updateUser(int id, User user) {
        Connection connection = getConnection();
        CallableStatement callableStatement = null;
        try {
            callableStatement = connection.prepareCall("{call usp_updateUser(?,?,?,?)}");
            callableStatement.setInt(1, id);
            callableStatement.setString(2, user.getName());
            callableStatement.setString(3, user.getCountry());
            callableStatement.setString(4, user.getEmail());
            callableStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
                if (callableStatement != null) callableStatement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static void removeUser(int id) {
        Connection connection = getConnection();
        CallableStatement callableStatement = null;
        try {
            callableStatement = connection.prepareCall("{call usp_removeUser(?)}");
            callableStatement.setInt(1, id);
            callableStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
                if (callableStatement != null) callableStatement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
