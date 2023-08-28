package ai.sahaj.nano.controller;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ai.sahaj.nano.entities.Employee;
import ai.sahaj.nano.entities.EmployeeId;
import ai.sahaj.nano.entities.EmployeeRequest;
import ai.sahaj.nano.entities.Message;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;

@Controller
public class EmployeeController{

    Map<String, EmployeeRequest> map = new ConcurrentHashMap<>();
    public int id;

    public EmployeeController(){
        this.id = 1;
        loadMapFromFile("/home/pilli007/Documents/Projects/persistence-java/nano-employee-mgmt-pradeep007nc-java/src/main/java/ai/sahaj/nano/entities/employee_data.ser"); // Load existing data on initialization
        registerShutdownHook();
    }

    @Post("/employee")
    public HttpResponse<EmployeeId> create(@Body  EmployeeRequest request){
        String empId = generateEmpId();
        map.put(empId, request);
        return HttpResponse.ok(new EmployeeId(empId)).status(201);
    }

    @Get("/employee/{id}")
    public HttpResponse read(String id ){
        EmployeeRequest req;
        if(map.containsKey(id)){
            req = map.get(id);
            return HttpResponse.ok(new Employee(id, req.name, req.city));
        }
        return HttpResponse.ok(new Message(id)).status(404);
    }
    @Get("/employees/all")
    public HttpResponse readAll(){
        List<Employee> ans = new ArrayList<>();
        EmployeeRequest req;
        for (String str: map.keySet()){
            req = map.get(str);
            ans.add(new Employee(str, req.name, req.city));
        }

        return HttpResponse.ok(ans);
    }

    @Put("/employee/{id}")
    public HttpResponse update(String id, @Body  EmployeeRequest request){
        if (map.containsKey(id)){
            map.put(id, request);
            return HttpResponse.ok(new Employee(id, request.name, request.city)).status(201);
        }else{
            Message message = new Message(id);
            return HttpResponse.ok(message).status(404);
        }

    }

    @Delete("/employee/{id}")
    public HttpResponse delete(String id){
        if (map.containsKey(id)){
            EmployeeRequest temp = map.get(id);
            map.remove(id);
            return HttpResponse.ok(new Employee(id, temp.name, temp.city)).status(200);
        }else{
            return HttpResponse.ok(new Message(id)).status(404);
        }
    }

    public String generateEmpId(){
        return String.valueOf(this.id++);
    }

    public void saveMapToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(map);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadMapFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            map = (Map<String, EmployeeRequest>) ois.readObject();
            System.out.println("Data loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveMapToFile("/home/pilli007/Documents/Projects/persistence-java/nano-employee-mgmt-pradeep007nc-java/src/main/java/ai/sahaj/nano/entities/employee_data.ser"); // Persist data on shutdown
        }));
    }

}