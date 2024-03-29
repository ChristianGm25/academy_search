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


    public IndexServiceImpl(ElasticLowClientImpl e) {
        this.elasticLowClient = e;
    }

    @Async
    @Override
    public CompletableFuture<String> indexAsync(long numMovies) {
        long fileSize = numMovies;
        int batches = (int) Math.ceil(fileSize / bulkSize);
        readHeaders();
        try {
            addRating();
        } catch (IOException e) {
            System.out.println("Error reading ratings");
        }
        for (int i = 0; i < batches; i++) {
            try {
                read();
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
     * Read bulkSize lines in basics file and builds a movie with data from all the files,
     * If the movies are not adult, add them to a list
     */
    @Override
    public void read() throws IOException {

        //Data
        int readLines = 0;
        String line;
        Movie m;
        try {
            String lineAkas = akasReader.readLine();
            String lineCrew = crewReader.readLine();
            String linePrincipals = principalsReader.readLine();

            //Read until the specified bulkSize or until there are no more lines
            while ((readLines < bulkSize) && ((line = basicsReader.readLine()) != null)) {
                m = buildMovie(line);

                //Add the appropiate rating
                setRatings(m);

                // This condition is necessary because from this point the akas file is unordered, has no proper syntax
                // and the data are episodes of series
                if (lesserID(lineAkas.split("\t")[0], "tt2643120")) {
                    while (lesserID(lineAkas.split("\t")[0], m.getTconst()) && lineAkas != null) {
                        lineAkas = akasReader.readLine();
                    }
                    while (lineAkas.split("\t")[0].equals(m.getTconst())) {
                        m.getAkas().add(addAkas(lineAkas.split("\t")));
                        lineAkas = akasReader.readLine();
                    }
                }

                //Crew reading until files get messed up (shorten times)
                if (lesserID(lineCrew.split("\t")[0], "tt2701228")) {
                    while (lesserID(lineCrew.split("\t")[0], m.getTconst()) && lineCrew != null) {
                        lineCrew = crewReader.readLine();
                    }
                    if (lineCrew.split("\t")[0].equals(m.getTconst())) {
                        m.setCrew(addCrew(lineCrew.split("\t")));
                        lineCrew = crewReader.readLine();
                    }
                }

                //Principals reading until files get messed up
                while (lesserID(linePrincipals.split("\t")[0], m.getTconst()) && linePrincipals != null) {
                    linePrincipals = principalsReader.readLine();
                }
                while (linePrincipals.split("\t")[0].equals(m.getTconst())) {
                    m.getPrincipals().add(addPrincipals(linePrincipals.split("\t")));
                    linePrincipals = principalsReader.readLine();
                }


                //skip the adult ones
                if (!m.isAdult()) {
                    this.movies.add(m);
                }
                readLines++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add the appropiate Rating object to a movie
     *
     * @param m, movie to add the rating to
     */
    public void setRatings(Movie m) {
        if (this.ratings.containsKey(m.getTconst())) {
            m.setAverageRating(this.ratings.get(m.getTconst()).getAverageRating());
            m.setNumVotes(this.ratings.get(m.getTconst()).getNumVotes());
        } else {
            m.setAverageRating(-1);
            m.setNumVotes(0);
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
        String[] split;
        Movie m = new Movie();
        split = line.split("\t");
        m.setTconst(split[0]);
        m.setTitleType(split[1]);
        m.setPrimaryTitle(split[2]);
        m.setOriginalTitle(split[3]);
        m.setAdult(getBoolean(split[4]));
        try {
            m.setStartYear(Integer.parseInt(split[5]));
        } catch (NumberFormatException e) {
            m.setStartYear(-1);
        }

        try {
            m.setEndYear(Integer.parseInt(split[6]));
        } catch (NumberFormatException e) {
            m.setEndYear(-1);
        }
        try {
            m.setRuntimeMinutes(Integer.parseInt(split[7]));
        } catch (NumberFormatException e) {
            m.setRuntimeMinutes(-1);
        }
        m.setGenres(split[8].split(","));

        m.setAkas(new LinkedList<Akas>());
        m.setCrew(new Crew());
        m.setPrincipals(new LinkedList<Principals>());
        m.setEpisodes(new LinkedList<Episode>());
        return m;
    }

    /**
     * Stores in a map all the ratings to assign properly to the basics
     *
     * @throws IOException Error handling for the readLine
     */
    private void addRating() throws IOException {

        //Prepare the objects
        Object[] split;
        double averageRating;
        int votes;
        String line;

        while ((line = ratingsReader.readLine()) != null) {
            split = line.split("\t");
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
            Rating rating = new Rating(split[0].toString(), averageRating, votes);
            this.ratings.put(rating.getTconst(), rating);
        }

    }


    /**
     * Calculates whether an id is lesser than another
     *
     * @param fileID,   tconst in any file
     * @param basicsID, tconst in basics file
     * @return true if fileID < basicsID, false otherwise
     */
    public boolean lesserID(String fileID, String basicsID) {
        int idFile = Integer.parseInt(fileID.split("t")[2]);
        int idBasics = Integer.parseInt(basicsID.split("t")[2]);
        if (idFile < idBasics) {
            return true;
        }
        return false;
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
        if((line = episodeReader.readLine()) == null){
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
            if(split[2].toString().equals("\\N")){
                episode.setSeasonNumber(-1);
            }
            episode.setSeasonNumber(Integer.parseInt(split[2].toString()));
            if(split[3].toString().equals("\\N")){
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
     * @return Akas built from the split
     * @throws IOException Error handling for the readLine
     */
    public Akas addAkas(String[] split) throws IOException {

        Akas aka = new Akas();
        //Set attributes
        aka.setTitleId(split[0]);
        try {
            aka.setOrdering(Integer.parseInt(split[1]));
        } catch (NumberFormatException e) {
            aka.setOrdering(-1);
        }
        aka.setTitle(split[2]);
        aka.setRegion(split[3]);
        aka.setLanguage(split[4]);
        aka.setTypes(split[5].split(","));
        aka.setAttributes(split[6].split(","));
        aka.setOriginalTitle(getBoolean(split[7]));
        return aka;
    }

    /**
     * @return Crew object built from the split
     * @throws IOException Error handling for the readLine
     */
    private Crew addCrew(String[] split) throws IOException {
        Crew crew = new Crew(split[0], new String[]{"No information"}, new String[]{"No information"});
        ;
        crew.setDirectors(split[1].toString().split(","));
        crew.setWriters(split[2].toString().split(","));
        return crew;
    }


    /**
     *
     * @return Principals built from the split
     * @throws IOException Error handling for the readLine
     */
    private Principals addPrincipals(String[] split) throws IOException {

        Principals principal = new Principals();
        ;
        principal.setTconst(split[0]);
        try {
            principal.setOrdering(Integer.parseInt(split[1].toString()));
        } catch (NumberFormatException e) {
            principal.setOrdering(-1);
        }
        principal.setNconst(split[2].toString());
        principal.setCategory(split[3].toString());
        principal.setJob(split[4].toString());
        principal.setCharacters(split[5].toString());
        return principal;
    }

    /**
     *
     * @param value 1 or 0 as a string
     * @return boolean value (true if 1, false if 0)
     */
    public static boolean getBoolean(String value) {
        return !value.equals("0");
    }
}
