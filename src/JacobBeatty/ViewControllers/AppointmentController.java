package JacobBeatty.ViewControllers;

import JacobBeatty.Models.Appointment;
import JacobBeatty.Models.Contact;
import JacobBeatty.Models.Customer;
import JacobBeatty.MySQL.MySQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ResourceBundle;

import static JacobBeatty.Models.Contact.ContactTable;
import static JacobBeatty.Models.Customer.CustomerTable;
import static java.time.LocalTime.*;
import static javafx.collections.FXCollections.*;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

public class AppointmentController implements Initializable {
    @FXML
    private TextField AppointmentID;
    @FXML
    private TextField AppointmentTitle;
    @FXML
    private TextField AppointmentDescription;
    @FXML
    private TextField AppointmentLocation;
    @FXML
    private TextField AppointmentType;
    @FXML
    private ComboBox<Integer> AppointmentStartHour;
    @FXML
    private ComboBox<Integer> AppointmentStartMin;
    @FXML
    private ComboBox<Integer> AppointmentEndHour;
    @FXML
    private ComboBox<Integer> AppointmentEndMin;
    @FXML
    private ComboBox<Customer> AppointmentCustomer;
    @FXML
    private ComboBox<Contact> AppointmentContact;
    @FXML
    private DatePicker AppointmentEndDate;
    @FXML
    private DatePicker AppointmentStartDate;
    @FXML
    private void CancelPressed(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }
    private boolean modify = false;

    public void fillInAppointmentData(Appointment appointment) {
        modify = true;
        AppointmentID.setText(String.valueOf(appointment.getAppointmentID()));
        AppointmentTitle.setText(appointment.getTitle());
        AppointmentDescription.setText(appointment.getDescription());
        AppointmentLocation.setText(appointment.getLocation());
        AppointmentContact.setValue(appointment.getContact());
        AppointmentType.setText(appointment.getType());
        var convertedStart = appointment.getStartTimeObj().withZoneSameInstant(ZoneId.systemDefault());
        AppointmentStartDate.setValue(convertedStart.toLocalDate());
        AppointmentStartHour.setValue(convertedStart.getHour());
        AppointmentStartMin.setValue(convertedStart.getMinute());
        var convertedEnd = appointment.getEndTimeObj().withZoneSameInstant(ZoneId.systemDefault());
        AppointmentEndDate.setValue(convertedEnd.toLocalDate());
        AppointmentEndHour.setValue(convertedEnd.getHour());
        AppointmentEndMin.setValue(convertedEnd.getMinute());
        AppointmentCustomer.setValue(appointment.getCustomer());
    }


    @FXML
    private void SavePressed(ActionEvent event) throws SQLException {
        if (AppointmentTitle.getText().trim().isEmpty() || AppointmentDescription.getText().trim().isEmpty() || AppointmentLocation.getText().trim().isEmpty() || AppointmentType.getText().trim().isEmpty() || AppointmentContact.getValue() == null || AppointmentStartDate.getValue() == null || AppointmentStartHour.getValue() == null || AppointmentStartMin.getValue() == null || AppointmentEndDate.getValue() == null || AppointmentEndHour.getValue() == null || AppointmentEndMin.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error Saving");
            alert.setHeaderText("Error");
            alert.setContentText("Form contains blank fields.");
            alert.showAndWait();
        } else {
            String tmp = AppointmentID.getText();
        int ID = 0;
        if (modify) ID = Integer.parseInt(tmp);

            String title;
            title = AppointmentTitle.getText();
            String descriptionText = AppointmentDescription.getText();
            String locationText;
            locationText = AppointmentLocation.getText();
            Contact contactValue = AppointmentContact.getValue();
            String typeText;
            typeText = AppointmentType.getText();
            LocalDate startDateValue;
            startDateValue = AppointmentStartDate.getValue();
            String startHour;
            startHour = AppointmentStartHour.getValue().toString();
            String startMinute = AppointmentStartMin.getValue().toString();
            if (startHour.length() >= 2) {
                if (startMinute.length() >= 2) {
                } else {
                    startMinute = "0" + startMinute;
                }
            } else {
                startHour = "0" + startHour;
                if (startMinute.length() >= 2) {
                } else {
                    startMinute = "0" + startMinute;
                }
            }
            LocalTime startTime;
            startTime = parse(startHour + ":" + startMinute, DateTimeFormatter.ISO_TIME);
            var begin = ZonedDateTime.of(startDateValue, startTime, ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC+0"));
            LocalDate endDate;
            endDate = AppointmentEndDate.getValue();
            String endHour;
            endHour = AppointmentEndHour.getValue().toString();
            String endMinute = AppointmentEndMin.getValue().toString();
            if (endHour.length() >= 2) {
            } else {
                endHour = "0" + endHour;
            }
            if (endMinute.length() < 2) endMinute = "0" + endMinute;
            LocalTime endTime;
            endTime = parse(endHour + ":" + endMinute, DateTimeFormatter.ISO_TIME);
            ZonedDateTime end;
            end = ZonedDateTime.of(endDate, endTime, ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC+0"));

            Customer customer = AppointmentCustomer.getValue();
            Appointment appointment;
            appointment = new Appointment(ID, title, descriptionText, locationText, contactValue, typeText, begin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), customer);

            if (!businessHoursCheck(appointment)) {
                Alert alert = new Alert(CONFIRMATION);
                alert.setTitle("Outside of business hours.");
                alert.setHeaderText("Please select a time and day that is within business hours.");
                alert.showAndWait();
                return;
            }
            if (isOverlapping(appointment)) {
                Alert alert = new Alert(CONFIRMATION);
                alert.setTitle("This overlaps with another appointment.");
                alert.setHeaderText("Please select a time that does not overlap with other appointments.");
                alert.showAndWait();
                return;
            }

                if (!modify) {
                    MySQL.database.addAppointment(appointment);
                } else {
                    MySQL.database.updateAppointment(appointment);
                }


            ((Node) (event.getSource())).getScene().getWindow().hide();
        }
    }

    private boolean businessHoursCheck(Appointment appointment){
        ZonedDateTime start;
        start = appointment.getStartTimeObj().withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime end;
        end = appointment.getEndTimeObj().withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime open;
        open = ZonedDateTime.of(start.toLocalDate(), of(7, 59), ZoneId.of("America/New_York"));
        ZonedDateTime close;
        close = ZonedDateTime.of(start.toLocalDate(), of(22, 1), ZoneId.of("America/New_York"));
        ZonedDateTime startLimit2;
        startLimit2 = ZonedDateTime.of(end.toLocalDate(), of(7, 59), ZoneId.of("America/New_York"));
        ZonedDateTime stopLimit2;
        stopLimit2 = ZonedDateTime.of(end.toLocalDate(), of(22, 1), ZoneId.of("America/New_York"));
        if (start.getDayOfWeek() == DayOfWeek.SATURDAY || start.getDayOfWeek() == DayOfWeek.SUNDAY || end.getDayOfWeek() == DayOfWeek.SATURDAY || end.getDayOfWeek() == DayOfWeek.SUNDAY) return false;
        if (open.isBefore(start))
            if (close.isAfter(start)) if (startLimit2.isBefore(end)) if (stopLimit2.isAfter(end)) return true;
        return false;
    }

    boolean overlapCheck = false;


    private boolean isOverlapping(Appointment app) throws SQLException {
        ZonedDateTime start;
        start = app.getStartTimeObj();
        ZonedDateTime end;
        end = app.getEndTimeObj();
        overlapCheck = false;
        String startDate;
        startDate = start.withZoneSameInstant(ZoneId.of("Z")).withHour(0).withMinute(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss" ));
        String endDate;
        endDate = end.withZoneSameInstant(ZoneId.of("Z")).withHour(23).withMinute(59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss" ));
        ObservableList<Appointment> List;
        List = MySQL.database.getAppointmentTable(startDate, endDate);
        for (Appointment appList : List) {
            if (appList.getAppointmentID() == app.getAppointmentID()) {
                continue;
            }
            if (!appList.getStartTimeObj().isBefore(start)) {
                if (!end.isAfter(appList.getStartTimeObj())) {
                    continue;
                }
                overlapCheck = true;
            } else {
                if (!appList.getEndTimeObj().isAfter(start)) {
                    continue;
                }
                overlapCheck = true;
            }
        }
        return overlapCheck;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AppointmentContact.setItems(ContactTable);
        AppointmentCustomer.setItems(CustomerTable);
        ObservableList<Integer> hour;
        hour = observableArrayList();
        int i = 0;
        if (i < 24) {
            do {
                hour.add(i);
                i++;
            } while (i < 24);
        }
        var min = observableArrayList(0, 15, 30, 45);
        for (ComboBox<Integer> integerComboBox : Arrays.asList(AppointmentStartHour, AppointmentEndHour)) {
            integerComboBox.setItems(hour);
        }
        for (ComboBox<Integer> integerComboBox : Arrays.asList(AppointmentStartMin, AppointmentEndMin)) {
            integerComboBox.setItems(min);
        }
    }
}
