# DefenseDrillSecurity
Security Microservice for the [DefenseDrillWeb backend](https://github.com/DamienWesterman/DefenseDrillWeb/).

# Purpose
This microservice is responsible for _creating_ JWT authorizations for users. It also offers a login/logout webpage for the web portal. It interacts with the PostgreSQL users database for persistent data. Also offers RESTful API endpoints to interact with user data.

# Design Considerations
As this whole application uses an [API Gateway](https://github.com/DamienWesterman/DefenseDrillGateway), this microservice does not actually perform endpoint authorization checks; this is done in the gateway itself. This service is specifically responsible for generating the JWT.

# Security Considerations
Due to the simplicity and low sensitivity nature of the application, this microservice does not check for authorization. As such, it should only be accessed through the [API Gateway](https://github.com/DamienWesterman/DefenseDrillGateway) - for example the POST endpoint to create a new user.
