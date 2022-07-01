CREATE TABLE `user` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255) DEFAULT "Anonymous",
  `password_hash` varchar(255) NOT NULL,
  `head_image` varchar(255),
  `introduction` varchar(255) DEFAULT "无",
  `email` varchar(255) DEFAULT "无",
  `create_time` timestamp NOT NULL
);

CREATE TABLE `role` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(10)
);

CREATE TABLE `user_role` (
  `uid` int,
  `rid` int
);

CREATE TABLE `result` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `content` varchar(255) NOT NULL,
  `isFile` boolean DEFAULT false,
  `score` int DEFAULT 0,
  `comment` varchar(255) DEFAULT "无",
  `create_time` timestamp NOT NULL
);

CREATE TABLE `student_result` (
  `sid` int,
  `rid` int
);

CREATE TABLE `question` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `extension` varchar(255),
  `score` int DEFAULT 10,
  `isFile` boolean DEFAULT false,
  `answer` varchar(255) NOT NULL,
  `comment` varchar(255) DEFAULT "无",
  `create_time` timestamp NOT NULL
);

CREATE TABLE `questionType` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `typeName` varchar(255)
);

CREATE TABLE `questio_type` (
  `qid` int,
  `qtid` int
);

CREATE TABLE `teacher_question` (
  `tid` int,
  `qid` int
);

CREATE TABLE `question_result` (
  `qid` int,
  `rid` int
);

CREATE TABLE `course` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `student_num` int DEFAULT 0,
  `create_time` timestamp NOT NULL
);

CREATE TABLE `teacher_course` (
  `tid` int,
  `cid` int
);

CREATE TABLE `student_course` (
  `sid` int,
  `cid` int
);

CREATE INDEX `user_role_index_0` ON `user_role` (`uid`, `rid`);

CREATE INDEX `user_role_index_1` ON `user_role` (`rid`, `uid`);

CREATE INDEX `student_result_index_2` ON `student_result` (`sid`, `rid`);

CREATE INDEX `student_result_index_3` ON `student_result` (`rid`, `sid`);

CREATE INDEX `questio_type_index_4` ON `questio_type` (`qid`, `qtid`);

CREATE INDEX `questio_type_index_5` ON `questio_type` (`qtid`, `qid`);

CREATE INDEX `teacher_question_index_6` ON `teacher_question` (`tid`, `qid`);

CREATE INDEX `teacher_question_index_7` ON `teacher_question` (`qid`, `tid`);

CREATE INDEX `question_result_index_8` ON `question_result` (`qid`, `rid`);

CREATE INDEX `question_result_index_9` ON `question_result` (`rid`, `qid`);

CREATE INDEX `teacher_course_index_10` ON `teacher_course` (`tid`, `cid`);

CREATE INDEX `teacher_course_index_11` ON `teacher_course` (`cid`, `tid`);

CREATE INDEX `student_course_index_12` ON `student_course` (`sid`, `cid`);

CREATE INDEX `student_course_index_13` ON `student_course` (`cid`, `sid`);

ALTER TABLE `user_role` ADD FOREIGN KEY (`uid`) REFERENCES `user` (`id`);

ALTER TABLE `user_role` ADD FOREIGN KEY (`rid`) REFERENCES `role` (`id`);

ALTER TABLE `student_result` ADD FOREIGN KEY (`sid`) REFERENCES `user` (`id`);

ALTER TABLE `student_result` ADD FOREIGN KEY (`rid`) REFERENCES `result` (`id`);

ALTER TABLE `questio_type` ADD FOREIGN KEY (`qid`) REFERENCES `question` (`id`);

ALTER TABLE `questio_type` ADD FOREIGN KEY (`qtid`) REFERENCES `questionType` (`id`);

ALTER TABLE `teacher_question` ADD FOREIGN KEY (`tid`) REFERENCES `user` (`id`);

ALTER TABLE `teacher_question` ADD FOREIGN KEY (`qid`) REFERENCES `question` (`id`);

ALTER TABLE `question_result` ADD FOREIGN KEY (`qid`) REFERENCES `question` (`id`);

ALTER TABLE `question_result` ADD FOREIGN KEY (`rid`) REFERENCES `result` (`id`);

ALTER TABLE `teacher_course` ADD FOREIGN KEY (`tid`) REFERENCES `user` (`id`);

ALTER TABLE `teacher_course` ADD FOREIGN KEY (`cid`) REFERENCES `course` (`id`);

ALTER TABLE `student_course` ADD FOREIGN KEY (`sid`) REFERENCES `user` (`id`);

ALTER TABLE `student_course` ADD FOREIGN KEY (`cid`) REFERENCES `course` (`id`);
