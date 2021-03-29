package JacobBeatty.ViewControllers;

import JacobBeatty.Models.Country;
import JacobBeatty.Models.Customer;
import JacobBeatty.Models.Division;
import JacobBeatty.MySQL.MySQL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
    @FXML
    private TextField ID;
    @FXML
    private TextField Name;
    @FXML
    private TextField Address;
    @FXML
    private TextField Zip;
    @FXML
    private TextField Phone;
    @FXML
    private ComboBox<Division> Division;
    @FXML
    private ComboBox<Country> Country;
    @FXML
    private Button SaveButton;
    @FXML
    private Button CancelButton;
    private boolean modify;


    @FXML
    private void savePressed(ActionEvent actionEvent) throws SQLException, IOException {
        int id;
        id = 0;
        if (!modify) {
        } else id = Integer.parseInt(ID.getText());
        String name, address, zip, phone;
        Division division;
        name = Name.getText();
        address = Address.getText();
        zip = Zip.getText();
        phone = Phone.getText();
        division = Division.getValue();
        Customer customer = new Customer(id, name, address, zip, phone, division);
        if (Name.getText().trim().isEmpty() || Address.getText().trim().isEmpty() || Zip.getText().trim().isEmpty() || Phone.getText().trim().isEmpty() || Division.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error Saving");
            alert.setHeaderText("Error");
            alert.setContentText("Form contains blank fields.");
            alert.showAndWait();
        }else {
            if (modify) MySQL.database.modifyCustomer(customer);
            else MySQL.database.newCustomer(customer);
            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
        }
    }
    @FXML
    private void changeCountry(ActionEvent event) {
        Country selected;
        selected = Country.getValue();
        Division.setDisable(false);
        Division.setItems(JacobBeatty.Models.Division.getDivisionByCountry(selected));
    }
    @FXML
    private void cancelButtonPressed(javafx.event.ActionEvent actionEvent) throws IOException {
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
    }
    public void fillCustomerInfo(Customer customer) {
        ID.setText(""+customer.getCustomerID());
        Name.setText(customer.getCustomerName());
        Address.setText(customer.getCustomerAddress());
        Zip.setText(customer.getCustomerZipCode());
        Phone.setText(customer.getCustomerPhoneNumber());
        Country.setValue(customer.getCustomerDivision().getCountry());
        Division.setValue(customer.getCustomerDivision());
        modify = true;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Country.setItems(JacobBeatty.Models.Country.listOfCountries);
        modify = false;
    }
}