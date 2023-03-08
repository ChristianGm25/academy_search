package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.User;
import org.springframework.http.HttpStatus;

public interface UserService {
    HttpStatus insert(User u);
    HttpStatus delete(int id);
    HttpStatus update(int id, User u);
    String list();
    String user(int id);
}
