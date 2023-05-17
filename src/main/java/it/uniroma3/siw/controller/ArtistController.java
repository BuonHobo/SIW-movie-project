package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.service.ArtistService;
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
    ArtistService artistService;

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
            Image immagine = new Image(file);
            artist.setPicture(immagine);
        } catch (IOException ex) {
            bindingResult.reject("image.readError");
        } catch (Exception e) {
            bindingResult.reject(e.getMessage());
        }

        if (bindingResult.hasErrors()) return "admin/artist-add";

        artistService.save(artist);
        model.addAttribute("artist", artist);
        return retrieveArtist(artist.getId(), model);
    }

    @GetMapping("/guest/artist/{id}")
    public String retrieveArtist(@PathVariable("id") Long id, Model model) {
        try {
            model.addAttribute("artist", artistService.findById(id));
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "guest/home";
        }
        return "guest/artist-view";
    }

    @PostMapping("/admin/artist/changePicture")
    public String changeArtistPicture(@RequestParam("artistId") Long id, @RequestParam("file") MultipartFile file, Model model) {
        try {
            Image immagine = new Image(file);
            artistService.changeArtistIdImage(id, immagine);
        } catch (IOException ex) {
            model.addAttribute("errorMessage", "image.readError");
            return "guest/home";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "guest/home";
        }

        return "redirect:/guest/artist/" + id;
    }

    @GetMapping("/admin/artist/delete/{artistId}")
    public String deleteArtist(@PathVariable("artistId") Long artistId, Model model) {
        try {
            artistService.deleteId(artistId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        model.addAttribute("errorMessage", "artist.deleted");
        return "guest/home";
    }

    @GetMapping("/guest/artists")
    public String artists(Model model) {
        model.addAttribute("artists", artistService.findAll());
        return "guest/artist-viewAll";
    }

    @PostMapping("/admin/artist/setDeath")
    public String setDeath(@RequestParam("artistId") Long id, @RequestParam("deathDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date deathDate, Model model) {
        try {
            artistService.setArtistIdDeath(id, deathDate);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "guest/home";
        }

        return "redirect:/guest/artist/" + id;
    }
}
