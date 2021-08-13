# BookShelf Security

This project is built to demonstrate various Cyber Security concepts. 

## Project Structure
 
 BookShelf Security is made up of five sub-projects, namely:
 

 1. **insecure-server**: Contains insecure code and a set of vulnerabilities for us to exploit. This project is built on top of [BookShelf Base Server](https://github.com/NandanDesai/BookShelf/tree/master/bookshelf-base/bookshelf-base-server).
 2. **secure-server**: Contains secure code and patches the vulnerabilities that are present in the *insecure-server*. This project is built on top of [BookShelf Base Server](https://github.com/NandanDesai/BookShelf/tree/master/bookshelf-base/bookshelf-base-server).
 3. **security-ui**: A common UI for both the *secure-server* and the *insecure server*. This project is built on top of [BookShelf Base UI](https://github.com/NandanDesai/BookShelf/tree/master/bookshelf-base/ui).
 4. **secure-server-monitoring**: This project monitors the *secure-server*. This project uses [Spring Boot Admin project by codecentric](https://github.com/codecentric/spring-boot-admin).
 5. **api-gateway**: This project is a gateway that will redirect the user to either *secure-server* or *insecure-server* based on the API endpoint that the user is accessing. This project uses [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) library.

The arrangement and functioning of all the above projects is shown below:

![bookshelf security project structure](https://raw.githubusercontent.com/NandanDesai/res/master/bookshelf-security-project-struct.png)

The *secure* and *insecure* projects are two independent servers with their own separate databases. And each of these projects are deployed in a separate Docker container (except for *security-ui* which will be served by the *api-gateway*).

Visit these sub-projects to know more!

## Vulnerabilities

This project demonstrates OWASP Top 10 vulnerabilities.

(to be continued...)
