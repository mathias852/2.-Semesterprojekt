package presentation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.UUID;

import domain.Facade;
import domain.LoginHandler;
import domain.accesscontrol.Producer;
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

public class GuestController implements Initializable {

    @FXML
    private ComboBox<String> searchSeriesCombo, searchProgramCombo, searchSeasonCombo, searchForProgramCB, searchForCreditCB, searchForFunctionCB;

    @FXML
    private ListView<String> searchListView, searchListViewCredits, searchForTVSLV, searchForProgramLV, searchForCreditLV, searchForFunctionLV;

    @FXML
    private ImageView creditedLogoImageView;

    private final String transmission = "Transmission";
    private final String tvSeries = "TV-Series";
    private final String episode = "Episode";

    private final String tv2Logo = "Tv2";
    private final String nordiskFilmLogo = "Nordisk Film";

    private final File nordiskFilmLogoFile = new File("src/main/resources/presentation/NF-Logo.png");
    private final File tv2LogoFile = new File("src/main/resources/presentation/tv2creditslogo.png");

    private final Image nordiskFilmLogoImage = new Image(nordiskFilmLogoFile.toURI().toString());
    private final Image tv2LogoImage = new Image(tv2LogoFile.toURI().toString());

    @FXML
    void logOutAction(ActionEvent e) throws IOException{
        App.setRoot("logInPage");
    }

    @FXML
    void exportButtonOnAction(ActionEvent event) throws IOException {
        //facade.exportToTxt();
    }

    @FXML
    void searchProgramComboAction(ActionEvent event) {
        searchSeriesCombo.getItems().clear();
        if (searchProgramCombo.getSelectionModel().getSelectedItem().equals(transmission)){
            searchSeriesCombo.setDisable(true);
            searchSeasonCombo.setDisable(true);
            searchSeriesCombo.getSelectionModel().clearSelection();
            searchSeasonCombo.getSelectionModel().clearSelection();
            searchSeriesCombo.setPromptText("Choose TV-series");
            searchSeasonCombo.setPromptText("Choose season");

            searchListView.getItems().clear();
            for (Program program : Facade.getInstance().getPrograms()) {
                if (program instanceof Transmission && program.isApproved()) {
                    searchListView.getItems().add(program.getName() + ": Programansvarlig: " + LoginHandler.getInstance().getUserFromUuid(program.getCreatedBy()).getUsername());

                }
            }

        } else if (searchProgramCombo.getSelectionModel().getSelectedItem().equals(tvSeries)){
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
            if (series.getSeasonMap() != null) {
                System.out.println("Vi kom ind");
                for (Integer i : series.getSeasonMap().keySet()) {
                    searchSeasonCombo.getItems().add(String.valueOf(i));
                }
            }
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
                for (Episode episode : series.getSeasonMap().get(Integer.parseInt(searchSeasonCombo.getSelectionModel().getSelectedItem()))) {
                    if (episode.isApproved()) {
                        searchListView.getItems().add(episode.getName() + ": Programansvarlig: " + LoginHandler.getInstance().getUserFromUuid(episode.getCreatedBy()).getUsername());
                        if (episode.getProduction().equals(tv2Logo)){
                            creditedLogoImageView.setImage(tv2LogoImage);
                        } else if (episode.getProduction().equals(nordiskFilmLogo)){
                            creditedLogoImageView.setImage(nordiskFilmLogoImage);
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("This is just happening because we parse the value 'null' as an integer. And we do that because" +
                    " we clear the season-combobox");
        }
    }

    @FXML
    void selectedProgramFromListView() {
        searchListViewCredits.getItems().clear();

        //System.out.println(searchListView.getItem);

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


    @FXML
    void showMatchingTVS() {
        searchForTVSLV.setVisible(true);
        String input = searchSeriesCombo.getEditor().getText();
        searchForTVSLV.getItems().clear();
        if (input.length() >= 1) {
            for (TVSeries tvSeries : Facade.getInstance().getTvSeriesList()) {
                if (tvSeries.getName().length() >= input.length()
                        && !searchForTVSLV.getItems().contains(input)
                        && tvSeries.getName().substring(0,input.length()).equalsIgnoreCase(input)) {
                    searchForTVSLV.getItems().add(tvSeries.getName());
                } else if (tvSeries.getName().contains(" ")) {
                    String[] splitName = tvSeries.getName().split(" ");
                    StringBuilder combinedName;
                    for (int i = 1; i < splitName.length; i++) {
                        combinedName = new StringBuilder(splitName[i]);
                        if (i != splitName.length-1) {
                            for (int j = i+1; j < splitName.length; j++) {
                                combinedName.append(" ").append(splitName[j]);
                            }
                        }
                        if (combinedName.length() >= input.length()
                                && combinedName.substring(0,input.length()).equalsIgnoreCase(input)) {
                            searchForTVSLV.getItems().add(tvSeries.getName());
                        }
                    }
                }
            }
        }
    }
    @FXML
    void selectAndHideMatchingTVS() {
        searchSeasonCombo.getItems().clear();
        String[] tvSeries = searchForTVSLV.getSelectionModel().getSelectedItem().split(";");
        searchSeriesCombo.setValue(tvSeries[0]);
        searchForTVSLV.setVisible(false);
    }

    @FXML
    void showMatchingPrograms() {
        searchForProgramLV.setVisible(true);
        String input = searchForProgramCB.getEditor().getText();
        searchForProgramLV.getItems().clear();
        if (input.length() >= 1) {
            ArrayList<Program> programs = new ArrayList<>();
            for (String programText : searchListView.getItems()) {
                String[] splitProgramText = programText.split(":");
                Program program = Facade.getInstance().getProgramFromUuid(UUID.fromString(splitProgramText[1].trim()));
                programs.add(program);
            }
            for (Program program : programs) {
                if (program.getName().length() >= input.length()
                        && !searchForProgramLV.getItems().contains(input)
                        && program.getName().substring(0,input.length()).equalsIgnoreCase(input)) {
                    searchForProgramLV.getItems().add(program.getName() + ": " + program.getUuid());
                } else if (program.getName().contains(" ")) {
                    String[] splitName = program.getName().split(" ");
                    StringBuilder combinedName;
                    for (int i = 1; i < splitName.length; i++) {
                        combinedName = new StringBuilder(splitName[i]);
                        if (i != splitName.length-1) {
                            for (int j = i+1; j < splitName.length; j++) {
                                combinedName.append(" ").append(splitName[j]);
                            }
                        }
                        if (combinedName.length() >= input.length()
                                && combinedName.substring(0,input.length()).equalsIgnoreCase(input)) {
                            searchForProgramLV.getItems().add(program.getName() + ": " + program.getUuid());
                        }
                    }
                }
            }
        }
    }
    @FXML
    void selectAndHideMatchingProgram() {
        searchListView.getSelectionModel().select(searchForProgramLV.getSelectionModel().getSelectedItem());
        selectedProgramFromListView();
        searchForProgramCB.getEditor().clear();
        searchForProgramLV.setVisible(false);
    }

    @FXML
    void showMatchingCredits() {
        searchForCreditLV.setVisible(true);
        String input = searchForCreditCB.getEditor().getText();
        searchForCreditLV.getItems().clear();
        if (input.length() >= 1 && searchForFunctionCB.getEditor().getText().equals("")) {
            for (String creditText : searchListViewCredits.getItems()) {
                String[] splitCreditText = creditText.split(":");
                String creditName = splitCreditText[0];
                String creditFunction = splitCreditText[1].trim();
                if (creditName.length() >= input.length()
                        && !searchForCreditLV.getItems().contains(input)
                        && creditName.substring(0,input.length()).equalsIgnoreCase(input)) {
                    searchForCreditLV.getItems().add(creditName + ": " + creditFunction);
                } else if (creditName.contains(" ")) {
                    String[] splitName = creditName.split(" ");
                    StringBuilder combinedName;
                    for (int i = 1; i < splitName.length; i++) {
                        combinedName = new StringBuilder(splitName[i]);
                        if (i != splitName.length-1) {
                            for (int j = i+1; j < splitName.length; j++) {
                                combinedName.append(" ").append(splitName[j]);
                            }
                        }
                        if (combinedName.length() >= input.length()
                                && combinedName.substring(0,input.length()).equalsIgnoreCase(input)) {
                            searchForCreditLV.getItems().add(creditName + ": " + creditFunction);
                        }
                    }
                }
            }
        } else if (input.length() >= 1){
            for (String creditText : searchListViewCredits.getItems()) {
                String[] splitCreditText = creditText.split(":");
                String creditName = splitCreditText[0];
                String creditFunction = splitCreditText[1].trim();
                if (creditName.length() >= input.length()
                        && !searchForCreditLV.getItems().contains(input)
                        && creditName.substring(0,input.length()).equalsIgnoreCase(input)
                        && creditFunction.equals(searchForFunctionCB.getEditor().getText().trim())) {
                    searchForCreditLV.getItems().add(creditName + ": " + creditFunction);
                } else if (creditName.contains(" ")) {
                    String[] splitName = creditName.split(" ");
                    StringBuilder combinedName;
                    for (int i = 1; i < splitName.length; i++) {
                        combinedName = new StringBuilder(splitName[i]);
                        if (i != splitName.length-1) {
                            for (int j = i+1; j < splitName.length; j++) {
                                combinedName.append(" ").append(splitName[j]);
                            }
                        }
                        if (combinedName.length() >= input.length()
                                && combinedName.substring(0,input.length()).equalsIgnoreCase(input)
                                && creditFunction.equals(searchForFunctionCB.getEditor().getText().trim())) {
                            searchForCreditLV.getItems().add(creditName + ": " + creditFunction);
                        }
                    }
                }
            }
        }
    }
    @FXML
    void selectAndHideMatchingCredit() {
        searchListViewCredits.getSelectionModel().select(searchForCreditLV.getSelectionModel().getSelectedItem());
        searchForCreditCB.getEditor().clear();
        searchForCreditLV.setVisible(false);
    }

    @FXML
    void showMatchingFunctions() {
        searchForFunctionLV.setVisible(true);
        String input = searchForFunctionCB.getEditor().getText();
        searchForFunctionLV.getItems().clear();
        if (input.length() >= 1) {
            for (Credit.Function function : Facade.getInstance().getFunctions()) {
                String functionName = function.role;
                if (functionName.length() >= input.length()
                        && !searchForFunctionLV.getItems().contains(input)
                        && functionName.substring(0,input.length()).equalsIgnoreCase(input)) {
                    searchForFunctionLV.getItems().add(functionName);
                } else if (functionName.contains(" ")) {
                    String[] splitName = functionName.split(" ");
                    StringBuilder combinedName;
                    for (int i = 1; i < splitName.length; i++) {
                        combinedName = new StringBuilder(splitName[i]);
                        if (i != splitName.length-1) {
                            for (int j = i+1; j < splitName.length; j++) {
                                combinedName.append(" ").append(splitName[j]);
                            }
                        }
                        if (combinedName.length() >= input.length()
                                && combinedName.substring(0,input.length()).equalsIgnoreCase(input)) {
                            searchForFunctionLV.getItems().add(functionName);
                        }
                    }
                }
            }
        }
    }
    @FXML
    void selectAndHideMatchingFunction() {
        searchForFunctionCB.setValue(searchForFunctionLV.getSelectionModel().getSelectedItem());

        searchForFunctionLV.setVisible(false);
    }

    @FXML
    void hideMatchingSearchResults() {
        searchForTVSLV.setVisible(false);
        searchForProgramLV.setVisible(false);
        searchForCreditLV.setVisible(false);
        searchForFunctionLV.setVisible(false);
    }

    private TVSeries getSelectedTvSeriesFromComboBox() {
        return Facade.getInstance().getTvSeriesList().get(searchSeriesCombo.getSelectionModel().getSelectedIndex());
    }

    private Program getSelectedProgramFromListView() {
        //Choose the String-item from the listview instead of the index
        String viewString = searchListView.getSelectionModel().getSelectedItem();
        //Split the string to get the UUID
        String[] viewStringArray = viewString.split(":");

        //Use the getProgramFromCreatedBy method from the facade to get the program from the UUID of the creator. The trim after the string is to get rid of whitespace
        return Facade.getInstance().getProgramFromCreatedBy(viewStringArray[0], LoginHandler.getInstance().getUserFromUsername(viewStringArray[2].trim()).getUuid());
    }

    private Credit getSelectedCreditFromListView() {
        return getSelectedProgramFromListView().getCredits().get(searchListViewCredits.getSelectionModel().getSelectedIndex());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchProgramCombo.getItems().add(transmission);
        searchProgramCombo.getItems().add(tvSeries);
        for (Credit.Function function : Facade.getInstance().getFunctions()) {
            searchForFunctionCB.getItems().add(function.role);
        }
    }
}