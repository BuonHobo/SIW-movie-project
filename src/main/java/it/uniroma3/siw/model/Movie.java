package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Year;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"title", "year"})})
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String title;

    @Column(nullable = false)
    @PastOrPresent
    private Year year;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Admin addedBy;

    @OneToMany(mappedBy = "movie")
    private Set<Review> reviews;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Artist director;

    @ManyToMany
    private Set<Artist> actors;

    @OneToMany
    private Set<Image> images;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        if (!title.equals(movie.title)) return false;
        return year.equals(movie.year);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + year.hashCode();
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public Admin getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(Admin addedBy) {
        this.addedBy = addedBy;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public Artist getDirector() {
        return director;
    }

    public void setDirector(Artist director) {
        this.director = director;
    }

    public Set<Artist> getActors() {
        return actors;
    }

    public void setActors(Set<Artist> actors) {
        this.actors = actors;
    }

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }
}
