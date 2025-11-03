# Checkpoint
Checkpoint is a documentation and organization tool made for game developers who are tired of scattered notes, playtests, and project files.
It centralizes to-do lists, development logs, and documentation into a single, customizable desktop app. Built with JavaFX for flexibility and SQLite for lightweight data storage, Checkpoint helps game teams focus on what matters: Creating the games. 

# ⭐ __**MVP-Milestone Instructions:**__ ⭐
To set up the **Checkpoint** app, all we have right now is the front-end portion of the application that fully works at the moment. We will have the Springboot and SQL portion set up very soon. Currently, the Frontend and SQL portion run separately.

## Software Required:
- Software: Java JDK 17+
- Libraries: JavaFX SDK
- IDE: Visual Studio Code or another, similar IDE

## Setup Steps
- Download and install JavaFX SDK.
- Add the JavaFX library to your project’s module path.
- Clone or download this repository.
- Open the project in your IDE.
- Run the main JavaFX file to start the application.

## FrontEnd Instructions: 
- Once in the app, make a board for yourself and then click on it.
- Then, once in a board, make a notes section.
- Then type what you would like. It does not save yet, as that is set up for SQL. 

## SQL Instructions:
- install mySQL workbench with all add-ons from https://dev.mysql.com/downloads/installer/
- start a local hosted mysql server and run the schema in the file with the excecute all option under query
- in a seperate query run the commands "SHOW DATABASES;" and confirm "checkpoint_database" is in the table for all databases if not at the start of the other file re run it with "CREATE DATABASE checkpoint_database;
- in the spring boot project on the applicaion.properties change the tempuser and temppassword to "root" for the username then whatever password you used to log in
- to run the project do "mvn spring-boot:run" and it should be able to run without issues
- currently their is no functionality between the server and springboot project except ensuring a connection is made

## Notes
- CSS styling is included for layout and aesthetics.
- TLDR; Only the frontend works in this MVP. Backend and database integration will come in future milestones.

