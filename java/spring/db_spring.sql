DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`
(
    `id`   int unsigned NOT NULL AUTO_INCREMENT,
    `age`  tinyint unsigned DEFAULT NULL,
    `name` char(5) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `t_user`
VALUES (1, 18, 'zs'),
       (2, 23, 'ls');
