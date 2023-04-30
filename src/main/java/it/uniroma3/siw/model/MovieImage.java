package it.uniroma3.siw.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class MovieImage extends Image{
    @ManyToOne(cascade = jakarta.persistence.CascadeType.ALL)
    @JoinColumn
    private Movie movie;

    public MovieImage(String name, byte[] data,Movie m){
        super(name,data);
        movie=m;
    }

    public MovieImage(){
        super();
    }
    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
