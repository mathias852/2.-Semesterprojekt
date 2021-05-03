package presentation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.UUID;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class GuestController implements Initializable {

    @FXML
    private ComboBox<String> searchSeriesCombo, searchProgramCombo, searchSeasonCombo;

    @FXML
    private ListView<String> searchListView, searchListViewCredits, searchForTVSLV;

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

    private final Facade facade = new Facade();

    @FXML
    void logOutAction(ActionEvent e) throws IOException{
        App.setRoot("primary");
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
            searchSeriesCombo.getSelectionModel().clearSelection();
            searchSeasonCombo.getSelectionModel().clearSelection();
            searchSeriesCombo.setPromptText("Choose TV-series");
            searchSeasonCombo.setPromptText("Choose season");

            searchListView.getItems().clear();
            for (Program program : facade.getPrograms()) {
                if (program instanceof Transmission && program.isApproved()) {
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
            if (series.getSeasonMap() != null) {
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
                        searchListView.getItems().add(episode.getName() + ": " + episode.getUuid());
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
    void selectedProgramFromListView(MouseEvent event) {
        searchListViewCredits.getItems().clear();

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
            for (TVSeries tvSeries : facade.getTvSeriesList()) {
                if (tvSeries.getName().length() >= input.length()
                        && !searchForTVSLV.getItems().contains(input)
                        && tvSeries.getName().substring(0,input.length()).equalsIgnoreCase(input)) {
                    searchForTVSLV.getItems().add(tvSeries.getName() + ";" + tvSeries.getUuid());
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
    void hideMatchingTVS() {
        searchForTVSLV.setVisible(false);
    }

    private TVSeries getSelectedTvSeriesFromComboBox() {
        return facade.getTvSeriesList().get(searchSeriesCombo.getSelectionModel().getSelectedIndex());
    }

    private Program getSelectedProgramFromListView() {
        //Choose the String-item from the listview instead of the index
        String viewString = searchListView.getSelectionModel().getSelectedItem();
        //Split the string to get the UUID
        String[] viewStringArray = viewString.split(":");
        //Use the getProgramFromUuid method from the facade to get the program from the string. The trim after the string is to get rid of whitespace
        return facade.getProgramFromUuid(UUID.fromString(viewStringArray[1].trim()));
    }

    private Credit getSelectedCreditFromListView() {
        return getSelectedProgramFromListView().getCredits().get(searchListViewCredits.getSelectionModel().getSelectedIndex());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        facade.importFromTxt();

        searchProgramCombo.getItems().add(transmission);
        searchProgramCombo.getItems().add(tvSeries);
    }
}