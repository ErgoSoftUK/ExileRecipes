# Exile Recipes App

## Introduction
Web application to allow search and display of Exile recipes.  Includes Java application to parse the server config.cpp file to generate the recipes.json file used by the web application.

NOTE: This application uses Maven

## Cookbook
Angular web application with Bootstrap UI. In Cookbook folder..  
* Run `npm install` to get dependency packages
* Run `npm run start` to run in local/development mode
* Run `npm run build` to build distribution release

## Recipe Parser
Java application to generate the recipes.json file from the Exile server config.cpp file
* Run `RecipeParser <config.cpp file> <recipes.json file>`
