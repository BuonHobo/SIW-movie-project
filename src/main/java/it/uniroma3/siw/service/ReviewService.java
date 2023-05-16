package it.uniroma3.siw.service;

import it.uniroma3.siw.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class ReviewService {

    @Autowired
    ReviewRepository reviewRepository;
}
