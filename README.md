# Spring Boot Application "CurrencyAPI" 

CurrencyAPI is an application for converting currenciec based on Polish National Bank medium rates;

## Installation 

Project runs on JDK 11. 
That's a maven-based project, and application is Dockerized. There are three containers for: database, api and pgadmin for administraing database.<br/>
To start api use docker-compose. 
Make sure you have your docker-compose and docker installed and running and in project's directory run:
```
            docker-compose up
```
It should build images and run containers with api, database and pgadmin.<br/>
You can also build and run app with maven or java but you still going to need your database container.
    
# Accessing Database

API is not specified to have access to database throgh controllers, so to access databaseb with log history use pgadmin. <br/>
Make sure your database container and pgadmin container are running and in your browser got to:
```
            http://localhost:5050/
```
Login to the page using login and password:
```
            login: admin@currency.com
            password: password
```
In pgadmin add new server(left top corner), and in connection_name(connection section) type:
```
            host_name/adress: postgres_database
            user: user
            password: password
```
Now you should have access to the database(Database postgres -> Schemas -> Tables -> action table)

## API Info

API starts on port :
```
            http://localhost:8080/
```
Api has three controllers:

1.GET - getAllCurrency() - doesn't require parameters and returns a List of available currencies.<br/>
Returns: HTTP STATUS 200 in case of success.<br/>
JSON:
```

                  [
                    {
                      "code": "string",
                      "name": "string"
                    }
                  ]
```

2.GET - getConvertation(String codeFromCurrency, String codeToCurrency) - returns amount in the result of convertation from one currency to another.<br/>
Requires Parameters: codeFromCurrency - currency from which convert code(case is ignored)<br/>
                     codeToCurrency - currency to which convert code(case is ignored)<br/>
                     amount - amount of money to convert(>0)<br/>
Returns: HTTP STATUS 200 - in case of success.<br/>
         HTTP STATUS 400 - in case of bad input(code or amount)<br/>
JSON: 
```

                  {
                    "amount": 0,
                    "name": "string"
                  }
```
3.POST - getRates(List<String> codes) - returns list of rates for required currencies<br/>
 Requires Body: 
```

                  [
                    {
                      "name": "string",
                      "rate": 0
                    }
                  ]
```
 Returns: HTTP_STATUS 200 - in case of success<br/>
          HTTP STATUS 400 - in case of bad code of currency<br/>

JSON: 
```
                  [
                    {
                      "name": "string",
                      "rate": 0
                    }
                  ]

```
## SWAGGER
    
API has a build-in swagger tool for easier testing api. To use it, just after running the app go to:
```
    http://localhost:8080/swagger-ui/
```
    

 
  
                                 
            
    
    
    
