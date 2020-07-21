package com.csci5408.assignment3.controller;

import com.csci5408.assignment3.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/* News REST API endpoint controller for this application.
 * URI : http://localhost:9090/news
 */
@RestController
@RequestMapping("/news")
public class NewsController {

    private Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsService newsService;

    /*   API endpoint method for searching movie with description.
     *   URI: ../search?id=University&page=1
     *   Method: POST
     */
    @PostMapping("/search")
    public Object fetchNews(@RequestParam("id") String id, @RequestParam(name = "page", required = false) Integer page) {
        return newsService.getNewsData(id, page);
    }
}