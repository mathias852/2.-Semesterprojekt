package presentation;

import domain.Facade;
import domain.LoginHandler;
import domain.credit.Credit;
import domain.credit.CreditedPerson;
import domain.program.Episode;
import domain.program.Program;
import domain.program.TVSeries;
import domain.program.Transmission;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.UUID;

public class ProducerController implements Initializable {
    @FXML
    private TextField durationText, descriptionText, seasonNumberText, episodeNumberText, nameText,
            creditedPersonNameText, nameUpdateText, descriptionUpdateText, episodeNumberUpdateText, durationUpdateText,
            creditedPersonNameUpdateText, seasonNumberUpdateText;

    @FXML
    private ComboBox<String> tvSeriesSelection, searchSeriesCombo, functionSelection, searchProgramCombo,
            searchSeasonCombo, creditedPersonSelection, programSelection, programTypeSelection, tvSeriesUpdateSelection,
            functionUpdateSelection, productionSelectionCombo, myProgramTabApprovedComboBox, myProgramTabSearchProgramCombo,
            myProgramTabSearchSeriesCombo, myProgramTabSearchSeasonCombo, productionSelectionUpdateCombo, searchForProgramCB,
            searchForCreditCB, searchForFunctionCB;

    @FXML
    private Label creditedPersonLabel, tvSLabel, durationLabel, nameLabel, seasonNoLabel, messageLabel,
            episodeNoLabel, descriptionLabel, nameUpdateLabel, descriptionUpdateLabel, seasonNoUpdateLabel,
            episodeNoUpdateLabel, durationUpdateLabel, tvSUpdateLabel, currentlyUpdatingLabel, currentlyUpdatingUUID,
            programUpdateSelection, creditedPersonUpdateLabel, productionLabel, programUuidUpdateSelection, creditIndexUpdateSelection;

    @FXML
    private Button createProgramBtn, createCreditBtn, createPersonBtn, updateProgramButton, updateCreditButton,
            updateProgramBtn, updatePersonButton, updateTvSeriesButton, updateTvSeriesBtn, myProgramTabUpdateTvSeriesButton,
            myProgramTabUpdateProgramButton, myProgramTabUpdateCreditButton, myProgramTabUpdatePersonButton, updateCreditBtnForMyProgramTab;

    @FXML
    private ListView<String> searchListView, searchListViewCredits, myProgramTabSearchListView, myProgramTabSearchListViewCredits,
            searchForTVSLV, searchForProgramLV, searchForCreditLV, searchForFunctionLV;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab updateTab;

    @FXML
    private ImageView creditedLogoImageView, myProgramTabCreditedLogoImageView;

    private final String transmission = "Transmission", tvSeries = "TV-Series", episode = "Episode";

    private final String tv2Logo = "TV2", nordiskFilmLogo = "Nordisk Film";

    private final String approved = "Approved", pending = "Pending approval";

    private final File nordiskFilmLogoFile = new File("src/main/resources/presentation/NF-Logo.png");
    private final File tv2LogoFile = new File("src/main/resources/presentation/TV2Logo.png");

    private final Image nordiskFilmLogoImage = new Image(nordiskFilmLogoFile.toURI().toString());
    private final Image tv2LogoImage = new Image(tv2LogoFile.toURI().toString());

    SearchFunctionality searchFunctionality = new SearchFunctionality();

    @FXML
    void logOutAction() throws IOException{
        App.setRoot("logInPage");
    }

    //Method for the createPerson-button
    @FXML
    void createPerson() {
        //Uses the createPerson-method from the facade
        if (!creditedPersonNameText.getText().isEmpty()) {
            Facade.getInstance().createPerson(creditedPersonNameText.getText());
            updateCreateUI();
            creditedPersonNameText.setText("");
        }
    }

    @FXML
    void createCredit() {
        try {
            //Programmet hentes igennem index for vores program drop-down menu
            Program program = Facade.getInstance().getPrograms().get(programSelection.getSelectionModel().getSelectedIndex());
            CreditedPerson creditedPerson = Facade.getInstance().getCreditedPeople().get(creditedPersonSelection.getSelectionModel().getSelectedIndex());
            Credit.Function function = Facade.getInstance().getFunctions().get(functionSelection.getSelectionModel().getSelectedIndex());

            if(program.getCreatedBy().equals(LoginHandler.getInstance().getCurrentUser().getUuid())){
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
            }


        } catch (IndexOutOfBoundsException ignored) {}
    }

    //Below method creates a program with given/relevant input
    @FXML
    void createProgram() {

        String name = nameText.getText();
        String description = descriptionText.getText();
        String production = productionSelectionCombo.getSelectionModel().getSelectedItem();

        if (programTypeSelection.getValue().equals(transmission)) {
            int duration = durationText.getText().isEmpty() ? -1 : Integer.parseInt(durationText.getText());
            if (!name.isEmpty()) {
                Facade.getInstance().createTransmission(name, description, LoginHandler.getInstance().getCurrentUser().getUuid(), duration, false, production);

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
                      Facade.getInstance().createEpisode(tvSeries, name, description, LoginHandler.getInstance().getCurrentUser().getUuid(), episodeNumber, seasonNumber, duration, false, production);
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
    void dropDownSelection() {
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
    void searchProgramComboAction() {
        searchSeriesCombo.getItems().clear();
        creditedLogoImageView.setImage(null);

        if (searchProgramCombo.getSelectionModel().getSelectedItem().equals(transmission)){
            //Disable irrelevant nodes
            searchSeriesCombo.setDisable(true);
            searchSeasonCombo.setDisable(true);
            updateTvSeriesButton.setDisable(true);

            searchSeriesCombo.getSelectionModel().clearSelection();
            searchSeasonCombo.getSelectionModel().clearSelection();

            //Resets the combobox text for further search
            searchSeriesCombo.setPromptText("Choose TV-series");
            searchSeasonCombo.setPromptText("Choose season");


            searchListView.getItems().clear();

            //Gets all approved transmissions and add them to the listview
            for (Program program : Facade.getInstance().getPrograms()) {
                if (program instanceof Transmission && program.isApproved()) {
                    searchListView.getItems().add(program.getName() +  "\nID: " + program.getUuid() + "\nCreated by: "
                            + LoginHandler.getInstance().getUserFromUuid(program.getCreatedBy()).getUsername());
                }
            }
        } else if (searchProgramCombo.getSelectionModel().getSelectedItem().equals(tvSeries)){
            searchListView.getItems().clear();
            //Enables relevant nodes
            searchSeriesCombo.setDisable(false);
            searchSeasonCombo.setDisable(false);

            //Add all tvSeries to the comboBox
            for (TVSeries tvSeries : Facade.getInstance().getTvSeriesList()) {
                searchSeriesCombo.getItems().add(tvSeries.getName());
            }
        }
    }

    @FXML
    void searchSeriesComboAction() {
        searchSeasonCombo.getItems().clear();
        //To find the episodes based on a season from a TV-series
        try {
            TVSeries series = Facade.getInstance().getTvSeriesList().get(searchSeriesCombo.getSelectionModel().getSelectedIndex());
            if (series.getSeasonMap() != null) {
                for (Integer i : series.getSeasonMap().keySet()) {
                    searchSeasonCombo.getItems().add(String.valueOf(i));
                }
            }
            updateTvSeriesButton.setDisable(false);
        } catch (IndexOutOfBoundsException ignored){}
    }

    @FXML
    void searchSeasonComboAction() {
        creditedLogoImageView.setImage(null);
        try {
            searchListView.getItems().clear();

            //Adds all episodes and the producer for said episode from a given series to the listView
            if (searchSeriesCombo.getSelectionModel().getSelectedIndex() != -1) {
                TVSeries series = getSelectedTvSeriesFromComboBox();
                for (Episode episode : series.getSeasonMap().get(Integer.parseInt(searchSeasonCombo.getSelectionModel().getSelectedItem()))) {
                    if(episode.isApproved()) {
                        searchListView.getItems().add(episode.getName() + "\nID: " + episode.getUuid() + "\nEpisode: " + episode.getEpisodeNo() + "\nCreated by: " +
                                LoginHandler.getInstance().getUserFromUuid(episode.getCreatedBy()).getUsername());
                        if (episode.getProduction().equals(tv2Logo)){
                            creditedLogoImageView.setImage(tv2LogoImage);
                        } else if (episode.getProduction().equals(nordiskFilmLogo)){
                            creditedLogoImageView.setImage(nordiskFilmLogoImage);
                        }
                    }
                }
            }
        } catch (NumberFormatException ignored) {
        }
    }

    @FXML
    void selectedProgramFromListView() {
        //List for related buttons.
        ArrayList<Button> buttons = new ArrayList<>(Arrays.asList(updateCreditButton, updatePersonButton, updateProgramButton));
        //Changes visibility for above buttons.
        buttons.forEach(button -> button.setDisable(true));

        if (searchListView.getItems().size() > 0) {
            searchListViewCredits.getItems().clear();
            //If the user who created the program is the current user, the update-buttons are enables
            if(getSelectedProgramFromListView().getCreatedBy().equals(LoginHandler.getInstance().getCurrentUser().getUuid())){
                buttons.forEach(button -> button.setDisable(false));
            }

            Program selectedProgram = getSelectedProgramFromListView();
            if (selectedProgram.getProduction().equals(tv2Logo)){
                creditedLogoImageView.setImage(tv2LogoImage);
            } else if (selectedProgram.getProduction().equals(nordiskFilmLogo)){
                creditedLogoImageView.setImage(nordiskFilmLogoImage);
            }

            //Get the credits from the selected program IF the program contains credits
            if(selectedProgram.getCredits() != null) {
                ArrayList<Credit> credits = selectedProgram.getCredits();
                for (Credit credit : credits) {
                    searchListViewCredits.getItems().add(credit.getCreditedPerson().getName() + ": " + credit.getFunction().role);
                }
            }
        }
    }

    @FXML
    void updateUpdateTabProgramOnAction() {
        if(LoginHandler.getInstance().getCurrentUser().getUuid().equals(getSelectedProgramFromListView().getCreatedBy())){

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
    }

    @FXML
    void updateTabUpdateCredit() {
        Program program = getSelectedProgramFromListView();
        Credit credit = null;

        //If the tab is "Search/view/ we get the credit highlighted from there if "My Program" is selected we get from there
        if (mainTabPane.getSelectionModel().getSelectedItem().getText().equals("Search/view")) {
            credit = program.getCredits().get(searchListViewCredits.getSelectionModel().getSelectedIndex());
            creditIndexUpdateSelection.setText(String.valueOf(searchListViewCredits.getSelectionModel().getSelectedIndex()));
        } else if (mainTabPane.getSelectionModel().getSelectedItem().getText().equals("My Programs")) {
            credit = program.getCredits().get(myProgramTabSearchListViewCredits.getSelectionModel().getSelectedIndex());
            creditIndexUpdateSelection.setText(String.valueOf(myProgramTabSearchListViewCredits.getSelectionModel().getSelectedIndex()));
        }

        //Set the label-text in the update-tab based on the if-statement above
        creditedPersonUpdateLabel.setText(credit.getCreditedPerson().getName() + ": " + credit.getCreditedPerson().getUuid());
        programUpdateSelection.setText(program.getName());
        programUuidUpdateSelection.setText(String.valueOf(program.getUuid()));

        //We set the function-combobox to the role-value based on the chosen credit
        functionUpdateSelection.getSelectionModel().select(credit.getFunction().role);
        //change the view to the update-tab
        mainTabPane.getSelectionModel().select(updateTab);
    }

    @FXML
    void updateUpdateTabPersonOnAction() {
        System.out.println("Not yet implemented");
    }

    @FXML
    void updateUpdateTabTVSeriesOnAction() {
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
    void myProgramTabSearchProgramCombo() {
        myProgramTabSearchSeriesCombo.getItems().clear();
        myProgramTabCreditedLogoImageView.setImage(null);
        String approvedComboBox = myProgramTabApprovedComboBox.getSelectionModel().getSelectedItem();

        if (myProgramTabSearchProgramCombo.getSelectionModel().getSelectedItem().equals(transmission)){
            myProgramTabSearchSeriesCombo.setDisable(true);
            myProgramTabSearchSeasonCombo.setDisable(true);
            myProgramTabUpdateTvSeriesButton.setDisable(true);
            myProgramTabSearchSeriesCombo.getSelectionModel().clearSelection();
            myProgramTabSearchSeasonCombo.getSelectionModel().clearSelection();
            myProgramTabSearchSeriesCombo.setPromptText("Choose TV-series");
            myProgramTabSearchSeasonCombo.setPromptText("Choose season");


            myProgramTabSearchListView.getItems().clear();
            for (Program program : Facade.getInstance().getPrograms()) {
                if (approvedComboBox.equals(approved) && program.getCreatedBy().equals(LoginHandler.getInstance().getCurrentUser().getUuid())) {
                    if (program instanceof Transmission && program.isApproved()) {
                        myProgramTabSearchListView.getItems().add(program.getName() + ": " + program.getUuid() + ": " +
                                LoginHandler.getInstance().getUserFromUuid(program.getCreatedBy()).getUsername());
                    }
                } else if (approvedComboBox.equals(pending) && program.getCreatedBy().equals(LoginHandler.getInstance().getCurrentUser().getUuid())) {
                    if (program instanceof Transmission && !program.isApproved()){
                        myProgramTabSearchListView.getItems().add(program.getName() + ": " + program.getUuid() + ": " +
                                LoginHandler.getInstance().getUserFromUuid(program.getCreatedBy()).getUsername());

                    }
                }
            }
        } else if (myProgramTabSearchProgramCombo.getSelectionModel().getSelectedItem().equals(tvSeries)){
            myProgramTabSearchSeriesCombo.setDisable(false);
            myProgramTabSearchSeasonCombo.setDisable(false);

            for (TVSeries tvSeries : Facade.getInstance().getTvSeriesList()) {
                myProgramTabSearchSeriesCombo.getItems().add(tvSeries.getName());
            }
        }
    }


    @FXML
    void myProgramTabSearchSeriesCombo() {
        myProgramTabSearchSeasonCombo.getItems().clear();
        //To find the episodes based on a season from a TV-series
        try {
            TVSeries series = Facade.getInstance().getTvSeriesList().get(myProgramTabSearchSeriesCombo.getSelectionModel().getSelectedIndex());
            if (series.getSeasonMap() != null) {
                for (Integer i : series.getSeasonMap().keySet()) {
                    myProgramTabSearchSeasonCombo.getItems().add(String.valueOf(i));
                }
            }
            myProgramTabUpdateTvSeriesButton.setDisable(false);
        } catch (IndexOutOfBoundsException ignored){
        }
    }

    @FXML
    void myProgramTabSearchSeasonCombo() {
        myProgramTabCreditedLogoImageView.setImage(null);
        String approvedComboBox = myProgramTabApprovedComboBox.getSelectionModel().getSelectedItem();

        try {
            myProgramTabSearchListView.getItems().clear();

            if (myProgramTabSearchSeriesCombo.getSelectionModel().getSelectedIndex() != -1) {
                TVSeries series = getSelectedTvSeriesFromComboBox();
                for (Episode episode : series.getSeasonMap().get(Integer.parseInt(myProgramTabSearchSeasonCombo.getSelectionModel().getSelectedItem()))) {
                    if (approvedComboBox.equals(approved) && episode.isApproved() && episode.getCreatedBy().equals(LoginHandler.getInstance().getCurrentUser().getUuid())) {
                        myProgramTabSearchListView.getItems().add(episode.getName() + ": " + episode.getUuid() + ": " +
                                LoginHandler.getInstance().getUserFromUuid(episode.getCreatedBy()).getUsername());
                    } else if (approvedComboBox.equals(pending) && !episode.isApproved() && episode.getCreatedBy().equals(LoginHandler.getInstance().getCurrentUser().getUuid())) {
                        myProgramTabSearchListView.getItems().add(episode.getName() + ": " + episode.getUuid() + ": " +
                                LoginHandler.getInstance().getUserFromUuid(episode.getCreatedBy()).getUsername());
                    }
                    if (episode.getProduction().equals(tv2Logo)) {
                        myProgramTabCreditedLogoImageView.setImage(tv2LogoImage);
                    } else if (episode.getProduction().equals(nordiskFilmLogo)) {
                        myProgramTabCreditedLogoImageView.setImage(nordiskFilmLogoImage);
                    }
                }
            }
        } catch (NumberFormatException ignored) {

        }
    }

    @FXML
    void myProgramTabSelectedProgramFromListView() {
        ArrayList<Button> buttons = new ArrayList<>(Arrays.asList(myProgramTabUpdateCreditButton, myProgramTabUpdatePersonButton,
                myProgramTabUpdateProgramButton));
        buttons.forEach(node -> node.setDisable(true));

        if (myProgramTabSearchListView.getItems().size() > 0) {
            myProgramTabSearchListViewCredits.getItems().clear();

            if(getSelectedProgramFromListView().getCreatedBy().equals(LoginHandler.getInstance().getCurrentUser().getUuid())){
                buttons.forEach(node -> node.setDisable(false));
            }
            Program selectedProgram = getSelectedProgramFromListView();

            if (selectedProgram.getProduction().equals(tv2Logo)){
                myProgramTabCreditedLogoImageView.setImage(tv2LogoImage);
            } else if (selectedProgram.getProduction().equals(nordiskFilmLogo)){
                myProgramTabCreditedLogoImageView.setImage(nordiskFilmLogoImage);
            }

            //Get the credits from the selected program IF the program contains credits
            if(selectedProgram.getCredits() != null) {
                ArrayList<Credit> credits = selectedProgram.getCredits();
                for (Credit credit : credits) {
                    myProgramTabSearchListViewCredits.getItems().add(credit.getCreditedPerson().getName() + ": " + credit.getFunction().role);
                }
            }
        }
    }

    @FXML
    void updateCredit() {
        try {
            //Programmet hentes igennem index for vores program drop-down menu
            if (getSelectedCreditFromListView() != null) {
                Credit credit = getSelectedCreditFromListView();
                Credit.Function function = Facade.getInstance().getFunctions().get(functionUpdateSelection.getSelectionModel().getSelectedIndex());

                Facade.getInstance().updateCredit(credit, function);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @FXML
    void updateProgram() {
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
        }
        else if (program instanceof Episode) {
            Facade.getInstance().updateEpisode(program, name, description, duration, seasonNo, episodeNo, tvSeries, production);
        }
        updateUpdateUI();
    }

    @FXML
    void updateTvSeries() {
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
        productionSelectionCombo.getSelectionModel().clearSelection();
    }

    public void updateUpdateUI() {
        ArrayList<TextField> textFields = new ArrayList<>(Arrays.asList(nameUpdateText, descriptionUpdateText,
                durationUpdateText, seasonNumberUpdateText, episodeNumberUpdateText, creditedPersonNameUpdateText));
        textFields.forEach(node -> node.clear());
        ArrayList<ComboBox> comboBoxes = new ArrayList<>(Arrays.asList(tvSeriesUpdateSelection, functionUpdateSelection));
        comboBoxes.forEach(node -> node.getItems().clear());
        currentlyUpdatingLabel.setText("Choose program in \"Search/view\" or in \"My Programs\" tab");
        currentlyUpdatingUUID.setText("");

        Facade.getInstance().getTvSeriesList().forEach(tvSeries -> tvSeriesUpdateSelection.getItems().add(tvSeries.getName()));
    }

    private TVSeries getSelectedTvSeriesFromComboBox() {
        TVSeries tvSeries = null;
        if (mainTabPane.getSelectionModel().getSelectedItem().getText().equals("Search/view")) {
            tvSeries = Facade.getInstance().getTvSeriesList().get(searchSeriesCombo.getSelectionModel().getSelectedIndex());

        } else if (mainTabPane.getSelectionModel().getSelectedItem().getText().equals("My Programs")) {
            tvSeries = Facade.getInstance().getTvSeriesList().get(myProgramTabSearchSeriesCombo.getSelectionModel().getSelectedIndex());
        }
        return tvSeries;
    }

    private Program getSelectedProgramFromListView() {
        String viewString = null;

        if (mainTabPane.getSelectionModel().getSelectedItem().getText().equals("Search/view")) {
            //Choose the String-item from the listview instead of the index
            viewString = searchListView.getSelectionModel().getSelectedItem();

        } else if (mainTabPane.getSelectionModel().getSelectedItem().getText().equals("My Programs")) {
            viewString = myProgramTabSearchListView.getSelectionModel().getSelectedItem();
        }

        //Split the string to get the UUID
        String[] viewStringArray = viewString.split(":");

        //Use the getProgramFromUuid method from the facade to get the program from the string. The trim after the string is to get rid of whitespace
        return Facade.getInstance().getProgramFromUuid(UUID.fromString(viewStringArray[1].substring(1,37)));
    }

    private Credit getSelectedCreditFromListView() {
        Credit credit = null;

        //If the method is called from the update-tab, the following if-statement's value is returned.
        if (mainTabPane.getSelectionModel().getSelectedItem().getText().equals("Update")){

            try {
                Program program = Facade.getInstance().getProgramFromUuid(UUID.fromString(programUuidUpdateSelection.getText()));
                program.setApproved(false);
                return program.getCredits().get(Integer.parseInt(creditIndexUpdateSelection.getText()));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid credit information given...");
                return null;
            }
        }

        Program selectedProgram = getSelectedProgramFromListView();
        selectedProgram.setApproved(false);

        if (mainTabPane.getSelectionModel().getSelectedItem().getText().equals("Search/view")) {
            //Choose the String-item from the listview instead of the index
            credit = selectedProgram.getCredits().get(searchListViewCredits.getSelectionModel().getSelectedIndex());

        } else if (mainTabPane.getSelectionModel().getSelectedItem().getText().equals("My Programs")) {
            credit = selectedProgram.getCredits().get(myProgramTabSearchListView.getSelectionModel().getSelectedIndex());
        }

        return credit;
    }


    //Searches for TVSeries based on user-input
    @FXML
    void showMatchingTVS() {
        searchFunctionality.searchForTVSeries(searchForTVSLV, searchSeriesCombo);
    }
    //Selects the clicked TVSeries and hides the search results
    @FXML
    void selectAndHideMatchingTVS() {
        searchFunctionality.selectAndHideTVSeries(searchSeasonCombo, searchForTVSLV, searchSeriesCombo);
    }

    //Searches for programs based on user-input.
    @FXML
    void showMatchingPrograms() {
        searchFunctionality.searchForProgram(searchForProgramLV, searchForProgramCB,  searchListView);
    }
    //Selects the clicked program and hides the search results
    @FXML
    void selectAndHideMatchingProgram() {
        searchListView.getSelectionModel().select(searchForProgramLV.getSelectionModel().getSelectedItem());
        selectedProgramFromListView();
        searchForProgramCB.getEditor().clear();
        searchForProgramLV.setVisible(false);
    }

    //Searches for credits matching the given user-input
    @FXML
    void showMatchingCredits() {
        searchFunctionality.searchForCredits(searchForCreditLV, searchForCreditCB, searchForFunctionCB, searchListViewCredits);
    }
    //Selects the clicked credit and hides search results
    @FXML
    void selectAndHideMatchingCredit() {
        searchFunctionality.selectAndHideCredits(searchListViewCredits, searchForCreditLV, searchForCreditCB);
    }

    //Searches for functions matching the user-input
    @FXML
    void showMatchingFunctions() {
        searchFunctionality.searchForFunctions(searchForFunctionLV, searchForFunctionCB);
    }
    //Selects the clicked functions and hides the search results
    @FXML
    void selectAndHideMatchingFunction() {
        searchFunctionality.selectAndHideFunctions(searchForFunctionLV, searchForFunctionCB);
    }

    //Hides all listviews displaying search results
    @FXML
    void hideMatchingSearchResults() {
        searchFunctionality.hideSearchResults(searchForTVSLV, searchForProgramLV, searchForCreditLV, searchForFunctionLV);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchProgramCombo.getItems().add(transmission);
        searchProgramCombo.getItems().add(tvSeries);

        myProgramTabSearchProgramCombo.getItems().add(transmission);
        myProgramTabSearchProgramCombo.getItems().add(tvSeries);

        productionSelectionCombo.getItems().add(tv2Logo);
        productionSelectionCombo.getItems().add(nordiskFilmLogo);

        productionSelectionUpdateCombo.getItems().add(tv2Logo);
        productionSelectionUpdateCombo.getItems().add(nordiskFilmLogo);

        myProgramTabApprovedComboBox.getItems().add(approved);
        myProgramTabApprovedComboBox.getItems().add(pending);

        myProgramTabApprovedComboBox.getSelectionModel().selectFirst();

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