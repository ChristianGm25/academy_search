package co.empathy.academy.search.Controller;

import co.empathy.academy.search.Model.User;
import co.empathy.academy.search.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Operation(summary = "Retrieve the full list of users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List successfully retrieved"),
            @ApiResponse(responseCode = "202", description = "Retrieved empty list"),
            @ApiResponse(responseCode = "400", description = "Error"),
    })
    @GetMapping("/list")
    public ResponseEntity list(){
        String ret = userService.list();
        if(!(ret.isEmpty())){
            String s = "List obtained: \n" + ret + "\n";
            return new ResponseEntity<>(s, HttpStatus.OK);
        }
        if(ret.isEmpty()){
            return new ResponseEntity<>("There are no users\n", HttpStatus.NO_CONTENT);
        }
        else{
            return new ResponseEntity<>("Bad Request\n", HttpStatus.BAD_REQUEST);
        }
    }

    /*
    Way of use:
    curl -i -H "Content-Type: application/json" -d '{"id":1,"name":"Name", "email":"insert@email.com"}' -X POST "http://localhost:8080/insert"
     */
    @Operation(summary = "Insert a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully created"),
            @ApiResponse(responseCode = "400", description = "Error"),
    })
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

    @Operation(summary = "Delete a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully deleted"),
            @ApiResponse(responseCode = "204", description = "User was not found"),
            @ApiResponse(responseCode = "400", description = "Error"),
    })
    @RequestMapping(path="/delete/{id}",method={RequestMethod.DELETE, RequestMethod.GET})
    public ResponseEntity delete(@PathVariable(value="id") int id){
        HttpStatus ret = userService.delete(id);
        if(ret.value()==200){
            return new ResponseEntity<>("User removed\n", HttpStatus.OK);
        }
        if(ret.value()==204){
            return new ResponseEntity<>("User not found\n", HttpStatus.NO_CONTENT);
        }
        else{
            return new ResponseEntity<>("Bad Request\n", ret);
        }
    }

    @Operation(summary = "Retrieve information about a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully retrieved"),
            @ApiResponse(responseCode = "204", description = "User was not found"),
            @ApiResponse(responseCode = "400", description = "Error"),
    })
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
    curl -i -H "Content-Type: application/json" -d '{"id":3,"name":"Carlos", "email":"name@email.com"}' -X POST "http://localhost:8080/update"
     */
    @Operation(summary = "Update a user or insert it")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated"),
            @ApiResponse(responseCode = "202", description = "User successfully created"),
            @ApiResponse(responseCode = "400", description = "Error"),
    })
    @RequestMapping(path="/update", method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity update(@RequestBody User user){
        HttpStatus ret = userService.update(user);
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

    @PostMapping(path="/upload")
    public ResponseEntity upload(@RequestBody MultipartFile file){
        HttpStatus ret = userService.upload(file);
        return new ResponseEntity<>("File accepted", HttpStatus.OK);
    }
}
