package com.csci5408.assignment3.controller;

import com.csci5408.assignment3.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/* OMDb REST API endpoint controller for this application.
 * URI : http://localhost:9090/movies
 */
@RestController
@RequestMapping("/movies")
public class MovieController {

    private Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private MovieService movieService;

    /*   API endpoint method for searching movie with description.
     *   URI: ../search?s=University&page=1
     *   Method: POST
     */
    @PostMapping("/search")
    public Object fetchNews(@RequestParam(name = "s") String searchKeyWord, @RequestParam(name = "page", required = false) Integer page) {
        return movieService.getMovieData(searchKeyWord, page);
    }
}
