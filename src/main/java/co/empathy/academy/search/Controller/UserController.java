package co.empathy.academy.search.Controller;

import co.empathy.academy.search.Model.ResponseModel;
import co.empathy.academy.search.Model.User;
import co.empathy.academy.search.Service.UserService;
import jakarta.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    UserService userService;


    @GetMapping("/list")
    public ResponseEntity list(){
        ResponseModel ret = userService.list();
        return new ResponseEntity<>(ret.getMessage(), HttpStatus.OK);
    }

    /*
    Way of use:
    curl -i -H "Content-Type: application/json" -d '{"id":2,"name":"Name", "email":"name@email.com"}' -X POST "http://localhost:8080/insert"
     */
    @RequestMapping(path="/insert", method=RequestMethod.POST)
    public ResponseEntity<String> insert(@RequestBody User user){
        ResponseModel ret = userService.insert(user);
        return new ResponseEntity<>(ret.getMessage(), HttpStatus.OK);

    }

    @DeleteMapping(value="/delete/{id}")
    public ResponseEntity delete(@PathVariable int id){
        ResponseModel ret = userService.delete(id);
        return new ResponseEntity<>(ret.getMessage(), HttpStatus.OK);

    }

    @GetMapping("/users/{id}")
    public ResponseEntity users(@PathParam("id") int id){
        ResponseModel ret = userService.user(id);
        return new ResponseEntity<>(ret.getMessage(), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity update(@PathParam("id") int id, @RequestBody User user){
        ResponseModel ret = userService.update(id, user);
        return new ResponseEntity<>(ret.getMessage(), HttpStatus.OK);
    }
}
