package it.uniroma3.siw.controller.validator;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class MovieValidator implements Validator {
    @Autowired
    private MovieService movieService;

    @Override
    public boolean supports(Class<?> aClass) {
        return Movie.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Movie movie = (Movie) o;
        if (movieService.existsByTitleAndReleaseDate(movie.getTitle(), movie.getReleaseDate())) {
            errors.reject("movie.duplicate");
        }
    }
}
