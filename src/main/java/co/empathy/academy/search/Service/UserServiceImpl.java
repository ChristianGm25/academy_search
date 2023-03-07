package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.ResponseModel;
import co.empathy.academy.search.Model.User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {


    UserEngineImpl userEngine;

    public UserServiceImpl(UserEngineImpl userEngine) {
        this.userEngine = userEngine;
    }
    @Override
    public ResponseModel insert(User u) {
        if (u != null){
            return userEngine.insert(u);
        }
        return new ResponseModel(400,"Error");
    }



    @Override
    public ResponseModel delete(int id) {
        if (id > 0){
            return userEngine.delete(id);
        }
        return new ResponseModel(400,"Error");
    }

    @Override
    public ResponseModel update(int id, User u) {
        if ((id>0) && (u != null)){
            return userEngine.update(id, u);
        }
        return new ResponseModel(400, "Invalid id or user");
    }

    @Override
    public ResponseModel list() {
        return userEngine.list();
    }

    @Override
    public ResponseModel user(int id) {

        return userEngine.user(id);
    }

}
