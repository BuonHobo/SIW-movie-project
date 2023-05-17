package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ReviewController {
    @Autowired
    CredentialsService credentialsService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    MovieService movieService;

    @GetMapping("/user/review/write/{movieId}")
    public String writeReview(@PathVariable("movieId") Long movieId, Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        User user = credentials.getUser();

        try {
            model.addAttribute("review", reviewService.getUserReviewOfMovieId(movieId, user));
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "guest/home";
        }
        return "user/review-add";
    }

    @PostMapping("user/review/add")
    public String addReview(@RequestParam("movieId") Long movieId, @RequestParam("rating") int rating, @Valid @ModelAttribute("review") Review review, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "user/review-add";
        }
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        User author = credentials.getUser();

        try {
            reviewService.updateReview(author, movieId, review, rating);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "guest/home";
        }
        return movieReviews(review.getMovie().getId(), model);
    }

    @GetMapping("/guest/movie/reviews/{movieId}")
    public String movieReviews(@PathVariable("movieId") Long movieId, Model model) {
        Movie movie;
        try {
            movie = movieService.findById(movieId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "guest/home";
        }
        model.addAttribute("movie", movie);

        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            model.addAttribute("userReview", reviewService.findByAuthorAndMovie(credentials.getUser(), movie).orElse(null));
            model.addAttribute("reviews", reviewService.findByMovieAndNotByAuthor(movie, credentials.getUser()));
        } else {
            List<Review> reviews = reviewService.findAllByMovie(movie);
            model.addAttribute("reviews", reviews);
        }

        return "guest/movie-reviews";
    }

    @GetMapping("/guest/reviews/{userId}")
    public String userReviews(@PathVariable("userId") Long id, Model model) {
        List<Review> userReviews = reviewService.findAllByAuthor_Id(id);

        model.addAttribute("reviews", userReviews);
        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            model.addAttribute("isAuthor", credentials.getUser().getId().equals(id));
        } else {
            model.addAttribute("isAuthor", false);
        }

        return "guest/user-reviews";
    }

    @GetMapping("/user/review/delete/{reviewId}")
    public String deleteReview(@PathVariable("reviewId") Long id, Model model) {
        Long movieId = null;
        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());

            try {
                movieId = reviewService.deleteByIdIfPossible(id, credentials).getId();
            } catch (Exception e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "guest/home";
            }

        }

        return movieReviews(movieId, model);
    }

}
