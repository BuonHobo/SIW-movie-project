package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Set;

@Repository
public interface MovieRepository extends CrudRepository<Movie, Long> {

    boolean existsByTitleAndReleaseDate(String Title, Date releaseDate);

    Movie findByTitleAndReleaseDate(String Title, Date releaseDate);

    Set<Movie> findDistinctByActorsContaining(Artist a);

    Set<Movie> findDistinctByDirector(Artist a);

    @Query("SELECT avg(r.rating) from Review r where r.movie = :movie")
    Double getAverageRatingByMovie(@Param("movie") Movie movie);
}
