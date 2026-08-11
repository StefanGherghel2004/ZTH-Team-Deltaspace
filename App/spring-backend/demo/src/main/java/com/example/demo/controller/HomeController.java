package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {
    @GetMapping(produces = "text/plain;charset=UTF-8")
    public String getHomePage() {
        return """
    ==============================================
    
    🚀Δ🚀Δ🚀Δ🚀Δ🚀WELCOME TO DELTASPACE🚀Δ🚀Δ🚀Δ🚀
    
    ==============================================
    """;
    }

}
