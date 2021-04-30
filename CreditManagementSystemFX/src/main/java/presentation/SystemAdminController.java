package presentation;

import domain.Facade;
import domain.credit.Credit;
import domain.credit.CreditedPerson;
import domain.program.Episode;
import domain.program.Program;
import domain.program.TVSeries;
import domain.program.Transmission;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemAdminController extends ProducerController implements Initializable {
    @FXML
    private TextField usernameText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private ComboBox<String> usertypeCombo;

    @FXML
    private Button createUserButton;

    @FXML
    private Label createUserLabel;

    @FXML
    private void createUserAction(ActionEvent e){

        Pattern pattern = Pattern.compile("^(?=.{2,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$");
        Matcher matcher = pattern.matcher(usernameText.getText());
        boolean matchFound = matcher.find();

        if (matchFound && !passwordText.getText().isBlank()) {
            boolean success = false;
            if (usertypeCombo.getSelectionModel().getSelectedItem().equals("Producer")) {
                success = LoginController.loginHandler.createProducer(usernameText.getText(), passwordText.getText().hashCode());
            } else if (usertypeCombo.getSelectionModel().getSelectedItem().equals("System Administrator")) {
                success = LoginController.loginHandler.createSystemAdmin(usernameText.getText(), passwordText.getText().hashCode());
            }

            if (success) {
                createUserLabel.setText("Created a User");
            } else {
                createUserLabel.setText("Could not create user");
            }

        } else {
            createUserLabel.setText("Could not create user");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        usertypeCombo.getItems().add("Producer");
        usertypeCombo.getItems().add("System Administrator");
    }


}