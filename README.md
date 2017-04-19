# Depicture
A drawing game app for Android

The basic idea is, that only the creator of a game lobby has to be a registered user,
all other players can join (if they have the app) simply via the lobby id.

Each phone acts as a personal screen to every player on which information, which only he is supposed to
see, can be displayed.
The actual lobby can be viewed in a web browser via the lobby id on the webserver.
It displays scores, pictures and other information everyone is allowed to see.

The webserver I'm using for this app is a Django-uWSGI server with a MySQL database behind it and rest-framework-jwt for authentication.
Both clients and the weblobby get updates and notifications from the server via the Google Firebase API.

Sadly I can't upload the Django server on GitHub so if you want to build this project yourself you'll have to either use mine (which I neither appreciate nor will I guarantee the functionality of it) or build your own backend.
I also currently use Imgur for image storage so you'll have to get your own API key for that as well.

![Alt text](/app/src/main/res/mipmap-xxhdpi/ic_launcher.png?raw=true)
