package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {


    UserEngineImpl userEngine;

    public UserServiceImpl(UserEngineImpl userEngine) {
        this.userEngine = userEngine;
    }
    @Override
    public HttpStatus insert(User u) {
        if (u != null){
            return userEngine.insert(u);
        }
        return HttpStatus.BAD_REQUEST;
    }



    @Override
    public HttpStatus delete(int id) {
        if (id > 0){
            return userEngine.delete(id);
        }
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public HttpStatus update(int id, User u) {
        if ((id>0) && (u != null)){
            return userEngine.update(id, u);
        }
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String list() {
        return userEngine.list();
    }

    @Override
    public String user(int id) {

        return userEngine.user(id);
    }

}
