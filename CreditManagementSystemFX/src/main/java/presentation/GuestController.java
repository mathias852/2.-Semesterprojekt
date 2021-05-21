package presentation;

import domain.Facade;
import domain.LoginHandler;
import domain.credit.Credit;
import domain.program.Episode;
import domain.program.Program;
import domain.program.TVSeries;
import domain.program.Transmission;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

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

    private final String tv2Logo = "TV2";
    private final String nordiskFilmLogo = "Nordisk Film";

    private final File nordiskFilmLogoFile = new File("src/main/resources/presentation/NF-Logo.png");
    private final File tv2LogoFile = new File("src/main/resources/presentation/tv2creditslogo.png");

    private final Image nordiskFilmLogoImage = new Image(nordiskFilmLogoFile.toURI().toString());
    private final Image tv2LogoImage = new Image(tv2LogoFile.toURI().toString());

    SearchFunctionality searchFunctionality = new SearchFunctionality();

    @FXML
    void logOutAction() throws IOException{
        App.setRoot("logInPage");
    }

    @FXML
    void searchProgramComboAction() {
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
                    searchListView.getItems().add(program.getName() + "\nProgramansvarlig: "
                            + LoginHandler.getInstance().getUserFromUuid(program.getCreatedBy()).getUsername());
                }
            }

        } else if (searchProgramCombo.getSelectionModel().getSelectedItem().equals(tvSeries)){
            searchListView.getItems().clear();
            searchSeriesCombo.setDisable(false);
            searchSeasonCombo.setDisable(false);

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
        } catch (IndexOutOfBoundsException ignored){}
    }

    @FXML
    void searchSeasonComboAction() {
        try {
            searchListView.getItems().clear();

            if (searchSeriesCombo.getSelectionModel().getSelectedIndex() != -1) {
                TVSeries series = getSelectedTvSeriesFromComboBox();
                for (Episode episode : series.getSeasonMap().get(Integer.parseInt(searchSeasonCombo.getSelectionModel().getSelectedItem()))) {
                    if (episode.isApproved()) {
                        searchListView.getItems().add(episode.getName() + "\nProgramansvarlig: "
                                + LoginHandler.getInstance().getUserFromUuid(episode.getCreatedBy()).getUsername()
                                + "\nEpisode: " + episode.getEpisodeNo());
                        if (episode.getProduction().equals(tv2Logo)){
                            creditedLogoImageView.setImage(tv2LogoImage);
                        } else if (episode.getProduction().equals(nordiskFilmLogo)){
                            creditedLogoImageView.setImage(nordiskFilmLogoImage);
                        }
                    }
                }
            }
        } catch (NumberFormatException ignored) {}
    }

    @FXML
    void selectedProgramFromListView() {
        if (searchListView.getItems().size() > 0) {
            searchListViewCredits.getItems().clear();

            Program selectedProgram = getSelectedProgramFromListView();

            //Checks if program has been selected
            if (selectedProgram != null) {

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
        searchFunctionality.searchForProgramAsGuest(searchForProgramLV, searchForProgramCB,  searchListView);
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



    private TVSeries getSelectedTvSeriesFromComboBox() {
        return Facade.getInstance().getTvSeriesList().get(searchSeriesCombo.getSelectionModel().getSelectedIndex());
    }

    private Program getSelectedProgramFromListView() {
        String viewString;
        String[] viewStringArray;
        String[] createdBy;
        try {
            //Choose the String-item from the listview instead of the index
            viewString = searchListView.getSelectionModel().getSelectedItem();
            //Split the string to get the UUID
            viewStringArray = viewString.split("\n");
            createdBy = viewStringArray[1].split(":");

        } catch (NullPointerException e) {
            return null;
        }

        //Use the getProgramFromCreatedBy method from the facade to get the program from the UUID of the creator. The trim after the string is to get rid of whitespace
        return Facade.getInstance().getProgramFromCreatedBy(viewStringArray[0], LoginHandler.getInstance().getUserFromUsername(createdBy[1].trim()).getUuid());
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