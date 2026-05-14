# Lost & Found Map App (SIT305 Task 9.1P)

## Overview
The **Lost & Found Map App** is an Android mobile application designed to connect lost items with their owners. Building upon a local SQLite database, this application integrates Google Maps and location services to allow users to geographically tag where an item was lost or found and visualize all active listings on an interactive map.

This project was developed as part of SIT305 (Mobile Application Development) for Task 9.1P.

## Key Features
* **Advert Creation:** Users can post detailed listings for lost or found items, including titles, descriptions, categories, contact information, and an image.
* **Smart Location Input:** * **Places Autocomplete:** Users can search and select an address using the Google Places API.
  * **Current Location:** Users can capture their exact GPS coordinates using the device's location services (Fused Location Provider) and reverse-geocode it into a readable address.
* **Interactive Map Visualization:** * Displays all items stored in the database on a Google Map.
  * Custom map markers differentiate item types (e.g., Red for Lost, Green for Found).
* **Proximity/Radius Search (Subtask):** Users can dynamically filter the map to only show lost/found items within a specific radius (in kilometers) of their current physical location.

## Technologies & Libraries Used
* **Language:** Java
* **IDE:** Android Studio
* **Database:** SQLite (Local storage via `SQLiteOpenHelper`) 
* **Google APIs & SDKs:**
  * Maps SDK for Android
  * Places API
  * Google Play Services Location (`FusedLocationProviderClient`)

## Project Structure
* `AddItemActivity.java`: Handles the UI and logic for creating a new listing, capturing image URIs, and determining the item's latitude/longitude via Autocomplete or GPS.
* `MapActivity.java`: Initializes the Google Map, retrieves coordinates from the database, plots the markers, and handles the distance-calculation logic for the radius filter.
* `DatabaseHelper.java`: Manages the SQLite database, handling CRUD operations and specifically querying items with valid coordinate data.

## Setup & Installation
To run this project locally on your machine:

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/](https://github.com/)[aarnavanoop]/LostAndFound-9.1P.git