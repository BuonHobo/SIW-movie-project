package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface MovieRepository extends CrudRepository<Movie,Long> {

    @Query(value = "from Movie m order by m.addedDate limit 5")
    List<Movie> getLatest3Movies();

    boolean existsByTitleAndReleaseDate(String Title, Date releaseDate);
    Movie findByTitleAndReleaseDate(String Title, Date releaseDate);

    Set<Movie> findDistinctByActorsContaining(Artist a);
    Set<Movie> findDistinctByDirector(Artist a);
}
