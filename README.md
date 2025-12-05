# ⚠️ [DEPRECATED] Penguin Storage Explorer

> **Note:** This repository is no longer maintained. It represents an early iteration (V1) of my inventory management vision.
>
> 🚀 **Please check out the complete rewrite and successor:**
> **[Penguin Inventory Manager (PIM)](https://github.com/aYudizzle/PenguinInventoryManager)**

---

## The Evolution: From Explorer to Manager 🐧

This project started as my first exploration into Kotlin Multiplatform. While it worked, I learned that a robust inventory app needs more than just API calls.

I have taken all the learnings from this project and built **[Penguin Inventory Manager (PIM)]([LINK](https://github.com/aYudizzle/PenguinInventoryManager))** from the ground up.

**Improvements in the new PIM app:**
* **Architecture:** Migrated from a simple MVVM to a modular **Clean Architecture** (Core/Features).
* **Data Layer:** Moved from online-only (Ktor) to **Offline-First** (Room + Ktor).
* **Sync:** Implemented a robust **Bi-Directional Sync** with conflict resolution and batch processing.
* **Concurrency:** Replaced basic coroutines with extensive use of **Flows** and Reactive State Management.
* **UX:** Updated to Material 3 with adaptive layouts and optimistic UI updates.

Feel free to browse this code for historical context, but for a production-grade KMP example, please visit the new repository.

---


# PenguinStorageExplorer 🐧🗄️

PenguinStorageExplorer is a cross-platform Storage Administration App, designed to make managing storage location a breeze. It leverages the power of Kotlin Multiplatform and Compose Multiplatform to deliver a seamless experience on both Android and Desktop.

## Features

* **Item Operations:** Add, delete, edit items.
* **Storage Information:** View detailed information about your storage locations.
* **Cross-platform:** Works on both Android and Desktop, thanks to Kotlin Multiplatform and Compose Multiplatform.

## Tech Stack

* **Kotlin Multiplatform:** Share core logic and business rules across Android and Desktop.
* **Compose Multiplatform:** Build a consistent and beautiful UI for both platforms.
* **Koin:** Dependency Injection with Koin
* **Ktor:** Ktor for Api Requests
* **Android:** Target the Android platform with native performance.
* **Desktop:** Target desktop platforms (Windows, macOS, Linux) with a native look and feel.
