package com.csci5408.assignment3.service;

import com.csci5408.assignment3.util.RegexConstant;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/* OMDb business logic class for this application.
 * Rest controller calls methods of this class.
 * This class invokes APIS provided by OMDb and stores data into MongoDB after filtering.
 */
@Service
public class MovieService {

    private Logger logger = LoggerFactory.getLogger(MovieService.class);
    private static final String COLLECTION_MOVIES_RAW = "movie_raw";
    private static final String COLLECTION_MOVIES_FILTERED = "movie_filtered";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${data.omdb.api.apiKey}")
    private String apiKey;

    @Value("${data.omdb.api.url}")
    private String url;

    @Autowired
    private MongoTemplate mongoTemplate;

    /* OMDb business logic method,
     * invoked by ../movies/search?s=University&page=1 rest endpoint of this application.
     * This method invokes OMDb API http://www.omdbapi.com/?s=keyword
     * and for each resultant movie data in the array,
     * it calls http://www.omdbapi.com/?i=movieId to fetch full movie details.
     * Each movie data is stored into MongoDB after filtering.
     */
    public HttpEntity<String> getMovieData(String keyword, Integer page) {

        // URI construction
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("s", keyword)
                .queryParam("apiKey", apiKey);

        if (page != null && page > 0) {
            builder.queryParam("page", page);
        }

        //Adding api key in the header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);

        /* Calling OMDb api http://www.omdbapi.com/?s=keyword
         * for fetching list of movies with the given keyword.
         */
        HttpEntity<String> res = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, String.class);
        String resJsonString = res.getBody().toString();
        JSONObject resObj = new JSONObject(resJsonString);
        logger.info("OMDb api response: " + resObj);

        /* From the first OMDb call, if the result has list of movies with the given keyword,
         * call OMDb api http://www.omdbapi.com/?i=movieId for each movie item for fetching full movie details.
         */
        if (resObj.has("Search")) {
            JSONArray seachResult = resObj.getJSONArray("Search");
            seachResult.forEach(movie -> {
                JSONObject movieObj = (JSONObject) movie;
                String imdbID = movieObj.getString("imdbID");

                // Calling and fetching movie object.
                fetchAndStoreMovieData(imdbID);
            });
        }
        return res;
    }

    /*  This method invokes OMDb API http://www.omdbapi.com/?i=movieId ,
     * filter the response to remove special characters from the "plot" attribute, a
     * and store the record in MongoDB.
     * Both raw and filtered data are stored in separate MongoDB collection.
     */
    private void fetchAndStoreMovieData(String id) {

        // URI construction
        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("i", id)
                .queryParam("plot", "full")
                .queryParam("apiKey", apiKey);

        //Adding api key in the header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);

        //Calling OMDb api
        HttpEntity<String> res = restTemplate.exchange(movieBuilder.toUriString(), HttpMethod.GET, httpEntity, String.class);

        String resJsonString = res.getBody().toString();
        logger.info("OMDb api response for movie id: " + id + ": " + resJsonString);

        //Inserting raw data response in MongoDb
        mongoTemplate.insert(resJsonString, COLLECTION_MOVIES_RAW);

        /* Filtering the "plot" attribute of the OMDb response
         * and removing special characters except certain punctuations.
         * Remaining attributes are kept unchanged for the future use.
         */
        JSONObject movie = new JSONObject(resJsonString);
        String Plot = new String(movie.getString("Plot")
                .replaceAll(RegexConstant.EMOJI_SPECIAL_CHAR_FILTER, "")
                .replaceAll(RegexConstant.ALPHANUMERIC_SPECIAL_CHAR_FILTER, " "));
        movie.put("Plot", Plot);

        //Inserting filtered data response in MongoDb
        mongoTemplate.insert(movie.toString(), COLLECTION_MOVIES_FILTERED);
    }
}