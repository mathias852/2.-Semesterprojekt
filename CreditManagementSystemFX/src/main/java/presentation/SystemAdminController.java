package presentation;

import domain.Facade;
import domain.credit.Credit;
import domain.credit.CreditedPerson;
import domain.program.Episode;
import domain.program.Program;
import domain.program.TVSeries;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class SystemAdminController implements Initializable {

    @FXML
    private ComboBox<String> programTypeSelection;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField nameText;
    @FXML
    private Label descriptionLabel;
    @FXML
    private TextField descriptionText;
    @FXML
    private TextField seasonNumberText;

    @FXML
    private TextField episodeNumberText;

    @FXML
    private TextField durationText;

    @FXML
    private Label seasonNoLabel;

    @FXML
    private Label episodeNoLabel;

    @FXML
    private Label durationLabel;

    @FXML
    private ComboBox<String> tvSeriesSelection;

    @FXML
    private Label tvSLabel;

    @FXML
    private Button createProgramBtn;

    @FXML
    private ComboBox<String> programSelection;

    @FXML
    private ComboBox<String> functionSelection;

    @FXML
    private ComboBox<String> creditedPersonSelection;

    @FXML
    private TextField creditedPersonNameText;

    @FXML
    private Label creditedPersonLabel;

    @FXML
    private Button createPersonBtn;
    @FXML
    private Button createCreditBtn;

    private final String transmission = "Transmission";
    private final String tvSeries = "TV-Series";
    private final String episode = "Episode";

    private Facade facade = new Facade();

    @FXML
    void createPerson(ActionEvent event) {
        if (!creditedPersonNameText.getText().isEmpty()) {
            facade.createPerson(creditedPersonNameText.getText());
            updateUI();
            creditedPersonNameText.setText("");
        }
    }
    @FXML
    void createCredit(ActionEvent event) {
        try {
            //Programmet hentes igennem index for vores program drop-down menu
            Program program = facade.getPrograms().get(programSelection.getSelectionModel().getSelectedIndex());
            CreditedPerson creditedPerson = facade.getCreditedPeople().get(creditedPersonSelection.getSelectionModel().getSelectedIndex());
            Credit.Function function = facade.getFunctions().get(functionSelection.getSelectionModel().getSelectedIndex());

            //Tjekker om credit allerede eksisterer
            if (program.getCredits() == null) {
                facade.createCredit(program, creditedPerson, function);
            } else {
                boolean exists = false;
                for (Credit credit: program.getCredits()) {
                    if (credit.getCreditedPerson().equals(creditedPerson) && credit.getFunction().equals(function)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    facade.createCredit(program, creditedPerson, function);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Ikke implementeret endnu");
        }
    }

    @FXML
    void createProgram(ActionEvent event) {
        if (programTypeSelection.getValue().equals(transmission)) {
            String name = nameText.getText();
            String description = descriptionText.getText();
            if (!name.isEmpty() && !description.isEmpty()) {
                facade.createTransmission(name, description, 1, 60);
            }
        } else if (programTypeSelection.getValue().equals(tvSeries)) {
            String name = nameText.getText();
            String description = nameText.getText();
            if (!name.isEmpty() && !description.isEmpty()) {
                facade.createTvSeries(name, description, 1);
            }
        } else if (programTypeSelection.getValue().equals(episode)) {
            try {
                TVSeries tvSeries = facade.getTvSeriesList().get(tvSeriesSelection.getSelectionModel().getSelectedIndex());
                String name = nameText.getText();
                String description = descriptionText.getText();
                int episodeNumber = episodeNumberText.getText().isEmpty() ? -1 : Integer.parseInt(episodeNumberText.getText());
                int seasonNumber = seasonNumberText.getText().isEmpty() ? -1 : Integer.parseInt(seasonNumberText.getText());
                if (!tvSeriesSelection.getSelectionModel().isEmpty() && !name.isEmpty() && !description.isEmpty() && episodeNumber > 0 && seasonNumber > 0) {
                    facade.createEpisode(tvSeries, name, description, 1, episodeNumber, seasonNumber, 60);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input (only numbers in episode and seasons fields)");
            }
        }
        updateUI();
    }

    public void updateUI() {
        programSelection.getItems().clear();
        tvSeriesSelection.getItems().clear();
        creditedPersonSelection.getItems().clear();
        for (Program program: facade.getPrograms()) {
            programSelection.getItems().add(program.getName());
        }
        for (TVSeries tvSeries: facade.getTvSeriesList()) {
            tvSeriesSelection.getItems().add(tvSeries.getName());
        }
        for (CreditedPerson creditedPerson: facade.getCreditedPeople()) {
            creditedPersonSelection.getItems().add(creditedPerson.getName() + ": " + creditedPerson.getUuid());
        }
    }

    @FXML
    void dropDownSelection(ActionEvent event) {
        if (programTypeSelection.getValue().equals(transmission)) {
            System.out.println(programTypeSelection.getValue());
            //Text fields
            seasonNumberText.setVisible(false);
            episodeNumberText.setVisible(false);
            tvSeriesSelection.setVisible(false);
            durationText.setVisible(true);
            //Labels
            tvSLabel.setVisible(false);
            seasonNoLabel.setVisible(false);
            episodeNoLabel.setVisible(false);
            durationLabel.setVisible(true);
        } else if (programTypeSelection.getValue().equals(tvSeries)) {
            System.out.println(programTypeSelection.getValue());
            seasonNumberText.setVisible(false);
            episodeNumberText.setVisible(false);
            tvSeriesSelection.setVisible(false);
            tvSLabel.setVisible(false);
            seasonNoLabel.setVisible(false);
            episodeNoLabel.setVisible(false);
            durationLabel.setVisible(false);
            durationText.setVisible(false);
        } else if (programTypeSelection.getValue().equals(episode)) {
            System.out.println(programTypeSelection.getValue());
            seasonNumberText.setVisible(true);
            episodeNumberText.setVisible(true);
            tvSeriesSelection.setVisible(true);
            tvSLabel.setVisible(true);
            seasonNoLabel.setVisible(true);
            episodeNoLabel.setVisible(true);
            durationLabel.setVisible(true);
            durationText.setVisible(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        programTypeSelection.getItems().add(transmission);
        programTypeSelection.getItems().add(tvSeries);
        programTypeSelection.getItems().add(episode);
        for (Credit.Function function: facade.getFunctions()) {
            functionSelection.getItems().add(function.role);
        }
        facade.createStuff();
        updateUI();
    }
}