# TimeSync

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

TimeSync is an Android app that allows users to track and categorize their time entries. It features
account creation, time entry logging, category management, filtered reports, and user goal settings.

## Table of Contents

1. [Features](#features)
2. [Getting Started](#getting-started)
3. [Contribute](#contribute)
4. [Own Features](#own-features)
4. [License](#license)

## Features

- Sign Up / Sign In.
- Home Screen displaying all the time entries.
- Categories Screen for categorizing time entries and adding new categories.
- Add Time Entry with details: date, category, start time, end time, description, image.
- Reports Screen for filtered reports based on date range.
- Settings Screen for setting min and max daily hour goals.
- Log out functionality.

## Getting Started

To clone and run this application, you'll need [Git](https://git-scm.com)
and [Android Studio](https://developer.android.com/studio) installed on your computer. From your
command line:

```bash
# Clone this repository
$ git clone https://github.com/johanngeustyn/TimeSync.git

# Go into the repository
$ cd TimeSync

# Open with Android Studio and run the app on your emulator or actual device.
```

## Built With

- [Kotlin](https://kotlinlang.org/)
- [Firebase](https://firebase.google.com/)
    - Firebase Authentication: Used for managing user sign up/sign in.
    - Firebase Firestore: NoSQL cloud database to store and sync data for client- and server-side
      development.
    - Firebase Storage: Used for storing images related to time entries.

## Contribute

We love to receive contributions from the community and hear your opinions! We want to make
contributing to this project as easy and as transparent as possible, whether it's:

- Reporting a bug
- Submitting a fix
- Proposing new features

## Own Features

### Home Page

The app retrieves timesheet data from the Firebase Firestore database using the HomeViewModel class.
It fetches timesheet entries specific to the logged-in user and populates the home page with the
retrieved data.
Display of Timesheet Entries: The retrieved timesheet entries are displayed in a RecyclerView, where
each entry is represented as a card. Each card contains the following information:

- Entry Description: A brief description of the task or activity performed. 
- Start Time: The start time of the task. 
- End Time: The end time of the task. 
- Date: The date on which the task was performed. 
- Category: The category associated with the task (if applicable). 
- Photo (Optional): If a photo was uploaded for the task, it is displayed alongside other details.
- Dynamic Category Mapping: The app maintains a mapping of category IDs to their corresponding names. This mapping is used to display the category name for each timesheet entry, enhancing the user's understanding of the tasks performed. 
- Asynchronous Image Loading: If a photo was uploaded for a timesheet entry, the app asynchronously  loads the image from the provided URL and displays it in the corresponding card using the LoadImageTask class. This ensures a smooth user experience while retrieving and displaying images.

### Timesheet Entry and Image Upload

The app allows users to create new timesheet entries and optionally upload photos related to their tasks. The following functionalities are implemented:

Timesheet Entry Creation: Users can create new timesheet entries by providing the necessary details, including the entry description, start time, end time, and an optional category. Upon creating a new entry, the app sends the data to the Firebase Firestore database, associating it with the logged-in user.

Image Upload: Users have the option to upload photos related to their timesheet entries. When creating a new entry, users can select an image from their device's gallery. The app uploads the selected image to Firebase Storage and associates the generated URL with the timesheet entry in the Firestore database.

Display of Uploaded Photos: If a photo was uploaded for a timesheet entry, the app retrieves the corresponding URL and displays the image alongside other entry details on the home page. This provides visual context and enhances the user's experience while reviewing their timesheet entries.

## License

This project is licensed under the terms of the MIT license.
