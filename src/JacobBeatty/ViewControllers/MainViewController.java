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

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static JacobBeatty.Models.Appointment.*;
import static JacobBeatty.Models.Customer.*;
import static JacobBeatty.MySQL.MySQL.*;
import static java.time.ZoneId.*;
import static java.time.ZonedDateTime.*;
import static java.time.format.DateTimeFormatter.*;
import static javafx.scene.control.Alert.*;

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
        if (CustomerList.getSelectionModel().getSelectedItem() == null) {
            return ;
        } else {
            Customer selected = CustomerList.getSelectionModel().getSelectedItem();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Customer.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Modify Customer");
            stage.setScene(scene);
            stage.show();
            CustomerController controller;
            controller = loader.getController();
            controller.fillCustomerInfo(selected);
        }
    }
    @FXML
    private void deleteCustomerClicked(ActionEvent event) {
        Customer selectedCustomer;
        selectedCustomer = CustomerList.getSelectionModel().getSelectedItem();
        if (CustomerList.getSelectionModel().getSelectedItem() != null) {
            Alert a = new Alert(AlertType.NONE);
            a.setAlertType(AlertType.CONFIRMATION);
            a.setContentText("Are you sure?");
            //Lambda used here.
            a.showAndWait().ifPresent(type -> {
                if (type == ButtonType.OK) {
                    try {
                        database.deleteCustomer(selectedCustomer);
                        Alert a2 = new Alert(AlertType.INFORMATION);
                        a2.setContentText("Customer deleted.");
                        a2.show();

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } else {
                    return;
                }
            });
        } else {
            return;
        }
    }
    @FXML
    private void addAppointmentClicked() throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Appointment.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
    }
    @FXML
    private void modifyAppointmentClicked() throws IOException {
        if(AppointmentList.getSelectionModel().getSelectedItem() == null) {
            return ;
        }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Appointment.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Modify Appointment");
            stage.setScene(scene);
            stage.show();
            AppointmentController controller;
            controller = loader.getController();
            controller.fillInAppointmentData(AppointmentList.getSelectionModel().getSelectedItem());
    }
    @FXML
    private void deleteAppointmentClicked() {
        Appointment selectedAppointment = AppointmentList.getSelectionModel().getSelectedItem();
        if (AppointmentList.getSelectionModel().getSelectedItem() != null) {
            Alert a = new Alert(AlertType.NONE);
            a.setAlertType(AlertType.CONFIRMATION);
            a.setContentText("Are you sure?");
            //Lambda used here.
            a.showAndWait().ifPresent(type -> {
                if (type == ButtonType.OK) {
                    try {
                        database.deleteAppointment(selectedAppointment);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } else {
                    return;
                }
            });
        } else {
            return;
        }
    }
    @FXML
    private void changeView() throws SQLException {
        if (!ViewBy.getSelectedToggle().equals(byWeek)) {
            if (!ViewBy.getSelectedToggle().equals(byMonth)) {
            } else {
                byWeek.setSelected(false);
                startView = now().withHour(0).withMinute(0);
                endView = startView.plusMonths(1).withHour(23).withMinute(59);
            }
        } else {
            startView = now().withHour(0).withMinute(0);
            endView = startView.plusWeeks(1).withHour(23).withMinute(59);
            if (!ViewBy.getSelectedToggle().equals(byMonth)) {
            } else {
                byWeek.setSelected(false);
                startView = now().withHour(0).withMinute(0);
                endView = startView.plusMonths(1).withHour(23).withMinute(59);
            }
        }
        swapView();
    }
    private void swapView() throws SQLException {
        String begin = startView.withZoneSameInstant(of("UTC+0")).format(ISO_LOCAL_DATE);
        String end = endView.withZoneSameInstant(of("UTC+0")).format(ISO_LOCAL_DATE);
            database.updateAppointmentTableByEnd(begin, end);

    }
    @FXML
    private void previous() throws SQLException {
        if (!ViewBy.getSelectedToggle().equals(byWeek)) {
        } else {
            startView = startView.minusWeeks(1);
            endView = startView.plusWeeks(1);
        }
        if (!ViewBy.getSelectedToggle().equals(byMonth)) {
        } else {
            startView = startView.minusMonths(1);
            endView = startView.plusMonths(1);
        }
        swapView();
    }
    @FXML
    private void next() throws SQLException {
        if (!ViewBy.getSelectedToggle().equals(byWeek)) {
        } else {
            startView = startView.plusWeeks(1);
            endView = startView.plusWeeks(1);
        }
        if (!ViewBy.getSelectedToggle().equals(byMonth)) {
        } else {
            startView = startView.plusMonths(1);
            endView = startView.plusMonths(1);
        }
        swapView();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            database.updateCountryTable();
            database.updateDivisionTable();
            database.updateCustomerTable();
            database.updateContactTable();
            database.updateAppointmentTable();
            startView = now().withZoneSameInstant(of("UTC"));
            endView = startView.plusMinutes(15);
            String begin = startView.format(ofPattern("yyyy-MM-dd HH:mm:ss"));
            String end = endView.format(ofPattern("yyyy-MM-dd HH:mm:ss"));
            database.updateAppointmentTableByStart(begin, end);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            Report1.setText(database.report1());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            Report2.setText(database.report2());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            Report3.setText(database.report3());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        CustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        CustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        CustomerAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        CustomerDivisions.setCellValueFactory(new PropertyValueFactory<>("customerDivision"));
        CustomerZip.setCellValueFactory(new PropertyValueFactory<>("customerZipCode"));
        CustomerPhone.setCellValueFactory(new PropertyValueFactory<>("customerPhoneNumber"));
        CustomerList.setItems(CustomerTable);
        AppointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        AppointmentTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        AppointmentDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        AppointmentLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        AppointmentContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        AppointmentType.setCellValueFactory(new PropertyValueFactory<>("type"));
        AppointmentCustomer.setCellValueFactory(new PropertyValueFactory<>("customer"));
        AppointmentStart.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        AppointmentEnd.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        byWeek.setToggleGroup(ViewBy);
        byMonth.setToggleGroup(ViewBy);
        byWeek.setSelected(true);
        AppointmentList.setItems(AppointmentTable);
        try {
            previous();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
