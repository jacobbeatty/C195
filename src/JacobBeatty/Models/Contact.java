package JacobBeatty.Models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Contact {
    public static ObservableList<Contact> ContactTable= FXCollections.observableArrayList();
    private final SimpleIntegerProperty ID;
    private final SimpleStringProperty Name, Email;

    /**
     * This is the constructor for a contact object.
     */
    public Contact(int id, String name, String email) {
        this.ID = new SimpleIntegerProperty(id);
        this.Name = new SimpleStringProperty(name);
        this.Email = new SimpleStringProperty(email);
    }

    /**
     * Grabs ID.
     * @return
     */
    public int getID() {
        return this.ID.get();
    }
    /**
     * Grabs name.
     * @return
     */
    public String getName() {return this.Name.get();}
    /**
     * getter
     * @return
     */
    /**
     * Grabs contact email.
     * @return
     */
    public String getEmail() {return this.Email.get();}

    /**
     * Finds the contact based on ID.
     * @param id
     * @return
     */
    public static Contact findContactByID(int id) {
        int i = 0;
        if (i < ContactTable.size()) {
            do {
                if (id == ContactTable.get(i).getID()) return ContactTable.get(i);
                i++;
            } while (i < ContactTable.size());
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
