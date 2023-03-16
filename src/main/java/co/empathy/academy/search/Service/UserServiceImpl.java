package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.ConcurrentHashMap;

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
    public HttpStatus update(User u) {
        if (u != null){
            return userEngine.update(u);
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

    @Override
    public ConcurrentHashMap<Integer,String> upload(MultipartFile file) {
        return userEngine.upload(file);
    }

    @Override
    public void uploadAsync(MultipartFile file) {
        userEngine.uploadAsync(file);
    }

}
