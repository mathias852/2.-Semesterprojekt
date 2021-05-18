package presentation;

import domain.Facade;
import domain.LoginHandler;
import domain.Notification;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.File;
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
    private ImageView creditedLogoImageView;

    @FXML
    private TextField durationText, descriptionText, seasonNumberText, episodeNumberText, nameText,
            creditedPersonNameText, nameUpdateText, descriptionUpdateText, episodeNumberUpdateText, durationUpdateText,
            creditedPersonNameUpdateText, seasonNumberUpdateText, usernameText;

    @FXML
    private ComboBox<String> tvSeriesSelection, searchSeriesCombo, functionSelection, searchProgramCombo,
            searchSeasonCombo, creditedPersonSelection, programSelection, programTypeSelection, tvSeriesUpdateSelection,
            functionUpdateSelection, usertypeCombo, searchApprovedProgramCombo, searchApprovedSeriesCombo, searchApprovedSeasonCombo,
            productionSelectionCombo, productionSelectionUpdateCombo;

    @FXML
    private Label creditedPersonLabel, tvSLabel, durationLabel, nameLabel, seasonNoLabel, messageLabel,
            episodeNoLabel, descriptionLabel, nameUpdateLabel, descriptionUpdateLabel, seasonNoUpdateLabel,
            episodeNoUpdateLabel, durationUpdateLabel, tvSUpdateLabel, currentlyUpdatingLabel, currentlyUpdatingUUID,
            programUpdateSelection, creditedPersonUpdateLabel, createUserLabel, productionLabel;

    @FXML
    private Button createProgramBtn, createCreditBtn, createPersonBtn, exportButton, updateProgramButton, updateCreditButton,
            updateProgramBtn, updatePersonBtn, updateCreditBtn, updateTvSeriesButton, updateTvSeriesBtn, deleteSelectedButton,
            createUserButton, confirmDeleteButton, declineDeleteButton, approveSelectedButton;

    @FXML
    private ListView<String> searchListView, searchListViewCredits,searchApprovedListView, searchApprovedListViewCredits, systemLogLV;

    @FXML
    private TableView<String> searchTableView;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab updateTab, searchViewTab, approvedTab;

    private final String transmission = "Transmission";
    private final String tvSeries = "TV-Series";
    private final String episode = "Episode";


    private final String tv2Logo = "TV2";
    private final String nordiskFilmLogo = "Nordisk Film";

    private final File nordiskFilmLogoFile = new File("src/main/resources/presentation/NF-Logo.png");
    private final File tv2LogoFile = new File("src/main/resources/presentation/tv2creditslogo.png");

    private final Image nordiskFilmLogoImage = new Image(nordiskFilmLogoFile.toURI().toString());
    private final Image tv2LogoImage = new Image(tv2LogoFile.toURI().toString());



    @FXML
    private void createUserAction(ActionEvent e) {

        //Create a pattern that the username must obey
        Pattern pattern = Pattern.compile("^(?=.{2,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$");
        Matcher matcher = pattern.matcher(usernameText.getText());
        //Checks if the matcher variable did find a match that correspond to the pattern
        boolean matchFound = matcher.find();

        //We get the value from the usernameTextField
        String userName = usernameText.getText();
        //We get the value from the passwordTextField and convert it to hash
        int password = passwordText.getText().hashCode();


        //Checks if the userName correspond to the constraint made in pattern and that the password is not empty
        if (matchFound && !passwordText.getText().isBlank()) {
            //If the userType is equal to producer we call the createProducer()
            if (usertypeCombo.getSelectionModel().getSelectedItem().equals("Producer")) {

                if (LoginHandler.getInstance().createProducer(userName, password)) {
                    createUserLabel.setText("Created a producer");
                } else {
                    createUserLabel.setText("Could not create a producer");
                }
            //If the userType is equal to System Administrator we call the createSystemAdmin()
            } else if (usertypeCombo.getSelectionModel().getSelectedItem().equals("System Administrator")) {

                if (LoginHandler.getInstance().createSystemAdmin(userName, password)) {
                    createUserLabel.setText("Created a System Administrator");
                } else {
                    createUserLabel.setText("Could not create System Administrator");
                }
            }
        } else {
            createUserLabel.setText("Your username does not match our constraints or you forgot to type a password");
        }
    }

    @FXML
    void logOutAction(ActionEvent e) throws IOException {
        //loginHandler.exportUsersToTxt();
        //facade.exportToTxt();
        App.setRoot("logInPage");
    }

    //Method for the createPerson-button
    @FXML
    void createPerson(ActionEvent event) {
        //Uses the createPerson-method from the facade
        if (!creditedPersonNameText.getText().isEmpty()) {
            Facade.getInstance().createPerson(creditedPersonNameText.getText());
            updateCreateUI();
            creditedPersonNameText.setText("");
        }
    }

    @FXML
    void createCredit(ActionEvent event) {
        try {
            //Programmet hentes igennem index for vores program drop-down menu
            Program program = Facade.getInstance().getPrograms().get(programSelection.getSelectionModel().getSelectedIndex());
            CreditedPerson creditedPerson = Facade.getInstance().getCreditedPeople().get(creditedPersonSelection.getSelectionModel().getSelectedIndex());
            Credit.Function function = Facade.getInstance().getFunctions().get(functionSelection.getSelectionModel().getSelectedIndex());

            //Tjekker om credit allerede eksisterer
            if (program.getCredits() == null) {
                Facade.getInstance().createCredit(creditedPerson, function, program);
            } else {
                boolean exists = false;
                for (Credit credit : program.getCredits()) {
                    if (credit.getCreditedPerson().equals(creditedPerson) && credit.getFunction().equals(function)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    Facade.getInstance().createCredit(creditedPerson, function, program);
                }
            }
        } catch (IndexOutOfBoundsException e) {
        }
    }

    @FXML
    void createProgram(ActionEvent event) {

        String name = nameText.getText();
        String description = descriptionText.getText();
        String production = productionSelectionCombo.getSelectionModel().getSelectedItem();

        if (programTypeSelection.getValue().equals(transmission)) {
            int duration = durationText.getText().isEmpty() ? -1 : Integer.parseInt(durationText.getText());
            if (!name.isEmpty()) {
                Facade.getInstance().createTransmission(name, description, LoginHandler.getInstance().getCurrentUser().getUuid(), duration,false, production);
            } else {
                messageLabel.setText("Cannot create " + transmission + " without a name");
            }
        } else if (programTypeSelection.getValue().equals(tvSeries)) {
            if (!name.isEmpty()) {
                Facade.getInstance().createTvSeries(name, description, LoginHandler.getInstance().getCurrentUser().getUuid());
            } else {
                messageLabel.setText("Cannot create " + tvSeries + " without a name");
            }
        } else if (programTypeSelection.getValue().equals(episode)) {
            try {
                TVSeries tvSeries = tvSeriesSelection.getSelectionModel().getSelectedIndex() == -1 ? null : Facade.getInstance().getTvSeriesList().get(tvSeriesSelection.getSelectionModel().getSelectedIndex());
                int episodeNumber = episodeNumberText.getText().isEmpty() ? -1 : Integer.parseInt(episodeNumberText.getText());
                int seasonNumber = seasonNumberText.getText().isEmpty() ? -1 : Integer.parseInt(seasonNumberText.getText());
                int duration = durationText.getText().isEmpty() ? -1 : Integer.parseInt(durationText.getText());

                if (!name.isEmpty() && tvSeries != null) {
                    Facade.getInstance().createEpisode(tvSeries, name, description, LoginHandler.getInstance().getCurrentUser().getUuid(),
                            episodeNumber, seasonNumber, duration, false, production);
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
            //Episode nodes
            episodeNodes.forEach(node -> node.setVisible(false));
            //Duration nodes
            durationNodes.forEach(node -> node.setVisible(true));

        } else if (programTypeSelection.getValue().equals(tvSeries)) {
            //Episode nodes
            episodeNodes.forEach(node -> node.setVisible(false));
            //Duration nodes
            durationNodes.forEach(node -> node.setVisible(false));

        } else if (programTypeSelection.getValue().equals(episode)) {
            //Episode nodes
            episodeNodes.forEach(node -> node.setVisible(true));
            //Duration nodes
            durationNodes.forEach(node -> node.setVisible(true));
        }
    }

    @FXML
    void exportButtonOnAction(ActionEvent event) throws IOException {
        Facade.getInstance().exportToTxt();
    }

    @FXML
    void searchProgramComboAction(ActionEvent event) {
        searchSeriesCombo.getItems().clear();
        if (searchProgramCombo.getSelectionModel().getSelectedItem().equals(transmission)) {
            searchSeriesCombo.setDisable(true);
            searchSeasonCombo.setDisable(true);
            updateTvSeriesButton.setDisable(true);
            searchSeriesCombo.getSelectionModel().clearSelection();
            searchSeasonCombo.getSelectionModel().clearSelection();
            searchSeriesCombo.setPromptText("Choose TV-series");
            searchSeasonCombo.setPromptText("Choose season");


            searchListView.getItems().clear();
            for (Program program : Facade.getInstance().getPrograms()) {
                if (program instanceof Transmission && program.isApproved()) {
                    searchListView.getItems().add(program.getName() + ": " + program.getUuid() + ": " +
                            LoginHandler.getInstance().getUserFromUuid(program.getCreatedBy()).getUsername());
                }
            }

        } else if (searchProgramCombo.getSelectionModel().getSelectedItem().equals(tvSeries)) {
            searchListView.getItems().clear();
            searchSeriesCombo.setDisable(false);
            searchSeasonCombo.setDisable(false);

            for (TVSeries tvSeries : Facade.getInstance().getTvSeriesList()) {
                searchSeriesCombo.getItems().add(tvSeries.getName());
            }
        }
    }

    @FXML
    void searchSeriesComboAction(ActionEvent event) {

        searchSeasonCombo.getItems().clear();
        //To find the episodes based on a season from a TV-series
        try {
            TVSeries series = Facade.getInstance().getTvSeriesList().get(searchSeriesCombo.getSelectionModel().getSelectedIndex());

            //Enables the delete button when a series has been chosen
            deleteSelectedButton.setDisable(false);
            deleteSelectedButton.setText("Delete Tv-Series");

            if (series.getSeasonMap() != null) {
                for (Integer i : series.getSeasonMap().keySet()) {
                    searchSeasonCombo.getItems().add(String.valueOf(i));
                }
            }
            updateTvSeriesButton.setDisable(false);
        } catch (IndexOutOfBoundsException e) {
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
                    if (episode.isApproved()) {
                        searchListView.getItems().add(episode.getName() + ": " + episode.getUuid() + ": " +
                                LoginHandler.getInstance().getUserFromUuid(episode.getCreatedBy()).getUsername());
                        if (episode.getProduction().equals(tv2Logo)) {
                            creditedLogoImageView.setImage(tv2LogoImage);
                        } else if (episode.getProduction().equals(nordiskFilmLogo)) {
                            creditedLogoImageView.setImage(nordiskFilmLogoImage);
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
        }
    }

    @FXML
    void selectedProgramFromListView(MouseEvent event) {
        searchListViewCredits.getItems().clear();
        updateProgramButton.setDisable(false);
        deleteSelectedButton.setDisable(false);

        if (getSelectedProgramFromListView() instanceof Transmission) {
            deleteSelectedButton.setText("Delete transmission");
        } else if (getSelectedProgramFromListView() instanceof Episode) {
            deleteSelectedButton.setText("Delete Episode");
        }
        Program selectedProgram = getSelectedProgramFromListView();
        if (selectedProgram.getProduction().equals(tv2Logo)){
            creditedLogoImageView.setImage(tv2LogoImage);
        } else if (selectedProgram.getProduction().equals(nordiskFilmLogo)){
            creditedLogoImageView.setImage(nordiskFilmLogoImage);
        }

        //Get the credits from the selected program IF the program contains credits
        if (selectedProgram.getCredits() != null) {
            ArrayList<Credit> credits = selectedProgram.getCredits();
            for (Credit credit : credits) {
                searchListViewCredits.getItems().add(credit.getCreditedPerson().getName() + ": " + credit.getFunction().role);
            }
        }
    }

    @FXML
    void selectCreditFromListView(MouseEvent event) {
        if (!searchListViewCredits.getSelectionModel().isEmpty() && !searchListViewCredits.getSelectionModel().getSelectedItem().isEmpty()) {
            deleteSelectedButton.setText("Delete credit");
        }
    }

    @FXML
    void updateUpdateTabProgramOnAction(ActionEvent event) {

        //Insert related notes to an arrayList
        ArrayList<Node> updateNodes = new ArrayList<>(Arrays.asList(
                nameUpdateLabel, descriptionUpdateLabel, durationUpdateLabel,
                nameUpdateText, descriptionUpdateText, durationUpdateText));
        ArrayList<Node> episodeNodes = new ArrayList<>(Arrays.asList(
                seasonNoUpdateLabel, seasonNumberUpdateText, episodeNoUpdateLabel,
                episodeNumberUpdateText, tvSUpdateLabel, tvSeriesUpdateSelection));

        if (searchListView.getSelectionModel().getSelectedItem() != null) {
            //Get the chosen program
            Program program = getSelectedProgramFromListView();
            //Changes the visibility for relevant nodes
            updateNodes.forEach(node -> node.setVisible(true));
            updateProgramBtn.setVisible(true);
            updateTvSeriesBtn.setVisible(false);

            //Updates the text in the update-tab based on the chosen program
            nameUpdateText.setText(program.getName());
            descriptionUpdateText.setText(program.getDescription());
            durationUpdateText.setText(String.valueOf(program.getDuration()));

            //To get the correct logo
            if (program.getProduction().equals(tv2Logo)){
                productionSelectionUpdateCombo.getSelectionModel().select(0);
            } else if (program.getProduction().equals(nordiskFilmLogo)){
                productionSelectionUpdateCombo.getSelectionModel().select(1);
            }


            if (searchProgramCombo.getSelectionModel().getSelectedItem().equals(tvSeries)) {
                //Casts the chosen program to an episode
                Episode episode = (Episode) program;
                //Changes the visibility for relevant nodes
                episodeNodes.forEach(node -> node.setVisible(true));

                //Updates the text in the update-tab based on the chosen program
                seasonNumberUpdateText.setText(String.valueOf(episode.getSeasonNo()));
                episodeNumberUpdateText.setText(String.valueOf(episode.getEpisodeNo()));

                tvSeriesUpdateSelection.getItems().clear();
                for (TVSeries tvSeries : Facade.getInstance().getTvSeriesList()) {
                    tvSeriesUpdateSelection.getItems().add(tvSeries.getName());
                }
                tvSeriesUpdateSelection.getSelectionModel().select(Facade.getInstance().getTvSeriesFromEpisode(episode).getName());
            } else if (searchProgramCombo.getSelectionModel().getSelectedItem().equals(transmission)) {
                episodeNodes.forEach(node -> node.setVisible(false));
            }
            currentlyUpdatingLabel.setText("Currently editing: " + program.getName());
            currentlyUpdatingUUID.setText(String.valueOf(program.getUuid()));

            mainTabPane.getSelectionModel().select(updateTab);
        }
    }

    @FXML
    void updateUpdateTabCreditOnAction(ActionEvent event) {
        Program program = getSelectedProgramFromListView();
        Credit credit = program.getCredits().get(searchListViewCredits.getSelectionModel().getSelectedIndex());
        creditedPersonUpdateLabel.setText(credit.getCreditedPerson().getName() + ": " + credit.getCreditedPerson().getUuid());
        programUpdateSelection.setText(program.getName());
        functionUpdateSelection.getSelectionModel().select(credit.getFunction().role);
        mainTabPane.getSelectionModel().select(updateTab);
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
            Credit.Function function = Facade.getInstance().getFunctions().get(functionUpdateSelection.getSelectionModel().getSelectedIndex());

            Facade.getInstance().updateCredit(credit, function);
        } catch (IndexOutOfBoundsException e) {
        }
    }

    @FXML
    void updateProgram(ActionEvent event) {
        Program program = Facade.getInstance().getProgramFromUuid(UUID.fromString(currentlyUpdatingUUID.getText()));
        String name = nameUpdateText.getText();
        String description = descriptionUpdateText.getText();
        String production = productionSelectionUpdateCombo.getSelectionModel().getSelectedItem();
        int duration = durationUpdateText.getText().isEmpty() ? -1 : Integer.parseInt(durationUpdateText.getText());
        int seasonNo = seasonNumberUpdateText.getText().isEmpty() ? -1 : Integer.parseInt(seasonNumberUpdateText.getText());
        int episodeNo = episodeNumberUpdateText.getText().isEmpty() ? -1 : Integer.parseInt(episodeNumberUpdateText.getText());

        TVSeries tvSeries = tvSeriesUpdateSelection.getSelectionModel().getSelectedIndex() == -1 ? null :
                Facade.getInstance().getTvSeriesList().get(tvSeriesUpdateSelection.getSelectionModel().getSelectedIndex());

        if (program instanceof Transmission) {
            Facade.getInstance().updateTransmission(program, name, description, duration, production);
        } else if (program instanceof Episode) {
            Facade.getInstance().updateEpisode(program, name, description, duration, seasonNo, episodeNo, tvSeries, production);
        }
        updateUpdateUI();
    }

    @FXML
    void updateTvSeries(ActionEvent event) {
        TVSeries tvSeries = Facade.getInstance().getTvSeriesFromUuid(UUID.fromString(currentlyUpdatingUUID.getText()));
        String name = nameUpdateText.getText();
        String description = descriptionUpdateText.getText();

        Facade.getInstance().updateTvSeries(tvSeries, name, description);
        updateUpdateUI();
    }

    public void updateCreateUI() {
        programSelection.getItems().clear();
        tvSeriesSelection.getItems().clear();
        creditedPersonSelection.getItems().clear();

        for (Program program : Facade.getInstance().getPrograms()) {
            programSelection.getItems().add(program.getName());
        }
        for (TVSeries tvSeries : Facade.getInstance().getTvSeriesList()) {
            tvSeriesSelection.getItems().add(tvSeries.getName());
        }
        for (CreditedPerson creditedPerson : Facade.getInstance().getCreditedPeople()) {
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

        Facade.getInstance().getTvSeriesList().forEach(tvSeries -> tvSeriesUpdateSelection.getItems().add(tvSeries.getName()));
    }

    @FXML
    void openConfirmBox(MouseEvent event) {
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
                        Facade.getInstance().getTvSeriesFromEpisode((Episode) getSelectedProgramFromListView()).getName() +
                        "\nMed ID: " + getSelectedProgramFromListView().getUuid());
            }
        }
    }

    @FXML
    void declineDeleteSelected(ActionEvent event) {
        confirmAnchorPane.setVisible(false);
    }

    @FXML
    void deleteSelected(ActionEvent event) {
        // Deletes selected program
        if (!searchSeriesCombo.getSelectionModel().isEmpty() && searchSeasonCombo.getSelectionModel().isEmpty()) {
        } else if (!searchListViewCredits.getSelectionModel().isEmpty() && !searchListViewCredits.getSelectionModel().getSelectedItem().isEmpty()) {
            Facade.getInstance().deleteCredit(getSelectedProgramFromListView(), getSelectedCreditFromListView());
        } else if (getSelectedProgramFromListView() != null) {
            Facade.getInstance().deleteProgram(getSelectedProgramFromListView());
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
            for (Program program : Facade.getInstance().getPrograms()) {
                if (program instanceof Transmission && !program.isApproved()) {
                    searchApprovedListView.getItems().add(program.getName() +  ": " + program.getUuid());
                }
            }

        } else if (searchApprovedProgramCombo.getSelectionModel().getSelectedItem().equals(tvSeries)){
            searchApprovedSeriesCombo.setDisable(false);
            searchApprovedSeasonCombo.setDisable(false);

            for (TVSeries tvSeries : Facade.getInstance().getTvSeriesList()) {
                searchApprovedSeriesCombo.getItems().add(tvSeries.getName());
            }
        }
    }

    @FXML
    void searchApprovedSeriesComboAction (ActionEvent event){
        searchApprovedSeasonCombo.getItems().clear();
        //To find the episodes based on a season from a TV-series
        try {
            TVSeries series = Facade.getInstance().getTvSeriesList().get(searchApprovedSeriesCombo.getSelectionModel().getSelectedIndex());

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
        }
    }

    @FXML
    void selectedApprovedProgramFromListView(MouseEvent event) {
        searchApprovedListViewCredits.getItems().clear();

        Program selectedProgram = getSelectedProgramFromApprovedListView();

        //Get the credits from the selected program IF the program contains credits
        if(selectedProgram.getCredits() != null) {
            ArrayList<Credit> credits = selectedProgram.getCredits();
            for (Credit credit : credits) {
                searchApprovedListViewCredits.getItems().add(credit.getCreditedPerson().getName() + ": " + credit.getFunction().role + ": persons ID " +
                        credit.getCreditedPerson().getUuid());
            }
        }
    }

    @FXML
    void approveSelectedProgram(ActionEvent event){
        Facade.getInstance().approveProgram(getSelectedProgramFromApprovedListView());
    }

    @FXML
    void markNotificationsAsSeen(MouseEvent event) {
        String title = systemLogLV.getSelectionModel().getSelectedItem();
        Facade.getInstance().setNotificationAsSeen(Facade.getInstance().getNotificationFromTitle(title.substring(5)));
        refreshSystemLogs();
    }
    @FXML
    void markAllNotificationsAsSeen() {
         for (String title : systemLogLV.getItems()) {
             Facade.getInstance().setNotificationAsSeen(Facade.getInstance().getNotificationFromTitle(title.substring(5)));
         }
        refreshSystemLogs();
    }

    @FXML
    void refreshSystemLogs() {
        systemLogLV.getItems().clear();
        for (Notification notification : Facade.getInstance().getNotifications()) {
            if (notification.getSeen()) {
                systemLogLV.getItems().add("Old: " + notification.getTitle());
            } else  {
                systemLogLV.getItems().add("New: " + notification.getTitle());
            }
        }
    }


    private TVSeries getSelectedTvSeriesFromComboBox() {
        try{
            if (searchViewTab.isSelected()){
                return Facade.getInstance().getTvSeriesList().get(searchSeriesCombo.getSelectionModel().getSelectedIndex());

            } else if (approvedTab.isSelected()) {
                return Facade.getInstance().getTvSeriesList().get(searchApprovedSeriesCombo.getSelectionModel().getSelectedIndex());
            }
        } catch (NullPointerException e) {
        }
        return null;
    }

    /**
     * Can't get the below code to work. It ignores the if-statement even though it should be true -
     * Just made two independent methods
     */

/*    private Program getSelectedProgramFromListView() {
        String viewString = "";
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
    } */

    private Program getSelectedProgramFromListView() {
        //Choose the String-item from the listview instead of the index
        String viewString = searchListView.getSelectionModel().getSelectedItem();
        //Split the string to get the UUID
        String[] viewStringArray = viewString.split(":");
        //Use the getProgramFromUuid method from the facade to get the program from the string. The trim after the string is to get rid of whitespace
        return Facade.getInstance().getProgramFromUuid(UUID.fromString(viewStringArray[1].trim()));
    }

    private Program getSelectedProgramFromApprovedListView() {
        //Choose the String-item from the listview instead of the index
        approveSelectedButton.setDisable(false);
        String viewString = searchApprovedListView.getSelectionModel().getSelectedItem();
        String[] viewStringArray = viewString.split(":");
        //Use the getProgramFromUuid method from the facade to get the program from the string. The trim after the string is to get rid of whitespace
        return Facade.getInstance().getProgramFromUuid(UUID.fromString(viewStringArray[1].trim()));
    }

    private Credit getSelectedCreditFromListView() {
        return getSelectedProgramFromListView().getCredits().get(searchListViewCredits.getSelectionModel().getSelectedIndex());
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        usertypeCombo.getItems().add("Producer");
        usertypeCombo.getItems().add("System Administrator");

        searchProgramCombo.getItems().add(transmission);
        searchProgramCombo.getItems().add(tvSeries);
        searchApprovedProgramCombo.getItems().add(transmission);
        searchApprovedProgramCombo.getItems().add(tvSeries);

        productionSelectionCombo.getItems().add(tv2Logo);
        productionSelectionCombo.getItems().add(nordiskFilmLogo);

        productionSelectionUpdateCombo.getItems().add(tv2Logo);
        productionSelectionUpdateCombo.getItems().add(nordiskFilmLogo);


        programTypeSelection.getItems().add(transmission);
        programTypeSelection.getItems().add(tvSeries);
        programTypeSelection.getItems().add(episode);

        for (Credit.Function function : Facade.getInstance().getFunctions()) {
            functionSelection.getItems().add(function.role);
            functionUpdateSelection.getItems().add(function.role);
        }

        updateCreateUI();
    }
}