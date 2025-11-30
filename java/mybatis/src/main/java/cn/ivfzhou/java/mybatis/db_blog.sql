DROP TABLE IF EXISTS `t_author`;
CREATE TABLE `t_author`
(
    `id`   int(10) unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4;
INSERT INTO `t_author`
VALUES (1, 'author1'),
       (2, 'author2'),
       (3, 'author3'),
       (4, 'author4');


DROP TABLE IF EXISTS `t_blog`;
CREATE TABLE `t_blog`
(
    `id`           int(10) unsigned NOT NULL AUTO_INCREMENT,
    `title`        varchar(50) DEFAULT NULL,
    `grade`        double      DEFAULT NULL,
    `type`         int(11)     DEFAULT NULL,
    `author_id`    int(11)     DEFAULT NULL,
    `coauthor_id`  int(11)     DEFAULT NULL,
    `has_coauthor` bit(1)      DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `t_blog`
VALUES (1, 'title1', 99.89, 0, 1, 0, NULL),
       (2, 'title2', 89.78, 1, 2, 0, NULL),
       (3, 'title3', 90.2, 1, 3, 0, NULL),
       (4, 'title4', 69.11, 1, 1, 4, _binary '');


DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment`
(
    `id`      int(10) unsigned NOT NULL AUTO_INCREMENT,
    `blog_id` int(11)          NOT NULL,
    `comment` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 17
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `t_comment`
VALUES (1, 1, 'comment1'),
       (2, 1, 'comment2'),
       (3, 2, 'comment3'),
       (4, 2, 'comment4'),
       (5, 3, 'comment5'),
       (6, 3, 'comment6'),
       (7, 1, 'comment7'),
       (8, 1, 'comment8'),
       (9, 4, 'comment9'),
       (10, 4, 'comment10'),
       (11, 1, 'comment11'),
       (12, 2, 'comment12'),
       (13, 1, 'comment13'),
       (14, 3, 'comment14'),
       (15, 4, 'comment15'),
       (16, 1, 'comment16');

DROP PROCEDURE IF EXISTS selectBlog;
DELIMITER
$$
CREATE PROCEDURE selectBlog(IN param INT)
BEGIN
    SELECT *
    FROM `t_blog`
    WHERE `id` = param;
    SELECT *
    FROM `t_author`
    WHERE `id` = param;
END$$
DELIMITER ;
