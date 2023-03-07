package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.ResponseModel;
import co.empathy.academy.search.Model.User;
import org.elasticsearch.client.Response;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
@Service
public class UserEngineImpl implements  UserEngine{

    ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();
    @Override
    public ResponseModel insert(User u) {
        if (u == null){
            return new ResponseModel(400,"That user is not valid");
        }
        users.put(u.getId(), u);
        return new ResponseModel(200,"The user was succesfully added");
    }

    @Override
    public ResponseModel delete(int id) {
        if (users.contains(id)){
            users.remove(id);
            return new ResponseModel(200,"The user has been removed");
        }
        else{

            return new ResponseModel(400,"The user was not found");
        }
    }

    @Override
    public ResponseModel update(int id, User u) {

        if (users.contains(id)){
            users.put(id, u);
            return new ResponseModel(200,"The user has been updated");
        }
        else{
            users.put(id, u);
            return new ResponseModel(202,"The user did not exist, but it was added");
        }
    }

    @Override
    public ResponseModel list() {
        if (users.isEmpty()){
            return new ResponseModel(400,"There are no users");
        }
        String ret = "";
        for (int key: users.keySet()){
            ret += "ID: " + key + " Name: " + users.get(key).getName() + "" +
                    " Email: " + users.get(key).getEmail() + "\n";
        }
        return new ResponseModel(200,ret);
    }

    @Override
    public ResponseModel user(int id) {
        User user = users.get(id);
        if(user==null){
            return new ResponseModel(400, "User not found");
        }
        String ret = "ID: " + id + " Name: " + user.getName() + "" +
                " Email: " + user.getEmail() + "\n";
        return new ResponseModel(200, ret);
    }
}
