package com.url_shortener.Url.Shortener.Controller;

import com.url_shortener.Url.Shortener.DTO.ResponseDTO;
import com.url_shortener.Url.Shortener.DTO.StatisticsDTO;
import com.url_shortener.Url.Shortener.DTO.UrlDto;
import com.url_shortener.Url.Shortener.Service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class UrlController {
    @Autowired
    private UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<?> shortUrl(@RequestBody UrlDto urlDto) {
        try {
            String shortCode = urlService.shortUrl(urlDto);
            return ResponseEntity.ok(new ResponseDTO("http://localhost:8080/api/" + shortCode));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode) {
        try {
           String originalURL =  urlService.redirect(shortCode);
           return ResponseEntity
                   .status(302) // found - tells the browser "this resource has temporarily moved"
                   .location(URI.create(originalURL))
                   .build();
        } catch (ResponseStatusException responseStatusException) {
            return ResponseEntity.status(responseStatusException.getStatusCode())
                    .body(responseStatusException.getMessage());
        }
    }

    @GetMapping("/statistics/{shortCode}")
    public StatisticsDTO getStatistics (@PathVariable String shortCode) {
        return urlService.getStatistics(shortCode);
    }
}