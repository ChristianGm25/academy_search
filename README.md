# academy_search

This repository contains the project developed for the first phase of the academy program at empathy.

It consists on a REST API application that indexes the movies of the IMDB datasets (https://www.imdb.com/interfaces/)
into elastic search and performs
operations with them.

To start working the first thing would be to create an index so, with elasticsearch and the project running you should
follow this steps:

- {DELETE} to localhost:8080/movies ; This will delete any indexes with the name we are using in case you want to start
  fresh
- {PUT} to localhost:8080/movies ; This will create an index with the name "movies"
    - {POST} to localhost:8080/movies in which we will pass as files "akas","basics","crew","episode","principals","
      ratings" and
      their corresponding files from the link above ; This will begin the indexing of the files asynchronously and
      return once the files have been uploaded.