package JacobBeatty.ViewControllers;

import JacobBeatty.MySQL.MySQL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button LoginButton;

    @FXML
    private TextField UsernameField;

    @FXML
    private PasswordField PasswordField;

    @FXML
    private Label ErrorMessage;

    @FXML
    private Label UserZone;

    Locale locale = Locale.getDefault();
    ResourceBundle LanguageBundle = ResourceBundle.getBundle("JacobBeatty.Language.lang", locale);

    @FXML
    private void loginAttempt(ActionEvent event) throws IOException, SQLException {
        String User = UsernameField.getText();
        String Pass = PasswordField.getText();
        boolean verifyUser = false;
        verifyUser = MySQL.database.verifyUser(User, Pass);

        if(verifyUser) {
                JacobBeatty.Logs.Log.generateLog(User + " Login Successful.");
                ErrorMessage.setText("");
                Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setTitle("Main Menu");
                stage.setScene(scene);
                stage.show();
        }
        else
        {
            JacobBeatty.Logs.Log.generateLog(User + " Login Failed.");
            ErrorMessage.setText(LanguageBundle.getString("BadLogin"));
        }
    }
    /**
     *This function initializes the controller class. Properties in this function help set the language
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /**
         * This attempts to set up the database.
         */
        try {
            MySQL.database = new MySQL();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        String Country = locale.getDisplayCountry();
        String Zone = ZoneId.systemDefault().getDisplayName(TextStyle.NARROW, locale);
        UserZone.setText(Country +", "+ Zone);
        UsernameField.setPromptText(LanguageBundle.getString("Username"));
        PasswordField.setPromptText(LanguageBundle.getString("Password"));
        LoginButton.setText(LanguageBundle.getString("Login"));
    }
}
