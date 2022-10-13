package timeMaster.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class TimeMaster {
  
  private Employee chosenEmployee;
  // private Workday chosenWorkday;
  private Path saveDirPath;
  private TimeMasterJsonParser jsonParser;
  private ArrayList<Employee> employees;
  
  public TimeMaster() {
    this.saveDirPath = Paths.get(System.getProperty("user.dir"), "../core/timeMasterSaveFiles");
    this.jsonParser = new TimeMasterJsonParser(saveDirPath, "employees.json");
  }
  
  public LocalDate getCurrentDate() { 
    return LocalDate.now(); 
  }

  public LocalTime getCurrentTime() { 
    return LocalTime.now(); 
  }
  
  public void setChosenEmployee(int index) throws Exception {
    this.chosenEmployee = this.employees.get(index);
  }
  
  public Employee getChosenEmployee() {
    return this.chosenEmployee;
  }
  
  public ArrayList<Employee> getEmployees() { 
    return new ArrayList<>(this.employees); 
  }
  
  public void createEmployee(String name) throws IllegalArgumentException {
    // TODO: Validate name with Regex. Maybe split into first and sur name
    if (name.equals("")) {
      throw new IllegalArgumentException("Input required, please enter name");
    }
    this.employees.add(new Employee(name));
    this.saveEmployees();
  }
  
  public void saveEmployees() {
    this.jsonParser.write(this.employees);
  }
  
  public void readEmployees() {
    this.employees = this.jsonParser.read();
  }
  
  // If the employee is clocked in the Workday will be finished with the specified timestamp.
  // Returns true if the employee is at work after successfull execution.
  public boolean clockEmployeeInOut(LocalDate dateInput, LocalTime timeInput) throws IllegalStateException {
    if (this.chosenEmployee == null) {
      throw new IllegalStateException("No employee is selected");
    }
    LocalDate date = dateInput;
    LocalTime time = timeInput;
    
    if (!this.getChosenEmployee().isAtWork()) {
      this.getChosenEmployee().checkIn(date, time);
    } else { 
      this.getChosenEmployee().checkOut(time);
    }
    this.saveEmployees();
    return this.getChosenEmployee().isAtWork();
  }
  
  // If the employee is clocked in the Workday will be finished with the specified timestamp.
  // Returns true if the employee is at work after successfull execution.
  public boolean autoClockEmployeeInOut() throws IllegalStateException {
    if (this.chosenEmployee == null) {
      throw new IllegalStateException("No employee is selected");
    }
    LocalDate date = this.getCurrentDate();
    LocalTime time = this.getCurrentTime();
    
    if (!this.getChosenEmployee().isAtWork()) { 
      this.getChosenEmployee().checkIn(date, time);
    } else { 
      this.getChosenEmployee().checkOut(time);
    }
    this.saveEmployees();
    return this.getChosenEmployee().isAtWork();
  }
}
