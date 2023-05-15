package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.MovieRepository;
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

import jakarta.validation.Valid;
import java.util.List;

@Controller
public class ReviewController {
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    AuthenticationController authenticationController;
    @Autowired
    CredentialsService credentialsService;
    @Autowired
    MovieController movieController;

    @GetMapping("/user/review/write/{movieId}")
    public String writeReview(@PathVariable("movieId") Long movieId, Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        Movie movie = movieRepository.findById(movieId).orElse(null);

        if (movie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return authenticationController.index();
        }

        Review review = reviewRepository.findByAuthorAndMovie(credentials.getUser(), movie)
                .orElse(new Review(credentials.getUser(), movie));

        model.addAttribute("review", review);
        return "user/review-add";
    }

    @PostMapping("user/review/add")
    public String addReview(@RequestParam("movieId") Long movieId, @RequestParam("rating") int rating, @Valid @ModelAttribute("review") Review review, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "user/review-add";
        }
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return authenticationController.index();
        }


        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        User author = credentials.getUser();

        Review oldReview = reviewRepository.findByAuthorAndMovie(author, movie).orElse(null);
        review.setMovie(movie);
        if (oldReview != null) {
            oldReview.setRating(rating);
            oldReview.setBody(review.getBody());
            oldReview.setTitle(review.getTitle());
            reviewRepository.save(oldReview);
        } else {
            review.setAuthor(author);
            review.setRating(rating);
            reviewRepository.save(review);
        }
        return movieReviews(review.getMovie().getId(), model);
    }

    @GetMapping("/guest/movie/reviews/{movieId}")
    public String movieReviews(@PathVariable("movieId") Long movieId, Model model) {
        Movie movie = movieRepository.findById(movieId).orElse(null);

        if (movie == null) {
            model.addAttribute("errorMessage", "movie.notFound");
            return authenticationController.index();
        }
        model.addAttribute("movie", movie);


        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            model.addAttribute("userReview", reviewRepository.findByAuthorAndMovie(credentials.getUser(), movie).orElse(null));
            model.addAttribute("reviews", reviewRepository.findByMovieAndNotByAuthor(movie, credentials.getUser()));
        } else {
            List<Review> reviews = reviewRepository.findAllByMovie(movie);
            model.addAttribute("reviews", reviews);
        }

        return "guest/movie-reviews";
    }

    @GetMapping("/guest/reviews/{userId}")
    public String userReviews(@PathVariable("userId") Long id, Model model) {
        List<Review> userReviews = reviewRepository.findAllByAuthor_Id(id);

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
        Review review = reviewRepository.findById(id).orElse(null);
        if (review == null) {
            model.addAttribute("errorMessage", "review.notFound");
            return authenticationController.index();
        }

        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());

            if (credentials.getUser().getId().equals(review.getAuthor().getId()) || credentials.isAdmin()) {
                reviewRepository.delete(review);
            }
        }

        return movieReviews(review.getMovie().getId(), model);
    }

}
