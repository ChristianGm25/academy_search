package co.empathy.academy.search.Controller;

import co.empathy.academy.search.Model.User;
import co.empathy.academy.search.Service.UserService;
import jakarta.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/list")
    public ResponseEntity list(){
        String ret = userService.list();
        if(!(ret.isEmpty())){
            String s = "List obtained: \n" + ret + "\n";
            return new ResponseEntity<>(s, HttpStatus.OK);
        }
        if(ret.isEmpty()){
            return new ResponseEntity<>("There are no users\n", HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Bad Request\n", HttpStatus.BAD_REQUEST);
        }
    }

    /*
    Way of use:
    curl -i -H "Content-Type: application/json" -d '{"id":1,"name":"Name", "email":"insert@email.com"}' -X POST "http://localhost:8080/insert"
     */
    @RequestMapping(path="/insert", method=RequestMethod.POST)
    public ResponseEntity<String> insert(@RequestBody User user){
        HttpStatus ret = userService.insert(user);
        if(ret.is2xxSuccessful()) {
            return new ResponseEntity<>("User successfully added\n", ret);
        }
        else{
            return new ResponseEntity<>("Bad Request\n", ret);
        }
    }

    @RequestMapping(path="/delete/{id}",method={RequestMethod.DELETE, RequestMethod.GET})
    public ResponseEntity delete(@PathVariable(value="id") int id){
        HttpStatus ret = userService.delete(id);
        if(ret.value()==200){
            return new ResponseEntity<>("User removed\n", HttpStatus.OK);
        }
        if(ret.value()==204){
            return new ResponseEntity<>("User not found\n", HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Bad Request\n", ret);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity users(@PathVariable int id){
        String ret = userService.user(id);
        if(!(ret.isEmpty())){
            String s = "User found: " + ret;
            return new ResponseEntity<>(s, HttpStatus.OK);
        }
        else if (ret.equals("")) {
            return new ResponseEntity<>("User not found\n", HttpStatus.NO_CONTENT);
        }
        else{
            return new ResponseEntity<>("Bad Request\n", HttpStatus.BAD_REQUEST);
        }
    }

    /*
    Way of use:
    curl -i -H "Content-Type: application/json" -d '{"id":3,"name":"Name", "email":"name@email.com"}' -X POST "http://localhost:8080/update/1"
     */
    @RequestMapping(path="/update/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity update(@PathVariable(value="id") int id, @RequestBody User user){
        HttpStatus ret = userService.update(id, user);
        if(ret.value()==200){
            return new ResponseEntity<>("User updated\n", ret);
        }
        if(ret.value()==201){
            return new ResponseEntity<>("User created\n", ret);
        }
        else{
            return new ResponseEntity<>("Bad Request\n", ret);
        }
    }
}
