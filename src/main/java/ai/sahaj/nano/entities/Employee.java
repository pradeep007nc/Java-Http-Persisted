package ai.sahaj.nano.entities;

public class Employee {

    public Employee(){}

    public Employee(String employeeId, String name, String city){
        this.employeeId = employeeId;
        this.name = name;
        this.city = city;
    }
    
    public String  employeeId;
    public String name;
    public String city;
}
