# Data-extractor: Twitter, News API and OMDb

Data-extractor a Java project build using Spring boot for extracting and filtering data from Twitter API, News API and OMDb API.
 
## Installation

Import this project in Intellij/eclipse.

Using [gradle](https://gradle.org/install/), build the project by running following command:

```bash
gradle clean install
```

## Usage

This is a web service project build using Spring boot. It fetches data from Twitter API, News API and OMDb API.
Refer to the service classes under the `com.csci5408.assignment3.service` package which extracts the data from APIs, filters the response using `regex` and stores the data in MongoDB.

#### Run the project
```
Run the main() of the com.csci5408.assignment3.AssignmentApplication.java
```

This will spawn an embedded web server running on port: http://localhost:9090/.

#### API endpoints

##### 1) Twitter

- Search for tweets 


> URI: http://localhost:9090/twitter/search
>
> method: POST
>
> body: {
         	"keyword" : ["Canada"]
         }

This endpoint will call the `fetchTwitterDataWithSearch()` of TwitterService class which calls twitter search api to search tweets for the given keywords. 
Response from Twitter is first stored in the MongoDB collection: `tweet_raw`.
The `full_text` field from the response is filtered using regex to remove urls, emojis and special characters. 
Filtered data along with other unchanged fields is stored in MongoDB collection: `tweet_filtered`.
  

- Stream real time tweets 

> URI: http://localhost:9090/twitter/stream
>
> method: POST
>
> body: {
        	"keyword" : ["Canada", "University", "Dalhousie University", "Halifax", "Canada Education"]
        }

This endpoint will call the `fetchTwitterDataWithStream()` of TwitterService class which calls twitter stream api to fetch real time tweets for the given keywords. 
Response from Twitter is first stored in the MongoDB collection: `tweet_raw`.
The `text` field from the response is filtered using regex to remove urls, emojis and special characters. 
Filtered data along with other unchanged fields is stored in MongoDB collection: `tweet_filtered`.

##### 2) News API
- Search for news 

> URI: http://localhost:9090/news/search?id=Toronto&page=5
>
> method: POST

This endpoint will call the `getNewsData()` of NewsService class which calls NewsAPI to fetch news data (http://newsapi.org/v2/everything?q) for the given keywords. 
Response from NewsAPI is first stored in the MongoDB collection: `news_raw`.
The `description` and `content` field from the response is filtered using regex to special characters. 
Filtered data along with other unchanged fields is stored in MongoDB collection: `news_filtered`.

##### 3) OMDb API
- Search for movies 

This endpoint will call the `getMovieData()` of MovieService class which calls OMDb API to fetch list of movies containing the given keywords (http://www.omdbapi.com/?s=).
For each movie item in the returned list, it calls another OMDd api to fetch entire movie data based on movieID (http://www.omdbapi.com/?i=). 
Response from OMDd movie API is first stored in the MongoDB collection: `movie_raw`.
The `Plot` field from the response is filtered using regex to special characters. 
Filtered data along with other unchanged fields is stored in MongoDB collection: `movie_filtered`.

> URI: http://localhost:9090/movies/search?s=Niagara&page=5
>
> method: POST

## Library reference
[Twitter4j](http://twitter4j.org/en/code-examples.html)
