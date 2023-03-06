package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.User;
import org.elasticsearch.client.Response;

public interface UserEngine {
    Response insert(User u);
    Response delete(int id);
    Response update(int id, User u);
    Response list();
}
