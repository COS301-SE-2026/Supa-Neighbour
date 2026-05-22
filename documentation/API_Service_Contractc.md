# Supa-Neighbour — API Service Contract

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

#### Error Responses

| Status Code | Scenario | Response Body |
|---|---|---|
| `401 Unauthorized` | Missing or invalid token | `{ "error": "Unauthorized" }` |
| `403 Forbidden` | User is not a participant in this chat | `{ "error": "You are not authorised to view this chat" }` |
| `404 Not Found` | Chat does not exist | `{ "error": "Chat not found" }` |
| `500 Internal Server Error` | Unexpected server failure | `{ "error": "An unexpected error occurred. Please try again." }` |

---

## 5. HTTP Status Code Reference

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