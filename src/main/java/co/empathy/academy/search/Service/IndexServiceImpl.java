package co.empathy.academy.search.Service;

import co.empathy.academy.search.Model.*;
import co.empathy.academy.search.Repositories.ElasticLowClientImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class IndexServiceImpl implements IndexService {

    private final int bulkSize = 50000;
    private BufferedReader akasReader;
    private BufferedReader basicsReader;
    private BufferedReader crewReader;
    private BufferedReader episodeReader;
    private BufferedReader principalsReader;
    private BufferedReader ratingsReader;

    private final ElasticLowClientImpl elasticLowClient;
    private List<Movie> movies = new LinkedList<>();
    private Map<String, Rating> ratings = new HashMap<>();
    ;
    private Map<String, List<Akas>> akas = new HashMap<>();


    public IndexServiceImpl(ElasticLowClientImpl e) {
        this.elasticLowClient = e;
    }

    @Async
    @Override
    public CompletableFuture<String> indexAsync(long numMovies, long numAkas) {
        long fileSize = numMovies;
        int batches = (int) Math.ceil(fileSize / bulkSize);

        //Skip the first line
        readHeaders();
        try {
            addRatings();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            addAkas(numAkas);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Beginning movies reading");
        for (int i = 0; i < batches; i++) {
            read();
            //Maybe we can replace this with JSON
            try {
                elasticLowClient.indexMovies(this.movies);
            } catch (IOException e) {
                System.out.print("Error indexing");
            }
            this.movies.clear();
        }
        return CompletableFuture.completedFuture("Finished indexing");
    }

    public void indexCreation() throws IOException {
        elasticLowClient.indexCreation();
    }

    public void indexDeletion() throws IOException {
        elasticLowClient.indexDeletion();
    }

    @Override
    public void setReaders(MultipartFile akas, MultipartFile basics, MultipartFile crew, MultipartFile episode, MultipartFile principals, MultipartFile ratings) {
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


    /**
     *
     * @return  Map containing bulkSize movies
     */
    @Override
    public void read() {

        int readLines = 0;
        String line;
        Movie m;
        try {
            while ((readLines < bulkSize) && ((line = basicsReader.readLine()) != null)) {

                //We build the movie anyway so that we manage properly the lines in every fil
                m = buildMovie(line);
                //skip the adult ones
                if (!(m.isAdult())) {
                    this.movies.add(m);
                }
                readLines++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the first line in every file, so we skip the headers
     */
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

    /**
     *
     * @param line Line read from the buffer
     * @throws IOException Error handling for the readLine
     */
    public Movie buildMovie(String line) throws IOException {
        Object[] split;

        Movie m = new Movie();
        split = line.split("\t");
        m.setTconst(split[0].toString());
        m.setTitleType(split[1].toString());
        m.setPrimaryTitle(split[2].toString());
        m.setOriginalTitle(split[3].toString());
        m.setAdult(getBoolean(split[4].toString()));
        try {
            m.setStartYear(Integer.parseInt(split[5].toString()));
        } catch (NumberFormatException e) {
            m.setStartYear(-1);
        }

        try {
            m.setEndYear(Integer.parseInt(split[6].toString()));
        } catch (NumberFormatException e) {
            m.setEndYear(-1);
        }
        try {
            m.setRuntimeMinutes(Integer.parseInt(split[7].toString()));
        } catch (NumberFormatException e) {
            m.setRuntimeMinutes(-1);
        }
        m.setGenres(split[8].toString().split(","));
        m.setCrew(addCrew(m.getTconst()));
        m.setPrincipals(addPrincipals(m.getTconst()));
        m.setEpisodes(addEpisodes(m.getTconst()));

        //Add the rating if existing, -1 if not
        if (this.ratings.containsKey(m.getTconst())) {
            m.setRating(this.ratings.get(m.getTconst()));
        } else {
            m.setRating(new Rating(m.getTconst(), -1, 0));
        }

        //Add the akas
        if (this.akas.containsKey(m.getTconst())) {
            m.setAkas(this.akas.get(m.getTconst()));
        } else {
            m.setAkas(new LinkedList<>());
        }

        return m;
    }


    /**
     * Puts the lists of akas in a map to be accessed by the main function
     *
     * @return List of akas objects associated to the key
     * @throws IOException Error handling for the readLine
     */
    public void addAkas(long numAkas) throws IOException {
        Object[] split;
        Akas aka;
        List<Akas> tempAkas = new LinkedList<>();
        String line = akasReader.readLine();
        String prevKey = "tt0000001";
        while ((line != null) && (!(line.split("\t")[0].equals("tt0300559")))) {
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
            prevKey = split[0].toString();
            tempAkas.add(aka);

            line = akasReader.readLine();
            if (!(line.split("\t")[0].equals(prevKey))) {
                this.akas.put(prevKey, tempAkas);
                tempAkas = new LinkedList<>();
            }
        }
    }


    /**
     * Creates a map with all the ratings because they might be in
     * different order than the other files
     *
     * @throws IOException Error handling for the readLine
     */
    private void addRatings() throws IOException {
        //Prepare the objects
        Object[] split;
        String key;
        double averageRating;
        int votes;
        String line;

        while ((line = ratingsReader.readLine()) != null) {
            split = line.split("\t");
            key = split[0].toString();
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
            this.ratings.put(key, new Rating(key, averageRating, votes));
        }
    }

    /**
     * @param tconst Key associated to the series/movie
     * @return List of episodes associated to the key
     * @throws IOException Error handling for the readLine
     */
    private List<Episode> addEpisodes(String tconst) throws IOException {
        Object[] split;
        Episode episode;
        List<Episode> tempEpisodes = new LinkedList<>();
        String id;
        String line;
        if ((line = episodeReader.readLine()) == null) {
            return tempEpisodes;
        }
        episodeReader.mark(300);
        while ((line != null) && (id = line.split("\t")[1]).equals(tconst)) {
            //Mark the line in case the keys do not match
            episodeReader.mark(300);
            split = line.split("\t");
            episode = new Episode();

            //Set attributes
            episode.setTconst(split[0].toString());
            episode.setParentTconst(id);
            if (split[2].toString().equals("\\N")) {
                episode.setSeasonNumber(-1);
            }
            episode.setSeasonNumber(Integer.parseInt(split[2].toString()));
            if (split[3].toString().equals("\\N")) {
                episode.setEpisodeNumber(-1);
            }
            episode.setSeasonNumber(Integer.parseInt(split[3].toString()));
            tempEpisodes.add(episode);
            line = episodeReader.readLine();
        }
        episodeReader.reset();
        return tempEpisodes;
    }
    /**
     *
     * @param tconst Key to identify the movie
     * @return List of the principals associated to the key
     * @throws IOException Error handling for the readLine
     */
    private List<Principals> addPrincipals(String tconst) throws IOException {
        Object[] split;
        List<Principals> tempPrincipals = new LinkedList<>();
        Principals principal;

        String line = principalsReader.readLine();
        String id;
        principalsReader.mark(200);
        while ((id = line.split("\t")[0]).equals(tconst) && !(line.equals(""))) {
            principalsReader.mark(200);
            split = line.split("\t");
            //Create new object to add
            principal = new Principals();
            principal.setTconst(id);
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

    /**
     * @param key Key to identify the movie
     * @return Crew object associated to the key
     * @throws IOException Error handling for the readLine
     */
    private Crew addCrew(String key) throws IOException {
        Crew crew = new Crew("id", new String[]{"No information"}, new String[]{"No information"});
        ;
        String line;
        crewReader.mark(200);
        if ((line = crewReader.readLine()) != null) {
            return crew;
        }
        Object[] split = line.split("\t");
        if (split[0].toString().equals(key)) {
            crew.setTconst(split[0].toString());
            crew.setDirectors(split[1].toString().split(","));
            crew.setWriters(split[2].toString().split(","));
        } else {
            crewReader.reset();
        }
        return crew;
    }

    /**
     * @param value 1 or 0 as a string
     * @return boolean value (true if 1, false if 0)
     */
    public static boolean getBoolean(String value) {
        return !value.equals("0");
    }
}
