package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
@Service
public class UserServiceImpl implements UserService {

    ConcurrentHashMap<Integer, User> users;
    UserEngine userEngine;

    public ConcurrentHashMap<Integer, User> getUsers() {
        return users;
    }

    public void setUsers(ConcurrentHashMap<Integer, User> users) {
        this.users = users;
    }

    @Override
    public void insert(User u) {
        if (u != null){
            userEngine.insert(u);
        }
    }

    @Override
    public void delete(int id) {
        if (id > 0){
            userEngine.delete(id);
        }
    }

    @Override
    public void update(int id, User u) {
        if ((id>0) && (u != null)){
            userEngine.update(id, u);
        }
    }

    @Override
    public String list() {
        return userEngine.list();
    }
}
