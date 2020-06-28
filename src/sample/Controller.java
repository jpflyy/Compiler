package sample;

import compiler.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;


/**
 * @author Administrator
 */
public class Controller {
    @FXML
    private Button lxaBtn;
    @FXML
    private TextArea inputArea = new TextArea();
    @FXML
    private TextArea message = new TextArea();
    @FXML
    private TextArea code = new TextArea();

    @FXML
    private TableView<Word> token = new TableView<Word>();
    @FXML
    TableColumn<Word, String> value = new TableColumn<Word, String>();
    @FXML
    TableColumn<Word, String> type = new TableColumn<Word, String>();
    @FXML
    TableColumn<Word, String> addr = new TableColumn<Word, String>();

    @FXML
    private TableView<Symbol> symbolTable = new TableView<Symbol>();
    @FXML
    TableColumn<Symbol, String> index = new TableColumn<Symbol, String>();
    @FXML
    TableColumn<Symbol, String> variable = new TableColumn<Symbol, String>();
    @FXML
    TableColumn<Symbol, String> kind = new TableColumn<Symbol, String>();
    @FXML
    TableColumn<Symbol, String> offset = new TableColumn<Symbol, String>();

    @FXML
    public void onLxaBtnClick(){
        Scancer.input = inputArea.getText();
        Scancer.lexicalAnalysis();
        message.setText("");
        code.clear();
        for(String mes: Scancer.message){
            message.appendText(mes+"\n");
        }
        /*TableView显示词法分析结果*/
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        type.setCellValueFactory(new PropertyValueFactory<>("typeCode"));
        addr.setCellValueFactory(new PropertyValueFactory<>("addr"));
        ObservableList<Word> data = FXCollections.observableArrayList(Word.token);
        token.setItems(data);
        /*符号表*/
        index.setCellValueFactory(new PropertyValueFactory<>("index"));
        variable.setCellValueFactory(new PropertyValueFactory<>("name"));
        kind.setCellValueFactory(new PropertyValueFactory<>("type"));
        offset.setCellValueFactory(new PropertyValueFactory<>("addr"));
        ObservableList<Symbol> data1 = FXCollections.observableArrayList(Symbol.symbolsTable);
        symbolTable.setItems(data1);
    }

    @FXML
    public void onParseBtnClick(){
        Scancer.input = inputArea.getText();
        Scancer.lexicalAnalysis();
        /*TableView显示词法分析结果*/
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        type.setCellValueFactory(new PropertyValueFactory<>("typeCode"));
        addr.setCellValueFactory(new PropertyValueFactory<>("addr"));
        ObservableList<Word> data = FXCollections.observableArrayList(Word.token);
        token.setItems(data);
        /*符号表*/
        index.setCellValueFactory(new PropertyValueFactory<>("index"));
        variable.setCellValueFactory(new PropertyValueFactory<>("name"));
        kind.setCellValueFactory(new PropertyValueFactory<>("type"));
        offset.setCellValueFactory(new PropertyValueFactory<>("addr"));
        ObservableList<Symbol> data1 = FXCollections.observableArrayList(Symbol.symbolsTable);
        symbolTable.setItems(data1);

        LrParser.slrParser();
        message.setText("");
        code.clear();
        for(String mes: LrParser.reduce){
            message.appendText(mes+"\n");
        }
    }

    @FXML
    public void onAnalysisBtnClick(){
        Scancer.input = inputArea.getText();
        Scancer.lexicalAnalysis();
        /*TableView显示词法分析结果*/
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        type.setCellValueFactory(new PropertyValueFactory<>("typeCode"));
        addr.setCellValueFactory(new PropertyValueFactory<>("addr"));
        ObservableList<Word> data = FXCollections.observableArrayList(Word.token);
        token.setItems(data);
        /*符号表*/
        index.setCellValueFactory(new PropertyValueFactory<>("index"));
        variable.setCellValueFactory(new PropertyValueFactory<>("name"));
        kind.setCellValueFactory(new PropertyValueFactory<>("type"));
        offset.setCellValueFactory(new PropertyValueFactory<>("addr"));
        ObservableList<Symbol> data1 = FXCollections.observableArrayList(Symbol.symbolsTable);
        symbolTable.setItems(data1);

        SemanticAnalysis.midCode();

        message.setText("");
        for(String mes: SemanticAnalysis.errorMes){
            message.appendText(mes+"\n");
        }
        /*三地址码表*/
        code.setText("");
        if(SemanticAnalysis.errorMes.size() == 1){
            for(String mes: Tac.printTac()){
                code.appendText(mes+"\n");
            }
        }
        /*符号表*/
        index.setCellValueFactory(new PropertyValueFactory<>("index"));
        variable.setCellValueFactory(new PropertyValueFactory<>("name"));
        kind.setCellValueFactory(new PropertyValueFactory<>("type"));
        offset.setCellValueFactory(new PropertyValueFactory<>("addr"));
        ObservableList<Symbol> data2 = FXCollections.observableArrayList(Symbol.symbolsTable);
        symbolTable.setItems(data2);
    }
}
