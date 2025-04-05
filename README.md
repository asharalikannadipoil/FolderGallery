# 📸 Folder Gallery App

A modern Android gallery application that organizes media files into albums and provides a clean, intuitive browsing experience.

---

## ✨ Features

### 📁 Album View
- Displays all folders containing media files as distinct albums
- Special smart collections:
    - **All Images**: All images on the device
    - **All Videos**: All videos on the device
    - **Camera**: Photos and videos taken with the camera
- Each album shows name and media count

### 🗂️ Media Organization
- Automatically detects all folders with images/videos
- Excludes system folders, cache, thumbnails, and `.nomedia` directories
- Categorizes media by type and source

### 🖼️ Viewing Experience
- Grid layout for browsing albums and media
- Detail view for photos and videos
- Integrated video playback using **ExoPlayer**
- Smooth navigation between screens

---

## 🧠 Technical Implementation

### 🏛 Architecture
- **Clean Architecture**:
    - Domain layer (business logic)
    - Data layer (media access)
    - Presentation layer (UI)
- **MVVM Pattern** for maintainability
- **Jetpack Compose** for UI
- **Dagger Hilt** for dependency injection

### 🧩 Key Components

#### 📦 Data Layer
- `MediaRepository`: Retrieves media files

#### ⚙️ Domain Layer
- `Album` model: Collection of media items with metadata
- `MediaItem` model: Represents a media file
- Use cases for album and media operations

#### 🎨 Presentation Layer
- `AlbumListScreen`: Displays all albums in a grid
- `AlbumDetailScreen`: Displays media from a selected album
- `MediaDetailScreen`: Full-screen view of a media item (Video player and Image viewer)

---

## 🔐 Permissions Handling
- Runtime permission requests for media access
- Adaptive strategy for different Android versions

---

## 🛠 Libraries Used
- **Jetpack Compose** – UI toolkit
- **Coil** – Image loading and caching
- **ExoPlayer** – Video playback
- **Dagger Hilt** – Dependency injection
- **Kotlin Coroutines & Flow** – Async operations
- **Media3** – Modern media playback

---

## 🚀 Getting Started

### ✅ Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- Minimum SDK: **API 24 (Android 7.0)**
- Target SDK: **API 34 (Android 14)**

### 🏗️ Building the Project
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on an emulator or physical device

---

## 🧾 Permissions Required
- `READ_MEDIA_IMAGES` (Android 13+)
- `READ_MEDIA_VIDEO` (Android 13+)
- `READ_EXTERNAL_STORAGE` (Android 12 and below)

---

## 🔮 Future Improvements
- Media editing capabilities
- Cloud storage integration
- Favorites collection
- Search functionality
- Media sharing features

---

