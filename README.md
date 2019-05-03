# Nukr API

This project is part of Nubank's hiring process for a Software Engineer position. It consists of a REST API 
where we can simulate connections between users.

The API was written using Compojure-API. All the interactions with the API can be done using the swagger interface.


## Features

* add user
* retrieve user
* add friend
* request friends suggestion

## Usage

### Run the application locally

`lein ring server`

### Little Demo

The database is preloaded with data. It has seven accounts already filled with friends. 
The id's start from 12341 up to 12347. They are the Original Avengers formation 
(Tony Stark, Natasha Romanoff, Bruce Banner, Thor Odinson Clint Barton and Steve Rogers) and Steven Strange. 
At the beginning of Avengers Infinity War, Doctor Strange had the chance to meet two members of the original Avengers
team. He met Bruce Banner and Tony Stark.He met Thor Odinson during the Thor Ragnarok movie.

So, Stephen Strange has three friends registered in the database. All the Avengers knows each other.



### Packaging and running as standalone jar

```
lein do clean, ring uberjar
java -jar target/server.jar
```

### Packaging as war

`lein ring uberwar`

## License

Copyright ©  FIXME
