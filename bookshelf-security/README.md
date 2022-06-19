# BookShelf Security

This project is built to demonstrate various Cyber Security concepts. 

![BookShelf Security](https://raw.githubusercontent.com/NandanDesai/res/master/bookshelf-security-gif.gif)

## Project Structure
 
 BookShelf Security is made up of five sub-projects, namely:
 

 1. **insecure-server**: Contains insecure code and a set of vulnerabilities for us to exploit. This project is built on top of [BookShelf Base Server](https://github.com/NandanDesai/BookShelf/tree/master/bookshelf-base/bookshelf-base-server).
 2. **secure-server**: Contains secure code and patches the vulnerabilities that are present in the *insecure-server*. This project is built on top of [BookShelf Base Server](https://github.com/NandanDesai/BookShelf/tree/master/bookshelf-base/bookshelf-base-server).
 3. **security-ui**: A common UI for both the *secure-server* and the *insecure server*. This project is built on top of [BookShelf Base UI](https://github.com/NandanDesai/BookShelf/tree/master/bookshelf-base/ui).
 4. **secure-server-monitoring**: This project monitors the *secure-server*. This project uses [Spring Boot Admin project by codecentric](https://github.com/codecentric/spring-boot-admin).
 5. **api-gateway**: This project is a gateway that will redirect the user to either *secure-server* or *insecure-server* based on the API endpoint that the user is accessing. This project uses [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) library.

The arrangement and functioning of all the above projects is shown below:

![bookshelf security project structure](https://raw.githubusercontent.com/NandanDesai/res/master/bookshelf-security-project-struct.png)

The *secure* and *insecure* projects are two independent servers with their own separate databases. And *security-ui* which will be served by the *api-gateway*.

## How to use

To build the JAR files, run `build.sh` file. All the JAR files will be available in a newly-created *output* directory after this script completes. 

In order to run all the files, execute `run.sh`. The UI will be served by *api-gateway* at http://localhost:8080. 

*secure-server* will be running at http://localhost:8001/

*insecure-server* will be running at http://localhost:8002/

*secure-server-monitoring* UI will be available at http://localhost:9000/

## Analysis of BookSelf Security

Please refer analysis.md file.

## Security concepts

Application security boils down to two main concepts: **Authentication** (*who are you*) and **Authorization** (*what are you allowed to do*).

In this project, we've used Spring Security library that helps us with building both Authentication and Authorization mechanisms. In this section, we'll try to look into various Spring Security concepts and how it's used to implement security features into our application.

### Authentication

The first step of any *protected* application is authentication. This involves a user logging in usually with a username and a password. Let's try to understand how a Login functionality can be achieved with Spring Security.

`SecurityContextHolder` class is the foundation for Spring Security. The authentication and authorization basically revolves around this class. That is because, this class is supposed to contain all the information about a user, if that user is authenticated, what authorization does this user have etc. 

`SecurityContextHolder` class contains `SecurityContext` (it's an *Interface*) and `SecurityContext` contains `Authentication` object.

The  `Authentication`  object contains:
-   `principal`  - identifies the user. When authenticating with a username/password this is often an instance of  `UserDetails`.
-   `credentials`  - often a password.
-   `authorities`  - the  `GrantedAuthority` for the user. These are usually the roles assigned to the user.

We can access this `SecurityContextHolder`, `SecurityContext` and `Authentication` hierarchy like this: 

```java
SecurityContextHolder.getContext().getAuthentication().getPrincipal();
```
Let's take a look at how a user is authenticated when a username and password is provided.

```java
Authentication request = new UsernamePasswordAuthenticationToken(username, password);
try {
	Authentication result = authenticationManager.authenticate(request);
}catch (BadCredentialsException e){
	throw new Exception();
}
// If Authentication is successful
SecurityContextHolder.getContext().setAuthentication(result);
```

`AuthenticationManager` is a Spring Security class which has an `authenticate()` method containing the actual logic of verifying whether the username and password provided by the client matches with the one that is in our database. We can't override this logic but we can provide our own custom methods to the `AuthenticationManager` for retrieving the username and password from the database. We provide this to the `AuthenticationManager` using the `AuthenticationManagerBuilder` class which is also included in Spring Security.

The code snippet below shows how we provide our own custom methods. `userSecurityDetailsService` is an object that contains our custom method for retrieving user details from the database. And we can provide a password encoder which we've used to store the passwords in our database (the hashed passwords).

```java
authManagerBuilder.userDetailsService(userSecurityDetailsService).passwordEncoder(new BCryptPasswordEncoder());
```

`userSecurityDetailsService` object that we're passing in the above code is an instance of `UserSecurityDetailsService` class and it is our custom class that *implements* the `UserDetailsService` interface of Spring Security. We can implement it something like this:

```java
public class UserSecurityDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //our logic to retrieve user details from the database here
    }
}
```

In the `authenticate()` method of `AuthenticationManager` class, the code: `userDetailsService.loadUserByUsername()` is used and that's how it'll get to execute our logic to retrieve user details from the database. 

So, the authentication process goes like this: 

---> `authenticationManager.authenticate(request)` 

---> `userDetailsService.loadUserByUsername()` 

---> (retrieve the user details using the logic we provided) 

---> (verify whether the details from the database match with the credentials provided by the client) 

---> (if yes, then authentication is complete) 

---> (otherwise, raise `BadCredentialsException` exception)

Now that we've understood the authentication flow in Spring Security, let's concentrate more on the `SecurityContextHolder` and the stuff that happens **after** the authentication is successful.

You must have noticed that we've set the Authentication object as follows after a successful authentication:

```java
SecurityContextHolder.getContext().setAuthentication(result); //'result' is an instance of Authentication class.
```

Setting the *successful* Authentication object into the `SecurityContext` is important because this Authentication object will be referred during the authorization of this logged-in user. 

### Authorization / Access Control

After our application has successfully identified who the user is (i.e., the *authentication*), it needs to validate the user's actions. Let's try to understand how we can implement Access Control in our application.

Although there are plenty of ways to implement Access Control, we'll be taking a simplified approach for our use case.

As mentioned earlier, in the context of Spring Security, every user (`UserDetails`) has certain `authorities`. These authorities can be anything we want. These can be permissions like *read* or *write* on a resource for that particular user, or the roles assigned to the user. If we want to assign a role called Admin to the user, then we can simply put "ROLE_ADMIN" string as an authority. The prefix "ROLE_" helps Spring Security understand that this is a role and there are methods like `hasRole('Admin')` (notice that we don't have to provide the "ROLE_" prefix for `hasRole()`) to check the roles. But it's not mandatory that we need to use roles like this. In BookShelf, we've simply used "Free", "Basic", "Premium" and "Admin" strings as `authorities` for users without any prefix to keep things simple. And we can use a generic `hasAuthority('Admin')` method to check if our user has the *authority* that we expect for a task.

In BookShelf, we've put restrictions on the Java methods and who can execute them. Thus, we're making use of [Method Security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html) that Spring Security provides.

For example, in our use case, only Admins need to be able to add a new book in the database. So, in the `BookService` class in our codebase, we have `addBook()` method and we've put restrictions on it using the `@PreAuthorize` annotation that Spring Security provides. The method definition in our codebase looks something like this:

```java
@PreAuthorize("hasAuthority('Admin')")
public BookDto addBook(AddBookRequest addBookRequest){
	...
}
```

`@PreAuthorize` annotation has been given an expression where it looks like we're calling the `hasAuthority()` method. This expression is called [Spring Expression Language (SpEL)](https://docs.spring.io/spring-framework/docs/3.0.x/reference/expressions.html). *PreAuthorize* means, before we can execute the `addBook()` method, we're checking if the user who's requesting this has the 'Admin' authority or not. And as mentioned earlier, the `authentication` object in `SecurityContextHolder` is checked to see what authorities the user has.

Let's take one more example. We have a method called `getUserPhoto(Integer userId)` in the `UserService` class in our codebase. We want to allow only the owner of that photo and the Admin to access the photo. Hence, we've defined the `getUserPhoto()` method like this:

```java
@PreAuthorize("#userId == authentication.principal.grantedAuthorities[0].userId or hasAuthority('Admin')")
public Photo getUserPhoto(Integer userId){
	...
}
```
This way, before a user can access the photo, the expression in `@PreAuthorize` will be evaluated and it checks if that user is the owner of the photo (i.e., if their userId matches with the userId associated with the photo) or if the user is an Admin.

Although there are concepts like Role Hierarchy, fine-grained permission management etc. possible in Spring Security, this project focuses on a simple use case and an implementation of Authorization mechanism that could be understood by everyone.

One last thing we haven't covered here is, how will our application *identify* the user **after** the authentication is successful, i.e., how will it identify the user when they send subsequent requests after successful authentication? There needs to be a mechanism to remember a logged-in user. And that's something we need to think of while designing the Authentication architecture for our application.

### Stateful and Stateless Authentication

Stateful Authentication involves maintaining the user information (it's called a *session*) on the server after successful authentication and handing over an ID (called "JSESSIONID") to the client (the browser) in the form of a browser cookie. And for the subsequent requests, the client just needs to send this ID every time and the associated user information will be retrieved on the server for Authorization.   

Stateless Authentication is where the the server sends a token (commonly the Json Web Tokens (JWTs)) to the client after successful authentication. This token is cryptographically signed and it contains all the necessary information about the user (like username, user's role, other IDs etc.). The server doesn't store anywhere that this user has been authenticated (hence it's "stateless"). For the subsequent requests, the client will send this token to the server and the server will "re-authenticate" the user by reading this token and determining the user's role and other information from it and thus deciding what the user is allowed to do. 

### Pros and Cons of Stateful and Stateless Authentication mechanisms

The main advantage of Stateful Authentication is that the server has more control over logged-in (authenticated) users. For example, if the Admin of our application decides to elevate the privileges of a logged-in user from 'Basic' to 'Premium', then those changes will be reflected upon the user immediately as all the information is on the server and can be modified easily. For a Stateless Authentication, the server needs to send a new token consisting of new information about user's role. Hence, the new role for the logged-in user will be applied either after a logout and re-login (hence sending a new token upon the new login) or having another mechanism to send a new token while the user is still logged-in.

Another advantage of a Stateful Authentication is the user can be successfully logged out by just clearing that user's login session from the session store of the server. For a Stateless Authentication, there's no real logout until the token expires. A logout in a Stateless Authentication is just clearing the cookies in the browser but if the user has manually stored the token somewhere else and that token is not expired yet, then it will still work. We can maintain a blacklist of tokens on the server for the logged out users but implementing that is not very neat in my opinion.

It might sound like Stateless Authentication provides less security than Stateful Authentication, but both these mechanisms have their fair share of vulnerabilities and it really depends on how they are implemented.

In our project, we've used Stateless Authentication.

The user does the normal procedure of logging in with a username and password into our application and gets a JWT in response if the authentication is successful. But how will we check and re-authenticate the user in all their subsequent requests to our application?

The idea is to intercept the HTTP requests to check for the cookie that the client is sending our application. If this cookie is a valid JWT, then we'll extract the information from it and re-authenticate the user. And we can use Spring Boot's Filters to intercept these requests before they reach the Controller classes of our application.

### Filters

The basics of Spring Boot Filters is explained very well in this article: https://docs.spring.io/spring-security/reference/servlet/architecture.html

Spring Security's web-related tasks are mainly implemented using Servlet Filters described in the aforementioned article link.  

The `FilterChainProxy` class of Spring Security keeps a list of all the `SecurityFilterChain`s. Each HTTP request has a list of Security Filters associated with it (and this list is configurable) and these are stored in separate `SecurityFilterChain` instances. And depending upon the HTTP request, the associated `SecurityFilterChain` is executed by the `FilterChainProxy`.

We can also have no Security Filters in the `SecurityFilterChain`. For example, in our BookShelf secure server, we have the following configuration in our `WebSecurityConfig` class:

```java
@Override
public void configure(WebSecurity web) throws Exception {
	web.ignoring().antMatchers("/*","/assets/*","/actuator/**","/secure/login","/secure/signup");
}
```
With the above code, we're telling Spring Security to not associate any Security Filters for the above API endpoints. 

We also have the following configuration in the same `WebSecurityConfig` class: 

```java
@Override
protected void configure(HttpSecurity httpSecurity) throws Exception {
	httpSecurity.cors().disable()
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	httpSecurity.addFilterBefore(new ReauthenticationFilter(userSecurityDetailsService, jwtService), UsernamePasswordAuthenticationFilter.class);
    }
```

The `.authorizeRequests().anyRequest().authenticated()` line indicates that for *any* request (other than the ones that are mentioned to be ignored earlier), the user needs to be authenticated. That means, we need to have Security Filters in place. 

And there are some specific set of Security Filters that will be added by default and can't be configured and some others are configurable. It's also important to note that the order in which the Security Filters are placed also matters. 

And there is a standard order of Security Filters pre-defined. You can find the ordering here: https://docs.spring.io/spring-security/reference/servlet/architecture.html#servlet-security-filters

That being said, not all Security Filters mentioned above will be executed. As I said, some of them in that list are mandatorily executed and some of them depend on our configuration. But the order value is a standard.

In our project, we have created our own `ReauthenticationFilter` which will check if the JWT is valid or not for every request other than the ignored ones. But how are we going to place this Filter and in which order?

The following line in the configuration tells Spring Security about where to place our custom Filter:

```java
httpSecurity.addFilterBefore(new ReauthenticationFilter(userSecurityDetailsService, jwtService), UsernamePasswordAuthenticationFilter.class);
```

The above line can be confusing. `UsernamePasswordAuthenticationFilter` is a standard Security Filter and has a particular order value in the standard security filter chain list. But, in our application, we haven't configured to use this filter because we've created our own custom Filter to handle this scenario.

When we're saying `httpSecurity.addFilterBefore`, we're informing Spring Security to add our `ReauthenticationFilter` into the SecurityFilterChain with an order value less than the standard order value of `UsernamePasswordAuthenticationFilter`. The `addFilterBefore()` method doesn't say "execute this Filter before....". It says "give the order value to this filter less than the other standard filter mentioned".

Hence, in our application, the SecurityFilterChain lists for each of the API endpoints look like this (notice how the Filter list is empty for the ignored API endpoints as mentioned in our configuration):

```java
0 = {DefaultSecurityFilterChain@13419} "DefaultSecurityFilterChain [RequestMatcher=Ant [pattern='/*'], Filters=[]]"
1 = {DefaultSecurityFilterChain@13420} "DefaultSecurityFilterChain [RequestMatcher=Ant [pattern='/assets/*'], Filters=[]]"
2 = {DefaultSecurityFilterChain@13421} "DefaultSecurityFilterChain [RequestMatcher=Ant [pattern='/actuator/**'], Filters=[]]"
3 = {DefaultSecurityFilterChain@13422} "DefaultSecurityFilterChain [RequestMatcher=Ant [pattern='/secure/login'], Filters=[]]"
4 = {DefaultSecurityFilterChain@13423} "DefaultSecurityFilterChain [RequestMatcher=Ant [pattern='/secure/signup'], Filters=[]]"
5 = {DefaultSecurityFilterChain@13413} "DefaultSecurityFilterChain [RequestMatcher=any request, Filters=[
org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter@33bdd01, 
org.springframework.security.web.context.SecurityContextPersistenceFilter@49b89425, 
org.springframework.security.web.header.HeaderWriterFilter@63917fe1, 
org.springframework.security.web.authentication.logout.LogoutFilter@17fbfb02, 
io.github.nandandesai.secure.security.ReauthenticationFilter@159ac15f, 
org.springframework.security.web.savedrequest.RequestCacheAwareFilter@5345dfe8, 
org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter@760f1081, 
org.springframework.security.web.authentication.AnonymousAuthenticationFilter@7ac48f05, 
org.springframework.security.web.session.SessionManagementFilter@1c62c3fd, 
org.springframework.security.web.access.ExceptionTranslationFilter@76ac68b0, 
org.springframework.security.web.access.intercept.FilterSecurityInterceptor@799f354a
]]"
```

You can notice that in the above list, there is no `UsernamePasswordAuthenticationFilter` and the reason is explained earlier.

Helpful links related to the concepts explained here:

- https://docs.spring.io/spring-security/site/docs/3.0.x/reference/security-filter-chain.html
- https://docs.spring.io/spring-security/site/docs/3.0.x/reference/core-web-filters.html


### References

https://docs.spring.io/spring-security/reference/servlet/architecture.html#servlet-architecture

https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html

https://spring.io/guides/topicals/spring-security-architecture/

https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html

https://docs.spring.io/spring-security/site/docs/3.0.x/reference/technical-overview.html

https://github.com/spring-projects/spring-security-samples/tree/main/servlet/spring-boot/java/jwt/login

https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html
