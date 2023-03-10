package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

public interface UserEngine {
    HttpStatus insert(User u);
    HttpStatus delete(int id);
    HttpStatus update(User u);
    String list();
    String user(int id);

    HttpStatus upload(MultipartFile file);
}
