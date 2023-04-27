package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"movie","author"})})
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
    private String body;

    @ManyToOne
    @JoinColumn(nullable = false,name = "author")
    private RegisteredUser author;

    @ManyToOne
    @JoinColumn(nullable = false,name = "movie")
    private Movie movie;

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
}
