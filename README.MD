# Movies API

This project is a RESTful API built with Spring Boot, designed to manage a database of movies, actors, and genres. It provides endpoints for CRUD operations on movies, actors, and genres, allowing clients to search, create, update, and delete entries with associated relationships. This API also includes pagination, validation, and custom exception handling for robust error management.

## Features

- **Movies**: Create, update, delete, and search for movies with details like title, release year, duration, and associated actors and genres.
- **Actors**: Manage actor records, including details like name and birth date, with relationships to movies.
- **Genres**: Manage genre records and associate them with movies.
- **Pagination**: Retrieve lists of entities (movies, actors, genres) with optional pagination.
- **Error Handling**: Custom exception handling for clear error messages on validation failures, not found resources, and more.
- **OpenAPI Documentation**: Integrated Swagger/OpenAPI documentation for API exploration.

## Technologies Used

- **Java 17**: Primary language
- **Spring Boot 3.x**: Framework for building the API
- **Spring Data JPA**: For database interaction
- **Hibernate**: ORM tool
- **SQLite Database**: A lightweight, file-based database suitable for development and testing (can be switched to other databases in production).
- **Swagger (OpenAPI)**: API documentation
- **Jakarta Validation**: Input validation

## Project Structure

- **Controllers**: Define REST endpoints for interacting with Movies, Actors, and Genres:
  - `ActorController`
  - `GenreController`
  - `MovieController`

- **Entities**: Represent database entities for movies, actors, and genres with JPA annotations:
  - `Movie`
  - `Actor`
  - `Genre`

- **Repositories**: Database access layers using Spring Data JPA:
  - `ActorRepository`
  - `GenreRepository`
  - `MovieRepository`

- **Services**: Core business logic for handling operations:
  - `ActorService`
  - `GenreService`
  - `MovieService`

- **Exceptions**: Custom exceptions and global exception handling:
  - `GlobalExceptionHandler`
  - `ResourceNotFoundException`

- **Configuration**: Configuration files and OpenAPI documentation setup:
  - `OpenAPIConfig`

## Endpoints

### Actor Endpoints

- `POST /api/actors` - Create a new actor
- `GET /api/actors` - Retrieve all actors (optional pagination)
- `GET /api/actors/{id}` - Retrieve an actor by ID
- `PATCH /api/actors/{id}` - Update an existing actor
- `DELETE /api/actors/{id}` - Delete an actor (supports forced deletion)
- `GET /api/actors/{actorId}/movies` - Retrieve movies associated with an actor

### Genre Endpoints

- `POST /api/genres` - Create a new genre
- `GET /api/genres` - Retrieve all genres (optional pagination)
- `GET /api/genres/{id}` - Retrieve a genre by ID
- `PATCH /api/genres/{id}` - Update an existing genre
- `DELETE /api/genres/{id}` - Delete a genre (supports forced deletion)

### Movie Endpoints

- `POST /api/movies` - Create a new movie, with optional genre and actor associations
- `GET /api/movies` - Retrieve all movies (optional filters for genre, actor, release year, and pagination)
- `GET /api/movies/{id}` - Retrieve a movie by ID
- `PATCH /api/movies/{id}` - Update an existing movie with optional genre and actor associations
- `DELETE /api/movies/{id}` - Delete a movie (supports forced deletion)
- `GET /api/movies/{movieId}/actors` - Retrieve actors associated with a movie
- `GET /api/movies/search` - Search movies by title

## Error Handling

- **GlobalExceptionHandler**: Centralized error handling for custom exceptions and validation errors.
- **Custom Exceptions**: 
  - `ResourceNotFoundException`: Thrown when a requested resource is not found.

## OpenAPI (Swagger) Documentation

This project includes Swagger documentation for exploring the API. You can access the Swagger UI after running the application by navigating to:
http://localhost:8080/swagger-ui.html

## Getting Started

### Prerequisites

- **Java 17** or higher
- **Maven** for project build and dependency management

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/movies-api.git

2. **Navigate to the project directory**:
    ```bash
    cd movies-api

3. **Build the project**:
    ```bash
    mvn clean install

4. **Run the application**:
    ```bash
    mvn spring-boot:run

The API will be available at http://localhost:8080.

Feel free to reach out with questions or for further assistance!
