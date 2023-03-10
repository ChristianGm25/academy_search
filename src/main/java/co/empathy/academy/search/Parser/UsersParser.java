package co.empathy.academy.search.Parser;

import co.empathy.academy.search.Model.User;
import org.parboiled.BaseParser;
import org.springframework.web.multipart.MultipartFile;

public class UsersParser extends BaseParser {

    public UsersParser (MultipartFile file){
        super();
    }

    public User handleLine(String line){
        String[] fields = line.split(",");
        return new User(Integer.parseInt(fields[0]), fields[1].toString(), fields[2].toString());
    }

}
