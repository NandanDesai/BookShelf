CREATE TABLE roles(
    name VARCHAR(255) PRIMARY KEY,
    value INTEGER
);

CREATE TABLE books(
	id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    title VARCHAR(255) NOT NULL,
    description LONG VARCHAR NOT NULL,
    pdfFileName VARCHAR(255) NOT NULL,
    coverPhotoName VARCHAR(255) NOT NULL,
    roleName VARCHAR(255) NOT NULL REFERENCES roles(name)
);

CREATE TABLE users(
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    fullName VARCHAR(255) NOT NULL,
    profilePicName VARCHAR(255) NOT NULL,
    lastLogin TIMESTAMP NOT NULL,
    roleName VARCHAR(255) NOT NULL REFERENCES roles(name)
);

INSERT INTO roles VALUES ('Free', 1);
INSERT INTO roles VALUES ('Basic', 2);
INSERT INTO roles VALUES ('Premium', 3);
INSERT INTO roles VALUES ('Admin', 4);