package com.url_shortener.Url.Shortener.Repository;

import com.url_shortener.Url.Shortener.Model.UrlInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepo extends JpaRepository<UrlInfo, Long> {
    Optional<UrlInfo> findByShortCode(String shortCode);

    Optional<UrlInfo> findByOriginalUrl(String url);

    boolean existsByShortCode(String code);
}
