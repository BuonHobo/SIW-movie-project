package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
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


@Controller
public class MovieController {
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    MovieValidator movieValidator;

    @GetMapping("/")
    public String homepage(Model model) {
        model.addAttribute("latestMovies", movieRepository.getLatest3Movies());
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

        if (movie.isEmpty()) return "home";

        model.addAttribute("movie", movie.get());
        return "retrieveMovie";
    }

    @PostMapping("/addMovieImage/{id}")
    public String addMovieImage(@PathVariable("id") Long id, @RequestParam("files") MultipartFile file, Model model) {

        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isEmpty()) return "home";
        Movie movie = optionalMovie.get();
        try {
            movie.getImages().add(new Image(file.getOriginalFilename(), file.getBytes()));
        } catch (IOException ex) {
            return "home";
        }
        movieRepository.save(movie);
        model.addAttribute("movie", movie);
        return "retrieveMovie";
    }
}
