package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ArtistService {

    @Autowired
    ArtistRepository artistRepository;

    @Transactional
    public Artist findById(Long artistId) throws Exception {
        Artist artist = artistRepository.findById(artistId).orElse(null);
        if (artist == null) {
            throw new Exception("artist.notFound");
        }
        return artist;
    }

    @Transactional
    public Set<Artist> findDistinctByActedMoviesNotContaining(Movie movie){
        return artistRepository.findDistinctByActedMoviesNotContaining(movie);
    }

    @Transactional
    public Set<Artist> findDistinctByDirectedMoviesNotContaining(Movie movie){
        return artistRepository.findDistinctByDirectedMoviesNotContaining(movie);
    }
}
