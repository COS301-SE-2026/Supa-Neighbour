# Supa-Neighbour - API Service Contract


> All endpoints require a valid JWT Bearer Token unless stated otherwise.
> Include the token in the request header as follows:
> ```
> Authorization: Bearer <token>
> ```


---


## Table of Contents


1. [Authentication](#1-authentication)
   - [POST /api/auth/login](#11-post-apiauthlogin)
   - [POST /api/auth/register](#12-post-apiauthregister)
2. [Dashboard](#2-dashboard)
   - [GET /api/dashboard](#21-get-apidashboard)
3. [Task Management](#3-task-management)
   - [POST /api/task/create](#31-post-apitaskcreate)
   - [PUT /api/task/{taskID}](#32-put-apitasktaskid)
   - [PATCH /api/task/{taskID}/complete](#33-patch-apitasktaskidcomplete)
   - [PATCH /api/task/{taskID}/status](#34-patch-apitasktaskidstatus)
   - [GET /api/task/assigned/{userID}](#35-get-apitaskassigneduserid)
   - [GET /api/task/created/{userID}](#36-get-apitaskcreateduserid)
4. [Chat](#4-chat)
   - [GET /api/chats/{userID}](#41-get-apichatsuserid)
   - [GET /api/chats/{chatID}/messages](#42-get-apichatschatidmessages)
5. [Matching & Helpers](#5-matching--helpers)
   - [GET /api/helpers/available](#51-get-apihelpersavailable)
   - [GET /api/helpers/{helperId}/profile](#52-get-apihelpershelperidprofile)
   - [POST /api/task/{taskId}/invite](#53-post-apitasktaskidinvite)
   - [POST /api/task/{taskId}/accept](#54-post-apitasktaskidaccept)
   - [POST /api/task/{taskId}/decline](#55-post-apitasktaskiddecline)
   - [GET /api/helpers/me/tasks](#56-get-apihelpersmetasks)
6. [Profile & Gamification](#6-profile--gamification)
   - [GET /api/users/me/profile](#61-get-apiusersmeprofile)
   - [PATCH /api/users/me/profile](#62-patch-apiusersmeprofile)
   - [GET /api/leaderboard](#63-get-apileaderboard)
   - [GET /api/users/me/achievements](#64-get-apiusersmeachievements)
   - [POST /api/tasks/{taskId}/rate](#65-post-apitaskstaskidrate)
7. [Community Bulletin Board](#7-community-bulletin-board)
   - [GET /api/bulletin/posts](#71-get-apibulletinposts)
   - [GET /api/bulletin/posts/{postId}](#72-get-apibulletinpostspostid)
   - [POST /api/bulletin/posts](#73-post-apibulletinposts)
   - [POST /api/upload/image](#74-post-apiuploadimage)
   - [DELETE /api/bulletin/posts/{postId}](#75-delete-apibulletinpostspostid)
   - [POST /api/bulletin/posts/{postId}/like](#76-post-apibulletinpostspostidlike)
   - [DELETE /api/bulletin/posts/{postId}/like](#77-delete-apibulletinpostspostidlike)
   - [GET /api/bulletin/posts/{postId}/comments](#78-get-apibulletinpostspostidcomments)
   - [POST /api/comments/bulletin/{postId}](#79-post-apicommentsbulletinpostid)
   - [POST /api/bulletin/posts/{postId}/dislike](#710-post-apibulletinpostspostiddislike)
   - [DELETE /api/bulletin/posts/{postId}/dislike](#711-delete-apireactionpostspostiddislike)
   - [DELETE /api/bulletin/posts/{postId}/comments/{commentId}](#712-delete-apicommentsbulletinpostspostidcommentid)
8. [Settings & Privacy](#8-settings-and-privacy)
   - [GET /api/settings/users/show-status](#81-get-apisettingsusersshow-status)
   - [POST /api/settings/users/show-status](#82-post-apisettingsusersshow-status)
   - [GET /api/settings/users/mode](#83-get-apisettingsusersmode)
   - [POST /api/settings/users/mode](#84-post-apisettingsusersmode)
   - [GET api/settings//users/{userId}/status](#85-get-apisettingsusersuseridstatus)
   - [GET /api/settings/users/information/{userId}](#86-get-apisettingsusersinformationuserid)
9. [Admin & Reporting](#9--admin--reporting)
   - [9.1 POST /api/auth/admin/login](#91-post-apiauthadminlogin)
   - [9.2 GET /api/admin/dashboard](#92-get-apiadmindashboard)
   - [9.3 GET /api/report](#94-get-apireport)
   - [9.4 GET /api/suggestion](#95-get-apisuggestion)
   - [9.5 PUT /api/report](#96-put-apireport)
   - [9.6 PATCH /api/report](#97-patch-apireport)
   - [9.7 PUT /api/user (deregister admin)](#98-put-apiuser-deregister-admin) 
   - [9.8 GET /api/report/me](#99-get-apireportme)
   - [9.9 POST /api/report/match](#910-post-apireportmatch)
10.  [Http Status Code Reference](#8-http-status-code-reference)
---


## 1. Authentication


---


### 1.1 POST /api/auth/login


| Field | Details |
|---|---|
| **Endpoint** | `/api/auth/login` |
| **Method** | `POST` |
| **Purpose** | Authenticates an existing user and returns a JWT token |
| **Authentication** | None — public endpoint |
| **Content-Type** | `application/json` |


#### Required Parameters


| Parameter | Type | Description |
|---|---|---|
| `email` | String | The user's registered email address |
| `password` | String | The user's password |


#### Request Body


```json
{
  "email": "user@example.co.za",
  "password": "p@ssword1"
}
```


#### Success Response — `200 OK`


```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userID": 2,
  "userTypeID": "3"
}
```


#### Error Responses


| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Invalid email or password | `{ "error": "Invalid email or password" }` |
| `422 Unprocessable Entity` | Missing required fields | `{ "error": "Email and password are required" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |


---


### 1.2 POST /api/auth/register


| Field | Details |
|---|---|
| **Endpoint** | `/api/auth/register` |
| **Method** | `POST` |
| **Purpose** | Registers a new user account on the platform |
| **Authentication** | None — public endpoint |
| **Content-Type** | `application/json` |


#### Required Parameters


| Parameter | Type | Description |
|---|---|---|
| `firstName` | String | User's first name |
| `surName` | String | User's surname |
| `gender` | char(1) | `M`, `F`, or `O` |
| `email` | String | User's email address — must be unique |
| `phoneNumber` | String | User's contact number |
| `address` | String | User's residential address for zone assignment |
| `dateOfBirth` | Date | Format: `YYYY-MM-DD` |
| `password` | String | Minimum 8 characters, mixed case, number, and special character |
| `username` | String | Unique display name |


#### Request Body


```json
{
  "firstName": "John",
  "surName": "Doe",
  "gender": "M",
  "email": "john.doe@example.com",
  "phoneNumber": "0821234567",
  "address": "14 Example Road, Pretoria",
  "dateOfBirth": "2000-07-13",
  "password": "Gdi3efnwxS!",
  "username": "johndoe"
}
```


#### Success Response — `201 Created`


```json
{
  "userID": 3,
  "email": "john.doe@example.com",
  "username": "johndoe"
}
```


#### Error Responses


| Status Code | Scenario | Response Body |
|---|---|---|
| `400 Bad Request` | Password does not meet strength requirements | `{ "error": "Password must be at least 8 characters with mixed case, a number, and a special character" }` |
| `409 Conflict` | Email or username already registered | `{ "error": "An account with this email already exists" }` |
| `422 Unprocessable Entity` | Missing required fields | `{ "error": "All fields are required" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |


---


## 2. Dashboard


---


### 2.1 GET /api/dashboard


| Field | Details |
|---|---|
| **Endpoint** | `/api/dashboard` |
| **Method** | `GET` |
| **Purpose** | Returns the authenticated user's dashboard data — active tasks in their neighbourhood zone, their own tasks, and nearby helper availability |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |


#### Request Headers


```
Authorization: Bearer <token>
```


#### Success Response — `200 OK`


```json
{
  "userID": 2,
  "username": "johndoe",
  "neighbourhoodZone": "Pretoria East",
  "activeTasks": [
    {
      "taskID": 14,
      "taskType": "plant_care",
      "startDate": "2026-06-01",
      "endDate": "2026-06-05",
      "requesterUsername": "jane123",
      "distanceMetres": 320,
      "status": "open"
    }
  ],
  "myTasks": [
    {
      "taskID": 9,
      "taskType": "pet_feeding",
      "status": "in_progress",
      "helperUsername": "mike_helps"
    }
  ],
  "nearbyHelpers": [
    {
      "userID": 7,
      "username": "mike_helps",
      "trustScore": 4.8,
      "level": "Gold",
      "distanceMetres": 150
    }
  ]
}
```


#### Error Responses


| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |


---


## 3. Task Management


---


### 3.1 POST /api/task/create


| Field | Details |
|---|---|
| **Endpoint** | `/api/task/create` |
| **Method** | `POST` |
| **Purpose** | Creates a new task request in the user's neighbourhood zone |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |


#### Required Parameters


| Parameter | Type | Description |
|---|---|---|
| `userID` | int | ID of the user creating the task |
| `taskType` | int | Task type ID (1=plant care, 2=pet feeding, 3=bin collection, 4=package collection, 5=home check-in) |
| `startDate` | Date | Date the task begins — format `YYYY-MM-DD` |
| `endDate` | Date / null | Date the task ends — null if single day |
| `needsSpecialist` | boolean | Whether a verified/specialist helper is required |
| `adminReview` | varchar(1) | Whether task requires admin review before publishing — `Y` or `N` |
| `specialInstructions` | String / null | Optional additional instructions for the helper |


#### Request Body


```json
{
  "userID": 3,
  "taskType": 2,
  "startDate": "2026-06-01",
  "endDate": "2026-06-05",
  "needsSpecialist": false,
  "adminReview": "N",
  "specialInstructions": "Cat is shy — please speak softly. Food is in the cupboard above the sink."
}
```


#### Success Response — `201 Created`


```json
{
  "message": "Task created successfully",
  "taskID": 21,
  "status": "open"
}
```


#### Error Responses


| Status Code | Scenario | Response Body |
|---|---|---|
| `400 Bad Request` | Invalid task type or date range | `{ "error": "Invalid task type or date range provided" }` |
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `422 Unprocessable Entity` | Missing required fields | `{ "error": "userID, taskType, and startDate are required" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |


---


### 3.2 PUT /api/task/{taskID}


| Field | Details |
|---|---|
| **Endpoint** | `/api/task/{taskID}` |
| **Method** | `PUT` |
| **Purpose** | Updates the details of an existing task — only allowed before the task has been accepted by a helper |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |


#### Path Parameters


| Parameter | Type | Description |
|---|---|---|
| `taskID` | String | The unique ID of the task to update |


#### Request Body


```json
{
  "userID": 2,
  "taskType": 3,
  "needsSpecialist": true,
  "startDate": "2026-06-02",
  "endDate": "2026-06-06",
  "specialInstructions": "Updated instructions here"
}
```


#### Success Response — `200 OK`


```json
{
  "message": "Task updated successfully",
  "taskID": 21
}
```


#### Error Responses


| Status Code | Scenario | Response Body |
|---|---|---|
| `400 Bad Request` | Invalid field value | `{ "error": "Invalid value provided for one or more fields" }` |
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `403 Forbidden` | Task already accepted — cannot be edited | `{ "error": "Task cannot be edited after it has been accepted by a helper" }` |
| `404 Not Found` | Task does not exist | `{ "error": "Task not found" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |


---


### 3.3 PATCH /api/task/{taskID}/complete


| Field | Details |
|---|---|
| **Endpoint** | `/api/task/{taskID}/complete` |
| **Method** | `PATCH` |
| **Purpose** | Marks a task as complete — called by the helper upon finishing the task |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |


#### Path Parameters


| Parameter | Type | Description |
|---|---|---|
| `taskID` | String | The unique ID of the task to complete |


#### Request Body


```json
{
  "helperID": 7,
  "photoEvidenceUrl": "https://storage.blob.core.windows.net/evidence/task21.jpg"
}
```


> `photoEvidenceUrl` is optional. If provided, the photo must be uploaded to Azure Blob Storage first and the returned URL passed here.


#### Success Response — `200 OK`


```json
{
  "message": "Task marked as complete successfully",
  "taskID": 21,
  "status": "pending_confirmation"
}
```


#### Error Responses


| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `403 Forbidden` | Caller is not the assigned helper | `{ "error": "Only the assigned helper can mark this task as complete" }` |
| `404 Not Found` | Task does not exist | `{ "error": "Task not found" }` |
| `409 Conflict` | Task already completed | `{ "error": "Task has already been marked as complete" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |


---


### 3.4 PATCH /api/task/{taskID}/status


| Field | Details |
|---|---|
| **Endpoint** | `/api/task/{taskID}/status` |
| **Method** | `PATCH` |
| **Purpose** | Updates the status of a task as it moves through its lifecycle |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |


#### Path Parameters


| Parameter | Type | Description |
|---|---|---|
| `taskID` | String | The unique ID of the task |


#### Valid Status Values


| Status | Description | Who sets it |
|---|---|---|
| `open` | Task is published and awaiting a helper | System (on create) |
| `matched` | A helper has been assigned | System (on acceptance) |
| `in_progress` | Task is actively being worked on | Helper |
| `pending_confirmation` | Helper has marked complete, awaiting requester confirmation | System |
| `completed` | Requester has confirmed task completion | Requester |
| `cancelled` | Task was cancelled before completion | Requester or Admin |
| `expired` | Task passed its end date without completion | System |


#### Request Body


```json
{
  "updatedByUserID": 2,
  "status": "in_progress"
}
```


#### Success Response — `200 OK`


```json
{
  "message": "Task status updated successfully",
  "taskID": 21,
  "previousStatus": "matched",
  "currentStatus": "in_progress"
}
```


#### Error Responses


| Status Code | Scenario | Response Body |
|---|---|---|
| `400 Bad Request` | Invalid status value | `{ "error": "Invalid status value. Must be one of: open, matched, in_progress, pending_confirmation, completed, cancelled, expired" }` |
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `403 Forbidden` | User not authorised to set this status | `{ "error": "You are not authorised to set this task status" }` |
| `404 Not Found` | Task does not exist | `{ "error": "Task not found" }` |
| `409 Conflict` | Invalid status transition | `{ "error": "Cannot transition from current status to the requested status" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |


---


### 3.5 GET /api/task/assigned/{userID}


| Field | Details |
|---|---|
| **Endpoint** | `/api/task/assigned/{userID}` |
| **Method** | `GET` |
| **Purpose** | Returns all tasks that have been assigned to the specified user as a helper |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |


#### Path Parameters


| Parameter | Type | Description |
|---|---|---|
| `userID` | int | The unique ID of the helper |


#### Query Parameters (optional)


| Parameter | Type | Description |
|---|---|---|
| `status` | String | Filter by task status e.g. `?status=in_progress` |


#### Request Headers


```
Authorization: Bearer <token>
```


#### Success Response — `200 OK`


```json
{
  "userID": 7,
  "assignedTasks": [
    {
      "taskID": 21,
      "taskType": "pet_feeding",
      "startDate": "2026-06-01",
      "endDate": "2026-06-05",
      "status": "in_progress",
      "requesterUsername": "jane123",
      "specialInstructions": "Cat is shy — please speak softly."
    },
    {
      "taskID": 18,
      "taskType": "plant_care",
      "startDate": "2026-05-20",
      "endDate": "2026-05-22",
      "status": "completed",
      "requesterUsername": "bob_neighbour",
      "specialInstructions": null
    }
  ]
}
```


#### Error Responses


| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `403 Forbidden` | Requesting another user's tasks without permission | `{ "error": "You are not authorised to view this user's tasks" }` |
| `404 Not Found` | User does not exist | `{ "error": "User not found" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |


---


### 3.6 GET /api/task/created/{userID}


| Field | Details |
|---|---|
| **Endpoint** | `/api/task/created/{userID}` |
| **Method** | `GET` |
| **Purpose** | Returns all tasks created (posted) by the specified user as a requester |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |


#### Path Parameters


| Parameter | Type | Description |
|---|---|---|
| `userID` | int | The unique ID of the requester |


#### Query Parameters (optional)


| Parameter | Type | Description |
|---|---|---|
| `status` | String | Filter by task status e.g. `?status=open` |


#### Request Headers


```
Authorization: Bearer <token>
```


#### Success Response — `200 OK`


```json
{
  "userID": 3,
  "createdTasks": [
    {
      "taskID": 21,
      "taskType": "pet_feeding",
      "startDate": "2026-06-01",
      "endDate": "2026-06-05",
      "status": "matched",
      "assignedHelperUsername": "mike_helps",
      "needsSpecialist": false,
      "specialInstructions": "Cat is shy — please speak softly."
    },
    {
      "taskID": 15,
      "taskType": "bin_collection",
      "startDate": "2026-05-15",
      "endDate": null,
      "status": "completed",
      "assignedHelperUsername": "sarah_nearby",
      "needsSpecialist": false,
      "specialInstructions": null
    }
  ]
}
```


#### Error Responses


| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `403 Forbidden` | Requesting another user's tasks without permission | `{ "error": "You are not authorised to view this user's tasks" }` |
| `404 Not Found` | User does not exist | `{ "error": "User not found" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |


---


## 4. Chat


---


### 4.1 GET /api/chats/{userID}


| Field | Details |
|---|---|
| **Endpoint** | `/api/chats/{userID}` |
| **Method** | `GET` |
| **Purpose** | Returns a list of all chat threads for the specified user — each thread corresponds to a task and shows the last message and unread count, similar to a WhatsApp chat list |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |


#### Path Parameters


| Parameter | Type | Description |
|---|---|---|
| `userID` | int | The unique ID of the user |


#### Request Headers


```
Authorization: Bearer <token>
```


#### Success Response — `200 OK`


```json
{
  "userID": 3,
  "chats": [
    {
      "chatID": 101,
      "taskID": 21,
      "taskType": "pet_feeding",
      "otherUserID": 7,
      "otherUsername": "mike_helps",
      "lastMessage": "I will be there at 8am!",
      "lastMessageTimestamp": "2026-05-19T07:45:00Z",
      "unreadCount": 2,
      "taskStatus": "in_progress"
    },
    {
      "chatID": 98,
      "taskID": 18,
      "taskType": "plant_care",
      "otherUserID": 5,
      "otherUsername": "sarah_nearby",
      "lastMessage": "Plants are all watered!",
      "lastMessageTimestamp": "2026-05-17T14:30:00Z",
      "unreadCount": 0,
      "taskStatus": "completed"
    }
  ]
}
```


#### Error Responses


| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `403 Forbidden` | Requesting another user's chats | `{ "error": "You are not authorised to view this user's chats" }` |
| `404 Not Found` | User does not exist | `{ "error": "User not found" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |


---


### 4.2 GET /api/chats/{chatID}/messages


| Field | Details |
|---|---|
| **Endpoint** | `/api/chats/{chatID}/messages` |
| **Method** | `GET` |
| **Purpose** | Returns the full message history for a specific chat thread — equivalent to opening a conversation in WhatsApp and seeing the entire message history between two users |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |


#### Path Parameters


| Parameter | Type | Description |
|---|---|---|
| `chatID` | int | The unique ID of the chat thread |


#### Query Parameters (optional)


| Parameter | Type | Description |
|---|---|---|
| `page` | int | Page number for pagination — default `1` |
| `limit` | int | Number of messages per page — default `50` |


#### Request Headers


```
Authorization: Bearer <token>
```


#### Success Response — `200 OK`


```json
{
  "chatID": 101,
  "taskID": 21,
  "taskType": "pet_feeding",
  "participants": [
    { "userID": 3, "username": "johndoe" },
    { "userID": 7, "username": "mike_helps" }
  ],
  "page": 1,
  "totalMessages": 14,
  "messages": [
    {
      "messageID": 501,
      "senderID": 3,
      "senderUsername": "johndoe",
      "content": "Hi Mike, the cat food is in the cupboard above the sink.",
      "type": "text",
      "timestamp": "2026-05-19T06:30:00Z",
      "read": true
    },
    {
      "messageID": 502,
      "senderID": 7,
      "senderUsername": "mike_helps",
      "content": "Got it, thanks! I will be there at 8am.",
      "type": "text",
      "timestamp": "2026-05-19T07:45:00Z",
      "read": true
    },
    {
      "messageID": 503,
      "senderID": 7,
      "senderUsername": "mike_helps",
      "content": "https://storage.blob.core.windows.net/chat-media/task21-update.jpg",
      "type": "image",
      "timestamp": "2026-05-19T08:15:00Z",
      "read": false
    }
  ]
}
```


---
 
## 5. Matching & Helpers
 
---
 
### 5.1 GET /api/helpers/available
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/helpers/available` |
| **Method** | `GET` |
| **Purpose** | Returns a ranked list of available helpers in the requester's neighbourhood zone for a given task. Backs the Available Helpers screen (UC3-US1) |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
 
#### Query Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `taskId` | int | **Required.** The posted task to match helpers against |
| `verifiedOnly` | boolean | Optional — when `true`, only verified helpers are returned. Default `false` |
| `zoneScope` | String | Optional — how wide to search: `complex`, `street`, or `zone`. Defaults to widening outward from the requester |
 
#### Request Headers
 
```
Authorization: Bearer <token>
```
 
#### Example Request
 
```
GET /api/helpers/available?taskId=12&verifiedOnly=true
```
 
#### Success Response — `200 OK`
 
```json
{
  "taskId": 12,
  "helpers": [
    {
      "helperId": 5,
      "displayName": "David W.",
      "trustScore": 4.8,
      "level": "Gold",
      "skills": ["Home Repair", "Transportation Support"],
      "distanceTier": "Same complex",
      "available": true,
      "verified": true,
      "compatibilityScore": 95
    }
  ]
}
```
 
> When no helpers are available, the endpoint still returns `200 OK` with an empty array — `{ "taskId": 12, "helpers": [] }` — so the client can render the friendly "no helpers nearby" empty state (AC6).
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `400 Bad Request` | `taskId` query parameter missing | `{ "error": "taskId is required" }` |
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `404 Not Found` | Task or neighbourhood zone does not exist | `{ "error": "Task not found" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
---
 
### 5.2 GET /api/helpers/{helperId}/profile
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/helpers/{helperId}/profile` |
| **Method** | `GET` |
| **Purpose** | Returns a helper's public profile so a requester can review them before inviting (UC3-US2). The helper's exact address and contact details are deliberately excluded (R4.1.2) |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
 
#### Path Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `helperId` | int | The unique ID of the helper to preview |
 
#### Query Parameters (optional)
 
| Parameter | Type | Description |
|---|---|---|
| `taskId` | int | When provided, the response reflects the helper's availability for that task's date and time window |
 
#### Request Headers
 
```
Authorization: Bearer <token>
```
 
#### Example Request
 
```
GET /api/helpers/5/profile?taskId=12
```
 
#### Success Response — `200 OK`
 
```json
{
  "helperId": 5,
  "displayName": "David W.",
  "level": "Gold",
  "trustScore": 4.8,
  "completedTasks": 27,
  "skills": ["Home Repair", "Transportation Support"],
  "reviews": [
    { "rating": "Excellent", "snippet": "Reliable and on time", "date": "2026-05-01" }
  ],
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `404 Not Found` | Helper does not exist | `{ "error": "Helper not found" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
---
 
### 5.3 POST /api/task/{taskId}/invite
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/task/{taskId}/invite` |
| **Method** | `POST` |
| **Purpose** | Sends a task invitation to a chosen helper and marks them as "Invited" on the Available Helpers list (UC3-US2 AC4) |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
 
#### Path Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `taskId` | int | The unique ID of the task the helper is being invited to |
 
#### Required Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `helperId` | int | ID of the helper being invited |
 
#### Request Body
 
```json
{
  "helperId": 5
}
```
 
#### Success Response — `201 Created`
 
```json
{
  "message": "Invitation sent",
  "taskId": 12,
  "helperId": 5,
  "status": "Invited"
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `403 Forbidden` | Caller is not the owner of the task | `{ "error": "You are not authorised to invite helpers to this task" }` |
| `404 Not Found` | Task or helper does not exist | `{ "error": "Task not found" }` |
| `409 Conflict` | Helper has already been invited to this task | `{ "error": "Helper already invited" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |


### 5.4 POST /api/task/{taskId}/accept


| Field | Details |
|---|---|
| **Endpoint** | `/api/task/{taskId}/accept` |
| **Method** | `POST` |
| **Purpose** | Called by the **helper** when they see an open task listed on their side and choose to accept it. Creates a `task_invoice_table` row, locks the task to that helper, and triggers the address reveal flow (UC6-US1) — the requester's exact address becomes visible to the helper. |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |

                             
#### Path Parameters


| Parameter | Type | Description |
|---|---|---|
| `taskId` | int | The unique ID of the task being accepted |


> No request body is needed — the helper is resolved from the JWT.


#### Request Headers


```
Authorization: Bearer <token>
```


#### Success Response — `201 Created`


```json
{
  "message": "Task accepted successfully.",
  "taskId": 12,
  "status": "Accepted",
  "addressRevealed": true
}
```


> `addressRevealed: true` signals to the Flutter client to navigate to the Address Confirmation screen (UC6-US1).


#### Error Responses


| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `403 Forbidden` | Authenticated user is not registered as a helper | `{ "error": "User is not a helper" }` |
| `404 Not Found` | Task does not exist | `{ "error": "Task not found" }` |
| `409 Conflict` | Task has already been accepted by another helper | `{ "error": "This task has already been accepted" }` |
| `422 Unprocessable Entity` | Task is not in an open/available state | `{ "error": "Task is not available for acceptance" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |


### 5.5 POST /api/task/{taskId}/decline
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/task/{taskId}/decline` |
| **Method** | `POST` |
| **Purpose** | Called by the **helper** when they see an open task listed on their side and choose to decline it. The task remains open and visible to other helpers. |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
 
#### Path Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `taskId` | int | The unique ID of the task being declined |
 
> No request body is needed — the helper is resolved from the JWT.
 
#### Request Headers
 
```
Authorization: Bearer <token>
```
 
#### Success Response — `200 OK`
 
```json
{
  "message": "Task declined.",
  "taskId": 12,
  "status": "Declined"
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `403 Forbidden` | Authenticated user is not registered as a helper | `{ "error": "User is not a helper" }` |
| `404 Not Found` | Task does not exist | `{ "error": "Task not found" }` |
| `409 Conflict` | Task has already been accepted or declined | `{ "error": "Task cannot be declined in its current state" }` |
| `422 Unprocessable Entity` | Task is not in an open/available state | `{ "error": "Task is not available for declining" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |


### 5.6 GET /api/helpers/me/tasks
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/helpers/me/tasks` |
| **Method** | `GET` |
| **Purpose** | Returns the full task history for the authenticated helper — all tasks they've been invited to, are currently assigned to, or have previously completed. Backs the helper-side task list view. The helper is resolved from the JWT. |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
 
#### Query Parameters (optional)
 
| Parameter | Type | Description |
|---|---|---|
| `status` | String | Filter by task status: `Invited`, `Accepted`, `Completed`, `Declined`. Omit to return all. |
| `limit` | int | Number of results to return. Default `20` |
| `offset` | int | Pagination offset. Default `0` |
 
#### Request Headers
 
```
Authorization: Bearer <token>
```
 
#### Example Request
 
```
GET /api/helpers/me/tasks?status=Completed&limit=10
```
 
#### Success Response — `200 OK`
 
```json
{
  "helperId": 5,
  "total": 27,
  "tasks": [
    {
      "taskId": 1,
      "taskType": "Home Repair",
      "status": "Completed",
      "startDate": "2026-05-01",
      "endDate": "2026-05-01",
      "neighbourhood": "Greenfield",
      "xpAwarded": 600
    },
    {
      "taskId": 8,
      "taskType": "Pet Care",
      "status": "Invited",
      "startDate": "2026-07-10",
      "endDate": "2026-07-10",
      "neighbourhood": "Riverside",
      "xpAwarded": null
    }
  ]
}
```
 
> `xpAwarded` is `null` for tasks not yet completed.  
> The requester's exact address is **not** included in this list view — it is only revealed on the individual task detail screen after acceptance (R4.1.1).
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `400 Bad Request` | Invalid `status` value supplied | `{ "error": "status must be one of: Invited, Accepted, Completed, Declined" }` |
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `403 Forbidden` | Authenticated user is not registered as a helper | `{ "error": "User is not a helper" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |


---
 
## 6. Profile & Gamification
 
---
 
### 6.1 GET /api/users/me/profile
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/users/me/profile` |
| **Method** | `GET` |
| **Purpose** | Returns the authenticated user's own profile — neighbourhood zone, progression level, XP, trust score, skills/tags, availability, achievements, and completed-task history (UC5-US1). The user is resolved from the JWT |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
 
#### Request Headers
 
```
Authorization: Bearer <token>
```
 
#### Success Response — `200 OK`
 
```json
{
  "userId": 5,
  "displayName": "David W.",
  "neighbourhood": "Greenfield",
  "level": "Gold",
  "currentXp": 4500,
  "nextLevelXp": 5000,
  "trustScore": 4.8,
  "skills": ["Home Repair", "Transportation Support"],
  "availability": ["Weekday evenings", "Weekends"],
  "achievements": [
    { "badgeId": 5, "name": "Home Repair Specialist", "awardedOn": "2026-05-01" }
  ],
  "completedTasks": 27,
  "recentTasks": [
    { "taskId": 1, "type": "Home Repair", "endDate": "2026-05-01" }
  ]
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
---
 
### 6.2 PATCH /api/users/me/profile
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/users/me/profile` |
| **Method** | `PATCH` |
| **Purpose** | Updates the user's editable profile fields — skills/tags and availability preferences (UC5-US1 AC3 & AC6, requirement R1.2.3) |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
 
#### Required Parameters
 
At least one of the following must be supplied.
 
| Parameter | Type | Description |
|---|---|---|
| `skills` | Array | Array of task-type names or IDs the user can help with |
| `availability` | Array | Array of availability preference strings e.g. `"Weekends"` |
 
#### Request Body
 
```json
{
  "skills": ["Home Repair", "Pet Care"],
  "availability": ["Weekends"]
}
```
 
#### Success Response — `200 OK`
 
```json
{
  "message": "Profile updated",
  "skills": ["Home Repair", "Pet Care"],
  "availability": ["Weekends"]
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `400 Bad Request` | An unrecognised skill or value was supplied | `{ "error": "Invalid skill id" }` |
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `422 Unprocessable Entity` | Neither `skills` nor `availability` was provided | `{ "error": "At least one of skills or availability must be provided" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
---
 
### 6.3 GET /api/leaderboard
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/leaderboard` |
| **Method** | `GET` |
| **Purpose** | Returns the ranked top helpers in the user's neighbourhood zone, plus the user's own rank — pinned even if it falls outside the top N. Backs the Leaderboard screen (UC5-US2) |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
 
#### Query Parameters (optional)
 
| Parameter | Type | Description |
|---|---|---|
| `rankBy` | String | Ranking metric: `trustScore` or `xp`. Default `trustScore` |
| `limit` | int | Number of top helpers to return. Default `10` |
 
#### Request Headers
 
```
Authorization: Bearer <token>
```
 
#### Example Request
 
```
GET /api/leaderboard?rankBy=trustScore&limit=10
```
 
#### Success Response — `200 OK`
 
```json
{
  "neighbourhood": "Greenfield",
  "rankBy": "trustScore",
  "leaderboard": [
    { "rank": 1, "userId": 5, "displayName": "David W.", "level": "Gold", "score": 4.8 }
  ],
  "currentUser": {
    "rank": 14,
    "userId": 5,
    "displayName": "David W.",
    "level": "Gold",
    "score": 4.8
  }
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `400 Bad Request` | Invalid `rankBy` value | `{ "error": "rankBy must be one of: trustScore, xp" }` |
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
---
 
### 6.4 GET /api/users/me/achievements
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/users/me/achievements` |
| **Method** | `GET` |
| **Purpose** | Returns all defined achievements grouped into earned and unearned sections — with the award date for earned ones and progress toward unearned ones (UC5-US3). The user is resolved from the JWT |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
 
#### Request Headers
 
```
Authorization: Bearer <token>
```
 
#### Success Response — `200 OK`
 
```json
{
  "earned": [
    {
      "badgeId": 5,
      "name": "Home Repair Specialist",
      "description": "Complete 10 home repair tasks",
      "awardedOn": "2026-05-01"
    }
  ],
  "unearned": [
    {
      "badgeId": 2,
      "name": "Pet Care Helper",
      "description": "Complete 5 pet care tasks",
      "progress": "3/5"
    }
  ]
}
```


#### Error Responses


| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `403 Forbidden` | User is not a participant in this chat | `{ "error": "You are not authorised to view this chat" }` |
| `404 Not Found` | Chat does not exist | `{ "error": "Chat not found" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |


### 6.5 POST /api/tasks/{taskId}/rate
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/tasks/{taskId}/rate` |
| **Method** | `POST` |
| **Purpose** | Called by the **requester** after a task is marked complete. Submits a rating category and a free-text review for the helper. Writes to `task_invoice_table.helper_rating_review` (FK into `rating_table`) and a review snippet field. Contributes to the helper's overall trust score (R5.3.1). |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
 
#### Path Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `taskId` | int | The unique ID of the completed task being rated |
 
#### Request Body
 
| Field | Type | Description |
|---|---|---|
| `rating` | String | **Required.** One of the valid `rating_table` categories: `"Outstanding"`, `"Excellent"`, `"Very Good"`, `"Good"`, `"Average"` |
| `reviewSnippet` | String | Optional. Short free-text comment (max 300 characters) displayed on the helper's profile |
 
```json
{
  "rating": "Excellent",
  "reviewSnippet": "Arrived on time and did a great job fixing the sink."
}
```
 
#### Success Response — `200 OK`
 
```json
{
  "message": "Rating submitted successfully.",
  "taskId": 12,
  "rating": "Excellent",
  "reviewSnippet": "Arrived on time and did a great job fixing the sink."
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `400 Bad Request` | `rating` missing or not a valid category | `{ "error": "rating must be one of: Outstanding, Excellent, Very Good, Good, Average" }` |
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `403 Forbidden` | Caller is not the requester for this task | `{ "error": "You are not authorised to rate this task" }` |
| `404 Not Found` | Task does not exist | `{ "error": "Task not found" }` |
| `409 Conflict` | Task has already been rated by this requester | `{ "error": "You have already submitted a rating for this task" }` |
| `422 Unprocessable Entity` | Task is not yet in `Completed` status | `{ "error": "Task must be completed before it can be rated" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |


## 7. Community Bulletin Board
 
### 7.1 GET /api/bulletin/posts
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/bulletin/posts` |
| **Method** | `GET` |
| **Purpose** | Returns the community bulletin board feed — posts from the caller's neighbourhood zone, with optional category filtering, keyword search, and pagination |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
 
#### Query Parameters (optional)
 
| Parameter | Type | Description |
|---|---|---|
| `category` | String | Filter by post category. ⚠️ Pending schema addition — see note above |
| `search` | String | Free-text keyword search against `post_content` |
| `page` | int | Page number for pagination — default `1` |
| `limit` | int | Number of posts per page — default `20` |
 
#### Request Headers
 
```
Authorization: Bearer <token>
```
 
#### Example Request
 
```
GET /api/bulletin/posts?category=lost_found&search=cat&page=1&limit=20
```
 
#### Success Response — `200 OK`
 
```json
{
  "neighbourhoodZone": "Pretoria East",
  "page": 1,
  "totalPosts": 42,
  "posts": [
    {
      "postId": 14,
      "userId": 3,
      "authorUsername": "johndoe",
      "postContent": "Has anyone seen a grey tabby cat near Example Road? Answers to Milo.",
      "mediaUrl": "https://parseandcoblob.blob.core.windows.net/posts/abc123.jpg",
      "category": "lost_found",
      "helpfulCount": 5,
      "disHelpfulCount": 0,
      "commentCount": 2,
      "createdAt": "2026-07-10T09:15:00Z",
      "updatedAt": "2026-07-10T09:15:00Z"
    }
  ]
}
```
 
> When no posts match the given filters, the endpoint still returns `200 OK` with an empty array — `{ "neighbourhoodZone": "Pretoria East", "page": 1, "totalPosts": 0, "posts": [] }`.
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
---
 
### 7.2 GET /api/bulletin/posts/{postId}
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/bulletin/posts/{postId}` |
| **Method** | `GET` |
| **Purpose** | Returns a single bulletin board post by ID, for a permalink/detail view |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
 
#### Path Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `postId` | int | The unique ID of the post |
 
#### Request Headers
 
```
Authorization: Bearer <token>
```
 
#### Success Response — `200 OK`
 
```json
{
  "postId": 14,
  "userId": 3,
  "authorUsername": "johndoe",
  "postContent": "Has anyone seen a grey tabby cat near Example Road? Answers to Milo.",
  "mediaUrl": "https://parseandcoblob.blob.core.windows.net/posts/abc123.jpg",
  "category": "lost_found",
  "helpfulCount": 5,
  "disHelpfulCount": 0,
  "commentCount": 2,
  "createdAt": "2026-07-10T09:15:00Z",
  "updatedAt": "2026-07-10T09:15:00Z"
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `404 Not Found` | Post does not exist | `{ "error": "Post not found" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
---
 
### 7.3 POST /api/bulletin/posts
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/bulletin/posts` |
| **Method** | `POST` |
| **Purpose** | Creates a new bulletin board post in the caller's neighbourhood zone |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
| **Dependency** | ⚠️ If `mediaUrl` is supplied, it must already be a valid Blob Storage URL obtained from **7.4 POST /api/upload/image** — this endpoint does not accept raw image files itself. Blocked until the `posts` container is provisioned |
 
#### Required Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `postContent` | String | **Required.** The text content of the post |
| `mediaUrl` | String | Optional. A Blob Storage URL returned by `/api/upload/image` |
| `category` | String | Optional. ⚠️ Pending schema addition — see note above |
 
#### Request Body
 
```json
{
  "postContent": "Has anyone seen a grey tabby cat near Example Road? Answers to Milo.",
  "mediaUrl": "https://parseandcoblob.blob.core.windows.net/posts/abc123.jpg",
  "category": "lost_found"
}
```
 
#### Success Response — `201 Created`
 
```json
{
  "postId": 14,
  "userId": 3,
  "postContent": "Has anyone seen a grey tabby cat near Example Road? Answers to Milo.",
  "mediaUrl": "https://parseandcoblob.blob.core.windows.net/posts/abc123.jpg",
  "category": "lost_found",
  "createdAt": "2026-07-10T09:15:00Z"
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `400 Bad Request` | `mediaUrl` supplied but not a recognised Blob Storage URL | `{ "error": "mediaUrl must be a valid uploaded image URL" }` |
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `422 Unprocessable Entity` | `postContent` missing or empty | `{ "error": "postContent is required" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
---
 
### 7.4 POST /api/upload/image
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/upload/image` |
| **Method** | `POST` |
| **Purpose** | Uploads an image file to Azure Blob Storage and returns its URL, for use as a post's `mediaUrl` (see 7.3) |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `multipart/form-data` |
| **Dependency** | ⚠️ Blocked until the `posts` Blob Storage container is provisioned by Gendac |
 
#### Required Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `file` | File (multipart) | **Required.** The image file to upload. Supported types: `.jpg`, `.jpeg`, `.png`. Max size: 5MB |
 
#### Request Headers
 
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```
 
#### Success Response — `201 Created`
 
```json
{
  "imageUrl": "https://parseandcoblob.blob.core.windows.net/posts/abc123.jpg"
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `400 Bad Request` | Missing file, unsupported file type, or file exceeds size limit | `{ "error": "File must be a jpg or png under 5MB" }` |
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `500 Internal Server Error` | Blob Storage upload failed or unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
---
 
### 7.5 DELETE /api/bulletin/posts/{postId}
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/bulletin/posts/{postId}` |
| **Method** | `DELETE` |
| **Purpose** | Deletes a bulletin board post. Owner only. Cascades to associated comments and reactions (`comments_table` and `reaction_table` both have `on delete cascade` on `post_id`) |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
 
#### Path Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `postId` | int | The unique ID of the post to delete |
 
#### Request Headers
 
```
Authorization: Bearer <token>
```
 
#### Success Response — `200 OK`
 
```json
{
  "message": "Post deleted",
  "postId": 14
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `403 Forbidden` | Caller is not the post's owner | `{ "error": "You are not authorised to delete this post" }` |
| `404 Not Found` | Post does not exist | `{ "error": "Post not found" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
---
 
### 7.6 POST /api/bulletin/posts/{postId}/like
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/bulletin/posts/{postId}/like` |
| **Method** | `POST` |
| **Purpose** | Adds a "like" reaction to a post on behalf of the caller. Once per user per post |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
| **Dependency** | ⚠️ Requires `reaction_type` column on `reaction_table` — see schema note above |
 
#### Path Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `postId` | int | The unique ID of the post being reacted to |
 
#### Request Headers
 
```
Authorization: Bearer <token>
```
 
#### Success Response — `201 Created`
 
```json
{
  "message": "Reaction added",
  "postId": 14,
  "reactionType": "like",
  "helpfulCount": 6
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `404 Not Found` | Post does not exist | `{ "error": "Post not found" }` |
| `409 Conflict` | Caller has already reacted to this post (either helpful or dis-helpful) | `{ "error": "You have already reacted to this post" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
---
 
### 7.7 DELETE /api/bulletin/posts/{postId}/like
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/bulletin/posts/{postId}/like` |
| **Method** | `DELETE` |
| **Purpose** | Removes the caller's "like" reaction from a post |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
| **Dependency** | ⚠️ Requires `reaction_type` column on `reaction_table` — see schema note above |
 
#### Path Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `postId` | int | The unique ID of the post |
 
#### Request Headers
 
```
Authorization: Bearer <token>
```
 
#### Success Response — `200 OK`
 
```json
{
  "message": "Reaction removed",
  "postId": 14,
  "helpfulCount": 5
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `404 Not Found` | Post does not exist, or caller has no "helpful" reaction on it to remove | `{ "error": "No helpful reaction found to remove" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
---
 
### 7.8 GET /api/bulletin/posts/{postId}/comments
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/bulletin/posts/{postId}/comments` |
| **Method** | `GET` |
| **Purpose** | Returns comments for a post, including threaded replies via `parentCommentId` |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
 
#### Path Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `postId` | int | The unique ID of the post |
 
#### Query Parameters (optional)
 
| Parameter | Type | Description |
|---|---|---|
| `page` | int | Page number for pagination — default `1` |
| `limit` | int | Number of comments per page — default `20` |
 
#### Request Headers
 
```
Authorization: Bearer <token>
```
 
#### Success Response — `200 OK`
 
```json
{
  "postId": 14,
  "page": 1,
  "totalComments": 2,
  "comments": [
    {
      "commentId": 31,
      "userId": 7,
      "authorUsername": "mike_helps",
      "parentCommentId": null,
      "commentContent": "I think I saw a cat like that near the park this morning!",
      "createdAt": "2026-07-10T10:00:00Z",
      "updatedAt": null
    },
    {
      "commentId": 32,
      "userId": 3,
      "authorUsername": "johndoe",
      "parentCommentId": 31,
      "commentContent": "Which park? That would be so helpful, thank you!",
      "createdAt": "2026-07-10T10:05:00Z",
      "updatedAt": null
    }
  ]
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `404 Not Found` | Post does not exist | `{ "error": "Post not found" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
---
 
### 7.9 POST /api/comments/bulletin/{postId}
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/comments/bulletin/{postId}` |
| **Method** | `POST` |
| **Purpose** | Adds a comment to a post. Supply `parentCommentId` to post a threaded reply to another comment |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
 
#### Path Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `postId` | int | The unique ID of the post being commented on |
 
#### Required Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `commentContent` | String | **Required.** The text of the comment |
| `parentCommentId` | int | Optional. ID of the comment being replied to, for threaded replies |
 
#### Request Body
 
```json
{
  "commentContent": "Which park? That would be so helpful, thank you!",
  "parentCommentId": 31
}
```
 
#### Success Response — `201 Created`
 
```json
{
  "commentId": 32,
  "postId": 14,
  "userId": 3,
  "parentCommentId": 31,
  "commentContent": "Which park? That would be so helpful, thank you!",
  "createdAt": "2026-07-10T10:05:00Z"
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `404 Not Found` | Post does not exist, or `parentCommentId` does not exist on this post | `{ "error": "Post not found" }` |
| `422 Unprocessable Entity` | `commentContent` missing or empty | `{ "error": "commentContent is required" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
---
 
### 7.10 POST /api/bulletin/posts/{postId}/dislike
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/bulletin/posts/{postId}/dislike` |
| **Method** | `POST` |
| **Purpose** | Adds a "dislike" reaction to a post on behalf of the caller. Once per user per post |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
| **Dependency** | ⚠️ Requires `reaction_type` column on `reaction_table` — see schema note above |
 
#### Path Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `postId` | int | The unique ID of the post being reacted to |
 
#### Request Headers
 
```
Authorization: Bearer <token>
```
 
#### Success Response — `201 Created`
 
```json
{
  "message": "Reaction added",
  "postId": 14,
  "reactionType": "dis-helpful",
  "disHelpfulCount": 1
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `404 Not Found` | Post does not exist | `{ "error": "Post not found" }` |
| `409 Conflict` | Caller has already reacted to this post (either helpful or dis-helpful) | `{ "error": "You have already reacted to this post" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
---
 
### 7.11 DELETE /api/reaction/posts/{postId}/dislike
 
| Field | Details |
|---|---|
| **Endpoint** | /api/reaction/posts/{postId}/dislike` |
| **Method** | `DELETE` |
| **Purpose** | Removes the caller's "dislike" reaction from a post |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
| **Dependency** | ⚠️ Requires `reaction_type` column on `reaction_table` — see schema note above |
 
#### Path Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `postId` | int | The unique ID of the post |
 
#### Request Headers
 
```
Authorization: Bearer <token>
```
 
#### Success Response — `200 OK`
 
```json
{
  "message": "Reaction removed",
  "postId": 14,
  "disHelpfulCount": 0
}
```
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `404 Not Found` | Post does not exist, or caller has no "dis-helpful" reaction on it to remove | `{ "error": "No dis-helpful reaction found to remove" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
### 7.12 DELETE /api/comments/bulletin/posts/{postId}/{commentId}
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/comments/bulletin/posts/{postId}/{commentId}` |
| **Method** | `DELETE` |
| **Purpose** | Deletes a comment under a post. Works for both top-level comments and threaded replies, as long as the comment belongs to the specified post |
| **Authentication** | JWT Bearer Token required |
| **Content-Type** | `application/json` |
 
#### Path Parameters
 
| Parameter | Type | Description |
|---|---|---|
| `postId` | int | The unique ID of the post the comment belongs to |
| `commentId` | int | The unique ID of the comment to delete |
 
#### Request Headers
 
```
Authorization: Bearer <token>
```
 
#### Success Response — `204 No Content`
 
No response body.
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `403 Forbidden` | Caller is not the author of the comment | `{ "error": "You can only delete your own comments" }` |
| `404 Not Found` | Comment does not exist, or does not belong to `postId` | `{ "error": "Comment not found" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |
 
---

## 8. Settings and Privacy
### 8.1 GET /api/settings/users/show-status

| Field              | Details                                                                                          |
| ------------------ | ------------------------------------------------------------------------------------------------ |
| **Endpoint**       | `/api/settings/users/status`                                                                     |
| **Method**         | `GET`                                                                                            |
| **Purpose**        | Returns whether a user's online status is visible and, if visible, their current online presence |
| **Authentication** | Firebase ID Token required                                                                       |
| **Content-Type**   | `application/json`                                                                               |

#### Path Parameters

| Parameter | Type | Description                                                      |
| --------- | ---- | ---------------------------------------------------------------- |
| `userId`  | int  | The unique ID of the user whose online status is being requested |

#### Request Headers

```http
Authorization: Bearer <Firebase ID Token>
```

#### Success Response — `200 OK` (Visibility Disabled)

```json
{
  "visible": false
}
```

#### Success Response — `200 OK` (Visibility Enabled)

```json
{
  "visible": true,
  "online": true,
  "lastSeen": "2026-07-21T09:15:00Z"
}
```

#### Notes

* `visible` reflects the **target user's** `Show_status` setting, not the requester's.
* `online` is calculated server-side using `last_active_at` and a configured timeout (e.g. 2 minutes).
* `lastSeen` is only included when `visible` is `true`.

---

### 8.2 POST /api/settings/users/show-status

| Field              | Details                                                              |
| ------------------ | -------------------------------------------------------------------- |
| **Endpoint**       | `/api/settings/users/show-status`                                 |
| **Method**         | `POST`                                                               |
| **Purpose**        | Updates the authenticated user's online status visibility preference |
| **Authentication** | Firebase ID Token required                                           |
| **Content-Type**   | `application/json`                                                   |

#### Request Headers

```http
Authorization: Bearer <Firebase ID Token>
```

#### Request Body

```json
{
  "showStatus": true
}
```

#### Success Response — `200 OK`

```json
{
  "showStatus": true
}
```

#### Error Responses

| Status Code        | Scenario                                 | Response Body                             |
| ------------------ | ---------------------------------------- | ----------------------------------------- |
| `400 Bad Request`  | `showStatus` is missing or not a boolean | `{ "error": "Invalid showStatus value" }` |
| `401 Unauthorized` | Missing or invalid Firebase ID token     | `{ "error": "Unauthorized" }`             |

---

### 8.3 GET /api/settings/users/mode

| Field              | Details                                                    |
| ------------------ | ---------------------------------------------------------- |
| **Endpoint**       | `/api/settings/users/mode`                              |
| **Method**         | `GET`                                                      |
| **Purpose**        | Retrieves the authenticated user's display mode preference |
| **Authentication** | Firebase ID Token required                                 |
| **Content-Type**   | `application/json`                                         |

#### Request Headers

```http
Authorization: Bearer <Firebase ID Token>
```

#### Success Response — `200 OK`

```json
{
  "mode": "dark"
}
```

#### Notes

* `mode` is one of `"light"` or `"dark"` (subject to the database constraint defined for `settings_table.Mode`).

---

### 8.4 POST /api/settings/users/mode

| Field              | Details                                                  |
| ------------------ | -------------------------------------------------------- |
| **Endpoint**       | `/api/settings/users/mode`                            |
| **Method**         | `POST`                                                   |
| **Purpose**        | Updates the authenticated user's display mode preference |
| **Authentication** | Firebase ID Token required                               |
| **Content-Type**   | `application/json`                                       |

#### Request Headers

```http
Authorization: Bearer <Firebase ID Token>
```

#### Request Body

```json
{
  "mode": "dark"
}
```

#### Success Response — `200 OK`

```json
{
  "mode": "dark"
}
```

#### Error Responses

| Status Code        | Scenario                                             | Response Body                       |
| ------------------ | ---------------------------------------------------- | ----------------------------------- |
| `400 Bad Request`  | `mode` is missing or not one of the supported values | `{ "error": "Invalid mode value" }` |
| `401 Unauthorized` | Missing or invalid Firebase ID token                 | `{ "error": "Unauthorized" }`       |

--- 

### 8.5 GET /api/settings/users/{userId}/status

| Field              | Details                                                                |
| ------------------ | ----------------------------------------------------------------------- |
| **Endpoint**       | `/api/settings/users/{userId}/status`                                   |
| **Method**         | `GET`                                                                    |
| **Purpose**        | Retrieves another user's online status visibility/presence, respecting that user's `showStatus` preference |
| **Authentication** | Firebase ID Token required                                              |
| **Content-Type**   | `application/json`                                                      |

#### Request Headers

```http
Authorization: Bearer <Firebase ID Token>
```

#### Path Parameters

| Parameter | Type | Description                                  |
| --------- | ---- | --------------------------------------------- |
| `userId`  | int  | ID of the user whose status is being requested |

#### Success Response — `200 OK`

**If the target user has `showStatus` enabled:**

```json
{
  "visible": true,
  "online": true,
}
```

**If the target user has `showStatus` disabled:**

```json
{
  "visible": false
}
```

#### Error Responses

| Status Code        | Scenario                                     | Response Body                        |
| ------------------- | --------------------------------------------- | ------------------------------------- |
| `401 Unauthorized`  | Missing or invalid Firebase ID token          | `{ "error": "Unauthorized" }`         |
| `404 Not Found`     | No settings row exists for the given `userId` | `{ "error": "Settings not found" }`   |

#### Notes

* Unlike `GET /api/settings/users/show-status` (self-only), this endpoint looks up an **arbitrary** `userId` from the path — the caller's own token is only used to confirm authentication, not to identify the target user.
* Currently any authenticated user can query any `userId`'s status via this endpoint (subject to that user's own `showStatus` preference). 

---
### 8.6 GET /api/settings/users/information/{userId}

| Field              | Details                                                                |
| ------------------ | ----------------------------------------------------------------------- |
| **Endpoint**       | `/api/settings/users/information/{userId}`                              |
| **Method**         | `GET`                                                                    |
| **Purpose**        | Retrieves settings and profile information for the specified user       |
| **Authentication** | Firebase ID Token required                                              |
| **Content-Type**   | `application/json`                                                      |

#### Request Headers

```http
Authorization: Bearer <Firebase ID Token>
```

#### Path Parameters

| Parameter | Type | Description                     |
| --------- | ---- | -------------------------------- |
| `userId`  | int  | ID of the user whose settings/profile info is being requested |

#### Success Response — `200 OK`

```json
{
  "userId": 123,
  "showStatus": true
}
```

> ⚠️ **Note:** `UserSettingsDTO`'s full field list isn't visible in the reviewed controller snippet — only `showStatus` is confirmed elsewhere in this contract. Confirm the remaining fields (e.g. notification preferences, profile fields) against `UserSettingsDTO.java` before finalizing this response schema.

#### Error Responses

| Status Code        | Scenario                             | Response Body                  |
| ------------------- | -------------------------------------- | -------------------------------- |
| `401 Unauthorized`  | Missing or invalid Firebase ID token   | `"Invalid Firebase Token"`       |

#### Notes

* Like 8.5, this endpoint accepts an arbitrary `userId` path parameter — the caller's own token is used only to authenticate the request, not to scope the lookup to themselves.
* No ownership/permission check beyond authentication is currently implemented in the reviewed code — any authenticated user can look up any other user's settings/profile info. Flag this for David (UC7) as a potential access-control gap if `UserSettingsDTO` contains anything sensitive.

---

### 8.7 PUT /api/settings/me/phoneNumber

| Field              | Details                                                                |
| ------------------ | ----------------------------------------------------------------------- |
| **Endpoint**       | `/api/settings/me/phoneNumber`                                                 |
| **Method**         | `PUT`                                                                    |
| **Purpose**        | Updates phoneNumber for the specified user                                 |
| **Authentication** | Firebase ID Token required                                              |
| **Content-Type**   | `application/json`                                                      |

#### Request Headers

```http
Authorization: Bearer <Firebase ID Token>
Content-Type: application/json
```

#### Path Parameters

| Parameter | Type | Description                    |
| --------- | ---- | -------------------------------- |
| `userId`  | int  | ID of the user whose settings are being updated |

#### Request Body — `UpdateSettingsDTO`

```json
{
  "phoneNumber": 0191827272
}
```
#### Success Response — `200 OK`

Returns the updated `UserSettingsDTO` (see 8.6 for shape).

#### Error Responses

| Status Code        | Scenario                             | Response Body                  |
| ------------------- | -------------------------------------- | -------------------------------- |
| `401 Unauthorized`  | Missing or invalid Firebase ID token   | `"Invalid Firebase Token"`       |

---

### 8.8 DELETE /api/settings/me/user

| Field              | Details                                                                |
| ------------------ | ----------------------------------------------------------------------- |
| **Endpoint**       | `/api/settings/me/user`                                                 |
| **Method**         | `DELETE`                                                                 |
| **Purpose**        | Deletes the authenticated user's own account, including their Firebase Auth record, settings row, and user row |
| **Authentication** | Firebase ID Token required                                              |
| **Content-Type**   | `application/json`                                                      |

#### Request Headers

```http
Authorization: Bearer <Firebase ID Token>
```

#### Path Parameters

None — the target user is derived from the token, not the path.

#### Success Response — `204 No Content`

No response body.

#### Error Responses

| Status Code        | Scenario                                              | Response Body                  |
| ------------------- | -------------------------------------------------------- | -------------------------------- |
| `401 Unauthorized`  | Missing or invalid Firebase ID token                      | *(exception thrown — see note)*  |
| `404 Not Found`     | Firebase-authenticated user has no matching `User` row    | *(exception thrown — see note)*  |

---
#  9. — Admin & Reporting

> Renumber to match placement in the master API contract doc.
> Note on 9.6: written as `PUT` per spec, though `POST` is more conventional for creating a new resource without a pre-known ID — worth a quick team gut-check.

---

### 9.1 POST /api/auth/admin/login

| Field | Details |
|---|---|
| **Endpoint** | `/api/auth/admin/login` |
| **Method** | `POST` |
| **Purpose** | Verifies the authenticated user's `isAdmin` flag and confirms admin access before the frontend routes into the admin dashboard |
| **Authentication** | Firebase ID Token required |
| **Content-Type** | `application/json` |

#### Request Headers
```http
Authorization: Bearer <Firebase ID Token>
```

#### Request Body
None — identity is derived from the token.

#### Success Response — `200 OK`
```json
{
  "userId": 123,
  "isAdmin": true
}
```

#### Error Responses

| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid Firebase ID token | *(exception thrown — see note)* |
| `403 Forbidden` | Valid user, but `isAdmin = false` | `{ "error": "User is not an admin" }` |

---

### 9.2 GET /api/admin/dashboard

| Field | Details |
|---|---|
| **Endpoint** | `/api/admin/dashboard` |
| **Method** | `GET` |
| **Purpose** | Returns high-level summary stats for the logged-in admin: count of reports currently assigned to them, breakdown by report type, and count of reports they've completed |
| **Authentication** | Firebase ID Token required; user must have `isAdmin = true` |
| **Content-Type** | `application/json` |

#### Request Headers
```http
Authorization: Bearer <Firebase ID Token>
```

#### Success Response — `200 OK`
```json
{
  "assignedReportsCount": 12,
  "reportsByType": {
    "USER": 4,
    "POST": 3,
    "COMMENT": 2,
    "TASK_DISPUTE": 3
  },
  "completedReportsCount": 47
}
```

#### Error Responses

| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid Firebase ID token | *(exception thrown — see note)* |
| `403 Forbidden` | User is not an admin | `{ "error": "User is not an admin" }` |

---

### 9.4 GET /api/report

| Field | Details |
|---|---|
| **Endpoint** | `/api/report` |
| **Method** | `GET` |
| **Purpose** | Returns all reports currently assigned to the requesting admin |
| **Authentication** | Firebase ID Token required; user must have `isAdmin = true` |
| **Content-Type** | `application/json` |

#### Request Headers
```http
Authorization: Bearer <Firebase ID Token>
```

#### Query Parameters (optional)

| Param | Type | Description |
|---|---|---|
| `status` | string | Filter by `submitted`, `assigned`, `reviewed` |
| `reportType` | string | Filter by `USER`, `POST`, `COMMENT`, `TASK_DISPUTE` |

#### Success Response — `200 OK`
```json
[
  {
    "reportId": 501,
    "reportType": "POST",
    "status": "assigned",
    "adminId": 7,
    "reporterUserId": 42,
    "reportedPostId": 88,
    "reason": "Harassment",
    "createdAt": "2026-08-14T09:12:00Z"
  }
]
```

#### Error Responses

| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid Firebase ID token | *(exception thrown — see note)* |
| `403 Forbidden` | User is not an admin | `{ "error": "User is not an admin" }` |

---

### 9.5 GET /api/suggestion

| Field | Details |
|---|---|
| **Endpoint** | `/api/suggestion` |
| **Method** | `GET` |
| **Purpose** | Returns the system-suggested punishment for a given violation type + severity pair, for the admin to accept or override while reviewing a report |
| **Authentication** | Firebase ID Token required; user must have `isAdmin = true` |
| **Content-Type** | `application/json` |

#### Query Parameters (required)

| Param | Type | Description |
|---|---|---|
| `violationType` | string | e.g. `HARASSMENT`, `PRIVACY_VIOLATION`, `TASK_NO_SHOW` |
| `severity` | string | `MINOR`, `MODERATE`, `SEVERE` |

#### Success Response — `200 OK`
```json
{
  "violationType": "PRIVACY_VIOLATION",
  "severity": "SEVERE",
  "suggestedAction": "BAN"
}
```

#### Error Responses

| Status Code | Scenario | Response Body |
|---|---|---|
| `400 Bad Request` | `violationType` or `severity` is not a recognized enum value | `{ "error": "Invalid violationType or severity" }` |
| `401 Unauthorized` | Missing or invalid Firebase ID token | *(exception thrown — see note)* |
| `403 Forbidden` | User is not an admin | `{ "error": "User is not an admin" }` |

---

### 9.6 PUT /api/report

| Field | Details |
|---|---|
| **Endpoint** | `/api/report` |
| **Method** | `PUT` |
| **Purpose** | Submits a new report — user, post, comment, or task dispute. The required fields vary by `reportType` (see body below) |
| **Authentication** | Firebase ID Token required |
| **Content-Type** | `application/json` |

#### Request Body

```json
{
  "reportType": "TASK_DISPUTE",
  "reportedUserId": null,
  "reportedPostId": null,
  "reportedCommentId": null,
  "taskId": 301,
  "disputeReason": "NO_SHOW",
  "reason": "Helper never arrived",
  "description": "Waited 2 hours, no contact from helper."
}
```

Only the field(s) matching `reportType` should be populated — `reportedUserId` for `USER`, `reportedPostId` for `POST`, `reportedCommentId` for `COMMENT`, `taskId` (+ `disputeReason`) for `TASK_DISPUTE`. The backend should reject a body where the populated field doesn't match `reportType`, mirroring the `CHECK` constraint on `report_table`.

#### Success Response — `201 Created`
```json
{
  "reportId": 512,
  "reportType": "TASK_DISPUTE",
  "status": "submitted",
  "createdAt": "2026-08-14T10:03:00Z"
}
```

#### Error Responses

| Status Code | Scenario | Response Body |
|---|---|---|
| `400 Bad Request` | Field(s) don't match `reportType`, or required field missing | `{ "error": "Invalid report payload for reportType" }` |
| `401 Unauthorized` | Missing or invalid Firebase ID token | *(exception thrown — see note)* |
| `404 Not Found` | Referenced entity (post/comment/user/task) doesn't exist | `{ "error": "Target entity not found" }` |

---

### 9.7 PATCH /api/report

| Field | Details |
|---|---|
| **Endpoint** | `/api/report` |
| **Method** | `PATCH` |
| **Purpose** | Admin updates a report's status and/or verdict (`violationType`, `severity`, `actualAction`). On resolution, triggers a notification to the affected user informing them of the outcome |
| **Authentication** | Firebase ID Token required; user must have `isAdmin = true` |
| **Content-Type** | `application/json` |

#### Request Body
```json
{
  "reportId": 512,
  "status": "reviewed",
  "violationType": "TASK_NO_SHOW",
  "severity": "MODERATE",
  "actualAction": "SUSPEND_7D",
  "adminNotes": "Confirmed with requester, no evidence helper attempted contact."
}
```

#### Success Response — `200 OK`
```json
{
  "reportId": 512,
  "status": "reviewed",
  "actualAction": "SUSPEND_7D",
  "resolvedAt": "2026-08-14T11:20:00Z"
}
```

**Side effect:** notification dispatch should follow the existing event-driven pattern — publish an event inside the transaction, handle FCM send via `@TransactionalEventListener(phase = AFTER_COMMIT)` — rather than sending inline before the report update is committed.

#### Error Responses

| Status Code | Scenario | Response Body |
|---|---|---|
| `400 Bad Request` | Invalid enum value for `status`/`violationType`/`severity`/`actualAction` | `{ "error": "Invalid field value" }` |
| `401 Unauthorized` | Missing or invalid Firebase ID token | *(exception thrown — see note)* |
| `403 Forbidden` | User is not an admin, or report is not assigned to this admin | `{ "error": "Not authorized to update this report" }` |
| `404 Not Found` | `reportId` doesn't exist | `{ "error": "Report not found" }` |

---

### 9.8 PUT /api/user (deregister admin)

| Field | Details |
|---|---|
| **Endpoint** | `/api/user` |
| **Method** | `PUT` |
| **Purpose** | Reuses the existing user-update endpoint to deregister an admin by setting `isAdmin = false` |
| **Authentication** | Firebase ID Token required |
| **Content-Type** | `application/json` |

#### Request Body
```json
{
  "userId": 7,
  "isAdmin": false
}
```

#### Success Response — `200 OK`
```json
{
  "userId": 7,
  "isAdmin": false
}
```

#### Error Responses

| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid Firebase ID token | *(exception thrown — see note)* |
| `403 Forbidden` | Requesting user isn't authorized to deregister this admin | `{ "error": "Not authorized to modify this user" }` |
| `404 Not Found` | `userId` doesn't exist | `{ "error": "User not found" }` |

**Open decision:** who's allowed to call this on someone else's behalf — self-deregistration only, or does it need a "super-admin" concept? `isAdmin` is currently a flat boolean, so there's no tier above "admin" to gate this with. If any admin can deregister any other admin, worth a line in the PR description since it's a permission decision, not just plumbing.

---

### 9.9 GET /api/report/me
 
| Field | Details |
|---|---|
| **Endpoint** | `/api/report/me` |
| **Method** | `GET` |
| **Purpose** | Returns all reports submitted by the authenticated user, so they can track status on the "My Reports" page — distinct from 9.4, which returns reports *assigned to* an admin |
| **Authentication** | Firebase ID Token required — any authenticated user, not admin-only |
| **Content-Type** | `application/json` |
 
#### Request Headers
```http
Authorization: Bearer <Firebase ID Token>
```
 
#### Query Parameters (optional)
 
| Param | Type | Description |
|---|---|---|
| `status` | string | Filter by `submitted`, `assigned`, `reviewed` |
| `reportType` | string | Filter by `USER`, `POST`, `COMMENT`, `TASK_DISPUTE` |
 
#### Success Response — `200 OK`
```json
[
  {
    "reportId": 512,
    "reportType": "TASK_DISPUTE",
    "status": "reviewed",
    "taskId": 301,
    "disputeReason": "NO_SHOW",
    "reason": "Helper never arrived",
    "actualAction": "SUSPEND_7D",
    "createdAt": "2026-08-14T10:03:00Z",
    "resolvedAt": "2026-08-14T11:20:00Z"
  }
]
```
 
Note: `actualAction` and `resolvedAt` are `null` while `status` is `submitted` or `assigned` — the report hasn't reached a verdict yet. The user only needs to see the outcome, not `violationType`/`severity`/`adminNotes` — those stay admin-internal, so this response is a leaner shape than 9.4's, not a reuse of the same DTO.
 
#### Error Responses
 
| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid Firebase ID token | *(exception thrown — see note)* |
 
**Note on scope:** since this only returns reports where `reporter_user_id` matches the token's resolved user, there's no ownership check needed beyond that filter — same pattern as resolving ownership from the token rather than trusting a client-supplied ID, used elsewhere in the API.

---
## 10. HTTP Status Code Reference


| Code | Meaning | When used |
|---|---|---|
| `200 OK` | Request succeeded | Successful GET, PUT, PATCH |
| `201 Created` | Resource created successfully | Successful POST |
| `400 Bad Request` | Invalid input or field value | Validation failures |
| `401 Unauthorized` | Missing or invalid JWT token | All protected endpoints |
| `403 Forbidden` | Authenticated but not permitted | Accessing another user's data |
| `404 Not Found` | Resource does not exist | Invalid IDs |
| `409 Conflict` | Resource conflict | Duplicate accounts, invalid state transitions |
| `422 Unprocessable Entity` | Missing required fields | Incomplete request bodies |
| `500 Internal Server Error` | Unexpected server failure | All endpoints |


### 9.9 POST /api/report/match

| Field | Details |
|---|---|
| **Endpoint** | `/api/report/match` |
| **Method** | `POST` |
| **Purpose** | Assigns a submitted report to the admin with the fewest currently assigned reports. Uses a load-balancing strategy — the admin with the lowest count of `assigned` reports is selected. Ties are broken by the lowest `admin_id`. Only reports in `submitted` status can be matched |
| **Authentication** | Firebase ID Token required; user must have `isAdmin = true` |
| **Content-Type** | `application/json` |

#### Request Headers
```http
Authorization: Bearer <Firebase ID Token>
```

#### Request Body

| Field | Type | Description |
|---|---|---|
| `reportId` | int | **Required.** The ID of the report to assign |

```json
{
  "reportId": 14
}
```

#### Success Response — `200 OK`

```json
{
  "reportId": 14,
  "assignedAdminId": 1,
  "status": "assigned"
}
```

> `assignedAdminId` is the `user_id` (from `user_table`) of the admin who was assigned — not their `admin_id` from `admin_table`. The report's `admin_id` column and `status` are both updated atomically in the same transaction.

#### Error Responses

| Status Code | Scenario | Response Body |
|---|---|---|
| `400 Bad Request` | `reportId` missing from request body | `{ "error": "reportId is required" }` |
| `401 Unauthorized` | Missing or invalid Firebase ID token | `{ "error": "Unauthorized" }` |
| `403 Forbidden` | Authenticated user is not an admin | `{ "error": "User is not an admin" }` |
| `404 Not Found` | Report does not exist | `{ "error": "Report not found" }` |
| `409 Conflict` | Report is already `assigned` or `reviewed` — cannot be re-matched | `{ "error": "Report has already been assigned or reviewed" }` |
| `503 Service Unavailable` | No admins exist in the system | `{ "error": "No admins available" }` |

#### Notes

- This endpoint is intended to be called by an admin (or an automated system) after a new report is submitted via `PUT /api/report`.
- The typical flow is: user submits report (`PUT /api/report` → status `submitted`) → admin or system calls `POST /api/report/match` → report status becomes `assigned` and the selected admin can now see it via `GET /api/report`.
- An already-assigned report cannot be re-matched through this endpoint — use `PATCH /api/report` to update `adminId` manually if reassignment is needed.

---
