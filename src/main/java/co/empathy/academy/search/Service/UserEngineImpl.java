package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class UserEngineImpl implements  UserEngine{

    ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();
    @Override
    public HttpStatus insert(User u) {
        users.put(u.getId(), u);
        return HttpStatus.ACCEPTED;
    }

    @Override
    public HttpStatus delete(int id) {
        if (users.containsKey(id)){
            users.remove(id);
            return HttpStatus.OK;
        }
        else{
            return HttpStatus.NO_CONTENT;
        }
    }

    @Override
    public HttpStatus update(User u) {

        if (users.containsKey(u.getId())){
            users.put(u.getId(), u);
            return HttpStatus.OK;
        }
        else{
            users.put(u.getId(), u);
            return HttpStatus.CREATED;
        }
    }

    @Override
    public String list() {
        if (users.isEmpty()){
            return "";
        }
        String ret = "";
        for (int key: users.keySet()){
            ret +="ID: " + key + " Name: " + users.get(key).getName() + "" +
                    " Email: " + users.get(key).getEmail() + "\n";
        }
        return ret;
    }

    @Override
    public String user(int id) {
        User user = users.get(id);
        if(user==null){
            return "";
        }
        String ret = "ID: " + id + " Name: " + user.getName() + "" +
                " Email: " + user.getEmail() + "\n";
        return ret;
    }

    @Override
    public HttpStatus upload(MultipartFile file){
        try {
            /*
            InputStream inputStream = file.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            JSONObject jsonElement = gson.fromJson(reader, JSONObject.class); // read contents of file to JSON object
            System.out.print(jsonElement.get("users"));

             */
            ObjectMapper obj = new ObjectMapper();
            List<User> users = obj.readValue(file.getBytes(), new TypeReference<List<User>>() {});
            for (User u: users){
                //User user = new User(u.getAsNumber("id").intValue(), u.getAsString("name"),u.getAsString("email"));
                insert(u);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return HttpStatus.OK;
    }
}
