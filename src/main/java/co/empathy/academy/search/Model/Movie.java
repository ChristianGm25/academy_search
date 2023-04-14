package co.empathy.academy.search.Model;

import java.util.List;

public class Movie extends Basics {

    List<Akas> akas;
    List<Principals> principals;

    List<Episode> episodes;

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public List<Akas> getAkas() {
        return akas;
    }

    public void setAkas(List<Akas> akas) {
        this.akas = akas;
    }

    public List<Principals> getPrincipals() {
        return principals;
    }

    public void setPrincipals(List<Principals> principals) {
        this.principals = principals;
    }

    public Crew getCrew() {
        return crew;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
    }

    double averageRating;
    int numVotes;

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(int numVotes) {
        this.numVotes = numVotes;
    }

    Crew crew;


    public String toString() {
        String result = "Tconst: " + this.getTconst().toString() + " Title: " + this.getPrimaryTitle().toString() +
                " RuntimeMinutes: " + Integer.toString(this.getRuntimeMinutes()) + " StartYear: " + this.getStartYear()
                + " Rating: " + this.getAverageRating();
        if (this.getGenres() != null) {
            result += " Genres: [";
            for (String genre : this.getGenres()) {
                result += genre + " ";
            }
            result += "]";
        }
        return result;
    }

}
