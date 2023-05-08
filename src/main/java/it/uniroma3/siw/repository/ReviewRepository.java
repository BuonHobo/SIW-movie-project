package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends CrudRepository<Review, Long> {

    List<Review> findAllByMovie(Movie movie);

    Optional<Review> findByAuthorAndMovie(User Author, Movie movie);
    @Query("SELECT r FROM Review r WHERE r.movie = :movie AND r.author != :author")
    List<Review> findByMovieAndNotByAuthor(@Param("movie") Movie movie, @Param("author") User author);

    void deleteAllByMovie(Movie movie);
}
