# BookShelf

This repository contains a sample use case (called 'BookShelf') that I created to demonstrate various concepts related to software design, security etc.

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

There are 2 versions of BookShelf:

1. BookShelf Base
2. BookShelf Security


### BookSelf Base

*BookShelf Base* explains the basics of how Spring Boot and the Angular are used to build this project. *BookShelf Base* will be your starting point if you are interested to study all the concepts presented in this repository. *BookShelf Base* also acts as an independent project. If you simply want to know how to build an end-to-end standalone Spring Boot application, then *BookShelf Base* is for you!

### BookShelf Security

*BookShelf Security* exclusively focuses on security concepts and analysis and also explains how Spring Security library is used in this project for authentication and authorization.

## Index



## License

MIT License

Copyright 2022 Nandan Desai

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

