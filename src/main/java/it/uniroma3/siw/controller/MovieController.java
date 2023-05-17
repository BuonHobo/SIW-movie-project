package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


@Controller
public class MovieController {
    @Autowired
    MovieService movieService;
    @Autowired
    MovieValidator movieValidator;
    @Autowired
    ArtistService artistService;

    @GetMapping("/admin/movie/add")
    public String createMovie(Model model) {
        model.addAttribute("movie", new Movie());
        return "admin/movie-add";
    }

    @PostMapping("/admin/movie/add")
    public String addMovie(@RequestParam("files") MultipartFile[] files, @Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult) {
        movieValidator.validate(movie, bindingResult);
        if (files.length < 1) bindingResult.reject("image.notSubmitted");
        if (bindingResult.hasErrors()) {
            return "admin/movie-add";
        }

        Set<Image> immagini = new HashSet<>();

        for (MultipartFile file : files) {
            try {
                immagini.add(new Image(file));
            } catch (IOException e) {
                bindingResult.reject("image.readError");
            } catch (Exception e) {
                bindingResult.reject("image.formatNotSupported");
            }
        }

        if (bindingResult.hasErrors()) return "admin/movie-add";

        movie.setImages(new HashSet<>());
        movie.setImages(immagini);

        movieService.save(movie);
        return "redirect:/guest/movie/" + movie.getId();
    }

    @GetMapping("/guest/movie/{id}")
    public String retrieveMovie(@PathVariable("id") Long id, Model model) {
        try {
            model.addAttribute("movie", movieService.findById(id));
            model.addAttribute("rating", movieService.getRatingByMovieId(id));
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "guest/home";
        }

        return "guest/movie-view";
    }

    @PostMapping("/admin/movie/addImages/")
    public String addMovieImage(@RequestParam("movieId") Long id, @RequestParam("files") MultipartFile[] files, Model model) {
        Set<Image> immagini = new HashSet<>();

        for (MultipartFile file : files) {
            try {
                immagini.add(new Image(file));
            } catch (IOException e) {
                model.addAttribute("errorMessage", "image.readError");
                return retrieveMovie(id, model);
            } catch (Exception e) {
                model.addAttribute("errorMessage", e.getMessage());
                return retrieveMovie(id, model);
            }
        }

        try {
            movieService.addImagesToMovieId(id, immagini);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "guest/home";
        }
        return "redirect:/guest/movie/" + id;
    }

    @GetMapping("/admin/movie/selectDirector/{movieId}")
    public String addMovieDirector(@PathVariable("movieId") Long id, Model model) {
        try {
            Movie movie = movieService.findById(id);
            model.addAttribute("artists", artistService.findDistinctByDirectedMoviesNotContaining(movie));
            model.addAttribute("movie", movie);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "guest/home";
        }

        return "/admin/movie-selectDirector";
    }

    @GetMapping("/admin/movie/removeDirector/{movieId}")
    public String removeMovieDirector(@PathVariable("movieId") Long id, Model model) {
        try {
            movieService.deleteDirectorFromMovieId(id);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "guest/home";
        }

        return "redirect:/guest/movie/" + id;
    }


    @GetMapping("/admin/movie/addDirector/{movieId}/{artistId}")
    public String addMovieDirector(@PathVariable("movieId") Long movieId, @PathVariable("artistId") Long artistId, Model model) {
        try {
            movieService.addDirectorIdToMovieId(artistId, movieId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "guest/home";
        }

        return "redirect:/guest/movie/" + movieId;
    }

    @GetMapping("/admin/movie/deleteImage/{movieId}/{imageId}")
    public String removeMovieImage(@PathVariable("movieId") Long movieId, @PathVariable("imageId") Long imageId, Model model) {
        try {
            movieService.removeMovieIdImage(movieId, imageId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "guest/home";
        }

        return "redirect:/guest/movie/" + movieId;
    }

    @GetMapping("/admin/movie/editActors/{movieId}")
    public String changeMovieActor(@PathVariable("movieId") Long movieId, Model model) {
        try {
            Movie movie = movieService.findById(movieId);
            model.addAttribute("movie", movie);
            model.addAttribute("artists", artistService.findDistinctByActedMoviesNotContaining(movie));
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "guest/home";
        }

        return "admin/movie-editActors";
    }

    @GetMapping("/admin/movie/addActor/{movieId}/{artistId}")
    public String addMovieActor(@PathVariable("movieId") Long movieId, @PathVariable("artistId") Long artistId, Model model) {
        try {
            movieService.addActorIdToMovieId(artistId, movieId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "guest/home";
        }

        return "redirect:/admin/movie/editActors/" + movieId;
    }

    @GetMapping("/admin/movie/removeActor/{movieId}/{artistId}")
    public String removeMovieActor(@PathVariable("movieId") Long movieId, @PathVariable("artistId") Long artistId, Model model) {
        try {
            movieService.removeActorIdFromMovieId(artistId, movieId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "guest/home";
        }

        return "redirect:/admin/movie/editActors/" + movieId;
    }

    @GetMapping("/admin/movie/delete/{movieId}")
    public String deleteMovie(@PathVariable("movieId") Long movieId, Model model) {
        movieService.deleteMovieId(movieId);
        model.addAttribute("errorMessage", "movie.deleted");
        return "guest/home";
    }

    @GetMapping("/guest/movies")
    public String movies(Model model) {
        model.addAttribute("movies", movieService.findAll());
        return "guest/movie-viewAll";
    }
}
