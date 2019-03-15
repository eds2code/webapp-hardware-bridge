package tigerworkshop.webapphardwarebridge.config.controller;

import com.fazecast.jSerialComm.SerialPort;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tigerworkshop.webapphardwarebridge.config.models.ObservableStringPair;
import tigerworkshop.webapphardwarebridge.services.SettingService;

import javax.print.PrintService;
import java.awt.*;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SettingController implements Initializable {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @FXML
    private TableView<ObservableStringPair> tableSerial;
    @FXML
    private TableColumn<ObservableStringPair, String> columnSerialType;
    @FXML
    private TableColumn<ObservableStringPair, String> columnPort;

    @FXML
    private TableView<ObservableStringPair> tablePrinter;
    @FXML
    private TableColumn<ObservableStringPair, String> columnPrintType;
    @FXML
    private TableColumn<ObservableStringPair, String> columnPrinter;
    @FXML
    private Button buttonLog;
    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonReset;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Printer List
        ObservableList<String> printerList = FXCollections.observableArrayList();
        printerList.addAll(listPrinters());

        tablePrinter.setEditable(true);
        tablePrinter.getSelectionModel().setCellSelectionEnabled(true);

        columnPrintType.setCellValueFactory(new PropertyValueFactory<>("left"));
        columnPrinter.setCellValueFactory(new PropertyValueFactory<>("right"));
        columnPrinter.setCellFactory(ComboBoxTableCell.forTableColumn(printerList));
        columnPrinter.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ObservableStringPair, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {
                ObservableStringPair printerMapping = (ObservableStringPair) event.getTableView().getItems().get(event.getTablePosition().getRow());
                printerMapping.setRight((String) event.getNewValue());
            }
        });

        // Serial List
        ObservableList<String> serialList = FXCollections.observableArrayList();
        serialList.addAll(listSerials());

        tableSerial.setEditable(true);
        tableSerial.getSelectionModel().setCellSelectionEnabled(true);

        columnSerialType.setCellValueFactory(new PropertyValueFactory<>("left"));
        columnPort.setCellValueFactory(new PropertyValueFactory<>("right"));
        columnPort.setCellFactory(ComboBoxTableCell.forTableColumn(serialList));
        columnPort.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ObservableStringPair, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {

            }
        });

        loadValues();

        buttonLog.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Desktop.getDesktop().open(new File("log"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Save Values
        buttonSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveValues();
            }
        });

        // Reset Values
        buttonReset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loadValues();
            }
        });
    }

    private void loadValues() {
        logger.debug("loadValues");

        SettingService settingService = SettingService.getInstance();

        // Serials
        ArrayList<ObservableStringPair> serialArrayList = new ArrayList<>();
        HashMap<String, String> portHashMap = settingService.getSerials();
        for (Map.Entry<String, String> mapEntry : portHashMap.entrySet()) {
            String key = mapEntry.getKey();
            String value = mapEntry.getValue();
            serialArrayList.add(ObservableStringPair.of(key, value));
        }
        ObservableList<ObservableStringPair> serialMappingList = FXCollections.observableArrayList(serialArrayList);
        tableSerial.setItems(serialMappingList);

        // Printers
        ArrayList<ObservableStringPair> printerArrayList = new ArrayList<>();
        HashMap<String, String> printerHashMap = settingService.getPrinters();
        for (Map.Entry<String, String> mapEntry : printerHashMap.entrySet()) {
            String key = mapEntry.getKey();
            String value = mapEntry.getValue();
            printerArrayList.add(ObservableStringPair.of(key, value));
        }
        ObservableList<ObservableStringPair> printerMappingList = FXCollections.observableArrayList(printerArrayList);
        tablePrinter.setItems(printerMappingList);
    }

    private void saveValues() {
        logger.debug("saveValues");

        ObservableList<ObservableStringPair> list = tablePrinter.getItems();
        for (ObservableStringPair map : list) {
            System.out.println(map.toString());
        }
    }

    private ArrayList<String> listPrinters() {
        ArrayList<String> printerList = new ArrayList<>();
        PrintService[] printServices = PrinterJob.lookupPrintServices();
        for (PrintService printService : printServices) {
            printerList.add(printService.getName());
        }
        return printerList;
    }

    private ArrayList<String> listSerials() {
        ArrayList<String> portList = new ArrayList<>();
        for (SerialPort port : SerialPort.getCommPorts()) {
            portList.add(port.getSystemPortName());
        }
        return portList;
    }
}