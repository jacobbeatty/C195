package JacobBeatty.ViewControllers;

import JacobBeatty.Models.Customer;
import JacobBeatty.Models.Division;
import JacobBeatty.MySQL.MySQL;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML
    private TableView<Customer> CustomerList;
    @FXML
    private TableColumn<Customer, Integer> CustomerID;
    @FXML
    private TableColumn<Customer, String> CustomerName;
    @FXML
    private TableColumn<Customer, String> CustomerAddress;
    @FXML
    private TableColumn<Customer, String> CustomerZip;
    @FXML
    private TableColumn<Customer, String> CustomerPhone;
    @FXML
    private TableColumn<Customer, Division> CustomerDivisions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Populates table with customer data
        try {
            MySQL.database.updateCustomerTable();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //Setup table and columns for customer list
        CustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        CustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        CustomerAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        CustomerDivisions.setCellValueFactory(new PropertyValueFactory<>("customerDivision"));
        CustomerZip.setCellValueFactory(new PropertyValueFactory<>("customerZipCode"));
        CustomerPhone.setCellValueFactory(new PropertyValueFactory<>("customerPhoneNumber"));
        CustomerList.setItems(Customer.CustomerTable);
    }
}
