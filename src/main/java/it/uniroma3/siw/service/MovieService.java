package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.MovieRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service

public class MovieService {

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    ArtistService artistService;
    @Autowired
    ImageService imageService;

    @Transactional
    public Movie findById(Long id) throws Exception {
        Movie movie = movieRepository.findById(id).orElse(null);
        if (movie == null) {
            throw new Exception("movie.notFound");
        }
        return movie;
    }

    @Transactional
    public Double getRatingByMovieId(Long id) throws Exception {
        return movieRepository.getAverageRatingByMovie(findById(id));
    }

    @Transactional
    public void addDirectorIdToMovieId(Long artistId, Long movieId) throws Exception {
        Movie movie = findById(movieId);
        Artist artist = artistService.findById(artistId);

        movie.setDirector(artist);
        movieRepository.save(movie);
    }

    @Transactional
    public void removeMovieIdImage(Long movieId, Long imageId) throws Exception {
        Movie movie = findById(movieId);
        Image image = imageService.findById(imageId);

        movie.getImages().remove(image);
        movieRepository.save(movie);
        imageService.delete(image);
    }

    @Transactional
    public void addActorIdToMovieId(Long artistId, Long movieId) throws Exception {
        Artist actor = artistService.findById(artistId);
        Movie movie = findById(movieId);

        movie.getActors().add(actor);
        movieRepository.save(movie);
    }

    @Transactional
    public void removeActorIdFromMovieId(Long artistId, Long movieId) throws Exception {
        Artist actor = artistService.findById(artistId);
        Movie movie = findById(movieId);

        movie.getActors().remove(actor);
        movieRepository.save(movie);
    }

    @Transactional
    public void deleteMovieId(Long movieId) {
        movieRepository.deleteById(movieId);
    }

    @Transactional
    public void deleteDirectorFromMovieId(long movieId) throws Exception {
        Movie movie=findById(movieId);
        movie.setDirector(null);
        movieRepository.save(movie);
    }

    @Transactional
    public Iterable<Movie> findAll() {
        return movieRepository.findAll();
    }

    @Transactional
    public void save(Movie movie) {
        movieRepository.save(movie);
    }

    @Transactional
    public void addImagesToMovieId(Long id, Set<Image> images) throws Exception {
        Movie movie = findById(id);
        movie.getImages().addAll(images);
        movieRepository.save(movie);
    }
}
