package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.MovieRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Controller
public class MovieController {
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    MovieValidator movieValidator;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    ArtistRepository artistRepository;

    @GetMapping("/")
    public String homepage(Model model) {
        model.addAttribute("latestMovies", movieRepository.getLatest3Movies());
        model.addAttribute("artists",artistRepository.get5Artists());
        return "home";
    }

    @GetMapping("/createMovie")
    public String createMovie(Model model) {
        model.addAttribute("movie", new Movie());
        return "createMovie";
    }

    @PostMapping("/createMovie")
    public String addMovie(@RequestParam("files") MultipartFile[] files, @Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, Model model) {
        movieValidator.validate(movie, bindingResult);
        if (bindingResult.hasErrors()) {
            return "createMovie";
        }

        movie.setAddedDate(new Date());
        movie.setImages(new HashSet<>());

        for (MultipartFile file : files) {
            try {
                Image immagine = new Image(file.getOriginalFilename(), file.getBytes());
                String format = immagine.getFormat();
                if (!(format.equals("jpeg") || format.equals("png") || format.equals("jpg"))) {
                    bindingResult.reject("image.formatNotSupported");
                    continue;
                }
                movie.getImages().add(immagine);
            } catch (IOException ex) {
                bindingResult.reject("image.readError");
            }
        }

        if (bindingResult.hasErrors()) return "createMovie";

        movieRepository.save(movie);
        model.addAttribute("movie", movie);
        return "retrieveMovie";
    }

    @GetMapping("/retrieveMovie/{id}")
    public String retrieveMovie(@PathVariable("id") Long id, Model model) {
        Optional<Movie> movie = movieRepository.findById(id);

        if (movie.isEmpty()) {
            model.addAttribute("errorMessage", "movie.notFound");
            return homepage(model);
        }
        model.addAttribute("movie", movie.get());
        return "retrieveMovie";
    }

    @PostMapping("/addMovieImage/")
    public String addMovieImage(@RequestParam("movieId") Long id, @RequestParam("files") MultipartFile[] files, Model model) {

        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isEmpty()) {
            model.addAttribute("errorMessage", "movie.notFound");
            return homepage(model);
        }
        Movie movie = optionalMovie.get();

        for (MultipartFile file : files) {
            try {
                Image immagine = new Image(file.getOriginalFilename(), file.getBytes());
                String format = immagine.getFormat();
                if (!(format.equals("jpeg") || format.equals("png") || format.equals("jpg"))) {
                    model.addAttribute("errorMessage", "image.formatNotSupported");
                    return retrieveMovie(id, model);
                }
                movie.getImages().add(immagine);
            } catch (IOException ex) {
                model.addAttribute("errorMessage", "image.readError");
                return retrieveMovie(id, model);
            }
        }

        movieRepository.save(movie);
        model.addAttribute("movie", movie);
        return "retrieveMovie";
    }

    @GetMapping("/addMovieDirector/{movieId}")
    public String addMovieDirector(@PathVariable("movieId") Long id, Model model) {

        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isEmpty()) {
            model.addAttribute("errorMessage", "movie.notFound");
            return homepage(model);
        }
        Movie movie = optionalMovie.get();

        Set<Artist> possibleDirectors = artistRepository.findDistinctByDirectedMoviesNotContaining(movie);
        model.addAttribute("artists", possibleDirectors);
        model.addAttribute("movie", movie);
        return "selectDirectorForMovie";
    }

    @GetMapping("/addMovieDirector/{movieId}/{artistId}")
    public String addMovieDirector(@PathVariable("movieId") Long movieId, @PathVariable("artistId") Long artistId, Model model) {

        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        if (optionalMovie.isEmpty()) {
            model.addAttribute("errorMessage", "movie.notFound");
            return homepage(model);
        }
        Movie movie = optionalMovie.get();

        Optional<Artist> optionalArtist = artistRepository.findById(artistId);
        if (optionalArtist.isEmpty()) {
            model.addAttribute("errorMessage", "artist.notFound");
            return homepage(model);
        }
        Artist artist = optionalArtist.get();

        movie.setDirector(artist);
        movieRepository.save(movie);

        model.addAttribute("movie", movie);
        return "retrieveMovie";
    }

    @GetMapping("/removeMovieImage/{movieId}/{imageId}")
    public String removeMovieImage(@PathVariable("movieId") Long movieId, @PathVariable("imageId") Long imageId, Model model) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return homepage(model);
        }
        model.addAttribute("movie",movie);

        Image image= imageRepository.findById(imageId).orElse(null);
        if (image==null){
            model.addAttribute("errorMessage","image.notFound");
            return "retrieveMovie";
        }

        movie.getImages().remove(image);
        movieRepository.save(movie);
        imageRepository.delete(image);
        return "retrieveMovie";

    }

    @GetMapping("/addMovieActor/{movieId}")
    public String changeMovieActor(@PathVariable("movieId")Long movieId,Model model){
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return homepage(model);
        }
        model.addAttribute("movie",movie);

        Set<Artist> possibleArtists=artistRepository.findDistinctByActedMoviesNotContaining(movie);
        model.addAttribute("artists",possibleArtists);
        return "addMovieActor";
    }

    @GetMapping("/addMovieActor/{movieId}/{artistId}")
    public String addMovieActor(@PathVariable("movieId")Long movieId,@PathVariable("artistId")Long artistId,Model model){
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return homepage(model);
        }

        Artist artist = artistRepository.findById(artistId).orElse(null);
        if (artist == null) {
            model.addAttribute("errorMessage", "artist.notFound");
            return homepage(model);
        }

        movie.getActors().add(artist);
        movieRepository.save(movie);

        return changeMovieActor(movieId,model);
    }

    @GetMapping("/removeMovieActor/{movieId}/{artistId}")
    public String removeMovieActor(@PathVariable("movieId")Long movieId,@PathVariable("artistId")Long artistId,Model model){
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return homepage(model);
        }

        Artist artist = artistRepository.findById(artistId).orElse(null);
        if (artist == null) {
            model.addAttribute("errorMessage", "artist.notFound");
            return homepage(model);
        }

        movie.getActors().remove(artist);
        movieRepository.save(movie);

        return changeMovieActor(movieId,model);
    }

    @GetMapping("/deleteMovie/{movieId}")
    public String deleteMovie(@PathVariable("movieId") Long movieId, Model model){
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return homepage(model);
        }

        movieRepository.delete(movie);
        model.addAttribute("errorMessage","movie.deleted");
        return homepage(model);
    }
}
