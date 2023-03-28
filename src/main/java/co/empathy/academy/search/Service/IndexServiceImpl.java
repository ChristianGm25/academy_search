package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class IndexServiceImpl implements IndexService {

    private BufferedReader akasReader;
    private BufferedReader basicsReader;
    private BufferedReader crewReader;
    private BufferedReader episodeReader;
    private BufferedReader principalsReader;
    private BufferedReader ratingsReader;
    private Map<String, Movie> result = new ConcurrentHashMap<>();

    private String lastKey; //Store the last key read in the basics for the bulk


    public IndexServiceImpl(MultipartFile akas, MultipartFile basics,
                            MultipartFile crew, MultipartFile episode, MultipartFile principals,
                            MultipartFile ratings) {
        try {
            this.akasReader = new BufferedReader(new InputStreamReader(akas.getInputStream()));
            this.basicsReader = new BufferedReader(new InputStreamReader(basics.getInputStream()));
            this.crewReader = new BufferedReader(new InputStreamReader(crew.getInputStream()));
            this.episodeReader = new BufferedReader(new InputStreamReader(episode.getInputStream()));
            this.principalsReader = new BufferedReader(new InputStreamReader(principals.getInputStream()));
            this.ratingsReader = new BufferedReader(new InputStreamReader(ratings.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Movie> read(int bulkSize) {
        int readLines = 0;
        String line;
        try {
            readHeaders();
            while ((readLines < bulkSize) && ((line = basicsReader.readLine()) != "")) {
                buildMovie(line);
                readLines++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String key : this.result.keySet()) {
            Movie tempMovie = this.result.get(key);
            System.out.println(tempMovie);
            for (Akas aka : tempMovie.getAkas()) {
                System.out.println("\t" + aka.getTitle());
            }
        }

        return result;
    }


    public void readHeaders() {
        try {
            akasReader.readLine();
            basicsReader.readLine();
            crewReader.readLine();
            episodeReader.readLine();
            principalsReader.readLine();
            ratingsReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buildMovie(String line) throws IOException {
        Object[] split;
        List<Akas> akas = new LinkedList<>();

        Movie m = new Movie();
        split = line.split("\t");
        m.setTconst(split[0].toString());
        m.setTitleType(split[1].toString());
        m.setPrimaryTitle(split[2].toString());
        m.setOriginalTitle(split[3].toString());
        m.setAdult(getBoolean(split[4].toString()));
        if (split[5].toString().equals("\\N")) {
            m.setStartYear("No information");
        }
        m.setStartYear(split[5].toString());
        if (split[6].toString().equals("\\N")) {
            m.setStartYear("No information");
        }
        m.setEndYear(split[6].toString());
        try {
            m.setRuntimeMinutes(Integer.parseInt(split[7].toString()));
        } catch (NumberFormatException e) {
            m.setRuntimeMinutes(0);
        }
        m.setGenres(split[8].toString().split(","));
        m.setAkas(addAkas(m.getTconst()));
        m.setRating(addRating(m.getTconst()));
        m.setCrew(addCrew(m.getTconst()));
        m.setPrincipals(addPrincipals(m.getTconst()));
        this.result.put(m.getTconst(), m);
    }

    public List<Akas> addAkas(String tconst) throws IOException {

        Object[] split;
        Akas aka;
        List<Akas> tempAkas = new LinkedList<>();
        String id;
        String line = akasReader.readLine();

        while ((id = line.split("\t")[0]).equals(tconst)) {
            //Mark the line in case the keys do not match
            akasReader.mark(300);
            split = line.split("\t");
            aka = new Akas();

            //Set attributes
            aka.setTitleId(split[0].toString());
            try {
                aka.setOrdering(Integer.parseInt(split[1].toString()));
            } catch (NumberFormatException e) {
                aka.setOrdering(-1);
            }
            aka.setTitle(split[2].toString());
            aka.setRegion(split[3].toString());
            aka.setLanguage(split[4].toString());
            aka.setTypes(split[5].toString().split(","));
            aka.setAttributes(split[6].toString().split(","));
            aka.setOriginalTitle(getBoolean(split[7].toString()));

            tempAkas.add(aka);
            line = akasReader.readLine();
        }
        akasReader.reset();
        return tempAkas;
    }

    private Crew addCrew(String key) throws IOException {
        Crew crew;
        crewReader.mark(200);
        Object[] split = crewReader.readLine().split("\t");
        if (split[0].toString().equals(key)) {
            crew = new Crew(split[0].toString(), split[1].toString().split(","), split[2].toString().split(","));
        } else {
            crew = new Crew("id", new String[]{"No information"}, new String[]{"No information"});
        }
        return crew;
    }

    private Rating addRating(String tconst) throws IOException {
        //Prepare the objects
        Object[] split;
        Double averageRating = 0.0;
        int votes = 0;
        Rating rating;

        ratingsReader.mark(100);
        split = ratingsReader.readLine().split("\t");
        if (split[0].toString().equals(tconst)) {
            try {
                averageRating = Double.parseDouble(split[1].toString());
            } catch (NumberFormatException e) {
                averageRating = -1.0;
            }
            try {
                votes = Integer.parseInt(split[2].toString());
            } catch (NumberFormatException e) {
                votes = 0;
            }
            rating = new Rating(tconst, averageRating, votes);
        } else {
            ratingsReader.reset();
            rating = new Rating(split[0].toString(), -1, 0);
        }
        return rating;
    }

    private List<Principals> addPrincipals(String tconst) throws IOException {
        Object[] split;
        List<Principals> tempPrincipals = new LinkedList<>();
        Principals principal;

        String line = principalsReader.readLine();
        String id;
        while ((id = line.split("\t")[0]).equals(tconst)) {
            principalsReader.mark(200);
            split = line.split("\t");
            //Create new object to add
            principal = new Principals();
            try {
                principal.setOrdering(Integer.parseInt(split[1].toString()));
            } catch (NumberFormatException e) {
                principal.setOrdering(-1);
            }
            principal.setNconst(split[2].toString());
            principal.setCategory(split[3].toString());
            principal.setJob(split[4].toString());
            principal.setCharacters(split[5].toString());

            tempPrincipals.add(principal);

            line = akasReader.readLine();
        }
        principalsReader.reset();
        return tempPrincipals;
    }

    public static boolean getBoolean(String value) {
        return !value.equals("0");
    }
}
