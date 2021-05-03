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
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemAdminController implements Initializable {
    @FXML
    private PasswordField passwordText;

    @FXML
    private AnchorPane confirmAnchorPane;

    @FXML
    private TextArea deleteProgramTextArea;

    @FXML
    private TextField durationText, descriptionText, seasonNumberText, episodeNumberText, nameText,
            creditedPersonNameText, nameUpdateText, descriptionUpdateText, episodeNumberUpdateText, durationUpdateText,
            creditedPersonNameUpdateText, seasonNumberUpdateText, usernameText;

    @FXML
    private ComboBox<String> tvSeriesSelection, searchSeriesCombo, functionSelection, searchProgramCombo,
            searchSeasonCombo, creditedPersonSelection, programSelection, programTypeSelection, tvSeriesUpdateSelection,
            functionUpdateSelection, usertypeCombo, searchApprovedProgramCombo, searchApprovedSeriesCombo, searchApprovedSeasonCombo;

    @FXML
    private Label creditedPersonLabel, tvSLabel, durationLabel, nameLabel, seasonNoLabel, messageLabel,
            episodeNoLabel, descriptionLabel, nameUpdateLabel, descriptionUpdateLabel, seasonNoUpdateLabel,
            episodeNoUpdateLabel, durationUpdateLabel, tvSUpdateLabel, currentlyUpdatingLabel, currentlyUpdatingUUID,
            programUpdateSelection, creditedPersonUpdateLabel, createUserLabel;

    @FXML
    private Button createProgramBtn, createCreditBtn, createPersonBtn, exportButton, updateProgramButton, updateCreditButton,
            updateProgramBtn, updatePersonBtn, updateCreditBtn, updateTvSeriesButton, updateTvSeriesBtn, deleteSelectedButton,
            createUserButton, confirmDeleteButton, declineDeleteButton, approveSelectedButton;

    @FXML
    private ListView<String> searchListView, searchListViewCredits,searchApprovedListView, searchApprovedListViewCredits;

    @FXML
    private TableView<String> searchTableView;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab updateTab, searchViewTab, approvedTab;

    private final String transmission = "Transmission";
    private final String tvSeries = "TV-Series";
    private final String episode = "Episode";

    private Facade facade = new Facade();

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

    @FXML
    void logOutAction(ActionEvent e) throws IOException {
        App.setRoot("primary");
    }

    //Method for the createPerson-button
    @FXML
    void createPerson(ActionEvent event) {
        //Uses the createPerson-method from the facade
        if (!creditedPersonNameText.getText().isEmpty()) {
            facade.createPerson(creditedPersonNameText.getText());
            updateCreateUI();
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
                facade.createCredit(creditedPerson, function, program);
            } else {
                boolean exists = false;
                for (Credit credit : program.getCredits()) {
                    if (credit.getCreditedPerson().equals(creditedPerson) && credit.getFunction().equals(function)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    facade.createCredit(creditedPerson, function, program);
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
                facade.createTransmission(name, description, 1, duration,false);
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
                    facade.createEpisode(tvSeries, name, description, 1, episodeNumber, seasonNumber, duration, false);
                } else {
                    messageLabel.setText("Cannot create " + episode + " without a name & a TV-series");
                }

            } catch (NumberFormatException e) {
                messageLabel.setText("Invalid input (only numbers in episode and seasons fields)");
            }
        }
        updateCreateUI();
    }

    @FXML
    void dropDownSelection(ActionEvent event) {
        ArrayList<Node> durationNodes = new ArrayList<>(Arrays.asList(durationLabel, durationText));
        ArrayList<Node> episodeNodes = new ArrayList<>(Arrays.asList(tvSeriesSelection, seasonNumberText,
                episodeNumberText, tvSLabel, seasonNoLabel, episodeNoLabel));

        if (programTypeSelection.getValue().equals(transmission)) {
            System.out.println(programTypeSelection.getValue());
            //Episode nodes
            episodeNodes.forEach(node -> node.setVisible(false));
            //Duration nodes
            durationNodes.forEach(node -> node.setVisible(true));

        } else if (programTypeSelection.getValue().equals(tvSeries)) {
            System.out.println(programTypeSelection.getValue());
            //Episode nodes
            episodeNodes.forEach(node -> node.setVisible(false));
            //Duration nodes
            durationNodes.forEach(node -> node.setVisible(false));

        } else if (programTypeSelection.getValue().equals(episode)) {
            System.out.println(programTypeSelection.getValue());
            //Episode nodes
            episodeNodes.forEach(node -> node.setVisible(true));
            //Duration nodes
            durationNodes.forEach(node -> node.setVisible(true));
        }
    }

    @FXML
    void exportButtonOnAction(ActionEvent event) {
        facade.exportToTxt();
    }

    @FXML
    void searchProgramComboAction(ActionEvent event) {
        searchSeriesCombo.getItems().clear();
        if (searchProgramCombo.getSelectionModel().getSelectedItem().equals(transmission)){
            searchSeriesCombo.setDisable(true);
            searchSeasonCombo.setDisable(true);
            updateTvSeriesButton.setDisable(true);
            searchSeriesCombo.getSelectionModel().clearSelection();
            searchSeasonCombo.getSelectionModel().clearSelection();
            searchSeriesCombo.setPromptText("Choose TV-series");
            searchSeasonCombo.setPromptText("Choose season");


            searchListView.getItems().clear();
            for (Program program : facade.getPrograms()) {
                if (program instanceof Transmission) {
                    searchListView.getItems().add(program.getName() +  ": " + program.getUuid());
                }
            }

        } else if (searchProgramCombo.getSelectionModel().getSelectedItem().equals(tvSeries)){
            searchSeriesCombo.setDisable(false);
            searchSeasonCombo.setDisable(false);

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

            //Enables the delete button when a series has been chosen
            deleteSelectedButton.setDisable(false);
            deleteSelectedButton.setText("Delete Tv-Series");

            if (series.getSeasonMap() != null) {
                for (Integer i : series.getSeasonMap().keySet()) {
                    searchSeasonCombo.getItems().add(String.valueOf(i));
                }
            }
            updateTvSeriesButton.setDisable(false);
        } catch (IndexOutOfBoundsException e){
            System.out.println("Not yet implemented && Not sure why this is happening??? Yikes - " +
                    "Think it has something to do with the change in combo-box you just made");
        }
    }

    @FXML
    void searchSeasonComboAction(ActionEvent event) {
        try {
            searchListView.getItems().clear();

            if (searchSeriesCombo.getSelectionModel().getSelectedIndex() != -1) {
                TVSeries series = getSelectedTvSeriesFromComboBox();
                //The deleteSelectedButton is just to make sure that you cannot delete a series when you've chosen an season
                deleteSelectedButton.setText("Delete Selected");
                deleteSelectedButton.setDisable(true);
                for (Episode episode : series.getSeasonMap().get(Integer.parseInt(searchSeasonCombo.getSelectionModel().getSelectedItem()))) {
                    searchListView.getItems().add(episode.getName() + ": " + episode.getUuid());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("This just happens because we parse the value 'null' as an integer. And we do that because" +
                    " we clear the season-combobox");
        }
    }

    @FXML
    void selectedProgramFromListView(MouseEvent event) {
        searchListViewCredits.getItems().clear();
        updateProgramButton.setDisable(false);
        deleteSelectedButton.setDisable(false);

        if(getSelectedProgramFromListView() instanceof Transmission){
            deleteSelectedButton.setText("Delete transmission");
        }
        else if (getSelectedProgramFromListView() instanceof Episode){
            deleteSelectedButton.setText("Delete Episode");
        }
        Program selectedProgram = getSelectedProgramFromListView();

        //Get the credits from the selected program IF the program contains credits
        if(selectedProgram.getCredits() != null) {
            ArrayList<Credit> credits = selectedProgram.getCredits();
            for (Credit credit : credits) {
                searchListViewCredits.getItems().add(credit.getCreditedPerson().getName() + ": " + credit.getFunction().role);
            }
        }
    }

    @FXML
    void selectCreditFromListView(MouseEvent event){
        if( !searchListViewCredits.getSelectionModel().isEmpty() && !searchListViewCredits.getSelectionModel().getSelectedItem().isEmpty()){
            deleteSelectedButton.setText("Delete credit");
        }
    }

    @FXML
    void updateUpdateTabProgramOnAction(ActionEvent event) {
        ArrayList<Node> updateNodes = new ArrayList<>(Arrays.asList(
                nameUpdateLabel, descriptionUpdateLabel, durationUpdateLabel,
                nameUpdateText, descriptionUpdateText, durationUpdateText));
        ArrayList<Node> episodeNodes = new ArrayList<>(Arrays.asList(
                seasonNoUpdateLabel, seasonNumberUpdateText, episodeNoUpdateLabel,
                episodeNumberUpdateText, tvSUpdateLabel, tvSeriesUpdateSelection));
        if (searchListView.getSelectionModel().getSelectedItem() != null) {
            Program program = getSelectedProgramFromListView();
            updateNodes.forEach(node -> node.setVisible(true));
            updateProgramBtn.setVisible(true);
            updateTvSeriesBtn.setVisible(false);
            nameUpdateText.setText(program.getName());
            descriptionUpdateText.setText(program.getDescription());
            durationUpdateText.setText(String.valueOf(program.getDuration()));
            mainTabPane.getSelectionModel().select(updateTab);

            if (searchProgramCombo.getSelectionModel().getSelectedItem().equals(tvSeries)) {
                Episode episode = (Episode) program;
                episodeNodes.forEach(node -> node.setVisible(true));
                seasonNumberUpdateText.setText(String.valueOf(episode.getSeasonNo()));
                episodeNumberUpdateText.setText(String.valueOf(episode.getEpisodeNo()));
                tvSeriesUpdateSelection.getItems().clear();
                for (TVSeries tvSeries : facade.getTvSeriesList()) {
                    tvSeriesUpdateSelection.getItems().add(tvSeries.getName());
                }
                tvSeriesUpdateSelection.getSelectionModel().select(facade.getTvSeriesFromEpisode(episode).getName());

            } else if (searchProgramCombo.getSelectionModel().getSelectedItem().equals(transmission)) {
                episodeNodes.forEach(node -> node.setVisible(false));
            }
            currentlyUpdatingLabel.setText("Currently editing: " + getSelectedProgramFromListView().getName());
            currentlyUpdatingUUID.setText(String.valueOf(getSelectedProgramFromListView().getUuid()));
        }
    }

    @FXML
    void updateUpdateTabCreditOnAction(ActionEvent event) {
        mainTabPane.getSelectionModel().select(updateTab);
        Program program = getSelectedProgramFromListView();
        Credit credit = program.getCredits().get(searchListViewCredits.getSelectionModel().getSelectedIndex());
        creditedPersonUpdateLabel.setText(credit.getCreditedPerson().getName() + ": " + credit.getCreditedPerson().getUuid());
        programUpdateSelection.setText(program.getName());

        functionUpdateSelection.getSelectionModel().select(credit.getFunction().role);
    }

    @FXML
    void updateUpdateTabPersonOnAction(ActionEvent event) {

    }

    @FXML
    void updateUpdateTabTVSeriesOnAction(ActionEvent event) {
        ArrayList<Node> tvSeriesNodes = new ArrayList<>(Arrays.asList(
                nameUpdateLabel, nameUpdateText, descriptionUpdateText, descriptionUpdateLabel));
        ArrayList<Node> programNodes = new ArrayList<>(Arrays.asList(
                durationUpdateLabel, durationUpdateText, seasonNoUpdateLabel, seasonNumberUpdateText,
                episodeNoUpdateLabel, episodeNumberUpdateText, tvSUpdateLabel, tvSeriesUpdateSelection));

        if (searchSeriesCombo.getSelectionModel().getSelectedItem() != null) {
            TVSeries tvSeries = getSelectedTvSeriesFromComboBox();

            tvSeriesNodes.forEach(node -> node.setVisible(true));
            programNodes.forEach(node -> node.setVisible(false));

            currentlyUpdatingLabel.setText("Currently editing: " + tvSeries.getName());
            mainTabPane.getSelectionModel().select(updateTab);

            currentlyUpdatingUUID.setText(String.valueOf(tvSeries.getUuid()));
            nameUpdateText.setText(tvSeries.getName());
            descriptionUpdateText.setText(tvSeries.getDescription());

            updateTvSeriesBtn.setVisible(true);
            updateProgramBtn.setVisible(false);
        }
    }


    @FXML
    void updateCredit(ActionEvent event) {
        try {
            //Programmet hentes igennem index for vores program drop-down menu
            Credit credit = getSelectedCreditFromListView();
            Credit.Function function = facade.getFunctions().get(functionUpdateSelection.getSelectionModel().getSelectedIndex());

            facade.updateCredit(credit, function);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Ikke implementeret endnu");
        }
    }

    @FXML
    void updateProgram(ActionEvent event) {
        Program program = facade.getProgramFromUuid(UUID.fromString(currentlyUpdatingUUID.getText()));
        String name = nameUpdateText.getText();
        String description = descriptionUpdateText.getText();
        int duration = durationUpdateText.getText().isEmpty() ? -1 : Integer.parseInt(durationUpdateText.getText());
        int seasonNo = seasonNumberUpdateText.getText().isEmpty() ? -1 : Integer.parseInt(seasonNumberUpdateText.getText());
        int episodeNo = episodeNumberUpdateText.getText().isEmpty() ? -1 : Integer.parseInt(episodeNumberUpdateText.getText());

        TVSeries tvSeries = tvSeriesUpdateSelection.getSelectionModel().getSelectedIndex() == -1 ? null :
                facade.getTvSeriesList().get(tvSeriesUpdateSelection.getSelectionModel().getSelectedIndex());

        if (program instanceof Transmission) {
            facade.updateTransmission(program, name, description, 1, duration);
        }
        else if (program instanceof Episode) {
            facade.updateEpisode(program, name, description, 1, duration, seasonNo, episodeNo, tvSeries);
        }
        updateUpdateUI();
    }

    @FXML
    void updateTvSeries(ActionEvent event) {
        TVSeries tvSeries = facade.getTvSeriesFromUuid(UUID.fromString(currentlyUpdatingUUID.getText()));
        String name = nameUpdateText.getText();
        String description = descriptionUpdateText.getText();

        facade.updateTvSeries(tvSeries, name, description);
        updateUpdateUI();
    }

    public void updateCreateUI() {
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

    public void updateUpdateUI() {
        ArrayList<TextField> textFields = new ArrayList<>(Arrays.asList(nameUpdateText, descriptionUpdateText,
                durationUpdateText, seasonNumberUpdateText, episodeNumberUpdateText, creditedPersonNameUpdateText));
        textFields.forEach(node -> node.clear());
        ArrayList<ComboBox> comboBoxes = new ArrayList<>(Arrays.asList(tvSeriesUpdateSelection, functionUpdateSelection));
        comboBoxes.forEach(node -> node.getItems().clear());
        currentlyUpdatingLabel.setText("Choose program in \"Search/view\" tab");
        currentlyUpdatingUUID.setText("");

        facade.getTvSeriesList().forEach(tvSeries -> tvSeriesUpdateSelection.getItems().add(tvSeries.getName()));
    }

    @FXML
    void openConfirmBox(MouseEvent event){
        confirmAnchorPane.setVisible(true);
        //If-statement for bedre bruger-oplevelse ved sletning af forskellige typer programmer/krediteringer
        if (!searchListViewCredits.getSelectionModel().isEmpty() && !searchListViewCredits.getSelectionModel().getSelectedItem().isEmpty()) {
            deleteProgramTextArea.setText("Er du sikker på, at du vil slette følgende creditering? \n" +
                    searchListViewCredits.getSelectionModel().getSelectedItem() + "\nFra programmet " +
                    getSelectedProgramFromListView().getName() + "\nMed ID: " +
                    getSelectedProgramFromListView().getUuid());
        } else if (searchListView.getSelectionModel().getSelectedItem() != null) {
            if (getSelectedProgramFromListView() instanceof Transmission) {
                deleteProgramTextArea.setText("Er du sikker på, at du vil slette følgende tranmission \n" +
                        getSelectedProgramFromListView().getName() + "\nMed ID: " +
                        getSelectedProgramFromListView().getUuid());
            } else if (getSelectedProgramFromListView() instanceof Episode) {
                deleteProgramTextArea.setText("Er du sikker på, at du vil slette følgende episode? \n" +
                        searchListView.getSelectionModel().getSelectedItem() + "\nfra tv-serien " +
                        facade.getTvSeriesFromEpisode((Episode) getSelectedProgramFromListView()).getName() +
                        "\nMed ID: " + getSelectedProgramFromListView().getUuid());
            }
        }
    }

    @FXML
    void declineDeleteSelected(ActionEvent event){
        confirmAnchorPane.setVisible(false);
    }

    @FXML
    void deleteSelected(ActionEvent event){
        // Deletes selected program
        if (!searchSeriesCombo.getSelectionModel().isEmpty() && searchSeasonCombo.getSelectionModel().isEmpty()) {
            System.out.println("This has not been implemented - not sure if we need this option right now?");
            System.out.println("As goes for deleting a season");
        } else if (!searchListViewCredits.getSelectionModel().isEmpty() && !searchListViewCredits.getSelectionModel().getSelectedItem().isEmpty()) {
            facade.deleteCredit(getSelectedProgramFromListView(), getSelectedCreditFromListView());
        } else if (getSelectedProgramFromListView() != null) {
            facade.deleteProgram(getSelectedProgramFromListView());
        }
        confirmAnchorPane.setVisible(false);
        deleteSelectedButton.setDisable(true);
    }

    @FXML
    void searchApprovedProgramComboAction (ActionEvent event){
        searchApprovedSeriesCombo.getItems().clear();
        if (searchApprovedProgramCombo.getSelectionModel().getSelectedItem().equals(transmission)){
            searchApprovedSeriesCombo.setDisable(true);
            searchApprovedSeasonCombo.setDisable(true);
            searchApprovedSeriesCombo.getSelectionModel().clearSelection();
            searchApprovedSeasonCombo.getSelectionModel().clearSelection();
            searchApprovedSeriesCombo.setPromptText("Choose TV-series");
            searchApprovedSeasonCombo.setPromptText("Choose season");

            searchApprovedListView.getItems().clear();
            for (Program program : facade.getPrograms()) {
                if (program instanceof Transmission && !program.isApproved()) {
                    searchApprovedListView.getItems().add(program.getName() +  ": " + program.getUuid());
                }
            }

        } else if (searchApprovedProgramCombo.getSelectionModel().getSelectedItem().equals(tvSeries)){
            searchApprovedSeriesCombo.setDisable(false);
            searchApprovedSeasonCombo.setDisable(false);

            for (TVSeries tvSeries : facade.getTvSeriesList()) {
                searchApprovedSeriesCombo.getItems().add(tvSeries.getName());
            }
        }
    }

    @FXML
    void searchApprovedSeriesComboAction (ActionEvent event){
        searchApprovedSeasonCombo.getItems().clear();
        //To find the episodes based on a season from a TV-series
        try {
            TVSeries series = facade.getTvSeriesList().get(searchApprovedSeriesCombo.getSelectionModel().getSelectedIndex());

            //Enables the delete button when a series has been chosen
            deleteSelectedButton.setDisable(false);
            deleteSelectedButton.setText("Delete Tv-Series");

            if (series.getSeasonMap() != null) {
                for (Integer i : series.getSeasonMap().keySet()) {
                    searchApprovedSeasonCombo.getItems().add(String.valueOf(i));
                }
            }
            updateTvSeriesButton.setDisable(false);
        } catch (IndexOutOfBoundsException e){
            System.out.println("Not yet implemented && Not sure why this is happening??? Yikes - " +
                    "Think it has something to do with the change in combo-box you just made");
        }
    }

    @FXML
    void searchApprovedSeasonComboAction (ActionEvent event){
        try {
            searchApprovedListView.getItems().clear();

            if (searchApprovedSeriesCombo.getSelectionModel().getSelectedIndex() != -1) {
                TVSeries series = getSelectedTvSeriesFromComboBox();
                for (Episode episode : series.getSeasonMap().get(Integer.parseInt(searchApprovedSeasonCombo.getSelectionModel().getSelectedItem()))) {
                    if (!episode.isApproved()) {
                        searchApprovedListView.getItems().add(episode.getName() + ": " + episode.getUuid());
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("This just happens because we parse the value 'null' as an integer. And we do that because" +
                    " we clear the season-combobox");
        }
    }

    @FXML
    void selectedApprovedProgramFromListView(MouseEvent event) {
        searchApprovedListViewCredits.getItems().clear();

        Program selectedProgram = getSelectedProgramFromListView();

        //Get the credits from the selected program IF the program contains credits
        if(selectedProgram.getCredits() != null) {
            ArrayList<Credit> credits = selectedProgram.getCredits();
            for (Credit credit : credits) {
                searchApprovedListViewCredits.getItems().add(credit.getCreditedPerson().getName() + ": " + credit.getFunction().role);
            }
        }
    }

    @FXML
    void approveSelectedProgram(ActionEvent event){
        getSelectedProgramFromListView().setApproved(true);
        System.out.println(getSelectedProgramFromListView().isApproved());
    }


    private TVSeries getSelectedTvSeriesFromComboBox() {
        try{
            if (searchViewTab.isSelected()){
                return facade.getTvSeriesList().get(searchSeriesCombo.getSelectionModel().getSelectedIndex());

            } else if (approvedTab.isSelected()) {
                return facade.getTvSeriesList().get(searchApprovedSeriesCombo.getSelectionModel().getSelectedIndex());
            }
        } catch (NullPointerException e) {
            System.out.println("No series in the list - wow");
        }
        return null;
    }

    private Program getSelectedProgramFromListView() {
        try {
            String viewString = null;
            //Choose the String-item from the listview instead of the index
            if(searchViewTab.isSelected()) {
                viewString = searchListView.getSelectionModel().getSelectedItem();
            } else if (approvedTab.isSelected()) {
                approveSelectedButton.setDisable(false);
                viewString = searchApprovedListView.getSelectionModel().getSelectedItem();
            }
            //Split the string to get the UUID
            String[] viewStringArray = viewString.split(":");
            //Use the getProgramFromUuid method from the facade to get the program from the string. The trim after the string is to get rid of whitespace
            return facade.getProgramFromUuid(UUID.fromString(viewStringArray[1].trim()));
        } catch (NullPointerException e) {
            System.out.println("A program has not been selected");
        }
        return null;
    }

    private Credit getSelectedCreditFromListView() {
        return getSelectedProgramFromListView().getCredits().get(searchListViewCredits.getSelectionModel().getSelectedIndex());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        facade.importFromTxt();

        usertypeCombo.getItems().add("Producer");
        usertypeCombo.getItems().add("System Administrator");

        searchProgramCombo.getItems().add(transmission);
        searchProgramCombo.getItems().add(tvSeries);
        searchApprovedProgramCombo.getItems().add(transmission);
        searchApprovedProgramCombo.getItems().add(tvSeries);


        programTypeSelection.getItems().add(transmission);
        programTypeSelection.getItems().add(tvSeries);
        programTypeSelection.getItems().add(episode);
        for (Credit.Function function : facade.getFunctions()) {
            functionSelection.getItems().add(function.role);
            functionUpdateSelection.getItems().add(function.role);
        }

        updateCreateUI();
    }


}