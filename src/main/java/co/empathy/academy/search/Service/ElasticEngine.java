package co.empathy.academy.search.Service;

import net.minidev.json.parser.ParseException;

import java.io.IOException;

public interface ElasticEngine {
    String search() throws IOException, ParseException;
}
