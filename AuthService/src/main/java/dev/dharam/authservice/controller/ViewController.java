package dev.dharam.authservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Ye templates/login.html ko load karega
    }
}
