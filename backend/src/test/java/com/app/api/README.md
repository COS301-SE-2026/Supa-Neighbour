# Backend Testing Guide — Supa-Neighbour API

## Overview

This project uses **JUnit 5** and **Mockito** for unit testing, both of which are bundled inside `spring-boot-starter-test` — no additional dependencies need to be installed.

---

## Folder Structure

```
test/java/com/app/api/
├── unit/
│   └── services/          # Unit tests for service classes (no DB, no Docker)
└── integration/           # Integration tests (require Docker + real DB)
```

---


## Running Tests

Run all unit tests from the `backend/` directory:

```bash
cd backend
mvn test
```

Run only the unit tests (skip integration):

```bash
mvn test -Dtest="**/unit/**"
```

Run a specific test class:

```bash
mvn test -Dtest="TaskServiceTest"
```

Run with checkstyle skipped (useful during test development):

```bash
mvn test -Dcheckstyle.skip
```

> **Note:** You do NOT need to run `docker compose up` to run unit tests. Docker is only required for integration tests that need a live database connection.

---

## Do I Need Docker?

| Test Type | Needs Docker? | Command |
|---|---|---|
| Unit tests (`unit/`) | No | `mvn test` |
| Integration tests (`integration/`) |Yes | `docker compose up` first |

---

## Unit Tests — What They Test

Unit tests target **service layer logic only**. The repositories (database layer) are **mocked** using Mockito, meaning no real database connection is made.

### What to test in a service

- Methods that return data (verify correct value is returned)
- Methods that delete (verify repository calls are made correctly)
- Methods that update (verify only non-null fields are updated)
- Edge cases (e.g. entity not found → returns null or false)

---

## Example — TaskService Unit Test

Based on the existing `TaskService`, here is a full example test file:

```java
// test/java/com/app/api/unit/services/TaskServiceTest.java

package com.app.api.unit.services;

import com.app.api.models.Analytics;
import com.app.api.models.Dependent;
import com.app.api.models.Task;
import com.app.api.repositories.AnalyticsRepository;
import com.app.api.repositories.DependentRepository;
import com.app.api.repositories.TaskRepository;
import com.app.api.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    // Mocked dependencies — no real DB connection
    @Mock
    private TaskRepository taskRepo;

    @Mock
    private AnalyticsRepository analyticsRepo;

    @Mock
    private DependentRepository dependentRepo;

    // The real service, with mocks injected into it
    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task();
        sampleTask.setTaskId(1);
    }

    // --- getTaskById ---

    @Test
    void getTaskById_returnsTask_whenFound() {
        when(taskRepo.findById(1)).thenReturn(Optional.of(sampleTask));

        Task result = taskService.getTaskById(1);

        assertNotNull(result);
        assertEquals(1, result.getTaskId());
    }

    @Test
    void getTaskById_returnsNull_whenNotFound() {
        when(taskRepo.findById(99)).thenReturn(Optional.empty());

        Task result = taskService.getTaskById(99);

        assertNull(result);
    }

    // --- getAllTasks ---

    @Test
    void getAllTasks_returnsAllTasks() {
        when(taskRepo.findAll()).thenReturn(List.of(sampleTask));

        Iterable<Task> result = taskService.getAllTasks();

        assertNotNull(result);
        assertTrue(result.iterator().hasNext());
    }

    // --- deleteTask ---

    @Test
    void deleteTask_returnsTrue_whenTaskExists() {
        when(taskRepo.existsById(1)).thenReturn(true);
        when(analyticsRepo.findByTaskId(1)).thenReturn(List.of());

        boolean result = taskService.deleteTask(1);

        assertTrue(result);
        verify(taskRepo, times(1)).deleteById(1);
    }

    @Test
    void deleteTask_returnsFalse_whenTaskNotFound() {
        when(taskRepo.existsById(99)).thenReturn(false);

        boolean result = taskService.deleteTask(99);

        assertFalse(result);
        verify(taskRepo, never()).deleteById(any());
    }

    @Test
    void deleteTask_deletesLinkedAnalyticsFirst() {
        Analytics linkedAnalytic = new Analytics();
        when(taskRepo.existsById(1)).thenReturn(true);
        when(analyticsRepo.findByTaskId(1)).thenReturn(List.of(linkedAnalytic));

        taskService.deleteTask(1);

        verify(analyticsRepo, times(1)).deleteAll(List.of(linkedAnalytic));
        verify(taskRepo, times(1)).deleteById(1);
    }

    // --- updateTask ---

    @Test
    void updateTask_returnsNull_whenTaskNotFound() {
        when(taskRepo.findById(99)).thenReturn(Optional.empty());

        Task result = taskService.updateTask(99, new Task());

        assertNull(result);
    }

    @Test
    void updateTask_savesAndReturnsUpdatedTask() {
        Task updates = new Task();
        updates.setAdminReview("Approved");

        when(taskRepo.findById(1)).thenReturn(Optional.of(sampleTask));
        when(taskRepo.save(sampleTask)).thenReturn(sampleTask);

        Task result = taskService.updateTask(1, updates);

        assertNotNull(result);
        verify(taskRepo, times(1)).save(sampleTask);
    }

    // --- getTasksByUserId ---

    @Test
    void getTasksByUserId_returnsNull_whenDependentNotFound() {
        when(dependentRepo.findByUserId(99)).thenReturn(null);

        Iterable<Task> result = taskService.getTasksByUserId(99);

        assertNull(result);
    }

    @Test
    void getTasksByUserId_returnsTasks_whenDependentExists() {
        Dependent dependent = new Dependent();
        dependent.setDependentId(5);

        when(dependentRepo.findByUserId(1)).thenReturn(dependent);
        when(taskRepo.findByDependentId(5)).thenReturn(List.of(sampleTask));

        Iterable<Task> result = taskService.getTasksByUserId(1);

        assertNotNull(result);
        assertTrue(result.iterator().hasNext());
    }
}
```

---

## Key Concepts

| Concept | Description |
|---|---|
| `@ExtendWith(MockitoExtension.class)` | Enables Mockito in JUnit 5 tests |
| `@Mock` | Creates a fake version of a dependency (no real DB call) |
| `@InjectMocks` | Creates the real service and injects the mocks into it |
| `@BeforeEach` | Runs before every test — used to set up shared test data |
| `when(...).thenReturn(...)` | Defines what a mock returns when called with specific input |
| `verify(repo, times(1)).method()` | Asserts that a method was called a specific number of times |
| `verify(repo, never()).method()` | Asserts that a method was never called |
| `assertNotNull(result)` | Asserts the result is not null |
| `assertNull(result)` | Asserts the result is null |
| `assertEquals(expected, actual)` | Asserts two values are equal |
| `assertTrue(condition)` | Asserts a condition is true |
| `assertFalse(condition)` | Asserts a condition is false |

---

## Common Mistakes to Avoid

**Forgetting `@ExtendWith(MockitoExtension.class)`**

Without this, `@Mock` and `@InjectMocks` annotations are ignored and your mocks will be null.

**Using `@SpringBootTest` for unit tests**

```java
// ❌ Wrong for unit tests — loads the entire Spring context, needs DB
@SpringBootTest
class TaskServiceTest { }

// ✅ Correct — lightweight, no Spring context needed
@ExtendWith(MockitoExtension.class)
class TaskServiceTest { }
```

**Not stubbing a method that gets called**

```java
// ❌ Wrong — mock returns null by default, causing NullPointerException
taskService.deleteTask(1);

// ✅ Correct — always stub what the method under test will call
when(taskRepo.existsById(1)).thenReturn(true);
when(analyticsRepo.findByTaskId(1)).thenReturn(List.of());
taskService.deleteTask(1);
```

**Verifying the wrong number of calls**

If `deleteTask` should delete analytics before the task, verify order matters:
```java
verify(analyticsRepo, times(1)).deleteAll(any());
verify(taskRepo, times(1)).deleteById(1);
```

---