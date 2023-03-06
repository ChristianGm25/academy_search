package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.User;

public interface UserService {
    void insert(User u);
    void delete(int id);
    void update(int id, User u);
    String list();
}
