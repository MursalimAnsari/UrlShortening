package com.urlshortner.controller;

import com.urlshortner.model.Url;
import com.urlshortner.model.UrlDto;
import com.urlshortner.model.UrlErrorResponseDto;
import com.urlshortner.model.UrlResponseDto;
import com.urlshortner.service.UrlService;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/")
public class UrlShorteningController {



    @Autowired
    private UrlService urlService;

    @Autowired
    public HttpServletResponse response;


    @PostMapping("/create")
    ResponseEntity<?> generateShortLink(@RequestBody UrlDto urlDto){
        Url urlToReturn = urlService.generateShortLink(urlDto);

        if (urlToReturn!=null){
            UrlResponseDto urlResponseDto = new UrlResponseDto();
            urlResponseDto.setOriginalUrl(urlToReturn.getOriginalUrl());
            urlResponseDto.setExpirationDate(urlToReturn.getExpirationDate());
            urlResponseDto.setShortLink(urlToReturn.getShortLink());
            return new ResponseEntity<UrlResponseDto>(urlResponseDto, HttpStatus.CREATED);
        }

        UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
        urlErrorResponseDto.setStatus("404");
        urlErrorResponseDto.setError("Error in processing request!");
       return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);
    }


    @GetMapping("/{shortLink}")
    public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortLink) throws IOException {

        if (StringUtils.isEmpty(shortLink)) {
            UrlErrorResponseDto errorResponseDto = new UrlErrorResponseDto();
            errorResponseDto.setError("Invalid Url!");
            errorResponseDto.setStatus("400");
            return new ResponseEntity<UrlErrorResponseDto>(errorResponseDto, HttpStatus.OK);
        }

        Url urlToReturn = urlService.getEncodedUrl(shortLink);

        if (urlToReturn == null) {
            UrlErrorResponseDto errorResponseDto = new UrlErrorResponseDto();
            errorResponseDto.setError("Url doesn't exist or url has been expired!");
            errorResponseDto.setStatus("400");
            return new ResponseEntity<UrlErrorResponseDto>(errorResponseDto, HttpStatus.OK);
        }

        if (urlToReturn.getExpirationDate().isBefore(LocalDateTime.now())) {

           urlService.deleteShortLink(urlToReturn);

            UrlErrorResponseDto errorResponseDto = new UrlErrorResponseDto();
            errorResponseDto.setError("Url expired please generate a fresh one!");
            errorResponseDto.setStatus("200");
            return new ResponseEntity<UrlErrorResponseDto>(errorResponseDto, HttpStatus.OK);
        }

        response.sendRedirect(urlToReturn.getOriginalUrl());
        return null;
    }
}
