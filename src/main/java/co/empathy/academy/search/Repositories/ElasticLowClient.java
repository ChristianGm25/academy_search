package co.empathy.academy.search.Repositories;

import net.minidev.json.parser.ParseException;

import java.io.IOException;


public interface ElasticLowClient {
    String search() throws IOException;

}
