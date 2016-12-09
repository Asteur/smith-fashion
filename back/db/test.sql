CREATE TABLE test (
  id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  content mediumtext,
  PRIMARY KEY (id)
);

INSERT INTO test (id, content)
VALUES (1, "test");
