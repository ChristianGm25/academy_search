package co.empathy.academy.search.Controller;

import co.empathy.academy.search.Model.User;
import co.empathy.academy.search.Service.UserService;
import jakarta.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    UserService userService;
    @GetMapping("/list")
    public ResponseEntity list(){
        return new ResponseEntity<>(userService.list(), HttpStatus.OK);
    }

    @GetMapping("/insert")
    public ResponseEntity insert(@RequestParam(value = "id") int id, @RequestParam(value="name") String name, @RequestParam(value="email") String email){
        User u = new User(id,name,email);
        return new ResponseEntity(userService.insert(u), HttpStatus.OK);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity delete(@PathParam("id") int id){
        return new ResponseEntity(userService.delete(id), HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> users(@PathParam("id") int id){
        return new ResponseEntity<>(userService.user(id), HttpStatus.OK);
    }
}
