package com.stevedutch.intellectron.advice;

import java.security.Principal;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addUserAttributes(Model model, Principal principal) {
        if (principal != null) {
            String username = "Unknown User";
            
            if (principal instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
                OAuth2User oauth2User = oauth2Token.getPrincipal();
                
                // Try to get name from different OAuth2 providers
                if (oauth2User.getAttribute("name") != null) {
                    username = oauth2User.getAttribute("name");
                } else if (oauth2User.getAttribute("login") != null) {
                    username = oauth2User.getAttribute("login"); // GitHub
                } else if (oauth2User.getAttribute("email") != null) {
                    username = oauth2User.getAttribute("email");
                }
            } else {
                username = principal.getName();
            }
            
            model.addAttribute("currentUser", username);
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
        }
    }
}
