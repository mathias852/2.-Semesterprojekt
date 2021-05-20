package presentation;

import domain.Facade;
import domain.LoginHandler;
import domain.program.Program;
import domain.program.TVSeries;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import java.util.ArrayList;

public class SearchFunctionality {
    /**
     * Search for TVSeries
     */
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

    /**
     * Search for credits
     */
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

    /**
     * Search for program as guest
     */
    void searchForProgramAsGuest(ListView<String> searchForProgramLV, ComboBox<String> searchForProgramCB, ListView<String> searchListView) {
        searchForProgramLV.setVisible(true);
        String input = searchForProgramCB.getEditor().getText();
        searchForProgramLV.getItems().clear();
        if (input.length() >= 1) {
            ArrayList<Program> programs = new ArrayList<>();
            for (String programText : searchListView.getItems()) {
                String[] splitProgramText = programText.split(":");
                Program program = Facade.getInstance().getProgramFromCreatedBy(splitProgramText[0],
                        LoginHandler.getInstance().getUserFromUsername(splitProgramText[2].trim()).getUuid());
                programs.add(program);
            }
            for (Program program : programs) {
                String createdByName = LoginHandler.getInstance().getUserFromUuid(program.getCreatedBy()).getUsername();
                if (program.getName().length() >= input.length()
                        && !searchForProgramLV.getItems().contains(input)
                        && program.getName().substring(0,input.length()).equalsIgnoreCase(input)) {
                    searchForProgramLV.getItems().add(program.getName() + ": Programansvarlig: " + createdByName);
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
                            searchForProgramLV.getItems().add(program.getName() + ": Programansvarlig: " + createdByName);
                        }
                    }
                }
            }
        }
    }


















}
