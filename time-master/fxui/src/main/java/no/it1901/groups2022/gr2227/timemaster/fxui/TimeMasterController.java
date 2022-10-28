package no.it1901.groups2022.gr2227.timemaster.fxui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import no.it1901.groups2022.gr2227.timemaster.core.TimeMaster;

public class TimeMasterController {
  
  private TimeMaster timeMaster;
  private ObservableList<String> observableEmployeeList;
  private ObservableList<String> observableWorkdayList;

  
  @FXML private Button registerTimeButton;
  @FXML private Button autoRegisterTimeButton;
  @FXML private DatePicker chooseDateButton;
  @FXML private TextField inputHour;
  @FXML private TextField inputMinutes; 
  @FXML private TextField newEmployeeName;
  @FXML private VBox autoCheckInOutBox;
  @FXML private VBox manualCheckInOutBox;
  @FXML private Circle statusIndicator;
  @FXML private Text statusText;
  @FXML private Text clockInInfo;
  @FXML private Text historyEmployeeName;
  @FXML private ListView<String> workdayHistoryList;
  @FXML private ListView<String> chooseEmployeeListView;
  
  
  @FXML private void initialize() {
    this.chooseDateButton.setValue(LocalDate.now());
    workDayHistoryListenerSetup();
    chooseEmployeeListenerSetup();
  }

  private void workDayHistoryListenerSetup() {
    workdayHistoryList.getSelectionModel()
        .selectedItemProperty()
        .addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
          String selectedItem = workdayHistoryList.getSelectionModel().getSelectedItem();
          int index = workdayHistoryList.getSelectionModel().getSelectedIndex();

          System.out.println("Item selected : " + selectedItem + ", Item index : " + index);
        });

    observableWorkdayList = FXCollections.observableArrayList();
    workdayHistoryList.setItems(observableWorkdayList);
  }

  private void chooseEmployeeListenerSetup() {
    chooseEmployeeListView.getSelectionModel()
        .selectedItemProperty()
        .addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
          String selectedItem = chooseEmployeeListView.getSelectionModel().getSelectedItem();
          int index = chooseEmployeeListView.getSelectionModel().getSelectedIndex();
          setChosenEmployee(index);

          System.out.println("Employee selected : " + selectedItem + ", Item index : " + index);
        });

    observableEmployeeList = FXCollections.observableArrayList();
    chooseEmployeeListView.setItems(observableEmployeeList);
  }

  
  public void setupJsonParser(String fileName) {
    this.timeMaster = new TimeMaster(fileName);
    timeMaster.readEmployees();
    this.updateEmployeeMenu();
  }
  
  
  @FXML private void handleRegisterTime() {
    //TODO: Inputvalidation
    LocalDate date = chooseDateButton.getValue();
    LocalTime time = LocalTime.of(Integer.parseInt(this.inputHour.getText()),
                     Integer.parseInt(this.inputMinutes.getText()));
    LocalDateTime dateTime = LocalDateTime.of(date, time);
    try {
      timeMaster.clockEmployeeInOut(dateTime);
    
      this.clearTimeInputs();
      updateDisplay();
    } catch (IllegalStateException e) {
      displayError(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      displayError(e.getMessage());
    }
  }
  
  @FXML private void autoClockInOut() {
    try {
      timeMaster.autoClockEmployeeInOut();
      updateDisplay();
    } catch (IllegalStateException e) {
      displayError(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      displayError(e.getMessage());
    }
  }

  private void updateDisplay() {
    setTimeRegisterInputs();
    setEmployeeStatus();
  }
  
  private void setTimeRegisterInputs() {
    if (!timeMaster.employeeIsSet()) {
      autoCheckInOutBox.setDisable(true);
      manualCheckInOutBox.setDisable(true);
    } else {
      autoCheckInOutBox.setDisable(false);
      manualCheckInOutBox.setDisable(false);
    }
  }
  
  private void setEmployeeStatus() {
    setStatusIndicator();
    setStatusText();
    setTimeRegisterButtons();
    setClockInInfoLabel();
    showWorkdayHistory();
    setHistoryEmployeeName();
  }

  public void setHistoryEmployeeName() {
    this.historyEmployeeName.setText(timeMaster.getChosenEmployee().getName());
  }
  
  private void setStatusIndicator() {
    if (timeMaster.getChosenEmployee().isAtWork()) { 
      statusIndicator.setFill(Color.GREEN);
    } else {
      statusIndicator.setFill(Color.GRAY);
    }
  }
  
  private void setStatusText() {
    if (timeMaster.getChosenEmployee().isAtWork()) {
      statusText.setText("Active");
    } else {
      statusText.setText("Off");
    }
  }
  
  private void setTimeRegisterButtons() {
    if (timeMaster.getChosenEmployee().isAtWork()) {
      registerTimeButton.setText("Check out");
      autoRegisterTimeButton.setText("Check out");
    } else {
      registerTimeButton.setText("Check in");
      autoRegisterTimeButton.setText("Check in");
    }
  }
  
  private void setClockInInfoLabel() {
    if (timeMaster.getChosenEmployee().isAtWork()) {
      clockInInfo.setText("Clocked in at: " + timeMaster.getChosenEmployee().getLatestClockIn());
    } else {
      clockInInfo.setText(null);
    }
  }
  
  private void clearTimeInputs() {
    this.inputHour.clear();
    this.inputMinutes.clear();
  }
  
  private void updateEmployeeMenu() {
    List<String> employeeList = timeMaster.getEmployees()
    .stream()
    .map(employee -> employee.getName())
    .toList();

    observableEmployeeList.setAll(employeeList);
  }
  
  private void setChosenEmployee(int index) {
    try {
      timeMaster.setChosenEmployee(index);
      updateDisplay();
    } catch (Exception e) {
      e.printStackTrace();
      displayError(e.getMessage());
    }
  }
  
  
  @FXML private void handleCreateEmployee() {
    try {
      timeMaster.createEmployee(newEmployeeName.getText());
      newEmployeeName.clear();
      updateEmployeeMenu();
    } catch (IllegalArgumentException e) {
      displayError(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      displayError(e.getMessage());
    }

  }
  
  private void displayError(String errorMessage) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText("The program ecountered a problem!");
    alert.setContentText(errorMessage);
    alert.showAndWait();
  }

  private void showWorkdayHistory() {
    if(!timeMaster.employeeIsSet()) {
      this.emptyWorkdayHistory();
      System.out.println("Not doing things");
      return;
    }
    List<String> workdayList = timeMaster.getEmployeeWorkdayHistory()
    .stream()
    .map(workday -> workday.toString())
    .toList();
    
    System.out.println(workdayList);
    observableWorkdayList.setAll(workdayList);
    System.out.println(observableWorkdayList);
  }

  private void emptyWorkdayHistory() {
    workdayHistoryList.setItems(null);
  }

}
