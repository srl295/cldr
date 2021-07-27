
-- Note: This is only used for unit tests
-- Do not use for running Survey Tool

drop schema if exists cldrtest;
create schema cldrtest;
flush privileges;
drop user if exists 'cldrtest'@'localhost';
CREATE USER 'cldrtest'@'localhost' IDENTIFIED BY 'VbrB3LFCr6A!';
GRANT ALL PRIVILEGES ON cldrtest . * TO 'cldrtest'@'localhost';
flush privileges;
