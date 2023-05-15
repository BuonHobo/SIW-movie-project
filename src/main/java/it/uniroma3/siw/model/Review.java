package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"movie", "author"})})
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Min(0)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @Column(length = 4000)
    @NotBlank
    private String body;

    @ManyToOne
    @JoinColumn(nullable = false, name = "author")
    private User author;

    @ManyToOne
    @JoinColumn(nullable = false, name = "movie")
    private Movie movie;

    public Review(User author, Movie movie) {
        this.author = author;
        this.movie = movie;
    }

    public Review() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Review review = (Review) o;

        if (!author.equals(review.author)) return false;
        return movie.equals(review.movie);
    }

    @Override
    public int hashCode() {
        int result = author.hashCode();
        result = 31 * result + movie.hashCode();
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
