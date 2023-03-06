package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.User;

public interface UserEngine {
    String insert(User u);
    String delete(int id);
    String update(int id, User u);
    String list();
    User user(int id);
}
