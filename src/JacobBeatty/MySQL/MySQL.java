package JacobBeatty.MySQL;

import JacobBeatty.Models.*;
import JacobBeatty.ViewControllers.MainViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static JacobBeatty.Models.Appointment.AppointmentTable;
import static java.sql.DriverManager.getConnection;

public class MySQL {
    public static MySQL database;
    private final Connection connection;
    public MySQL() throws SQLException {
        var connectionString = "jdbc:mysql://wgudb.ucertify.com:3306/WJ07T3V?autoReconnect=true";
        this.connection = getConnection(connectionString, "U07T3V", "53689123060");
    }
    private int verifiedUsername;
    private String verifiedLogin;
    public boolean verifyLogin(String Username, String Password) throws SQLException {
        boolean res = false;
        String userSearch;
        userSearch = "SELECT * FROM users WHERE User_Name=?;";
        PreparedStatement query = this.connection.prepareStatement(userSearch);
        query.setString(1, Username);
        ResultSet result = query.executeQuery();
        if (result.next()) {
            if (Password.matches(result.getString("Password"))) {
                this.verifiedUsername = result.getInt("User_ID");
                this.verifiedLogin = result.getString("User_Name");
                res = true;
            }
        }
        return res;
    }
    public void updateCountryTable() throws SQLException {
        String query = "SELECT * FROM countries;";
        PreparedStatement ps = this.connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        Country.listOfCountries.clear();
        if (rs.next()) do {
            Country.listOfCountries.add(new Country(rs.getInt("Country_ID"), rs.getString("Country")));
        } while (rs.next());
    }
    public void updateDivisionTable() throws SQLException {
        String query = "SELECT * FROM first_level_divisions;";
        PreparedStatement ps = this.connection.prepareStatement(query);
        ResultSet result = ps.executeQuery();
        Division.listOfDivisions.clear();
        while(result.next()) {
            Division.listOfDivisions.add(new Division(result.getInt("Division_ID"), result.getString("Division"), Country.getCountryByID(result.getInt("Country_ID"))));
        }
    }
    public void updateContactTable() throws SQLException {
        String query = "SELECT * FROM contacts;";
        PreparedStatement ps = this.connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        Contact.ContactTable.clear();
        while(rs.next()) {
            Contact.ContactTable.add(new Contact(rs.getInt("Contact_ID"), rs.getString("Contact_Name"), rs.getString("Email")));
        }
        System.out.println("\t\tContacts List Updated to size: " + Contact.ContactTable.size() + "\n\tDone!");
    }
    public void updateCustomerTable() throws SQLException {
        String query = "SELECT * FROM customers;";
        PreparedStatement ps = this.connection.prepareStatement(query);
        ResultSet result = ps.executeQuery();
        Customer.CustomerTable.clear();
        if (result.next()) {
            do
                Customer.CustomerTable.add(new Customer(result.getInt("Customer_ID"), result.getString("Customer_Name"), result.getString("Address"), result.getString("Postal_Code"), result.getString("Phone"), Division.getDivByID(result.getInt("Division_ID"))));
            while (result.next());
        }
    }
    public void newCustomer(Customer eachCustomer) throws SQLException {
        String query = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Created_By, Last_Updated_By, Division_ID) VALUES (?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = this.connection.prepareStatement(query);
        ps.setString(1, eachCustomer.getCustomerName());
        ps.setString(2, eachCustomer.getCustomerAddress());
        ps.setString(3, eachCustomer.getCustomerZipCode());
        ps.setString(4, eachCustomer.getCustomerPhoneNumber());
        ps.setString(5, this.verifiedLogin);
        ps.setString(6, this.verifiedLogin);
        ps.setInt(7, eachCustomer.getCustomerDivision().getID());
        ps.executeUpdate();
        updateCustomerTable();
    }
    public void modifyCustomer(Customer eachCustomer) throws SQLException {
        String query = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = NOW(), Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?;";
        PreparedStatement ps = this.connection.prepareStatement(query);
        ps.setString(1, eachCustomer.getCustomerName());
        ps.setString(2, eachCustomer.getCustomerAddress());
        ps.setString(3, eachCustomer.getCustomerZipCode());
        ps.setString(4, eachCustomer.getCustomerPhoneNumber());
        ps.setString(5, this.verifiedLogin);
        ps.setInt(6, eachCustomer.getCustomerDivision().getID());
        ps.setInt(7, eachCustomer.getCustomerID());
        ps.executeUpdate();
        updateCustomerTable();
    }
    public void deleteCustomer(Customer eachCustomer) throws SQLException {
        deleteAppointmentByCustomer(eachCustomer);
        String query = "DELETE FROM customers WHERE Customer_ID = ?;";
        PreparedStatement ps = this.connection.prepareStatement(query);
        ps.setInt(1, eachCustomer.getCustomerID());
        ps.executeUpdate();
        updateCustomerTable();
    }

    public void updateAppointmentTable() throws SQLException {
        String sql = "SELECT * FROM appointments;";
        PreparedStatement ps = this.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        AppointmentTable.clear();
        while(rs.next()) {
            AppointmentTable.add(new Appointment(rs.getInt("Appointment_ID"), rs.getString("Title"), rs.getString("Description"), rs.getString("Location"), Contact.findContactByID(rs.getInt("Contact_ID")), rs.getString("Type"), rs.getString("Start"), rs.getString("End"), Customer.getCustomer(rs.getInt("Customer_ID"))));
        }
    }
    public void updateAppointmentTableByEnd(String timeStart, String timeEnd) throws SQLException {

        String query = "SELECT * FROM appointments WHERE (End BETWEEN ? AND ?);";
        PreparedStatement ps = this.connection.prepareStatement(query);
        ps.setString(1, timeStart);
        ps.setString(2, timeEnd);
        ResultSet rs = ps.executeQuery();
        AppointmentTable.clear();
        while(rs.next()) {
            AppointmentTable.add(new Appointment(rs.getInt("Appointment_ID"), rs.getString("Title"), rs.getString("Description"), rs.getString("Location"), Contact.findContactByID(rs.getInt("Contact_ID")), rs.getString("Type"), rs.getString("Start"), rs.getString("End"), Customer.getCustomer(rs.getInt("Customer_ID"))));
        }
        System.out.println("\t\tAppointment List Updated to size: " + AppointmentTable.size() + "\n\tDone!");
    }
    public void updateAppointmentTableByStart(String timeStart, String timeEnd) throws SQLException {
        String query = "SELECT * FROM appointments WHERE (Start BETWEEN ? AND ?);";
        PreparedStatement ps = this.connection.prepareStatement(query);
        ps.setString(1, timeStart);
        ps.setString(2, timeEnd);
        ResultSet rs = ps.executeQuery();
        AppointmentTable.clear();
        while(rs.next()) {
            AppointmentTable.add(new Appointment(rs.getInt("Appointment_ID"), rs.getString("Title"), rs.getString("Description"), rs.getString("Location"), Contact.findContactByID(rs.getInt("Contact_ID")), rs.getString("Type"), rs.getString("Start"), rs.getString("End"), Customer.getCustomer(rs.getInt("Customer_ID"))));
        }
    }
    public ObservableList<Appointment> getAppointmentTable(String timeStart, String timeEnd) throws SQLException {
        ObservableList<Appointment> AppointmentTable = FXCollections.observableArrayList();
        String sql = "SELECT * FROM appointments WHERE (Start BETWEEN ? AND ?);";
        PreparedStatement ps = this.connection.prepareStatement(sql);
        ps.setString(1, timeStart);
        ps.setString(2, timeEnd);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            AppointmentTable.add(new Appointment(rs.getInt("Appointment_ID"), rs.getString("Title"), rs.getString("Description"), rs.getString("Location"), Contact.findContactByID(rs.getInt("Contact_ID")), rs.getString("Type"), rs.getString("Start"), rs.getString("End"), Customer.getCustomer(rs.getInt("Customer_ID"))));
        }
        System.out.println("Appointment List Updated to size: " + AppointmentTable.size() + "\n\tDone!");

        return AppointmentTable;
    }



    public void addAppointment(Appointment appointment) throws SQLException {
        //Setting up SQL query with variables added in
        System.out.print("Making Sql Statement... ");
        String query = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?, ?, ?);";
        PreparedStatement ps = this.connection.prepareStatement(query);
        System.out.print("Done!\nFilling in variables... ");
        ps.setInt(1, appointment.getAppointmentID());
        ps.setString(2, appointment.getTitle());
        ps.setString(3, appointment.getDescription());
        ps.setString(4, appointment.getLocation());
        ps.setString(5, appointment.getType());
        ps.setString(6, appointment.getStartTimeUTC());
        ps.setString(7, appointment.getEndTimeUTC());
        ps.setString(8, this.verifiedLogin);
        ps.setString(9, this.verifiedLogin);
        ps.setInt(10, appointment.getCustomer().getCustomerID());
        ps.setInt(11, this.verifiedUsername);
        ps.setInt(12, appointment.getContact().getID());
        ps.executeUpdate();


        //Get the begin and end times from Main Menu and refresh the appointment list
        String begin = MainViewController.startView.withZoneSameInstant(ZoneId.of("UTC+0")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = MainViewController.endView.withZoneSameInstant(ZoneId.of("UTC+0")).format(DateTimeFormatter.ISO_LOCAL_DATE);
        updateAppointmentTableByEnd(begin, end);
    }
    public void updateAppointment(Appointment appointment) throws SQLException {
        //Setting up SQL query with variables added in
        System.out.print("Making Sql Statement... ");
        String sql = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = NOW(), Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?;";
        PreparedStatement ps = this.connection.prepareStatement(sql);
        System.out.print("Done!\nFilling in variables... ");
        ps.setString(1, appointment.getTitle());
        ps.setString(2, appointment.getDescription());
        ps.setString(3, appointment.getLocation());
        ps.setString(4, appointment.getType());
        ps.setString(5, appointment.getStartTimeUTC());
        ps.setString(6, appointment.getEndTimeUTC());
        ps.setString(7, this.verifiedLogin);
        ps.setInt(8, appointment.getCustomer().getCustomerID());
        ps.setInt(9, this.verifiedUsername);
        ps.setInt(10, appointment.getContact().getID());
        ps.setInt(11, appointment.getAppointmentID());

        //Execute Query
        System.out.print("Done!\nExecute Query... ");
        ps.executeUpdate();
        System.out.print("Done!\nUpdate Appointments...\n");

        //Get the begin and end times from Main Menu and refresh the appointment list
        String begin = MainViewController.startView.withZoneSameInstant(ZoneId.of("UTC+0")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = MainViewController.endView.withZoneSameInstant(ZoneId.of("UTC+0")).format(DateTimeFormatter.ISO_LOCAL_DATE);
        updateAppointmentTableByEnd(begin, end);
    }
    public void deleteAppointment(Appointment appointment) throws SQLException {
        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?;";
        PreparedStatement ps = this.connection.prepareStatement(sql);
        System.out.print("Done!\nFilling in variables... ");
        ps.setInt(1, appointment.getAppointmentID());
        ps.executeUpdate();
        String begin = MainViewController.startView.withZoneSameInstant(ZoneId.of("UTC+0")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = MainViewController.endView.withZoneSameInstant(ZoneId.of("UTC+0")).format(DateTimeFormatter.ISO_LOCAL_DATE);
        updateAppointmentTableByEnd(begin, end);
    }
    public void deleteAppointmentByCustomer(Customer customer) throws SQLException {
        String sql = "DELETE FROM appointments WHERE Customer_ID = ?;";
        PreparedStatement ps = this.connection.prepareStatement(sql);
        ps.setInt(1, customer.getCustomerID());
        ps.executeUpdate();
        String begin = MainViewController.startView.withZoneSameInstant(ZoneId.of("UTC+0")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = MainViewController.endView.withZoneSameInstant(ZoneId.of("UTC+0")).format(DateTimeFormatter.ISO_LOCAL_DATE);
        updateAppointmentTableByEnd(begin, end);
    }

    public String report1() throws SQLException{
        //Setting up SQL query with variables added in
        System.out.print("Making Sql Statement... ");
//        String query = "SELECT COUNT(Appointment_ID) AS \"Count\", `Type` , MONTH(`Start`) AS \"Month\", YEAR(`Start`) AS \"Year\" FROM appointments a GROUP BY Year, Month, `Type`;";
        String query = "SELECT COUNT(Appointment_ID), `Type` , MONTH(`Start`), YEAR(`Start`) FROM appointments a GROUP BY MONTH(`Start`), YEAR(`Start`), `Type`;";

        PreparedStatement ps = this.connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        String report = "";
        while(rs.next()){
            report += "There are " + rs.getString(1) + " appointments of type " + rs.getString(2) + " in month " + String.format(rs.getString(3), DateTimeFormatter.ofPattern("MMM")) + " of " + rs.getString(4) + ".\n";
        }

        return report;
    }

    public String report2() throws SQLException{
        //Setting up SQL query with variables added in
        System.out.print("Making Sql Statement... ");
        String sql = "SELECT Contact_Name, Appointment_ID, Title, `Type`, Description, `Start`, `End`, Customer_ID \n" +
                "FROM appointments\n" +
                "INNER JOIN contacts USING (Contact_ID)\n" +
                "ORDER BY Contact_Name, Start";
        PreparedStatement ps = this.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        String report = "";
        while(rs.next()){
            report += "Contact Name: " + rs.getString(1) + ",\t\tAppointment ID: " + rs.getString(2) + ",\t\tTitle: " + rs.getString(3) + ",\t\tType: " + rs.getString(4) + ",\t\tDescription: " + rs.getString(5) + ",\t\tStart: " + rs.getString(6) + ",\t\tEnd: " + rs.getString(7) + ",\t\tCustomer ID: " + rs.getString(8) + "\n";
        }
        return report;
    }
    public String report3() throws SQLException{
        //Setting up SQL query with variables added in
        System.out.print("Making Sql Statement... ");
        String sql = "SELECT * FROM contacts";
        PreparedStatement ps = this.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        String report = "";
        while(rs.next()){
            report += "" + rs.getString(2)  + ", ";
        }

        return report;
    }

}
