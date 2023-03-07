package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.ResponseModel;
import co.empathy.academy.search.Model.User;

public interface UserEngine {
    ResponseModel insert(User u);
    ResponseModel delete(int id);
    ResponseModel update(int id, User u);
    ResponseModel list();
    ResponseModel user(int id);
}
