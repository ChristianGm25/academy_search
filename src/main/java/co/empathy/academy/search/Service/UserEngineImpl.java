package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
@Service
public class UserEngineImpl implements  UserEngine{

    ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();
    @Override
    public String insert(User u) {
        if (u == null){
            return "That user is not valid";
        }
        users.put(u.getId(), u);
        return "The user was succesfully added";
    }

    @Override
    public String delete(int id) {
        if (users.contains(id)){
            users.remove(id);
            return "The user has been removed";
        }
        else{
            return "The user was not found";
        }
    }

    @Override
    public String update(int id, User u) {
        if (users.contains(id)){
            users.put(id, u);
            return "The user has been updated";
        }
        else{
            users.put(id, u);
            return "The user did not exist, but it was added";
        }
    }

    @Override
    public String list() {
        if (users.isEmpty()){
            return "There are no users";
        }
        String ret = "";
        for (int key: users.keySet()){
            ret += "ID: " + key + " Name: " + users.get(key).getName() + "" +
                    " Email: " + users.get(key).getEmail() + "\n";
        }
        return ret;
    }

    @Override
    public User user(int id) {
        return users.get(id);
    }
}
