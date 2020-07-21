package com.csci5408.assignment3.service;

import com.csci5408.assignment3.util.RegexConstant;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/* News business logic class for this application.
 * Rest controller calls methods of this class.
 * This class invokes APIS provided by News API and stores data into MongoDB after filtering.
 */
@Service
public class NewsService {

    private Logger logger = LoggerFactory.getLogger(NewsService.class);
    private static final String COLLECTION_NEWS_RAW = "news_raw";
    private static final String COLLECTION_NEWS_FILTERED = "news_filtered";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${data.news.api.apiKey}")
    private String apiKey;

    @Value("${data.news.api.url}")
    private String newsUrl;

    @Autowired
    private MongoTemplate mongoTemplate;

    /* News business logic method,
     * invoked by ../news/search?id=keyword&page=1 rest endpoint of this application.
     * This method invokes news API http://newsapi.org/v2/everything?q=keyword&page=1
     * and stores the data into MongoDB after filtering.
     */
    public HttpEntity<String> getNewsData(String keyword, Integer page) {

        // URI construction
        String uri = newsUrl + "/everything" + "?q=" + keyword;
        if (page != null && page > 0) {
            uri = uri + "&page=" + page;
        }

        //Adding api key in the header
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-api-key", apiKey);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);

        //Calling news api
        HttpEntity<String> res = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);

        String resJsonString = res.getBody().toString();
        logger.info("News API response: " + resJsonString);

        //Inserting raw data response in MongoDb
        mongoTemplate.insert(resJsonString, COLLECTION_NEWS_RAW);

        //filtering the response from news api
        // Each news item is analyzed for "description" and "content" attributes
        try {
            JSONObject jsonObject = new JSONObject(resJsonString);
            System.out.println("jsonObject: " + jsonObject);

            if (jsonObject.getJSONArray("articles").isEmpty()) {
                logger.info("No new data found for the keyword");
            } else {

                /* Filtering the "description" and "content"  attributes of the News api response
                 * and removing special characters.
                 * Remaining attributes are kept unchanged for the future use.
                 */
                JSONArray articles = jsonObject.getJSONArray("articles");
                System.out.println("articles: " + articles);
                articles.forEach(art -> {

                    JSONObject article = (JSONObject) art;

                    /* Filtering the "description" attribute of the response
                     * and removing special characters except certain punctuations.
                     */
                    if (article.has("description") && article.get("description") != null) {
                        String description = new String(article.get("description").toString()
                                .replaceAll(RegexConstant.EMOJI_SPECIAL_CHAR_FILTER, "")
                                .replaceAll(RegexConstant.ALPHANUMERIC_SPECIAL_CHAR_FILTER, " "));
                        article.put("description", description);
                    }
                    /* Filtering the "content" attribute of the response
                     * and removing special characters except certain punctuations.
                     */
                    if (article.has("content") && article.get("content") != null) {
                        String content = new String(article.get("content").toString()
                                .replaceAll(RegexConstant.EMOJI_SPECIAL_CHAR_FILTER, "")
                                .replaceAll(RegexConstant.ALPHANUMERIC_SPECIAL_CHAR_FILTER, " "));
                        article.put("content", content);
                    }
                });
                logger.info("Filtered news data to be stored in MongoDB" + jsonObject.toString());
                resJsonString = jsonObject.toString();

                //Inserting filtered data response in MongoDb
                mongoTemplate.insert(resJsonString, COLLECTION_NEWS_FILTERED);
            }
        } catch (Exception e) {
            System.out.println("exception e:" + e);
            e.printStackTrace();
        }
        return res;
    }
}