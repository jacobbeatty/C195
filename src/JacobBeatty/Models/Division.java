package JacobBeatty.Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Division {
    //Generating variables
    public static ObservableList<Division> listOfDivisions = FXCollections.observableArrayList();
    private final int ID;
    private final String name;
    private final Country country;
    /**
     * Constructs division object.
     * @param ID
     * @param name
     * @param country
     */
    public Division (int ID, String name, Country country) {
        this.ID = ID;
        this.name = name;
        this.country = country;
    }
    /**
     * Finds the division based on the ID
     * @param ID
     * @return
     */
    public static Division getDivByID(int ID) {
        int i = 0;
        while (i < listOfDivisions.size()) {
            if (ID == listOfDivisions.get(i).getID())
                return listOfDivisions.get(i);
            i++;
        }
        return null;

    }

    public static ObservableList<Division> getDivisionByCountry(Country country) {
        ObservableList<Division> temp = FXCollections.observableArrayList();

        int i = 0;
        while (i < listOfDivisions.size()) {
            if (country == listOfDivisions.get(i).getCountry())
                temp.add(listOfDivisions.get(i));
            i++;
        }
        return temp;
    }
    //Getters
    public int getID() {
        return this.ID;
    }
    public String getName() {
        return this.name;
    }
    public Country getCountry() {
        return this.country;
    }
    //Overriding toString to fix memory reference bug
    @Override
    public String toString() {
        return this.name;
    }



}
