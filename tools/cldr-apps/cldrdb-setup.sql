-- To setup MySQL for Survey Tool:
--
-- see <page>
--
-- $ mysql -uroot -p
-- <enter mysql root password>
-- mysql> set @password := '<Don't forget to put a strong password here!>';
-- mysql> set @user := 'surveytool';
-- mysql> set @db := 'cldrdb';

DROP SCHEMA IF EXISTS @db;
CREATE SCHEMA @db;
FLUSH PRIVILEGES;
DROP USER IF EXISTS @user @ localhost;
CREATE USER @user @'localhost' IDENTIFIED BY @password;
