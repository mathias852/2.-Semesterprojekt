package presentation;

import domain.Facade;
import domain.LoginHandler;
import domain.credit.Credit;
import domain.program.Episode;
import domain.program.Program;
import domain.program.TVSeries;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class SearchFunctionality {
    //Search for TVSeries
    void searchForTVSeries(ListView<String> searchForTVSLV, ComboBox<String> searchSeriesCombo) {
        searchForTVSLV.setVisible(true);
        String input = searchSeriesCombo.getEditor().getText();
        searchForTVSLV.getItems().clear();
        if (input.length() >= 1) {
            for (TVSeries tvSeries : Facade.getInstance().getTvSeriesList()) {
                if (tvSeries.getName().length() >= input.length()
                        && !searchForTVSLV.getItems().contains(input)
                        && tvSeries.getName().substring(0, input.length()).equalsIgnoreCase(input)) {
                    searchForTVSLV.getItems().add(tvSeries.getName());
                } else if (tvSeries.getName().contains(" ")) {
                    String[] splitName = tvSeries.getName().split(" ");
                    StringBuilder combinedName;
                    for (int i = 1; i < splitName.length; i++) {
                        combinedName = new StringBuilder(splitName[i]);
                        if (i != splitName.length - 1) {
                            for (int j = i + 1; j < splitName.length; j++) {
                                combinedName.append(" ").append(splitName[j]);
                            }
                        }
                        if (combinedName.length() >= input.length()
                                && combinedName.substring(0, input.length()).equalsIgnoreCase(input)) {
                            searchForTVSLV.getItems().add(tvSeries.getName());
                        }
                    }
                }
            }
        }
    }
    //Select the clicked TVSeries and hide the search result
    void selectAndHideTVSeries(ComboBox<String> searchSeasonCombo, ListView<String> searchForTVSLV, ComboBox<String> searchSeriesCombo) {
        searchSeasonCombo.getItems().clear();
        if (searchForTVSLV.getSelectionModel().getSelectedItem() != null) {
            String[] tvSeries = searchForTVSLV.getSelectionModel().getSelectedItem().split(";");
            searchSeriesCombo.setValue(tvSeries[0]);
        }
        searchForTVSLV.setVisible(false);
        searchSeasonCombo.show();
    }
    //Search for programs as producer or sysadmin
    void searchForProgram(ListView<String> searchForProgramLV, ComboBox<String> searchForProgramCB, ListView<String> searchListView) {
        searchForProgramLV.setVisible(true);
        String input = searchForProgramCB.getEditor().getText();
        searchForProgramLV.getItems().clear();
        if (input.length() >= 1) {
            ArrayList<Program> programs = new ArrayList<>();
            for (String programText : searchListView.getItems()) {
                String[] splitProgramText = programText.split("\n");
                String[] programIDText = splitProgramText[1].split(":");
                Program program = Facade.getInstance().getProgramFromUuid( UUID.fromString(programIDText[1].trim()));
                programs.add(program);
            }
            for (Program program : programs) {
                String createdByName = LoginHandler.getInstance().getUserFromUuid(program.getCreatedBy()).getUsername();
                if (program.getName().length() >= input.length()
                        && !searchForProgramLV.getItems().contains(input)
                        && program.getName().substring(0,input.length()).equalsIgnoreCase(input)) {
                    if (program instanceof Episode) {
                        searchForProgramLV.getItems().add(program.getName()
                                + "\nID: " + program.getUuid()
                                + "\nEpisode: " + ((Episode) program).getEpisodeNo()
                                + "\nCreated by: " + createdByName);
                    } else {
                        searchForProgramLV.getItems().add(program.getName()
                                + "\nID: " + program.getUuid()
                                + "\nCreated by: " + createdByName);
                    }
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

                            if (program instanceof Episode) {
                                searchForProgramLV.getItems().add(program.getName()
                                        + "\nID: " + program.getUuid()
                                        + "\nEpisode: " + ((Episode) program).getEpisodeNo()
                                        + "\nCreated by: " + createdByName);
                            } else {
                                searchForProgramLV.getItems().add(program.getName()
                                        + "\nID: " + program.getUuid()
                                        + "\nCreated by: " + createdByName);
                            }
                        }
                    }
                }
            }
        }
    }
    //Search for program as guest
    void searchForProgramAsGuest(ListView<String> searchForProgramLV, ComboBox<String> searchForProgramCB, ListView<String> searchListView) {
        searchForProgramLV.setVisible(true);
        String input = searchForProgramCB.getEditor().getText();
        searchForProgramLV.getItems().clear();
        if (input.length() >= 1) {
            ArrayList<Program> programs = new ArrayList<>();
            for (String programText : searchListView.getItems()) {
                String[] splitProgramText = programText.split("\n");
                String creatorID = splitProgramText[1].substring(18);
                Program program = Facade.getInstance().getProgramFromCreatedBy(splitProgramText[0],
                        LoginHandler.getInstance().getUserFromUsername(creatorID).getUuid());
                programs.add(program);
            }
            for (Program program : programs) {
                String createdByName = LoginHandler.getInstance().getUserFromUuid(program.getCreatedBy()).getUsername();
                if (program.getName().length() >= input.length()
                        && !searchForProgramLV.getItems().contains(input)
                        && program.getName().substring(0,input.length()).equalsIgnoreCase(input)) {
                    searchForProgramLV.getItems().add(program.getName() + "\nProgramansvarlig: " + createdByName);
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
                            searchForProgramLV.getItems().add(program.getName() + "\nProgramansvarlig: " + createdByName);
                        }
                    }
                }
            }
        }
    }
    //Search for credits
    void searchForCredits(ListView<String> searchForCreditLV, ComboBox<String> searchForCreditCB, ComboBox<String> searchForFunctionCB, ListView<String> searchListViewCredits) {
        searchForCreditLV.setVisible(true);
        String input = searchForCreditCB.getEditor().getText();
        searchForCreditLV.getItems().clear();
        //Searching for credits without a specified function filter
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
            //Searching for credits with a specified function filter
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
    //Select clicked credit and hide search results
    void selectAndHideCredits(ListView<String> searchListViewCredits, ListView<String> searchForCreditLV, ComboBox<String> searchForCreditCB) {
        searchListViewCredits.getSelectionModel().select(searchForCreditLV.getSelectionModel().getSelectedItem());
        searchForCreditCB.getEditor().clear();
        searchForCreditLV.setVisible(false);
    }

    //Search for functions
    void searchForFunctions(ListView<String> searchForFunctionLV, ComboBox<String> searchForFunctionCB) {
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
    //Select clicked function and hide search results
    void selectAndHideFunctions(ListView<String> searchForFunctionLV, ComboBox<String> searchForFunctionCB) {
        searchForFunctionCB.setValue(searchForFunctionLV.getSelectionModel().getSelectedItem());
        searchForFunctionLV.setVisible(false);
    }
    //Hides all listviews displaying search results
    void hideSearchResults(ListView<String> searchForTVSLV, ListView<String> searchForProgramLV, ListView<String> searchForCreditLV, ListView<String> searchForFunctionLV) {
        ArrayList<Node> searchResults = new ArrayList<>(Arrays.asList(searchForTVSLV, searchForProgramLV, searchForCreditLV, searchForFunctionLV));
        searchResults.forEach(node -> node.setVisible(false));
    }
}
