package ai.sahaj.nano.entities;

import java.io.Serializable;

public class EmployeeRequest implements Serializable {

    public String name;
    public String city;

    public String getName(){
        return this.name;
    }

    public String getCity(){
        return this.city;
    }

}
