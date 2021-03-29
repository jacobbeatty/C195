package JacobBeatty.ViewControllers;

import JacobBeatty.Models.Appointment;
import JacobBeatty.Models.Customer;
import JacobBeatty.Models.Division;
import JacobBeatty.MySQL.MySQL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.swing.text.View;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
    @FXML
    private TableView<Appointment> AppointmentList;
    @FXML
    private TableColumn<Appointment, Integer>AppointmentID;
    @FXML
    private TableColumn<Appointment, String>AppointmentTitle;
    @FXML
    private TableColumn<Appointment, String>AppointmentDescription;
    @FXML
    private TableColumn<Appointment, String>AppointmentLocation;
    @FXML
    private TableColumn<Appointment, String>AppointmentContact;
    @FXML
    private TableColumn<Appointment, String>AppointmentType;
    @FXML
    private TableColumn<Appointment, String> AppointmentStart;
    @FXML
    private TableColumn<Appointment, String> AppointmentEnd;
    @FXML
    private TableColumn<Appointment, Customer> AppointmentCustomer;
    @FXML
    private ToggleGroup ViewBy;
    @FXML
    private RadioButton byWeek;
    @FXML
    private RadioButton byMonth;
    @FXML
    private TextArea Report1;

    @FXML
    private TextArea Report2;
    @FXML
    private TextArea Report3;

    public static ZonedDateTime startView;
    public static ZonedDateTime endView;
    @FXML
    private void addCustomerClicked(ActionEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Customer.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("New Customer");
            stage.setScene(scene);
            stage.show();
    }
    @FXML
    private void modifyCustomerClicked(ActionEvent event) throws IOException {
        if (CustomerList.getSelectionModel().getSelectedItem() == null)return ;
        else {
            Customer selected = CustomerList.getSelectionModel().getSelectedItem();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Customer.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Modify Customer");
            stage.setScene(scene);
            stage.show();
            CustomerController controller = loader.getController();
            controller.fillInfo(selected);
        }
    }
    @FXML
    private void deleteCustomerClicked(ActionEvent event) {
        Customer selectedCustomer = CustomerList.getSelectionModel().getSelectedItem();
        if(selectedCustomer == null) return;
        Alert a = new Alert(Alert.AlertType.NONE);
        a.setAlertType(Alert.AlertType.CONFIRMATION);
        a.setContentText("Are you sure?");
        //Lamda used here.
        a.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK) {
                try {
                    MySQL.database.deleteCustomer(selectedCustomer);
                    Alert a2 = new Alert(Alert.AlertType.INFORMATION);
                    a2.setContentText("Customer deleted.");
                    a2.show();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else {
                return;
            }
        });
    }

    @FXML
    private void addAppointmentClicked(ActionEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Appointment.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
    }
    @FXML
    private void modifyAppointmentClicked(ActionEvent event) throws IOException {
        Appointment selected = AppointmentList.getSelectionModel().getSelectedItem();
        if(selected == null) return;//don't finish function if nothing is selected
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Appointment.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("CalenDo - Update Appointment");
            stage.setScene(scene);
            stage.show();
        AppointmentController controller = loader.getController();
        controller.prefill(selected);

    }
    @FXML
    private void deleteAppointmentClicked(ActionEvent event) {
        Appointment selectedAppointment = AppointmentList.getSelectionModel().getSelectedItem();

        if(selectedAppointment == null) return;
        Alert a = new Alert(Alert.AlertType.NONE);
        a.setAlertType(Alert.AlertType.CONFIRMATION);
        a.setContentText("Are you sure?");
        //Lambda used here.
        a.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK) {
                try {
                    MySQL.database.deleteAppointment(selectedAppointment);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else {
                return;
            }
        });
    }

    @FXML
    private void viewRadioChange(ActionEvent event) {
        if(ViewBy.getSelectedToggle().equals(byWeek)){
            System.out.println("Week is selected");
            startView = ZonedDateTime.now().withHour(0).withMinute(0);
            endView = startView.plusWeeks(1).withHour(23).withMinute(59);
        }
        //if month radio button
        if(ViewBy.getSelectedToggle().equals(byMonth)){
            byWeek.setSelected(false);
            System.out.println("Month is selected");
            startView = ZonedDateTime.now().withHour(0).withMinute(0);
            endView = startView.plusMonths(1).withHour(23).withMinute(59);
        }
        updateView();
    }

    //get a new list to diplay base on the begin and end dates
    private void updateView() {
        String begin = startView.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String end = endView.format(DateTimeFormatter.ISO_LOCAL_DATE);
//        viewLabel.setText(begin + " :: " + end);

        begin = startView.withZoneSameInstant(ZoneId.of("UTC+0")).format(DateTimeFormatter.ISO_LOCAL_DATE);
        end = endView.withZoneSameInstant(ZoneId.of("UTC+0")).format(DateTimeFormatter.ISO_LOCAL_DATE);

        try {
            MySQL.database.updateAppointmentTableByEnd(begin, end);
        }
        catch(SQLException e) {
            System.out.println("SQL Error!!! " + e);
        }
    }
    @FXML
    private void previous() {
        if(ViewBy.getSelectedToggle().equals(byWeek)){
            startView = startView.minusWeeks(1);
            endView = startView.plusWeeks(1);
        }

        if(ViewBy.getSelectedToggle().equals(byMonth)){
            startView = startView.minusMonths(1);
            endView = startView.plusMonths(1);
        }
        updateView();
    }

    //when the view plus button is pressed add a week or month based on radio button
    @FXML
    private void next() {
        if(ViewBy.getSelectedToggle().equals(byWeek)){
            startView = startView.plusWeeks(1);
            endView = startView.plusWeeks(1);
        }

        if(ViewBy.getSelectedToggle().equals(byMonth)){
            startView = startView.plusMonths(1);
            endView = startView.plusMonths(1);
        }
        updateView();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Populates table with customer data
        try {
            MySQL.database.updateCountryTable();
            MySQL.database.updateDivisionTable();
            MySQL.database.updateCustomerTable();
            MySQL.database.updateContactTable();
            MySQL.database.updateAppointmentTable();
            startView = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
            endView = startView.plusMinutes(15);
            String begin = startView.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String end = endView.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            MySQL.database.updateAppointmentTableByStart(begin, end);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            Report1.setText(MySQL.database.report1());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            Report2.setText(MySQL.database.report2());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            Report3.setText(MySQL.database.report3());
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

        //Setup table and column for appointment list
        AppointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        AppointmentTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        AppointmentDescription.setCellValueFactory(new PropertyValueFactory("description"));
        AppointmentLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        AppointmentContact.setCellValueFactory(new PropertyValueFactory("contact"));
        AppointmentType.setCellValueFactory(new PropertyValueFactory<>("type"));
        AppointmentCustomer.setCellValueFactory(new PropertyValueFactory<>("customer"));
        AppointmentStart.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        AppointmentEnd.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        byWeek.setToggleGroup(ViewBy);
        byMonth.setToggleGroup(ViewBy);
        byWeek.setSelected(true);
        AppointmentList.setItems(Appointment.AppointmentTable);
        previous();
        next();

    }
}
