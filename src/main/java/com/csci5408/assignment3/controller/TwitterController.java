package com.csci5408.assignment3.controller;

import com.csci5408.assignment3.model.dto.request.KeywordRequest;
import com.csci5408.assignment3.service.TwitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import twitter4j.TwitterException;


/* Twitter REST API endpoint controller for this application.
 * URI : http://localhost:9090/twitter
 */
@RestController
@RequestMapping("/twitter")
public class TwitterController {

    Logger logger = LoggerFactory.getLogger(TwitterController.class);

    @Autowired
    TwitterService twitterService;

    /*   API endpoint method for streaming tweets for given keywords(in string array)
     *   URI: ../stream
     *   body: { "keyword" : ["Canada", "University", "Dalhousie University", "Halifax", "Canada Education"] }
     *   Method: POST
     */
    @PostMapping("/stream")
    public String streamTwitter(@RequestBody KeywordRequest body) {
        twitterService.fetchTwitterDataWithStream(body.getKeyword());
        return "calling twitter streaming api";
    }

    /*   API endpoint method for searching tweets for given keywords
     *   URI: ../search
     *   body: { "keyword" : ["Canada"] }
     *   Method: POST
     */
    @PostMapping("/search")
    public ResponseEntity<Object> searchTwitter(@RequestBody KeywordRequest body) throws TwitterException {
        return new ResponseEntity<Object>(twitterService.fetchTwitterDataWithSearch(body.getKeyword().get(0)), HttpStatus.OK);
    }
}