package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.MovieRepository;

import javax.validation.Valid;

import it.uniroma3.siw.service.CredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
            if (!(format.equals("jpeg") || format.equals("png") || format.equals("jpg"))) {
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
            return authenticationController.index(model);
        }


        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            model.addAttribute("credentials", credentials);
        }

        model.addAttribute("artist", artistOptional);
        return "guest/artist-view";
    }

    @PostMapping("/admin/artist/changePicture")
    public String changeArtistPicture(@RequestParam("artistId") Long id, @RequestParam("file") MultipartFile file, Model model) {
        Artist artist = artistRepository.findById(id).orElse(null);
        if (artist == null) {
            model.addAttribute("errorMessage", "artist.notFound");
            return authenticationController.index(model);
        }

        try {
            Image immagine = new Image(file.getOriginalFilename(), file.getBytes());
            String format = immagine.getFormat();
            if (!(format.equals("jpeg") || format.equals("png") || format.equals("jpg"))) {
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

            return authenticationController.index(model);
        }

        artistRepository.delete(artist);
        model.addAttribute("errorMessage", "artist.deleted");
        return authenticationController.index(model);
    }

    @GetMapping("/guest/artists")
    public String artists(Model model) {
        model.addAttribute("artists", artistRepository.findAll());
        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            model.addAttribute("credentials", credentials);
        }
        return "guest/artist-viewAll";

    }
}
