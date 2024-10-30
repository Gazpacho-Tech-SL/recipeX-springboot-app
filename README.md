>[!NOTE]
> Currently under construction will update when app is done

# recipex API

## Overview

The **recipex API** is designed for managing recipes and facilitating user interactions. It provides a robust interface for creating, retrieving, updating, and deleting recipes, as well as handling user accounts and reviews.

## Version

**API Version:** 2.0  
**OpenAPI Specification Version:** 3.0.1

## Base URL

The base URL for the API is:

```
http://localhost:8080
```

## Swagger URL
```
http://localhost:8080/swagger-ui/index.html
```
## Authentication

The API does not currently require authentication. However, implementing user authentication is recommended for production use.

## API Endpoints

### 1. Recipe Management

#### Update an Existing Recipe

- **Endpoint:** `/recipe`
- **Method:** `PUT`
- **Description:** Update the details of a specific recipe.
- **Request Body:**
   - `RestUserRecipe` (JSON)
- **Responses:**
   - **200:** Recipe successfully updated.
   - **400:** Invalid input or recipe not found.

---

#### Delete a Recipe

- **Endpoint:** `/recipe`
- **Method:** `DELETE`
- **Description:** Remove a recipe using its ID.
- **Request Body:**
   - `Ids` (JSON)
- **Responses:**
   - **200:** Recipe successfully deleted.
   - **404:** Recipe not found.

---

#### Get Recipe by ID

- **Endpoint:** `/recipe/{recipeId}`
- **Method:** `GET`
- **Description:** Fetch details of a specific recipe using its unique ID.
- **Parameters:**
   - `recipeId` (string) - The ID of the recipe.
- **Responses:**
   - **200:** Recipe details retrieved.
   - **404:** Recipe not found.

---

#### Get Recipes by Title

- **Endpoint:** `/recipes/by-title/{title}`
- **Method:** `GET`
- **Description:** Search and retrieve recipes based on their title.
- **Parameters:**
   - `title` (string) - The title of the recipe.
- **Responses:**
   - **200:** Recipes retrieved.
   - **404:** No recipes found with the specified title.

---

#### Get Recipes by Tags

- **Endpoint:** `/recipes/by-tags`
- **Method:** `GET`
- **Description:** Search and retrieve recipes based on a list of tags.
- **Parameters:**
   - `tags` (array of strings) - List of tags to filter recipes.
- **Responses:**
   - **200:** Recipes retrieved.
   - **404:** No recipes found for the specified tags.

---

### 2. Review Management

#### Create a Recipe Review

- **Endpoint:** `/{recipeId}/reviews`
- **Method:** `POST`
- **Description:** Submit a review for the specified recipe.
- **Parameters:**
   - `recipeId` (string) - The ID of the recipe.
- **Request Body:**
   - `Review` (JSON)
- **Responses:**
   - **200:** Review successfully created.
   - **404:** Recipe not found.

---

#### Get Reviews for a Recipe

- **Endpoint:** `/{recipeId}/reviews`
- **Method:** `GET`
- **Description:** Retrieve all reviews associated with the specified recipe.
- **Parameters:**
   - `recipeId` (string) - The ID of the recipe.
- **Responses:**
   - **200:** Reviews retrieved successfully.
   - **404:** Recipe not found.

---

### 3. User Management

#### Create a New User

- **Endpoint:** `/user`
- **Method:** `POST`
- **Description:** Create a user by providing a username.
- **Request Body:**
   - `Username` (JSON)
- **Responses:**
   - **200:** User successfully created.
   - **400:** Invalid input.

---

#### Get User by ID

- **Endpoint:** `/user/{userId}`
- **Method:** `GET`
- **Description:** Retrieve user details using the unique user ID.
- **Parameters:**
   - `userId` (string) - The ID of the user.
- **Responses:**
   - **200:** User details retrieved.
   - **404:** User not found.

---

#### Delete a User

- **Endpoint:** `/user/{userId}`
- **Method:** `DELETE`
- **Description:** Delete the user associated with the specified user ID.
- **Parameters:**
   - `userId` (string) - The ID of the user.
- **Responses:**
   - **200:** User successfully deleted.
   - **404:** User not found.

---

### 4. Image Management

#### Get Image of a Recipe

- **Endpoint:** `/image/{recipeId}`
- **Method:** `GET`
- **Description:** Retrieve the image associated with the specified recipe.
- **Parameters:**
   - `recipeId` (string) - The ID of the recipe.
- **Responses:**
   - **200:** Image retrieved successfully.
   - **404:** Image or recipe not found.

---

#### Upload an Image for a Recipe

- **Endpoint:** `/image/{recipeId}`
- **Method:** `POST`
- **Description:** Upload an image for the specified recipe.
- **Parameters:**
   - `recipeId` (string) - The ID of the recipe.
- **Responses:**
   - **200:** Image uploaded successfully.
   - **404:** Recipe not found.

---

## Data Models

### RestUserRecipe

```json
{
  "recipeId": "string (uuid)",
  "userId": "string (uuid)",
  "title": "string",
  "description": "string",
  "ingredients": ["string"],
  "instructions": ["string"],
  "tags": ["string"],
  "imageUrl": "string",
  "imageUploadUrl": "string",
  "createdAt": "string (date-time)",
  "reviews": [
    {
      "reviewId": "string (uuid)",
      "recipeId": "string",
      "userId": "string",
      "rating": "integer",
      "comment": "string",
      "createdAt": "string (date-time)"
    }
  ],
  "averageRating": "number"
}
```

### RestUser

```json
{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
    "name": "string",
    "surname": "string",
    "email": "string",
    "password": "string",
  "recipes": [
    {
      "recipeId": "123e4567-e89b-12d3-a456-426614174001",
      "title": "string",
      "description": "string",
      "ingredients": ["string"],
      "instructions": ["string"],
      "tags": ["string"],
      "imageUrl": "string",
      "createdAt": "2024-10-22T10:00:00Z" 
    }
  ]
}

```

### Review

```json
{
  "recipeId": "string",
  "userId": "string",
  "rating": "integer",
  "comment": "string",
  "createdAt": "string (date-time)"
}
```
