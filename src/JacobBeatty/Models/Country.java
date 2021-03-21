package JacobBeatty.Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Country {
    /**
     * List object used to refrence all countrys
     */
    public static ObservableList<Country> countryList = FXCollections.observableArrayList();

    private final int countryID;
    private final String countryName;

    /**
     * The countries constructor
     * @param id the id of the country from the database
     * @param name the name of the country from the database
     */
    public Country(int id, String name) {
        this.countryID = id;
        this.countryName = name;
    }

    /**
     * Get a country object based on the Id passed
     * used by the mysql class to assign a country to a division
     * @param id the id of the country you want to return
     * @return the country of the id entered
     */
    public static Country findCountryById(int id) {
        int i = 0;
        while (i < countryList.size()) {
            if (id == countryList.get(i).countryID)
                return countryList.get(i);
            i++;
        }
        return null;
    }

    public int getCountryID() {
        return this.countryID;
    }

    public String getCountryName() {
        return this.countryName;
    }

    /**
     * Returns a nice looking string to display in the customer menu
     * @return
     */
    @Override
    public String toString() {
        return this.countryName;
    }

}
