package sample;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import jdk.nashorn.internal.ir.annotations.Ignore;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Controller {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField searchField;
    @FXML
    private Label caretLabel;
    @FXML
    private Label posLabel;
    @FXML
    private Button ReadFileBtn;
    @FXML
    private Button downBtn;
    @FXML
    private Button upBtn;
    @FXML
    private TableView<TableResultRow> resultTable;
    @FXML
    private TableColumn<Integer, String> avtomatColumn;
    @FXML
    private TableColumn<Integer, String> simpleColumn;
    @FXML
    private TableView<String[]> tableAvtomat;
    @FXML
    private CheckBox IgnoreCaseCheckBox;

    private ObservableList<TableResultRow> observableList;

    private int textAreaCaret = 0;
    private int currentSelectedPos = 0;
    private ArrayList<Integer> symbolsAvtomatPos;
    private ArrayList<Integer> symbolsSimplePos;


    @FXML
    private void initialize(){

        //resultTable.getColumns().get(0).setStyle("-fx-background-color: #ff99ff;");

        Platform.runLater(() -> textArea.requestFocus());

        resultTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectionModel<TableResultRow> selectionModel = resultTable.getSelectionModel();
                int selectedPos = selectionModel.getSelectedItem().getAvtomatResult();
                int selectedIndex = selectionModel.getSelectedIndex()+1;
                currentSelectedPos = selectedIndex;
                updatePosLabel(currentSelectedPos, resultTable.getItems().size());

                textArea.selectRange(selectedPos, selectedPos + searchField.getText().length());
            }
        });

        avtomatColumn.setCellValueFactory( new PropertyValueFactory<Integer, String>("avtomatResult"));
        simpleColumn.setCellValueFactory( new PropertyValueFactory<Integer, String>("simpleResult"));
    }




    @FXML
    private void onKeyRealeased(){
        String query;
        if(IgnoreCaseCheckBox.isSelected()) {
            query = searchField.getText().toLowerCase();
        }else{
            query = searchField.getText();
        }

        /**
         * если запрос не пустой - заполняем таблицу состояний автомата
         * если запрос пустой - чистим таблицу
         */
        if(query.length()!=0){
            ArrayList<Character> alphabetList = StringSearch.getAlphabet(query);
            alphabetList.add(0, ' ');  // столбец с пустым хедером для нумерации состояний автомата
            int[][] data = StringSearch.createAvtomat(query, alphabetList);

            String[] alphabetString = new String[alphabetList.size()];
            for(int i = 0; i<alphabetList.size(); i++){
                if(i == 0){
                    alphabetString[i] = "States";
                }else
                    alphabetString[i] = alphabetList.get(i).toString();
            }



            String[][] dataString = new String[data.length][data[0].length];
            for(int i = 0; i<data.length; i++){
                dataString[i][0] = Integer.toString(i);
                for(int j = 1; j<data[i].length; j++){
                    dataString[i][j] = Integer.toString(data[i][j]);
                }
            }

            tableAvtomat.getColumns().clear();
            fillTable(tableAvtomat, alphabetString, dataString);
        }else{
            tableAvtomat.getColumns().clear();
        }

        /**
         * работа с результатами поиска
         */
        if(IgnoreCaseCheckBox.isSelected()){
            symbolsAvtomatPos = StringSearch.avtomatSearchIgnoreCase(textArea.getText(), query);
            symbolsSimplePos = StringSearch.simpleSearchIgnoreCase(textArea.getText(), query);
        } else {
            symbolsAvtomatPos = StringSearch.avtomatSearch(textArea.getText(), query);
            symbolsSimplePos = StringSearch.simpleSearch(textArea.getText(), query);
        }

        if(symbolsAvtomatPos.size()==0){
            upBtn.setDisable(true);
            downBtn.setDisable(true);
            updatePosLabel(0, 0);
            currentSelectedPos = 0;
            textArea.selectRange(0, 0);

            resultTable.getItems().clear();
        }else{
            upBtn.setDisable(false);

            observableList = FXCollections.observableArrayList();
            fillObservableList(observableList, symbolsAvtomatPos, symbolsSimplePos);
            resultTable.setItems(observableList);

            downBtn.setDisable(false);
            updatePosLabel(1, symbolsAvtomatPos.size());
            currentSelectedPos = 1;
            textArea.selectRange(symbolsAvtomatPos.get(0), symbolsAvtomatPos.get(0) + query.length());
        }

    }

    @FXML
    private void TextAreaonKeyRealeased(){
        int newCaret = textArea.getCaretPosition();
        caretLabel.setText(caretLabel.getText().replace(Integer.toString(textAreaCaret), Integer.toString(newCaret)));
        textAreaCaret = newCaret;

        /**
         * работа с результатами поиска
         */
        String query;

        if(IgnoreCaseCheckBox.isSelected()){
            query = searchField.getText().toLowerCase();
            symbolsAvtomatPos = StringSearch.avtomatSearchIgnoreCase(textArea.getText(), query);
            symbolsSimplePos = StringSearch.simpleSearchIgnoreCase(textArea.getText(), query);
        } else {
            query = searchField.getText();
            symbolsAvtomatPos = StringSearch.avtomatSearch(textArea.getText(), query);
            symbolsSimplePos = StringSearch.simpleSearch(textArea.getText(), query);
        }

        if(symbolsAvtomatPos.size()==0){
            upBtn.setDisable(true);
            downBtn.setDisable(true);
            updatePosLabel(0, 0);
            currentSelectedPos = 0;
            //textArea.selectRange(0, 0);

            resultTable.getItems().clear();
        }else{
            upBtn.setDisable(false);

            observableList = FXCollections.observableArrayList();
            fillObservableList(observableList, symbolsAvtomatPos, symbolsSimplePos);
            resultTable.setItems(observableList);

            downBtn.setDisable(false);
            updatePosLabel(1, symbolsAvtomatPos.size());
            currentSelectedPos = 1;
            //textArea.selectRange(symbolsAvtomatPos.get(0), symbolsAvtomatPos.get(0) + query.length());
        }
    }

    @FXML
    private void textAreaOnMouseClicked(){

        int newCaret = textArea.getCaretPosition();
        caretLabel.setText(caretLabel.getText().replace(Integer.toString(textAreaCaret), Integer.toString(newCaret)));
        textAreaCaret = newCaret;
    }

    @FXML
    private void ignoreCaseCheckBoxOnMouseClicked(){
        onKeyRealeased();
    }

    @FXML
    private void upBtnListener(){
        --currentSelectedPos;
        if(currentSelectedPos<=0){
            currentSelectedPos = symbolsAvtomatPos.size();
        }
        int currentPosition = symbolsAvtomatPos.get(currentSelectedPos-1);
        textArea.selectRange(currentPosition, currentPosition + searchField.getText().length());
        updatePosLabel(currentSelectedPos, symbolsAvtomatPos.size());

        SelectionModel model = resultTable.getSelectionModel();
        model.select(currentSelectedPos-1);
    }

    @FXML
    private void downBtnListener(){

        ++currentSelectedPos;
        if(currentSelectedPos > symbolsAvtomatPos.size()){
            currentSelectedPos = 1;
        }
        int currentPosition = symbolsAvtomatPos.get(currentSelectedPos-1);
        textArea.selectRange(currentPosition, currentPosition + searchField.getText().length());
        updatePosLabel(currentSelectedPos, symbolsAvtomatPos.size());

        SelectionModel model = resultTable.getSelectionModel();
        model.select(currentSelectedPos-1);
    }

    @FXML
    private void readFileBtnListener(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text file", "*.txt"));

        anchorPane.setDisable(true);

        File file = fileChooser.showOpenDialog(new Main().getStage());

        anchorPane.setDisable(false);

        if(file != null){
            readFile(file, textArea);
        }

    }

    private void readFile(File file, TextArea textArea){
        new Thread(() -> {
            try(FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis, "Windows-1251");
                BufferedReader bufferedReader = new BufferedReader(isr);
                Scanner sc = new Scanner(bufferedReader)){
                StringBuilder sb = new StringBuilder();
                while(sc.hasNextLine()){
                    sb.append(sc.nextLine()+"\n\r");
                }
                textArea.setText(sb.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private void fillTable(TableView<String[]> table, String[] headers, String[][] avtomatStates){
        String[][] staffArray = new String[avtomatStates.length+1][avtomatStates[0].length];
        staffArray[0] = headers;
        for(int i = 1; i<staffArray.length; i++){
            staffArray[i] = avtomatStates[i-1];
        }


        ObservableList<String[]> data = FXCollections.observableArrayList();
        data.addAll(Arrays.asList(staffArray));
        data.remove(0);//remove titles from data
        for (int i = 0; i < staffArray[0].length; i++) {
            TableColumn tc = new TableColumn(staffArray[0][i]);
            final int colNo = i;
            tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<String[], String> p) {
                    return new SimpleStringProperty((p.getValue()[colNo]));
                }
            });
            tc.setPrefWidth(90);
            tc.setStyle( "-fx-alignment: CENTER;");

            table.getColumns().add(tc);
        }
        table.setItems(data);

    }

    private void updatePosLabel(int current, int total){
        posLabel.setText(current + " of " + total);
    }


    private boolean fillObservableList(ObservableList<TableResultRow> observableList,
                                    ArrayList<Integer> avtomatResult, ArrayList<Integer> simpleResult){
        if(observableList==null || avtomatResult == null || simpleResult == null)
            return false;

        if(avtomatResult.size() != simpleResult.size())
            return false;

        for(int i = 0; i<avtomatResult.size(); i++){
            observableList.add(new TableResultRow(avtomatResult.get(i), simpleResult.get(i)));
        }

        return true;
    }

}





