package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Service
public class ArtistService {

    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    ImageService imageRepository;
    @Autowired
    MovieRepository movieRepository;

    @Transactional
    public Artist findById(Long artistId) throws Exception {
        Artist artist = artistRepository.findById(artistId).orElse(null);
        if (artist == null) {
            throw new Exception("artist.notFound");
        }
        return artist;
    }

    @Transactional
    public Set<Artist> findDistinctByActedMoviesNotContaining(Movie movie) {
        return artistRepository.findDistinctByActedMoviesNotContaining(movie);
    }

    @Transactional
    public Set<Artist> findDistinctByDirectedMoviesNotContaining(Movie movie) {
        return artistRepository.findDistinctByDirectedMoviesNotContaining(movie);
    }

    @Transactional
    public void save(Artist artist) {
        artistRepository.save(artist);
    }

    @Transactional
    public void changeArtistIdImage(Long artistId, Image image) throws Exception {
        Artist artist = findById(artistId);
        Image oldPic = artist.getPicture();
        artist.setPicture(null);
        imageRepository.delete(oldPic);
        artist.setPicture(image);
        artistRepository.save(artist);
    }

    @Transactional
    public void deleteId(Long artistId) throws Exception {
        Artist artist = findById(artistId);
        for (Movie movie : artist.getDirectedMovies()) {
            movie.setDirector(null);
            movieRepository.save(movie);
        }
        artistRepository.delete(artist);
    }

    @Transactional
    public Iterable<Artist> findAll() {
        return artistRepository.findAll();
    }

    @Transactional
    public void setArtistIdDeath(Long artistId, Date deathDate) throws Exception {
        Artist artist = findById(artistId);
        artist.setDeathDate(deathDate);
        artistRepository.save(artist);
    }
}
