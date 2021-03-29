package JacobBeatty.Models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Customer {
    public static ObservableList<Customer> CustomerTable = FXCollections.observableArrayList();
    private final SimpleIntegerProperty customerID;
    private final SimpleStringProperty customerName, customerAddress, customerZipCode, customerPhoneNumber;
    private final Division customerDivision;

    public Customer(int customerID, String customerName, String customerAddress, String customerZipCode, String customerPhoneNumber, Division customerDivision) {
        this.customerID = new SimpleIntegerProperty(customerID);
        this.customerName = new SimpleStringProperty(customerName);
        this.customerAddress = new SimpleStringProperty(customerAddress);
        this.customerZipCode = new SimpleStringProperty(customerZipCode) ;
        this.customerPhoneNumber = new SimpleStringProperty(customerPhoneNumber);
        this.customerDivision = customerDivision;
    }
    public int getCustomerID() {
        return customerID.get();
    }
    public SimpleIntegerProperty customerIDProperty() {
        return customerID;
    }
    public String getCustomerName() {
        return customerName.get();
    }
    public SimpleStringProperty customerNameProperty() {
        return customerName;
    }
    public String getCustomerAddress() {
        return customerAddress.get();
    }
    public SimpleStringProperty customerAddressProperty() {
        return customerAddress;
    }
    public String getCustomerZipCode() {
        return customerZipCode.get();
    }
    public SimpleStringProperty customerZipCodeProperty() {
        return customerZipCode;
    }
    public String getCustomerPhoneNumber() {
        return customerPhoneNumber.get();
    }
    public SimpleStringProperty customerPhoneNumberProperty() {
        return customerPhoneNumber;
    }
    public Division getCustomerDivision() {
        return customerDivision;
    }
    public static Customer getCustomer(int ID) {
        int i = 0;
        while(i < CustomerTable.size()) {
            if(ID == CustomerTable.get(i).getCustomerID()) return CustomerTable.get(i);
            i++;
        }
        return null;
    }
    @Override
    public String toString() {
        return this.getCustomerName();
    }

}
