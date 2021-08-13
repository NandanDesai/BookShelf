# BookShelf

(This project is still under development)

### Introduction

This repository contains a sample use case (called 'BookShelf') that I created to demonstrate various concepts in Security and Cloud.

### Description of the Use Case

- BookShelf is a web application that allows users to read books online. A person can create an account (Signup) or login using an existing account. 
- Each user of the application has a role assigned to them.
- There are 4 roles, namely: **Free**, **Basic**, **Premium** and **Admin**. 
- The **Admin** role has the highest privilege in the app and the **Free** role has the lowest privilege. *Free < Basic < Premium < Admin* is the privilege order.
- Each book in this application also has one of the aforementioned roles assigned to them. 
- Users with the same or higher privilege than the book's labelled role should be able to read the book. For example, 'Free' user should only be able to read books which are labelled as 'Free' and 'Premium' user can read books which are labelled as 'Free', 'Basic' and 'Premium'.
- Admin should be able to:

	 1. Read all the books
	 2. Add a new book
	 3. Delete a book
	 4. Update the role of another user.

There are 3 versions of BookShelf: (<< make the below titles as hyperlinks to the README of each of those projects >>)

 1. BookShelf Base
 2. BookShelf Security
 3. BookShelf Cloud
