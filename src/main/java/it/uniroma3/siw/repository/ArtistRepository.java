package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ArtistRepository extends CrudRepository<Artist, Long> {
    Set<Artist> findDistinctByActedMoviesContaining(Movie m);

    Set<Artist> findDistinctByDirectedMoviesContaining(Movie m);

    Set<Artist> findDistinctByActedMoviesNotContaining(Movie notActed);

    Set<Artist> findDistinctByDirectedMoviesNotContaining(Movie notDirected);

}
