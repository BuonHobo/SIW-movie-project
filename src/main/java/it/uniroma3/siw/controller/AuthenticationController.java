package it.uniroma3.siw.controller;

import javax.validation.Valid;

import it.uniroma3.siw.controller.validator.CredentialsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;

@Controller
public class AuthenticationController {

    @Autowired
    private CredentialsService credentialsService;
    @Autowired
    private CredentialsValidator credentialsValidator;

    @GetMapping(value = "/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("credentials", new Credentials());
        return "guest/register";
    }

    @GetMapping(value = "/login")
    public String showLoginForm(Model model) {
        return "guest/login";
    }

    @GetMapping(value = "/")
    public String index(Model model) {
        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            model.addAttribute("credentials", credentials);
        }

        return "guest/home";
    }

    @GetMapping(value = "/success")
    public String defaultAfterLogin(Model model) {
        model.addAttribute("successMessage","login.success");
        return index(model);
    }

    @PostMapping(value = {"/register"})
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult userBindingResult, @Valid
                               @ModelAttribute("credentials") Credentials credentials,
                               BindingResult credentialsBindingResult,
                               Model model) {

        credentialsValidator.validate(credentials, credentialsBindingResult);

        // se user e credential hanno entrambi contenuti validi, memorizza User e the Credentials nel DB
        if (!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
            credentials.setUser(user);
            credentialsService.saveCredentials(credentials);
            model.addAttribute("user", user);
            model.addAttribute("messaggio", "register.success");
            return "guest/login";
        }
        return "guest/register";
    }
}