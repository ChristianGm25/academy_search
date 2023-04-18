# academy_search

This repository contains the project developed for the first phase of the academy program at empathy.

It consists on a REST API application that indexes the movies of the IMDB datasets (https://www.imdb.com/interfaces/)
into elastic search and performs
operations with them.

To deploy the application the steps you should follow are the following:

- pull the repository locally in the main branch
- docker compose up --build -d
- Run the compose image