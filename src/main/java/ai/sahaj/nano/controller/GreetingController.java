package ai.sahaj.nano.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller
public class GreetingController {
  @Get("/greeting")
  public HttpResponse<String>  greet(){ 
    return HttpResponse.ok("Hello world!");
  }
  
}
