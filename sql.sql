CREATE USER 'login2'@'localhost' IDENTIFIED BY 'Welcome123!';
GRANT ALL PRIVILEGES ON *.* TO 'login2'@'localhost';
CREATE USER 'login2'@'%' IDENTIFIED BY 'Welcome123!';
GRANT ALL PRIVILEGES ON *.* TO 'login2'@'%';
