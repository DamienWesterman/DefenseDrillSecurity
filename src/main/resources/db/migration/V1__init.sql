CREATE TABLE users(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(31)
        NOT NULL
        CONSTRAINT constraint_unique_name UNIQUE,
    password VARCHAR(255)
        NOT NULL,
    roles VARCHAR(511)
        NOT NULL
)
