package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.service.CredentialsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Controller
public class ArtistController {
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    CredentialsService credentialsService;
    @Autowired
    AuthenticationController authenticationController;

    @GetMapping("admin/artist/add")
    public String createArtist(Model model) {
        model.addAttribute("artist", new Artist());
        return "admin/artist-add";
    }

    @PostMapping("admin/artist/add")
    public String createArtist(@RequestParam("file") MultipartFile file, @Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/artist-add";
        }

        try {
            Image immagine = new Image(file.getOriginalFilename(), file.getBytes());
            String format = immagine.getFormat();
            if (!(format.equals("jpeg") || format.equals("png") || format.equals("jpg") || format.equals("webp"))) {
                bindingResult.reject("image.formatNotSupported");
            }
            artist.setPicture(immagine);
        } catch (IOException ex) {
            bindingResult.reject("image.readError");
        }

        if (bindingResult.hasErrors()) return "admin/artist-add";

        artistRepository.save(artist);
        model.addAttribute("artist", artist);
        return retrieveArtist(artist.getId(), model);
    }

    @GetMapping("/guest/artist/{id}")
    public String retrieveArtist(@PathVariable("id") Long id, Model model) {
        Artist artistOptional = artistRepository.findById(id).orElse(null);

        if (artistOptional == null) {
            model.addAttribute("errorMessage", "artist.notFound");
            return authenticationController.index();
        }

        model.addAttribute("artist", artistOptional);
        return "guest/artist-view";
    }

    @PostMapping("/admin/artist/changePicture")
    public String changeArtistPicture(@RequestParam("artistId") Long id, @RequestParam("file") MultipartFile file, Model model) {
        Artist artist = artistRepository.findById(id).orElse(null);
        if (artist == null) {
            model.addAttribute("errorMessage", "artist.notFound");
            return authenticationController.index();
        }

        try {
            Image immagine = new Image(file.getOriginalFilename(), file.getBytes());
            String format = immagine.getFormat();
            if (!(format.equals("jpeg") || format.equals("png") || format.equals("jpg") || format.equals("webp"))) {
                model.addAttribute("errorMessage", "image.formatNotSupported");
                return retrieveArtist(id, model);
            }
            Image oldPic = artist.getPicture();
            artist.setPicture(null);
            imageRepository.delete(oldPic);
            artist.setPicture(immagine);
        } catch (IOException ex) {
            model.addAttribute("errorMessage", "image.readError");
            return retrieveArtist(id, model);
        }

        artistRepository.save(artist);
        model.addAttribute("artist", artist);
        return retrieveArtist(id, model);
    }

    @GetMapping("/admin/artist/delete/{artistId}")
    public String deleteArtist(@PathVariable("artistId") Long artistId, Model model) {
        Artist artist = artistRepository.findById(artistId).orElse(null);
        if (artist == null) {
            model.addAttribute("errorMessage", "artist.notFound");

            return authenticationController.index();
        }

        artistRepository.delete(artist);
        model.addAttribute("errorMessage", "artist.deleted");
        return authenticationController.index();
    }

    @GetMapping("/guest/artists")
    public String artists(Model model) {
        model.addAttribute("artists", artistRepository.findAll());

        return "guest/artist-viewAll";
    }

    @PostMapping("/admin/artist/setDeath")
    public String setDeath(@RequestParam("artistId") Long id, @RequestParam("deathDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date deathDate, Model model) {
        Artist artist = artistRepository.findById(id).orElse(null);
        if (artist == null) {
            model.addAttribute("errorMessage", "artist.notFound");

            return authenticationController.index();
        }

        if (artist.getBirthday().before(deathDate) && deathDate.before(new Date())) {
            artist.setDeathDate(deathDate);
            artistRepository.save(artist);
        } else {
            model.addAttribute("errorMessage", "artist.deathDateInvalid");
        }

        return retrieveArtist(id, model);
    }
}
