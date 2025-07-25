-- Create databases
CREATE DATABASE users;

-- Create users for each service (optional)
CREATE USER parma WITH PASSWORD 'password1234';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE users TO parma;