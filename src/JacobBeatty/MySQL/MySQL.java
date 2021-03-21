package JacobBeatty.MySQL;

import JacobBeatty.Models.Customer;
import JacobBeatty.Models.Division;

import java.sql.*;

public class MySQL {
    /**
     * This delivers the info to login to the database
     * @throws SQLException
     */
    public static MySQL database;
    private final Connection connection;
    public MySQL() throws SQLException {
        String url = "jdbc:" + "mysql"+ "://" + "wgudb.ucertify.com" +  ":" + "3306" + "/" + "WJ07T3V" + "?autoReconnect=true";
        this.connection = DriverManager.getConnection(url, "U07T3V", "53689123060");
    }
    /**
     * This verifies login details by querying the database.
     * @param User
     * @param Pass
     * @return
     * @throws SQLException
     */
    private int verifiedUsername;
    private String verifiedLogin;
    public boolean verifyUser(String User, String Pass) throws SQLException {

        String userSearch = "SELECT * FROM users WHERE User_Name=?;";
        PreparedStatement query = this.connection.prepareStatement(userSearch);
        query.setString(1, User);
        ResultSet result = query.executeQuery();
        if(!result.next()) return false;
        if(Pass.matches(result.getString("Password"))) {
            this.verifiedUsername = result.getInt("User_ID");
            this.verifiedLogin = result.getString("User_Name");
            return true;
        }
        return false;
    }
    public void updateCustomerTable() throws SQLException {
        String query = "SELECT * FROM customers;";
        PreparedStatement ps = this.connection.prepareStatement(query);
        ResultSet result = ps.executeQuery();
        Customer.CustomerTable.clear();
        while(result.next()) {
            Customer.CustomerTable.add(new Customer(result.getInt("Customer_ID"), result.getString("Customer_Name"), result.getString("Address"), result.getString("Postal_Code"), result.getString("Phone"), Division.findDivisionByID(result.getInt("Division_ID"))));
        }
    }

}
