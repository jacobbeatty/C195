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
import java.util.ResourceBundle;

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
    private ComboBox<Contact> AppointmentContact;
    @FXML
    private TextField AppointmentType;
    @FXML
    private DatePicker AppointmentStartDate;
    @FXML
    private ComboBox<Integer> AppointmentStartHour;
    @FXML
    private ComboBox<Integer> AppointmentStartMin;

    @FXML
    private DatePicker AppointmentEndDate;
    @FXML
    private ComboBox<Integer> AppointmentEndHour;
    @FXML
    private ComboBox<Integer> AppointmentEndMin;
    @FXML
    private ComboBox<Customer> AppointmentCustomer;
    @FXML
    private Button SaveButton;
    @FXML
    private Button CancelButton;

    @FXML
    private void CancelPressed(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }
    private boolean modify = false;

    public void prefill(Appointment appointment) {                                         //PreFill all Customer info
        modify = true;

        AppointmentID.setText(String.valueOf(appointment.getAppointmentID()));
        AppointmentTitle.setText(appointment.getTitle());
        AppointmentDescription.setText(appointment.getDescription());
        AppointmentLocation.setText(appointment.getLocation());
        AppointmentContact.setValue(appointment.getContact());
        AppointmentType.setText(appointment.getType());
        ZonedDateTime convertedStart = appointment.getStartTimeObj().withZoneSameInstant(ZoneId.systemDefault());
        AppointmentStartDate.setValue(convertedStart.toLocalDate());
        AppointmentStartHour.setValue(convertedStart.getHour());
        AppointmentStartMin.setValue(convertedStart.getMinute());
        ZonedDateTime convertedEnd = appointment.getEndTimeObj().withZoneSameInstant(ZoneId.systemDefault());
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
        String tmp;
        tmp = AppointmentID.getText();
        int ID = 0;
        if (modify) ID = Integer.parseInt(tmp);

        String title = AppointmentTitle.getText();
        String des = AppointmentDescription.getText();
        String Loc = AppointmentLocation.getText();
        Contact contact = AppointmentContact.getValue();
        String type = AppointmentType.getText();
        LocalDate beginDate = AppointmentStartDate.getValue();
        String startHr = AppointmentStartHour.getValue().toString();
        String startMin = AppointmentStartMin.getValue().toString();
        if (startHr.length() < 2) startHr = "0" + startHr;
        if (startMin.length() < 2) startMin = "0" + startMin;
        System.out.println(startHr + ":" + startMin);
        LocalTime beginTime = LocalTime.parse(startHr + ":" + startMin, DateTimeFormatter.ISO_TIME);
        ZonedDateTime begin = ZonedDateTime.of(beginDate, beginTime, ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC+0"));
        System.out.println("Time: " + beginTime);
        System.out.println("TimeDate: " + begin);

        LocalDate endDate = AppointmentEndDate.getValue();
        String endHr = AppointmentEndHour.getValue().toString();
        String endMin = AppointmentEndMin.getValue().toString();
        if (endHr.length() < 2) endHr = "0" + endHr;
        if (endMin.length() < 2) endMin = "0" + endMin;
        LocalTime endTime = LocalTime.parse(endHr + ":" + endMin, DateTimeFormatter.ISO_TIME);
        ZonedDateTime end = ZonedDateTime.of(endDate, endTime, ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC+0"));

        Customer customer = AppointmentCustomer.getValue();
        Appointment appointment = new Appointment(ID, title, des, Loc, contact, type, begin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), customer);

            if (!businessHoursCheck(appointment)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Outside of business hours.");
                alert.setHeaderText("Please select a time and day that is within business hours.");
                alert.showAndWait();
                return;
            }

            //We need to check if the new appointment will overlap any existing appointment
            if (isOverlapping(appointment)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("This overlaps with another appointment.");
                alert.setHeaderText("Please select a time that does not overlap with other appointments.");
                alert.showAndWait();
                return;
            }

            try {
                if (modify) MySQL.database.updateAppointment(appointment);
                else MySQL.database.addAppointment(appointment);

            } catch (SQLException e) {
                System.out.println("SQL Error!!! " + e);
            }
            ((Node) (event.getSource())).getScene().getWindow().hide();
        }
    }

    private boolean businessHoursCheck(Appointment app){
        ZonedDateTime start = app.getStartTimeObj().withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime end = app.getEndTimeObj().withZoneSameInstant(ZoneId.of("America/New_York"));

        ZonedDateTime startLimit1 = ZonedDateTime.of(start.toLocalDate(), LocalTime.of(7, 59), ZoneId.of("America/New_York"));
        ZonedDateTime stopLimit1 = ZonedDateTime.of(start.toLocalDate(), LocalTime.of(22, 1), ZoneId.of("America/New_York"));
        ZonedDateTime startLimit2 = ZonedDateTime.of(end.toLocalDate(), LocalTime.of(7, 59), ZoneId.of("America/New_York"));
        ZonedDateTime stopLimit2 = ZonedDateTime.of(end.toLocalDate(), LocalTime.of(22, 1), ZoneId.of("America/New_York"));

        if(start.getDayOfWeek() == DayOfWeek.SATURDAY) return false;
        if(start.getDayOfWeek() == DayOfWeek.SUNDAY) return false;
        if(end.getDayOfWeek() == DayOfWeek.SATURDAY) return false;
        if(start.getDayOfWeek() == DayOfWeek.SUNDAY) return false;
        return startLimit1.isBefore(start) && stopLimit1.isAfter(start) && startLimit2.isBefore(end) && stopLimit2.isAfter(end);
    }

    boolean overlapCheck = false;


    private boolean isOverlapping(Appointment app){
        ZonedDateTime start = app.getStartTimeObj();
        ZonedDateTime end = app.getEndTimeObj();
        System.out.println("Starting OverLap check");
        overlapCheck = false;
        String startDate = start.withZoneSameInstant(ZoneId.of("Z")).withHour(0).withMinute(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss" ));
        String endDate = end.withZoneSameInstant(ZoneId.of("Z")).withHour(23).withMinute(59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss" ));
        ObservableList<Appointment> List = FXCollections.observableArrayList();

        try{
            List = MySQL.database.getAppointmentTable(startDate, endDate);
        }
        catch(SQLException e){
            System.out.println("Database Error!!! " + e);
        }

        List.forEach(appList -> {
            if(appList.getAppointmentID() != app.getAppointmentID() ){
                if(appList.getStartTimeObj().isBefore(start)){
                    if(appList.getEndTimeObj().isAfter(start)){
                        overlapCheck = true;
                        System.out.println("OverLap Check Failed!!!");
                    }
                }
                else{
                    if(end.isAfter(appList.getStartTimeObj())){
                        overlapCheck = true;
                        System.out.println("OverLap Check Failed!!!");
                    }
                }
            }
        });

        System.out.println("Overlap check done " + overlapCheck);
        return overlapCheck;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //fill combo boxes with lists
        AppointmentContact.setItems(Contact.ContactTable);
        AppointmentCustomer.setItems(Customer.CustomerTable);

        //Make an observable list with 0-23 hour options
        ObservableList<Integer> hr = FXCollections.observableArrayList();
        int i = 0;
        while(i < 24) {
            hr.add(i);
            i++;
        }
        //make list with 0, 15, 30, 45
        ObservableList<Integer> min = FXCollections.observableArrayList(0, 15, 30, 45);

        //fill hour combo boxes with hr list
        AppointmentStartHour.setItems(hr);
        AppointmentEndHour.setItems(hr);
        AppointmentStartMin.setItems(min);
        AppointmentEndMin.setItems(min);


    }
}
