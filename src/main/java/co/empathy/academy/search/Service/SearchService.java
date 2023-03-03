package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.QueryCluster;
import net.minidev.json.parser.ParseException;

import java.io.IOException;

public interface SearchService {
    String search(String query) throws IOException, ParseException, InterruptedException;
}
