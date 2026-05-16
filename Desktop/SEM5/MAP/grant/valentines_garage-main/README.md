# 📁 Valentine Garage – Project Structure Guide

This document explains the folder structure of the project to help new team members understand how the app is organized.

---

## Root Package

`com.example.valentine_garage`

This is the base package that contains all the app’s code, organized into logical layers.

---

## database

Handles all local data storage using Room.

### • dao

Contains **Data Access Objects (DAOs)**.

* Responsible for defining database queries.
* Example: Insert, update, delete, and fetch data.

### • entities

Defines **data models (tables)** for the database.

* Each class represents a table.
* Uses annotations like `@Entity`.

### • repositories

Acts as a **bridge between database and ViewModels**.

* Combines data sources (e.g., DAO, API if added later).
* Keeps business logic out of UI.

### • roomDatabase

Contains the **Room database setup class**.

* Initializes the database.
* Provides access to DAOs.

---

## di (Dependency Injection)

Handles dependency injection (e.g., Hilt modules).

* Provides instances like:

  * Database
  * DAOs
  * Repositories
* Ensures classes don’t manually create dependencies.

---

## ui

Handles everything related to the user interface.

### • repositories

* Sometimes used for UI-specific state handling.
* Optional layer depending on architecture.

---

## screens

Each folder represents a **screen of the app**.

### • authentication

* Login, signup, or user authentication logic.

### • checkIn

* Handles check-in related functionality.

### • home

* Main landing screen after login.

### • repairs

* Manages repair-related features.

### • reports

* Displays or generates reports.

Each screen typically contains:

* Composables (UI)
* State handling
* Navigation logic

---

## theme

Defines the app’s visual design.

### • Color.kt

* Contains color definitions.

### • Theme.kt

* Applies Material theme (light/dark styles).

### • Type.kt

* Defines typography (fonts, sizes).

---

## viewModels

Contains ViewModels for the app.

* Holds UI state.
* Communicates with repositories.
* Survives configuration changes.

---

## ValentineGarageApplication

Application class.

* Entry point of the app.
* Initializes global configurations (e.g., Hilt).

---

## Architecture Summary

The project roughly follows this flow:

UI (screens) → ViewModels → Repositories → DAO → Database

This ensures:

* Separation of concerns
* Easier testing
* Cleaner and maintainable code

---

## Key Benefits of This Structure

* Scalable for large projects
* Clear separation between UI and data layers
* Easy for teams to collaborate
* Follows modern Android development practices

---

## Notes for Developers

* Keep business logic out of UI (screens)
* Use ViewModels to manage state
* Use repositories to handle data sources
* Add new features inside the `screens` package

---

💡 Tip: When adding a new feature, follow the same structure:

* Create a new screen folder
* Add ViewModel
* Connect to repository
* Update DI if needed
