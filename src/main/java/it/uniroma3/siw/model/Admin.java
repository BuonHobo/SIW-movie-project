package it.uniroma3.siw.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Admin extends RegisteredUser {

    @OneToMany(mappedBy = "addedBy")
    private List<Movie> addedMovies;


    public List<Movie> getAddedMovies() {
        return addedMovies;
    }

    public void setAddedMovies(List<Movie> addedMovies) {
        this.addedMovies = addedMovies;
    }
}
