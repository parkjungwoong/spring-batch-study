DROP TABLE simple IF EXISTS;

CREATE TABLE simple  (
    id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(20),
    age INTEGER (20)
);

INSERT INTO simple VALUES ( 1,'pjw', 29);
INSERT INTO simple VALUES ( 2,'pjw2', 30);
INSERT INTO simple VALUES ( 3,'pjw3', 31);
INSERT INTO simple VALUES ( 4,'pjw4', 32);
INSERT INTO simple VALUES ( 5,'pjw5', 33);


DROP TABLE simple2 IF EXISTS;
CREATE TABLE simple2  (
  id BIGINT IDENTITY NOT NULL PRIMARY KEY,
  name VARCHAR(20),
  age INTEGER (20),
  addr VARCHAR(20)
);