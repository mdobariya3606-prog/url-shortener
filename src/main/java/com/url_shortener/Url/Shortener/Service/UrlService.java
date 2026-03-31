package com.url_shortener.Url.Shortener.Service;

import com.url_shortener.Url.Shortener.DTO.StatisticsDTO;
import com.url_shortener.Url.Shortener.DTO.UrlDto;
import com.url_shortener.Url.Shortener.Model.UrlInfo;
import com.url_shortener.Url.Shortener.Repository.UrlRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@Service
public class UrlService {
    @Autowired
    private UrlRepo urlRepo;

    private static void isValid(String url) {
        if (url == null || url.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL cannot be empty");
        }
        if (url.length() > 2048) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL too long, max 2048 characters");
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL must be starts with http/https");
        }

//        check format
        try {
            new URL(url).toURI();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid url format");
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url)
                    .openConnection();

            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            int status = connection.getResponseCode();
            if (status < 200 || status >= 400) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL is Unreachable");
            }
        } catch (ResponseStatusException responseStatusException) {
            throw responseStatusException;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL is not working.");
        }
    }

    public String shortUrl(UrlDto urlDto) {
        String originalUrl = urlDto.getUrl().trim();

        isValid(originalUrl);

        // check if url already exists - if exists return old shortCode - if not return new code.
        Optional<UrlInfo> alreadyExists = urlRepo.findByOriginalUrl(originalUrl);
        if (alreadyExists.isPresent()) {
            return alreadyExists.get().getShortCode();
        }


        UrlInfo urlInfo = new UrlInfo();
        String shortCode = getShortCode();

        urlInfo.setOriginalUrl(originalUrl);
        urlInfo.setShortCode(shortCode);
        urlInfo.setCreatedAt(LocalDateTime.now());
        urlInfo.setExpireAt(LocalDateTime.now().plusHours(1));
        urlInfo.setLastAccessed(LocalDate.now());
        urlRepo.save(urlInfo);

        return shortCode;
    }

    private String getShortCode() {
        String chars = "qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM";

        Random random = new Random();
        String code;

        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            code = sb.toString();
        } while (urlRepo.existsByShortCode(code));

        return code;
    }

    public String redirect(String shortCode) {
        UrlInfo urlInfo = urlRepo.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL Not Found with: " + shortCode));

        if (LocalDateTime.now().isAfter(urlInfo.getExpireAt())) {
            urlRepo.delete(urlInfo);
            throw new ResponseStatusException(HttpStatus.GONE, "URL is Expired.");
        }

        urlInfo.increaseClick();
        urlInfo.setLastAccessed(LocalDate.now());
        urlRepo.save(urlInfo);
        return urlInfo.getOriginalUrl();
    }

    public StatisticsDTO getStatistics(String shortCode) {
        UrlInfo urlInfo = urlRepo.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "shortURL Not found."));

        StatisticsDTO statisticsDTO = new StatisticsDTO();
        statisticsDTO.setCreatedAt(urlInfo.getCreatedAt().toLocalDate());
        statisticsDTO.setLastAccessed(getLastAccessed(urlInfo.getLastAccessed()));
        statisticsDTO.setTotalClicks(urlInfo.getClicks());

        return statisticsDTO;
    }

    private String getLastAccessed(LocalDate lastAccessed) {
        LocalDate today = LocalDate.now();

        long daysDifference = ChronoUnit.DAYS.between(lastAccessed, today);

        if (daysDifference == 0) {
            return "today";
        } else if (daysDifference == 1) {
            return "yesterday";
        } else {
            return daysDifference + " days ago";
        }
    }
}