# academy_search

This repository contains the project developed for the first phase of the academy program at empathy.

It consists on a REST API application that indexes the movies of the IMDB datasets (https://www.imdb.com/interfaces/)
into elastic search and performs operations with them.

# Deployment

To deploy the application the steps you should follow are the following:

- Pull the repository locally in the main branch: git clone https://github.com/ChristianGm25/academy_search.git
- Build the image project with the elastic search image: docker compose up --build -d
- Run the composed image

# Endpoints

Description of the endpoints:

- DELETE /movies, deletes the index
- PUT /movies, creates an index
- POST /movies, indexes the data from the files
- GET /movies, retrieves a list of movies without filters
- GET /movies/search, retrieves a list of movies with the specified filters as parameters
- POST /movies/recommended, retrieves a list of recommended movies depending on a list of movies passed as body
- GET /movies/genres, retrieves the movies associated to a genre passed as parameter