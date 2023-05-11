-- create stop word table
CREATE TABLE notice_stop_word(value VARCHAR(30));

-- insert stop word
INSERT INTO notice_stop_word
VALUES ('그리고'), ('게다가'), ('더욱이'), ('아울러'), ('동시에'), ('어쩌면'), ('하물며'), ('이처럼'), ('이같이'), ('바로'),
       ('그러나'), ('하지만'), ('그렇지만'), ('그럼에도'), ('반면에'), ('도리어'), ('오히려'), ('반대로'),
       ('그러므로'), ('따라서'), ('그러니까'), ('그리하여'), ('그렇게'), ('때문에'), ('그래서'), ('그러면'), ('그러니'), ('급기야'), ('마침내'), ('왜냐하면'),
       ('그런데'), ('다만'),
       ('말하자면'), ('예를들면'), ('일례로'), ('사실상'), ('예컨데'), ('덧붙혀'), ('구체적'),
       ('끝으로'), ('결국'), ('결론적으로'), ('마지막으로');

-- register notice_stop_word table
SET GLOBAL innodb_ft_server_stopword_table = 'kuring/notice_stop_word';

-- create full text index
CREATE FULLTEXT INDEX idx_notice_subject ON notice (subject);
