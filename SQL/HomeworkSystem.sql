CREATE TABLE IF NOT EXISTS `user`
(
    `id`            int PRIMARY KEY AUTO_INCREMENT,
    `name`          varchar(255) DEFAULT "Anonymous",
    `password_hash` varchar(255) NOT NULL,
    `head_image`    varchar(255),
    `introduction`  varchar(255) DEFAULT "无",
    `email`         varchar(255) DEFAULT "无",
    `create_time`   timestamp    NOT NULL
);

CREATE TABLE IF NOT EXISTS `role`
(
    `id`   int PRIMARY KEY AUTO_INCREMENT,
    `name` varchar(10)
);

CREATE TABLE IF NOT EXISTS `user_role`
(
    `uid` int,
    `rid` int
);

CREATE TABLE IF NOT EXISTS `result`
(
    `id`          int PRIMARY KEY AUTO_INCREMENT,
    `content`     varchar(255) NOT NULL,
    `isFile`      boolean      DEFAULT false,
    `isCheck`     boolean      DEFAULT false,
    `score`       int          DEFAULT 0,
    `comment`     varchar(255) DEFAULT "无",
    `create_time` timestamp    NOT NULL
);

CREATE TABLE IF NOT EXISTS `student_result`
(
    `sid` int,
    `rid` int
);

CREATE TABLE IF NOT EXISTS `question`
(
    `id`          int PRIMARY KEY AUTO_INCREMENT,
    `title`       varchar(255) NOT NULL,
    `extension`   varchar(255),
    `score`       int          DEFAULT 10,
    `isFile`      boolean      DEFAULT false,
    `answer`      varchar(255) NOT NULL,
    `comment`     varchar(255) DEFAULT "无",
    `create_time` timestamp    NOT NULL
);

CREATE TABLE IF NOT EXISTS `questionType`
(
    `id`       int PRIMARY KEY AUTO_INCREMENT,
    `typeName` varchar(255)
);

CREATE TABLE IF NOT EXISTS `question_type`
(
    `qid`  int,
    `qtid` int
);

CREATE TABLE IF NOT EXISTS `teacher_question`
(
    `tid` int,
    `qid` int
);

CREATE TABLE IF NOT EXISTS `question_result`
(
    `qid` int,
    `rid` int
);

CREATE TABLE IF NOT EXISTS `course`
(
    `id`          int PRIMARY KEY AUTO_INCREMENT,
    `name`        varchar(255) NOT NULL,
    `student_num` int DEFAULT 0,
    `create_time` timestamp    NOT NULL
);

CREATE TABLE IF NOT EXISTS `teacher_course`
(
    `tid` int,
    `cid` int
);

CREATE TABLE IF NOT EXISTS `student_course`
(
    `sid` int,
    `cid` int
);

CREATE TABLE IF NOT EXISTS `question_course`
(
    `qid` int,
    `cid` int
);

CREATE TABLE IF NOT EXISTS `result_course`
(
    `rid` int,
    `cid` int
);

CREATE INDEX `user_index_0` ON `user` (`name`);

CREATE UNIQUE INDEX `user_index_1` ON `user` (`email`);

CREATE UNIQUE INDEX `user_role_index_2` ON `user_role` (`uid`, `rid`);

CREATE UNIQUE INDEX `user_role_index_3` ON `user_role` (`rid`, `uid`);

CREATE UNIQUE INDEX `student_result_index_4` ON `student_result` (`sid`, `rid`);

CREATE UNIQUE INDEX `student_result_index_5` ON `student_result` (`rid`, `sid`);

CREATE UNIQUE INDEX `question_type_index_6` ON `question_type` (`qid`, `qtid`);

CREATE UNIQUE INDEX `question_type_index_7` ON `question_type` (`qtid`, `qid`);

CREATE UNIQUE INDEX `teacher_question_index_8` ON `teacher_question` (`tid`, `qid`);

CREATE UNIQUE INDEX `teacher_question_index_9` ON `teacher_question` (`qid`, `tid`);

CREATE UNIQUE INDEX `question_result_index_10` ON `question_result` (`qid`, `rid`);

CREATE UNIQUE INDEX `question_result_index_11` ON `question_result` (`rid`, `qid`);

CREATE UNIQUE INDEX `teacher_course_index_12` ON `teacher_course` (`tid`, `cid`);

CREATE UNIQUE INDEX `teacher_course_index_13` ON `teacher_course` (`cid`, `tid`);

CREATE UNIQUE INDEX `student_course_index_14` ON `student_course` (`sid`, `cid`);

CREATE UNIQUE INDEX `student_course_index_15` ON `student_course` (`cid`, `sid`);

CREATE UNIQUE INDEX `question_course_index_16` ON `question_course` (`qid`, `cid`);

CREATE UNIQUE INDEX `question_course_index_17` ON `question_course` (`cid`, `qid`);

CREATE UNIQUE INDEX `result_course_index_18` ON `result_course` (`rid`, `cid`);

CREATE UNIQUE INDEX `result_course_index_19` ON `result_course` (`cid`, `rid`);

ALTER TABLE `user_role`
    ADD FOREIGN KEY (`uid`) REFERENCES `user` (`id`);

ALTER TABLE `user_role`
    ADD FOREIGN KEY (`rid`) REFERENCES `role` (`id`);

ALTER TABLE `student_result`
    ADD FOREIGN KEY (`sid`) REFERENCES `user` (`id`);

ALTER TABLE `student_result`
    ADD FOREIGN KEY (`rid`) REFERENCES `result` (`id`);

ALTER TABLE `question_type`
    ADD FOREIGN KEY (`qid`) REFERENCES `question` (`id`);

ALTER TABLE `question_type`
    ADD FOREIGN KEY (`qtid`) REFERENCES `questionType` (`id`);

ALTER TABLE `teacher_question`
    ADD FOREIGN KEY (`tid`) REFERENCES `user` (`id`);

ALTER TABLE `teacher_question`
    ADD FOREIGN KEY (`qid`) REFERENCES `question` (`id`);

ALTER TABLE `question_result`
    ADD FOREIGN KEY (`qid`) REFERENCES `question` (`id`);

ALTER TABLE `question_result`
    ADD FOREIGN KEY (`rid`) REFERENCES `result` (`id`);

ALTER TABLE `teacher_course`
    ADD FOREIGN KEY (`tid`) REFERENCES `user` (`id`);

ALTER TABLE `teacher_course`
    ADD FOREIGN KEY (`cid`) REFERENCES `course` (`id`);

ALTER TABLE `student_course`
    ADD FOREIGN KEY (`sid`) REFERENCES `user` (`id`);

ALTER TABLE `student_course`
    ADD FOREIGN KEY (`cid`) REFERENCES `course` (`id`);

ALTER TABLE `question_course`
    ADD FOREIGN KEY (`qid`) REFERENCES `question` (`id`);

ALTER TABLE `question_course`
    ADD FOREIGN KEY (`cid`) REFERENCES `course` (`id`);

ALTER TABLE `result_course`
    ADD FOREIGN KEY (`rid`) REFERENCES `result` (`id`);

ALTER TABLE `result_course`
    ADD FOREIGN KEY (`cid`) REFERENCES `course` (`id`);

INSERT INTO `role` (`name`)
VALUES ("Student"),
       ("Teacher"),
       ("Admin");