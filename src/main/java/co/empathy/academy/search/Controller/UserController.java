package co.empathy.academy.search.Controller;

import co.empathy.academy.search.Model.User;
import co.empathy.academy.search.Service.UserServiceImpl;
import jakarta.websocket.server.PathParam;
import org.elasticsearch.client.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {
    @Autowired
    UserServiceImpl userService;
    @GetMapping("/list")
    public ResponseEntity list(){
        Response res = userService.list();
        String ret = "";
        for (int key: userService.getUsers().keySet()){
            ret += "ID: " + key + " Name: " + userService.getUsers().get(key).getName() + "" +
                    " Email: " + userService.getUsers().get(key).getEmail() + "\n";
        }
        return ret;
    }

    @GetMapping("/insert/{id,name,email}")
    public ResponseEntity insert(@PathParam("id") int id, @PathParam("name") String name, @PathParam("email") String email){
        User u = new User(id,name,email);
        userService.getUsers().put(id,u);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity delete(@PathParam("id") int id){
        userService.getUsers().remove(id);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity users(@PathParam("id") int id){
        return userService.getUsers().get(id);
    }


}
