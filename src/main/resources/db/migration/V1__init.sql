CREATE TABLE users(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(31)
        NOT NULL
        CONSTRAINT constraint_unique_name UNIQUE,
    password VARCHAR(255)
        NOT NULL,
    roles VARCHAR(511)
        NOT NULL
);

-- Insert the default admin user: USERNAME: adminadmin PASSWORD: adminadmin
INSERT INTO users (name, password, roles) VALUES ('adminadmin', '$2a$12$bnYk31yASR3ORzOaZtwLUe8qtahLfgvtuuJWNR8CzSkRUxk30TqcK', 'ADMIN,USER');
