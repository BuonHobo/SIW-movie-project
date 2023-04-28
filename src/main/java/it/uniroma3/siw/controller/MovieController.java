package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.MovieRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

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
    public String createMovie(Model model){
        model.addAttribute("movie",new Movie());
        return "createMovie";
    }

    @PostMapping("/createMovie")
    public String addMovie(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, Model model){
        movieValidator.validate(movie,bindingResult);
        if (bindingResult.hasErrors()){
            return "createMovie";
        }

        movie.setAddedDate(new Date());

        movieRepository.save(movie);
        model.addAttribute("movie",movie);
        return "retrieveMovie";
    }
}
