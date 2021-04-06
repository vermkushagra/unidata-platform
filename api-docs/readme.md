# REST UI API Console

This is Swagger UI application used to display REST API (currently only internal one)

- You can view API specification
- You can query API with test values and check output data

## Installation
It is assumed Swagger UI is run on same developer server with backend module.
*Note:* this module only visualize API specification, actual specification is provided by backend module in runtime.

As usual you have two options to deploy

1. Build WAR and deploy manually to Tomcat

    - Execute from module folder

        gradle war

    - OR execute from project root folder

        gradle :api-docs:war

2. Build WAR and deploy by gradle to remote Tomcat server

    - Execute from module folder

        gradle cargoDeployRemote

    - OR execute from project root folder

        gradle :api-docs:cargoDeployRemote

### Configuration options
In case backend module is located in non-default place you can change default URL in `index.html`

    url: "/unidata-backend/api/internal/api-docs/"

## Running
Swagger UI can be accessed using URL http://localhost:8080/api-docs/