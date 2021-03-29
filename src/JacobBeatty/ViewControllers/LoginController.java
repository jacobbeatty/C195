package JacobBeatty.ViewControllers;

import JacobBeatty.MySQL.MySQL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import java.util.Locale;
import java.util.ResourceBundle;

import static java.time.ZoneId.systemDefault;
import static java.time.format.TextStyle.NARROW;

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
    ResourceBundle LanguageBundle;
    @FXML
    private void loginAttempt() throws IOException, SQLException {
        String Username;
        Username = UsernameField.getText();
        String Password;
        Password = PasswordField.getText();
        boolean verifyUser = MySQL.database.verifyLogin(Username, Password);
        if (!verifyUser) {
            JacobBeatty.Logs.Log.generateLog(Username + " could not log in.");
            ErrorMessage.setText(LanguageBundle.getString("BadLogin"));
        } else {
                JacobBeatty.Logs.Log.generateLog(Username + " logged in.");
                Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setTitle("Schedule");
                stage.setScene(scene);
                stage.show();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LanguageBundle = ResourceBundle.getBundle("JacobBeatty.Language.lang", locale);

        try {
            MySQL.database = new MySQL();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        String Zone;
        Zone = systemDefault().getDisplayName(NARROW, locale);
        UserZone.setText(Zone);
        UsernameField.setPromptText(LanguageBundle.getString("Username"));
        PasswordField.setPromptText(LanguageBundle.getString("Password"));
        LoginButton.setText(LanguageBundle.getString("Login"));
    }
}
