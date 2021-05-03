package presentation;

import java.io.IOException;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.ResourceBundle;

import domain.accesscontrol.User;
import domain.Facade;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import persistence.PersistenceHandler;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private Label failedLoginLabel;

    @FXML
    private Button loginButton;

    protected static LoginHandler loginHandler = new LoginHandler();
    private PersistenceHandler persistenceHandler = new PersistenceHandler();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loginHandler.createSystemAdmin("admin", "admin".hashCode());
        importLogins();

    }

    private void importLogins(){

        ArrayList<String[]> producers = persistenceHandler.readProducer();
        for(String[] s : producers){
            loginHandler.createProducer(s[0], Integer.parseInt(s[1]));
        }

        ArrayList<String[]> sysAdmin = persistenceHandler.readSystemAdmin();
        for(String[] s : sysAdmin){
            loginHandler.createSystemAdmin(s[0], Integer.parseInt(s[1]));
        }

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
        if(loginHandler.verifyCredentials(usernameText.getText(), passwordText.getText().hashCode()) instanceof SystemAdmin){
            System.out.println("Logged in as SysAdmin");
            App.setRoot("SystemAdminView");
        } else if(loginHandler.verifyCredentials(usernameText.getText(), passwordText.getText().hashCode()) instanceof Producer){
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
