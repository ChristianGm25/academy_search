package co.empathy.academy.search.Controller;

import co.empathy.academy.search.Model.User;
import co.empathy.academy.search.Service.UserService;
import jakarta.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    UserService userService;


    @GetMapping("/list")
    public ResponseEntity list(){
        return new ResponseEntity<>(userService.list(), HttpStatus.OK);
    }


    /*
    Way of use:
    curl -i -H "Content-Type: application/json" -d
    '{"id":2,"name":"Name", "email":"name@email.com"}'
    -X POST "http://localhost:8080/insert"
     */
    @RequestMapping(path="/insert", method=RequestMethod.POST)
    public ResponseEntity<String> insert(@RequestBody User user){
        return new ResponseEntity(userService.insert(user), HttpStatus.OK);
    }

    @DeleteMapping(value="/delete/{id}")
    public ResponseEntity delete(@PathVariable int id){
        return new ResponseEntity(userService.delete(id), HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> users(@PathParam("id") int id){
        return new ResponseEntity<>(userService.user(id), HttpStatus.OK);
    }
}
