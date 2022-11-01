package no.it1901.groups2022.gr2227.timemaster.fxui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
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
    limitTextFieldToTwoNumbers(inputHour);
    limitTextFieldToTwoNumbers(inputMinutes);
  }

  private void workDayHistoryListenerSetup() {
    workdayHistoryList.getSelectionModel()
        .selectedItemProperty()
        .addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
          String selectedItem = workdayHistoryList.getSelectionModel().getSelectedItem();
          int index = workdayHistoryList.getSelectionModel().getSelectedIndex();
          System.out.println("Item selected : " + selectedItem + ", Item index : " + index);
          openWorkdayEditInterface(index);
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

  private boolean confirmationDialog(String body) {

    Alert dialog = new Alert(AlertType.CONFIRMATION);
    dialog.setContentText(body);
    
    Optional<ButtonType> result = dialog.showAndWait();
    System.out.println(result.get());
    if (result.isPresent()) {
        switch (result.get().getButtonData()) {
          case OK_DONE:
            System.out.println("Confirmed");
            return true;        
          case CANCEL_CLOSE:
            System.out.println("Cancelled");
            return false;
          default:
            displayError("The window wasn't closed properly...");
            return false;
        }
    } else {
      return false;
    }
  }

  private void warningDialog(String body) {
    Alert dialog = new Alert(AlertType.WARNING);
    dialog.setContentText(body);
    dialog.showAndWait();
  }

  private void limitTextFieldToTwoNumbers(TextField field) {
    int maxLength = 2;
    field.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, 
          String newValue) {
          if (!newValue.matches("\\d*")) {
              field.setText(newValue.replaceAll("[^\\d]", ""));
          }
          if (field.getText().length() > maxLength) {
            String s = field.getText().substring(0, maxLength);
            field.setText(s);
        }
      }
    });
  }

  private void openWorkdayEditInterface(int index) {

    // TODO: Clean code. Split into methods. Functions useful in other parts of the
    // controller should be implemented

    ButtonType okButtonType = new ButtonType("Ok", ButtonData.OK_DONE);
    ButtonType cancelButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Edit Workday");
    dialog.setHeaderText("Change workday values...");
    dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

    Label labelIn = new Label("Time in");
    DatePicker dateIn = new DatePicker(LocalDate.now());
    TextField timeInHour = new TextField();
    limitTextFieldToTwoNumbers(timeInHour);
    timeInHour.setPromptText("Hour: 0-23");
    TextField timeInMinute = new TextField();
    limitTextFieldToTwoNumbers(timeInMinute);
    timeInMinute.setPromptText("Min: 0-59");

    Label labelOut = new Label("Time Out");
    DatePicker dateOut = new DatePicker(LocalDate.now());
    TextField timeOutHour = new TextField();
    limitTextFieldToTwoNumbers(timeOutHour);
    timeOutHour.setPromptText("Hour: 0-23");
    TextField timeOutMinute = new TextField();
    limitTextFieldToTwoNumbers(timeOutMinute);
    timeOutMinute.setPromptText("Min: 0-59");
    dialog.getDialogPane().setContent(new VBox(8, labelIn, dateIn, timeInHour, timeInMinute,
        labelOut, dateOut, timeOutHour, timeOutMinute));

    // AutoFill
    LocalDateTime editingWorkdayTimeIn = timeMaster.getChosenEmployee().getWorkdays().get(index).getTimeIn();
    dateIn.setValue(LocalDate.of(editingWorkdayTimeIn.getYear(), editingWorkdayTimeIn.getMonth(),
        editingWorkdayTimeIn.getDayOfMonth()));
    timeInHour.setText(String.valueOf(editingWorkdayTimeIn.getHour()));
    timeInMinute.setText(String.valueOf(editingWorkdayTimeIn.getMinute()));

    if (timeMaster.getChosenEmployee().getWorkdays().get(index).isTimedOut()) {
      LocalDateTime editingWorkdayTimeOut = timeMaster.getChosenEmployee().getWorkdays().get(index).getTimeOut();
      dateOut.setValue(LocalDate.of(editingWorkdayTimeOut.getYear(), editingWorkdayTimeOut.getMonth(),
          editingWorkdayTimeOut.getDayOfMonth()));
      timeOutHour.setText(String.valueOf(editingWorkdayTimeOut.getHour()));
      timeOutMinute.setText(String.valueOf(editingWorkdayTimeOut.getMinute()));
    }

    try {
      Optional<ButtonType> choice = dialog.showAndWait();
      if (choice.isPresent()) {
        switch (choice.get().getButtonData()) {

          case OK_DONE:
            boolean result = confirmationDialog("Are you sure you want to change the workday to these values?");
            if (result) {
              if (!isValidMinuteInput(1)) {
                warningDialog("is not a valid input");
                openWorkdayEditInterface(index);
              }
              if (!isValidHourInput(1)) {
                openWorkdayEditInterface(index);
              }

              // DateTime in
              LocalDate date = dateIn.getValue();
              LocalTime time = LocalTime.of(Integer.parseInt(timeInHour.getText()),
                  Integer.parseInt(timeInMinute.getText()));
              LocalDateTime dateTimeIn = LocalDateTime.of(date, time);

              // DateTime out
              LocalDate date2 = dateOut.getValue();
              LocalTime time2 = LocalTime.of(Integer.parseInt(timeOutHour.getText()),
                  Integer.parseInt(timeOutMinute.getText()));
              LocalDateTime dateTimeOut = LocalDateTime.of(date2, time2);

              saveWorkdayEditChoices(index, dateTimeIn, dateTimeOut);

            } else {
              openWorkdayEditInterface(index);
            }
            break;

          case CANCEL_CLOSE:
            System.out.println("Workday editing cancelled");
            break;

          default:
            System.err.println("The window wasn't closed properly");
            break;
        }
      } else {
        System.out.println("The window wasn't closed properly");
      }
    } catch (Exception e) {
      e.printStackTrace();
      displayError(e.getMessage());
    }

  }

  private boolean isValidHourInput(int input) {
    // TODO: validate
    return true;
  }

  private boolean isValidMinuteInput(int input) {
    // TODO: validate
    return true;
  }

  private void saveWorkdayEditChoices(int index, LocalDateTime timeIn, LocalDateTime timeOut) {
    System.out.println(timeIn + " " + timeOut);
  }

}
