package presentation;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import domain.LoginHandler;
import domain.accesscontrol.User;
import domain.Facade;
import domain.accesscontrol.Producer;
import domain.accesscontrol.SystemAdmin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private Label failedLoginLabel;

    @FXML
    private Button loginButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LoginHandler.getInstance().importLogins();
    }

    //Used to login via then ENTER key
    @FXML
    void logInUsingEnter(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)){
           loginButton.fire();
        }
    }

    //Verifies credentials entered by the user
    @FXML
    private void verifyCredentials(ActionEvent e) throws IOException{
        String username = usernameText.getText();
        int password = passwordText.getText().hashCode();

        User user = LoginHandler.getInstance().verifyCredentials(username, password);

        if(user instanceof SystemAdmin){
            System.out.println("Logged in as SysAdmin");
            App.setRoot("SystemAdminView");
            LoginHandler.getInstance().setCurrentUser(user);
        } else if(user instanceof Producer){
            System.out.println("Logged in as Producer");
            App.setRoot("ProducerView");
            LoginHandler.getInstance().setCurrentUser(user);
        } else {
            failedLoginLabel.setVisible(true);
        }
    }

    @FXML
    private void goToGuest(ActionEvent e) throws IOException{
        App.setRoot("GuestView");
    }
}
