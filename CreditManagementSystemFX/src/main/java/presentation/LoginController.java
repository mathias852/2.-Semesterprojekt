package presentation;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import domain.accesscontrol.LoginHandler;
import domain.accesscontrol.Producer;
import domain.accesscontrol.SystemAdmin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private Label failedLoginLabel;

    protected static LoginHandler loginHandler = new LoginHandler();;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginHandler.createSystemAdmin("admin", "admin".hashCode());
        loginHandler.createProducer("anirv20", "hej123".hashCode());

    }

    @FXML
    private void verifyCredentials(ActionEvent e) throws IOException{

        if(loginHandler.verifyCredentials(usernameText.getText(), passwordText.getText()) instanceof SystemAdmin){
            System.out.println("Logged in as SysAdmin");
            App.setRoot("SystemAdminView");
        } else if(loginHandler.verifyCredentials(usernameText.getText(), passwordText.getText()) instanceof Producer){
            System.out.println("Logged in as Producer");
            App.setRoot("ProducerView");
        } else {
            failedLoginLabel.setVisible(true);
        }
    }

    @FXML
    private void goToGuest(ActionEvent e) throws IOException{
        App.setRoot("GuestView");
    }
}
