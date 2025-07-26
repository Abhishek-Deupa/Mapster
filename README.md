# Mapster

Mapster is an indoor navigation application for Android, designed to guide users within complex building layouts. By scanning a QR code at their current location, users can get step-by-step directions to any destination on the floor. The app is built with modern Android development tools, including Jetpack Compose, CameraX, and Firebase.

## Features

- **QR Code-Based Positioning**: Quickly determine your starting location by scanning a nearby QR code.
- **Upload QR from Gallery**: Select a QR code image from your device's gallery for navigation.
- **Dynamic Pathfinding**: Utilizes Dijkstra's algorithm to calculate the most efficient route to your destination.
- **Clear, Step-by-Step Guidance**: Receive detailed instructions, including directions and distances for each step of your journey.
- **User Authentication**: Secure user registration and login functionality powered by Firebase Authentication.
- **Intuitive UI**: A clean and modern user interface built entirely with Jetpack Compose.
- **Issue Reporting**: An in-app feature allows users to report navigation issues or bugs directly to administrators via EmailJS.

## How It Works

1.  **Authentication**: Users can sign up or log in to their account.
2.  **Set Starting Point**: On the landing screen, the user can either open the camera to scan a QR code or upload a QR image from their gallery.
3.  **Select Destination**: After scanning, the app identifies the user's current location. The user is then prompted to search for and select their desired destination from a list of available locations.
4.  **Navigate**: The app calculates the shortest path using Dijkstra's algorithm and displays a clear list of turn-by-turn directions to guide the user to their destination.

The core navigation logic is handled by the `FloorNavigation.kt` class, which contains a predefined graph of the building layout. Each node in the graph represents a specific location, and the edges represent the paths between them, complete with distance and directional information.

## Technologies Used

-   **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
-   **Camera**: [CameraX](https://developer.android.com/training/camerax)
-   **QR Code Scanning**: [ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning)
-   **Authentication**: [Firebase Authentication](https://firebase.google.com/docs/auth)
-   **Database**: [Firebase Firestore](https://firebase.google.com/docs/firestore) for user data storage.
-   **Asynchronous Programming**: Kotlin Coroutines
-   **Navigation**: Jetpack Navigation for Compose
-   **Networking**: OkHttp for sending report emails via EmailJS.
-   **Language**: [Kotlin](https://kotlinlang.org/)

## Setup and Installation

To build and run this project yourself, follow these steps:

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/abhishek-deupa/mapster.git
    ```

2.  **Open in Android Studio:**
    Open the cloned directory in the latest version of Android Studio.

3.  **Firebase Configuration:**
    The project includes a `google-services.json` file. To connect the app to your own Firebase project, replace the existing `app/google-services.json` file with the one from your Firebase project console.

4.  **EmailJS Configuration (for Issue Reporting):**
    The issue reporting feature uses EmailJS to send emails. You will need to create an EmailJS account and configure your own service. Update the following constants in `app/src/main/java/com/example/mapster/screens/ReportScreen.kt` with your credentials:
    ```kotlin
    private const val EMAILJS_SERVICE_ID = "YOUR_SERVICE_ID"
    private const val EMAILJS_TEMPLATE_ID = "YOUR_TEMPLATE_ID"
    private const val EMAILJS_PUBLIC_KEY = "YOUR_PUBLIC_KEY"
    ```

5.  **Build and Run:**
    Sync the Gradle files and run the application on an Android emulator or a physical device.
