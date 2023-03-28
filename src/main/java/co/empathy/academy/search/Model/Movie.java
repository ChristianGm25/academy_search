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

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    Crew crew;
    Rating rating;

    public String toString() {
        String result = "Tconst: " + this.getTconst().toString() + " Title: " + this.getPrimaryTitle().toString() +
                " RuntimeMinutes: " + Integer.toString(this.getRuntimeMinutes()) + " StartYear: " + this.getStartYear()
                + " Rating: " + this.getRating().getAverageRating();
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
