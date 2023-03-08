package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
@Service
public class UserEngineImpl implements  UserEngine{

    ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();
    @Override
    public HttpStatus insert(User u) {
        users.put(u.getId(), u);
        return HttpStatus.ACCEPTED;
    }

    @Override
    public HttpStatus delete(int id) {
        if (users.containsKey(id)){
            users.remove(id);
            return HttpStatus.OK;
        }
        else{
            return HttpStatus.NO_CONTENT;
        }
    }

    @Override
    public HttpStatus update(int id, User u) {

        if (users.containsKey(id)){
            users.put(id, u);
            return HttpStatus.OK;
        }
        else{
            users.put(id, u);
            return HttpStatus.CREATED;
        }
    }

    @Override
    public String list() {
        if (users.isEmpty()){
            return "";
        }
        String ret = "";
        for (int key: users.keySet()){
            ret +="ID: " + key + " Name: " + users.get(key).getName() + "" +
                    " Email: " + users.get(key).getEmail() + "\n";
        }
        return ret;
    }

    @Override
    public String user(int id) {
        User user = users.get(id);
        if(user==null){
            return "";
        }
        String ret = "ID: " + id + " Name: " + user.getName() + "" +
                " Email: " + user.getEmail() + "\n";
        return ret;
    }
}
