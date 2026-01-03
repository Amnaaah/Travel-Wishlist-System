package com.amnafar.cw1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping("/")
@Tag(name = "Page Navigation", description = "HTML page navigation endpoints")
public class PageController {

    @Operation(
            summary = "Home Page",
            description = "Display the main home page"
    )
    @GetMapping
    public String showHome() {
        return "home";
    }

    @Operation(
            summary = "Account Page",
            description = "Display user account dashboard"
    )
    @GetMapping("/account")
    public String accountPage() {
        return "account";
    }

    @Operation(
            summary = "Register Page",
            description = "Display registration/login form"
    )
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @Operation(
            summary = "Review Page",
            description = "Display all the reviews for searched location"
    )
    @GetMapping("/reviewsearch")
    public String reviewpage() {
        return "reviewsearch";
    }
}

