# BookShelf Base UI

This is an Angular 10 project for the UI of BookShelf Base.


## Angular Components

 - **login**

![login component](https://raw.githubusercontent.com/NandanDesai/res/master/bookshelf-base-login-component.PNG)

 - **signup**

![signup component](https://raw.githubusercontent.com/NandanDesai/res/master/bookshelf-base-signup-component.PNG)

 - **dash** (Dashboard Component)

![dash component](https://raw.githubusercontent.com/NandanDesai/res/master/bookshelf-base-dash-component.PNG)

 - **pdfviewer**

![pdfviewer component](https://raw.githubusercontent.com/NandanDesai/res/master/bookshelf-base-pdfviewer-component.PNG)

 - **admin-dash** (Admin Dashboard Component)

![admin-dash component](https://raw.githubusercontent.com/NandanDesai/res/master/bookshelf-base-admin-dash-component.PNG)

Working demo for **admin-dash**:

![admin-dash working](https://raw.githubusercontent.com/NandanDesai/res/master/bookshelf-base-admin-dash-working.gif)

 - **profile-view**

![profile-view component](https://raw.githubusercontent.com/NandanDesai/res/master/bookshelf-base-profile-view-component.PNG)

 - **user-edit**

![user-edit component](https://raw.githubusercontent.com/NandanDesai/res/master/bookshelf-base-user-edit-component.PNG)


## Main Functionalities

### Route Guards

[Route Guards](https://angular.io/guide/router#preventing-unauthorized-access) can be found in `_misc` directory. There are 3 route guards:

 - **auth.guard.ts**: This guard checks if the user is logged in and if the JWT token is valid. If the user is not logged in or if the token is expired, then this returns `false` and the user will be redirected to `login` Component.
 - **role.guard.ts**: This guard checks if the user has the `admin` role or not. If the user is an `admin`, then this returns `true` otherwise `false`. This is done to ensure that only the user with `admin` role should be able to view the `admin-dash` Component.
 - **loggedin.guard.ts**: This guard is the opposite of `auth.guard`. This guard ensures that the users who have already logged in shouldn't be able to see the `login` and `signup` components.

### Access Control

To demonstrate Access Control implemented in BookShelf Base, here is a GIF that shows a `Free` user trying to access a `Premium` book:

![access control demo](https://raw.githubusercontent.com/NandanDesai/res/master/bookshelf-base-access-control.gif)


## Libraries Used

You can find the entire list in `package.json`. But the prominent ones are:

 - [is-loading](https://gitlab.com/service-work/is-loading) ([related article](https://dev.to/johncarroll/angular-how-to-easily-display-loading-indicators-4359)): If we have a one indefinite progress bar on the top of the toolbar, we can display it whenever there is any async HTTP call going on. This small library helps us with that.
 - [angular-notifier](https://github.com/dominique-mueller/angular-notifier): To display notifications on the top-right corner of the app.
 - [ng2-pdf-viewer](https://github.com/VadimDez/ng2-pdf-viewer): For rendering PDF files.
 - [jwt-decode](https://github.com/auth0/jwt-decode): For decoding JWTs.

Here is a demo of the UI with a complete flow from Login to Logout:

![demo](https://raw.githubusercontent.com/NandanDesai/res/master/bookshelf-base-working.gif)

## Setup This Project

Make sure you are inside the `ui` directory:

```bash
npm install
```

The above command will install all the dependencies.

To run a dev server,

```bash
npm start
```

The above command will compile and serve the files on http://localhost:4200. In order for the frontend to work properly, it needs the backend API server to be running on port 8080. So, make sure you have `bookshelf-base-server` running.


## Build

To compile this project, 

```bash
ng build --prod
```

The compiled files will be written to `dist/` directory. 

**Note**: If you want to serve these compiled files from `bookshelf-base-server`, then place all the files from `dist/` into `public/` directory of the `resources/` folder of `bookshelf-base-server` project and then build the JAR file!

