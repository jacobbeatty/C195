package JacobBeatty.Models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {

    /**
     * This is the major variable that contains all appointment objects
     */
    public static ObservableList<Appointment> AppointmentTable = FXCollections.observableArrayList();

    private final SimpleIntegerProperty appointmentID;
    private final SimpleStringProperty title, description, location, type;
    private final Contact contact;
    private final ZonedDateTime startTime, endTime;
    private final Customer customer;

    public Appointment(int ID, String Title, String Description, String Location, Contact Contact, String Type, String Start, String End, Customer Customer) {
        this.appointmentID = new SimpleIntegerProperty(ID);
        this.title = new SimpleStringProperty(Title);
        this.description = new SimpleStringProperty(Description);
        this.location = new SimpleStringProperty(Location);
        this.contact = Contact;
        this.type = new SimpleStringProperty(Type);
        this.startTime = ZonedDateTime.of(LocalDateTime.parse(Start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ZoneId.of("UTC+0"));
        this.endTime = ZonedDateTime.of(LocalDateTime.parse(End, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ZoneId.of("UTC+0"));
        this.customer = Customer;
    }

    public int getAppointmentID() {
        return this.appointmentID.get();
    }

    public String getTitle() {
        return this.title.get();
    }
    public String getDescription() {
        return this.description.get();
    }
    public String getLocation() {
        return this.location.get();
    }
    public String getType() {
        return this.type.get();
    }
    public Contact getContact() {
        return this.contact;
    }
    public Customer getCustomer() {
        return this.customer;
    }
    public String getStartTime() {
        ZonedDateTime localTime = this.startTime.withZoneSameInstant(ZoneId.systemDefault());
        return localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    public ZonedDateTime getStartTimeObj() {
        return this.startTime;
    }
    public String getStartTimeUTC() {
        ZonedDateTime localTime = this.startTime;
        return localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    public String getEndTime() {
        ZonedDateTime localTime = this.endTime.withZoneSameInstant(ZoneId.systemDefault());
        return localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    public ZonedDateTime getEndTimeObj() {
        return this.endTime;
    }
    public String getEndTimeUTC() {
        ZonedDateTime localTime = this.endTime;
        return localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
