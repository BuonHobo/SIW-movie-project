package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.ArtistRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
public class ArtistController {
    @Autowired
    ArtistRepository artistRepository;

    @GetMapping("/createArtist")
    public String createArtist(Model model){
        model.addAttribute("artist",new Artist());
        return "createArtist";
    }

    @PostMapping("/createArtist")
    public String createArtist(@RequestParam("file")MultipartFile file, @Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()){
            return "createArtist";
        }

        try {
            Image immagine = new Image(file.getOriginalFilename(), file.getBytes());
            String format = immagine.getFormat();
            if (!(format.equals("jpeg") || format.equals("png") || format.equals("jpg"))) {
                bindingResult.reject("image.formatNotSupported");
            }
            artist.setPicture(immagine);
        } catch (IOException ex) {
            bindingResult.reject("image.readError");
        }

        if (bindingResult.hasErrors()) return "createArtist";

        artistRepository.save(artist);
        model.addAttribute("artist",artist);
        return "retrieveArtist";
    }

    @GetMapping("/retrieveArtist/{id}")
    public String retrieveMovie(@PathVariable("id") Long id, Model model){
        Optional<Artist> artistOptional =artistRepository.findById(id);

        if (artistOptional.isEmpty()) return "home";

        model.addAttribute("artist",artistOptional.get());
        return "retrieveArtist";
    }
}
