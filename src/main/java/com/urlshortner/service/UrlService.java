package com.urlshortner.service;

import com.urlshortner.model.Url;
import com.urlshortner.model.UrlDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Service
public interface UrlService {

  public Url generateShortLink(UrlDto urlDto);
  public Url persistShortLink(Url url);
  public Url getEncodedUrl(String url);
  public void deleteShortLink(Url url);

}
