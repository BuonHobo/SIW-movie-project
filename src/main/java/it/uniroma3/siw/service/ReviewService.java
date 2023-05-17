package it.uniroma3.siw.service;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class ReviewService {

    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    MovieService movieService;

    @Transactional
    public Review getUserReviewOfMovieId(Long movieId, User user) throws Exception {
        Movie movie = movieService.findById(movieId);
        return reviewRepository.findByAuthorAndMovie(user, movie)
                .orElse(new Review(user, movie));
    }

    @Transactional public void updateReview(User author, Long movieId, Review review,Integer rating) throws Exception {
        Movie movie= movieService.findById(movieId);
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
    }

    @Transactional
    public Review findById(Long reviewId) throws Exception {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null) {
            throw new Exception("review.notFound");
        }
        return review;
    }

    @Transactional
    public Movie deleteByIdIfPossible(Long reviewId, Credentials credentials) throws Exception {
        Review review = findById(reviewId);
        if (credentials.getUser().getId().equals(review.getAuthor().getId()) || credentials.isAdmin()) {
            reviewRepository.delete(review);
        }
        return review.getMovie();
    }

    @Transactional
    public List<Review> findAllByAuthor_Id(Long id) {
        return reviewRepository.findAllByAuthor_Id(id);
    }

    @Transactional
    public Optional<Review> findByAuthorAndMovie(User user, Movie movie) {
        return reviewRepository.findByAuthorAndMovie(user, movie);
    }

    @Transactional
    public List<Review> findByMovieAndNotByAuthor(Movie movie, User user) {
        return reviewRepository.findByMovieAndNotByAuthor(movie, user);
    }

    @Transactional
    public List<Review> findAllByMovie(Movie movie) {
        return reviewRepository.findAllByMovie(movie);
    }

}
