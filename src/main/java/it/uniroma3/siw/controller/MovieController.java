package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.MovieRepository;

import javax.validation.Valid;

import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.service.CredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;


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
    @Autowired
    CredentialsService credentialsService;
    @Autowired
    private AuthenticationController authenticationController;
    @Autowired
    ReviewRepository reviewRepository;

    @GetMapping("/admin/movie/add")
    public String createMovie(Model model) {
        model.addAttribute("movie", new Movie());
        return "admin/movie-add";
    }

    @PostMapping("/admin/movie/add")
    public String addMovie(@RequestParam("files") MultipartFile[] files, @Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, Model model) {
        movieValidator.validate(movie, bindingResult);
        if (files.length < 1) bindingResult.reject("image.notSubmitted");
        if (bindingResult.hasErrors()) {
            return "admin/movie-add";
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

        if (bindingResult.hasErrors()) return "admin/movie-add";

        movieRepository.save(movie);

        return "redirect:/guest/movie/" + movie.getId();
    }

    @GetMapping("/guest/movie/{id}")
    public String retrieveMovie(@PathVariable("id") Long id, Model model) {
        Movie movie = movieRepository.findById(id).orElse(null);
        if (movie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return "guest/home";
        }
        model.addAttribute("movie", movie);

        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            model.addAttribute("credentials", credentials);
        }

        return "guest/movie-view";
    }

    @PostMapping("/admin/movie/addImages/")
    public String addMovieImage(@RequestParam("movieId") Long id, @RequestParam("files") MultipartFile[] files, Model model) {

        Movie optionalMovie = movieRepository.findById(id).orElse(null);
        if (optionalMovie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return authenticationController.index(model);
        }
        Movie movie = optionalMovie;

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

        return "redirect:/guest/movie/" + movie.getId();
    }

    @GetMapping("/admin/movie/selectDirector/{movieId}")
    public String addMovieDirector(@PathVariable("movieId") Long id, Model model) {

        Movie optionalMovie = movieRepository.findById(id).orElse(null);
        if (optionalMovie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return authenticationController.index(model);
        }
        Movie movie = optionalMovie;

        Set<Artist> possibleDirectors = artistRepository.findDistinctByDirectedMoviesNotContaining(movie);
        model.addAttribute("artists", possibleDirectors);
        model.addAttribute("movie", movie);
        return "/admin/movie-selectDirector";
    }

    @GetMapping("/admin/movie/addDirector/{movieId}/{artistId}")
    public String addMovieDirector(@PathVariable("movieId") Long movieId, @PathVariable("artistId") Long artistId, Model model) {

        Movie optionalMovie = movieRepository.findById(movieId).orElse(null);
        if (optionalMovie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return authenticationController.index(model);
        }
        Movie movie = optionalMovie;

        Artist optionalArtist = artistRepository.findById(artistId).orElse(null);
        if (optionalArtist == null) {
            model.addAttribute("errorMessage", "artist.notFound");
            return authenticationController.index(model);
        }
        Artist artist = optionalArtist;

        movie.setDirector(artist);
        movieRepository.save(movie);

        return "redirect:/guest/movie/" + movie.getId();
    }

    @GetMapping("/admin/movie/deleteImage/{movieId}/{imageId}")
    public String removeMovieImage(@PathVariable("movieId") Long movieId, @PathVariable("imageId") Long imageId, Model model) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return authenticationController.index(model);
        }
        model.addAttribute("movie", movie);

        Image image = imageRepository.findById(imageId).orElse(null);
        if (image == null) {
            model.addAttribute("errorMessage", "image.notFound");
            return retrieveMovie(movieId, model);
        }

        movie.getImages().remove(image);
        movieRepository.save(movie);
        imageRepository.delete(image);
        return "redirect:/guest/movie/" + movie.getId();

    }

    @GetMapping("/admin/movie/editActors/{movieId}")
    public String changeMovieActor(@PathVariable("movieId") Long movieId, Model model) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return authenticationController.index(model);
        }
        model.addAttribute("movie", movie);

        Set<Artist> possibleArtists = artistRepository.findDistinctByActedMoviesNotContaining(movie);
        model.addAttribute("artists", possibleArtists);
        return "admin/movie-editActors";
    }

    @GetMapping("/admin/movie/addActor/{movieId}/{artistId}")
    public String addMovieActor(@PathVariable("movieId") Long movieId, @PathVariable("artistId") Long artistId, Model model) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return authenticationController.index(model);
        }

        Artist artist = artistRepository.findById(artistId).orElse(null);
        if (artist == null) {
            model.addAttribute("errorMessage", "artist.notFound");
            return authenticationController.index(model);
        }

        movie.getActors().add(artist);
        movieRepository.save(movie);

        return "redirect:/admin/movie/editActors/" + movie.getId();
    }

    @GetMapping("/admin/movie/removeActor/{movieId}/{artistId}")
    public String removeMovieActor(@PathVariable("movieId") Long movieId, @PathVariable("artistId") Long artistId, Model model) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return authenticationController.index(model);
        }

        Artist artist = artistRepository.findById(artistId).orElse(null);
        if (artist == null) {
            model.addAttribute("errorMessage", "artist.notFound");
            return authenticationController.index(model);
        }

        movie.getActors().remove(artist);
        movieRepository.save(movie);

        return "redirect:/admin/movie/editActors/" + movie.getId();
    }

    @GetMapping("/admin/movie/delete/{movieId}")
    public String deleteMovie(@PathVariable("movieId") Long movieId, Model model) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return authenticationController.index(model);
        }

        movieRepository.delete(movie);
        model.addAttribute("errorMessage", "movie.deleted");
        return authenticationController.index(model);
    }

    @GetMapping("/guest/movies")
    public String movies(Model model) {
        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            model.addAttribute("credentials", credentials);
        }
        model.addAttribute("movies", movieRepository.findAll());
        return "guest/movie-viewAll";

    }
}
