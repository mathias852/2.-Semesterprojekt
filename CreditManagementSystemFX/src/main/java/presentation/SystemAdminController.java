package presentation;

import domain.Facade;
import domain.credit.Credit;
import domain.credit.CreditedPerson;
import domain.program.Episode;
import domain.program.Program;
import domain.program.TVSeries;
import domain.program.Transmission;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

public class SystemAdminController implements Initializable {

    @FXML
    private TextField durationText;

    @FXML
    private ComboBox<String> tvSeriesSelection;

    @FXML
    private Label creditedPersonLabel;

    @FXML
    private Label tvSLabel;

    @FXML
    private TextField descriptionText;

    @FXML
    private ComboBox<String> searchSeriesCombo;

    @FXML
    private Label durationLabel;

    @FXML
    private TableView<String> searchTableView;

    @FXML
    private Button createProgramBtn;

    @FXML
    private ListView<String> searchListView;

    @FXML
    private ListView<String> searchListViewCredits;

    @FXML
    private ComboBox<String> functionSelection;

    @FXML
    private ComboBox<String> searchProgramCombo;

    @FXML
    private ComboBox<String> searchSeasonCombo;

    @FXML
    private ComboBox<String> creditedPersonSelection;

    @FXML
    private Label nameLabel;

    @FXML
    private Label seasonNoLabel;

    @FXML
    private TextField seasonNumberText;

    @FXML
    private TextField episodeNumberText;

    @FXML
    private Button createCreditBtn;

    @FXML
    private TextField nameText;

    @FXML
    private Button createPersonBtn;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField creditedPersonNameText;

    @FXML
    private Button exportButton;

    @FXML
    private ComboBox<String> programSelection;

    @FXML
    private ComboBox<String> programTypeSelection;

    @FXML
    private Label episodeNoLabel;

    @FXML
    private Label descriptionLabel;

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
                for (Credit credit : program.getCredits()) {
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

        String name = nameText.getText();
        String description = descriptionText.getText();

        if (programTypeSelection.getValue().equals(transmission)) {
            int duration = durationText.getText().isEmpty() ? -1 : Integer.parseInt(durationText.getText());
            if (!name.isEmpty()) {
                facade.createTransmission(name, description, 1, duration);
            } else {
                messageLabel.setText("Cannot create " + transmission + " without a name");
            }
        } else if (programTypeSelection.getValue().equals(tvSeries)) {
            if (!name.isEmpty()) {
                facade.createTvSeries(name, description, 1);
            } else {
                messageLabel.setText("Cannot create " + tvSeries + " without a name");
            }
        } else if (programTypeSelection.getValue().equals(episode)) {
            try {
                TVSeries tvSeries = tvSeriesSelection.getSelectionModel().getSelectedIndex() == -1 ? null : facade.getTvSeriesList().get(tvSeriesSelection.getSelectionModel().getSelectedIndex());
                int episodeNumber = episodeNumberText.getText().isEmpty() ? -1 : Integer.parseInt(episodeNumberText.getText());
                int seasonNumber = seasonNumberText.getText().isEmpty() ? -1 : Integer.parseInt(seasonNumberText.getText());
                int duration = durationText.getText().isEmpty() ? -1 : Integer.parseInt(durationText.getText());

                if (!name.isEmpty() && tvSeries != null) {
                    facade.createEpisode(tvSeries, name, description, 1, episodeNumber, seasonNumber, duration);
                } else {
                    messageLabel.setText("Cannot create " + episode + " without a name & a TV-series");
                }

            } catch (NumberFormatException e) {
                messageLabel.setText("Invalid input (only numbers in episode and seasons fields)");
            }
        }
        updateUI();
    }

    public void updateUI() {
        programSelection.getItems().clear();
        tvSeriesSelection.getItems().clear();
        creditedPersonSelection.getItems().clear();

        for (Program program : facade.getPrograms()) {
            programSelection.getItems().add(program.getName());
        }
        for (TVSeries tvSeries : facade.getTvSeriesList()) {
            tvSeriesSelection.getItems().add(tvSeries.getName());
        }
        for (CreditedPerson creditedPerson : facade.getCreditedPeople()) {
            creditedPersonSelection.getItems().add(creditedPerson.getName() + ": " + creditedPerson.getUuid());
        }

        nameText.clear();
        descriptionText.clear();
        episodeNumberText.clear();
        seasonNumberText.clear();
        durationText.clear();

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

    @FXML
    void searchProgramComboAction(ActionEvent event) {
        searchSeriesCombo.getItems().clear();
        if (searchProgramCombo.getSelectionModel().getSelectedItem() == transmission){
            searchSeriesCombo.setVisible(false);
            searchSeasonCombo.setVisible(false);

            searchListView.getItems().clear();
            for (Program program : facade.getPrograms()) {
                if (program instanceof Transmission) {
                    searchListView.getItems().add(program.getName() +  ": " + program.getUuid());
                }
            }

        } else if (searchProgramCombo.getSelectionModel().getSelectedItem() == tvSeries){
            searchSeriesCombo.setVisible(true);
            searchSeasonCombo.setVisible(true);

            for (TVSeries tvSeries : facade.getTvSeriesList()) {
                searchSeriesCombo.getItems().add(tvSeries.getName());
            }
        }
    }

    @FXML
    void searchSeriesComboAction(ActionEvent event) {
        searchSeasonCombo.getItems().clear();
        //To find the episodes based on a season from a TV-series
        try {
            TVSeries series = facade.getTvSeriesList().get(searchSeriesCombo.getSelectionModel().getSelectedIndex());
            if (series.getSeasonMap() != null) {
                for (Integer i : series.getSeasonMap().keySet()) {
                    searchSeasonCombo.getItems().add(String.valueOf(i));
                }
            }
        } catch (IndexOutOfBoundsException e){}
    }

    @FXML
    void searchSeasonComboAction(ActionEvent event) {
        searchListView.getItems().clear();

        if(searchSeriesCombo.getSelectionModel().getSelectedIndex() != -1) {
            TVSeries series = facade.getTvSeriesList().get(searchSeriesCombo.getSelectionModel().getSelectedIndex());
            for (Episode episode : series.getSeasonMap().get(Integer.parseInt(searchSeasonCombo.getSelectionModel().getSelectedItem()))){
                searchListView.getItems().add(episode.getName() + ": " + episode.getUuid());
            }
        }
    }

    @FXML
    void selectedProgramFromListView(MouseEvent event) {
        searchListViewCredits.getItems().clear();
        Program selectedProgram = facade.getPrograms().get(searchListView.getSelectionModel().getSelectedIndex());

        if(selectedProgram.getCredits() != null) {
            ArrayList<Credit> credits = selectedProgram.getCredits();
            for (Credit credit : credits) {
                searchListViewCredits.getItems().add(credit.getCreditedPerson().getName() + ": " + credit.getFunction().role);
            }
        }
    }

    @FXML
    void exportButtonOnAction(ActionEvent event) {
        facade.exportToTxt();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        facade.importFromTxt();

        searchProgramCombo.getItems().add(transmission);
        searchProgramCombo.getItems().add(tvSeries);

        programTypeSelection.getItems().add(transmission);
        programTypeSelection.getItems().add(tvSeries);
        programTypeSelection.getItems().add(episode);
        for (Credit.Function function : facade.getFunctions()) {
            functionSelection.getItems().add(function.role);
        }
//        facade.createStuff();

        updateUI();
    }
}