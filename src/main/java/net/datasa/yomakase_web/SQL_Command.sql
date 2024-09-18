CREATE TABLE `member` (
                          `member_num` int auto_increment primary key,
                          `id` varchar(50) NOT NULL UNIQUE,
                          `pw` varchar(100) NOT NULL,
                          `name` varchar(100) NOT NULL UNIQUE,
                          `gen` char(1) NOT NULL,
                          `birth_date` date NOT NULL,
                          `user_role` varchar(30) DEFAULT 'ROLE_USER' CHECK (`user_role` IN ('ROLE_USER', 'ROLE_SUBSCRIBER', 'ROLE_ADMIN')),
                          `enabled` tinyint(1) DEFAULT 1 CHECK (`enabled` IN (1, 0))
);

select * from `member`;
UPDATE `member` SET user_role ='ROLE_ADMIN' WHERE name ='Admin';

CREATE TABLE `stock` (
                         `ingredient_name` varchar(700) NOT NULL,
                         `member_num` int NOT NULL,
                         `is_having` tinyint(1) DEFAULT 1 CHECK (`is_having` IN (0, 1)), -- 1 : 재고 있음, 0 : 재고 없음
                         `use_by_date` DATE NOT NULL,
                         `update_date` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         PRIMARY KEY (`ingredient_name`, `member_num`),
                         FOREIGN KEY (`member_num`) REFERENCES `member`(`member_num`) ON DELETE CASCADE
);
select * from `stock`;

CREATE TABLE `allergy` (
                           `member_num` int NOT NULL,
                           `eggs` tinyint(1) DEFAULT 0 CHECK (`eggs` IN (0, 1)), -- 1 : 알레르기 있음, 0 : 알레르기 없음
                           `milk` tinyint(1) DEFAULT 0 CHECK (`milk` IN (0, 1)),
                           `buckwheat` tinyint(1) DEFAULT 0 CHECK (`buckwheat` IN (0, 1)),
                           `peanut` tinyint(1) DEFAULT 0 CHECK (`peanut` IN (0, 1)),
                           `soybean` tinyint(1) DEFAULT 0 CHECK (`soybean` IN (0, 1)),
                           `wheat` tinyint(1) DEFAULT 0 CHECK (`wheat` IN (0, 1)),
                           `mackerel` tinyint(1) DEFAULT 0 CHECK (`mackerel` IN (0, 1)),
                           `crab` tinyint(1) DEFAULT 0 CHECK (`crab` IN (0, 1)),
                           `shrimp` tinyint(1) DEFAULT 0 CHECK (`shrimp` IN (0, 1)),
                           `pork` tinyint(1) DEFAULT 0 CHECK (`pork` IN (0, 1)),
                           `peach` tinyint(1) DEFAULT 0 CHECK (`peach` IN (0, 1)),
                           `tomato` tinyint(1) DEFAULT 0 CHECK (`tomato` IN (0, 1)),
                           `walnuts` tinyint(1) DEFAULT 0 CHECK (`walnuts` IN (0, 1)),
                           `chicken` tinyint(1) DEFAULT 0 CHECK (`chicken` IN (0, 1)),
                           `beef` tinyint(1) DEFAULT 0 CHECK (`beef` IN (0, 1)),
                           `squid` tinyint(1) DEFAULT 0 CHECK (`squid` IN (0, 1)),
                           `shellfish` tinyint(1) DEFAULT 0 CHECK (`shellfish` IN (0, 1)),
                           `pine_nut` tinyint(1) DEFAULT 0 CHECK (`pine_nut` IN (0, 1)),
                           PRIMARY KEY (`member_num`),
                           FOREIGN KEY (`member_num`) REFERENCES `member`(`member_num`) ON DELETE CASCADE
);
select * from `allergy`;

CREATE TABLE `user_body_info` (
                                  `member_num` int NOT NULL,
                                  `weight` int NULL,
                                  `height` int NULL,
                                  PRIMARY KEY (`member_num`),
                                  FOREIGN KEY (`member_num`) REFERENCES `member`(`member_num`) ON DELETE CASCADE
);
select * from `user_body_info`;

CREATE TABLE `board` (
                         `board_num` int NOT NULL AUTO_INCREMENT,
                         `member_num` int NOT NULL,
                         `title` varchar(200) NOT NULL,
                         `category` varchar(10) NOT NULL,
                         `contents` mediumtext NOT NULL,
                         `create_date` timestamp DEFAULT current_timestamp,
                         `update_date` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `file_name` varchar(100) NULL,
                         `recommended` int DEFAULT 0 NOT NULL,
                         PRIMARY KEY (`board_num`),
                         FOREIGN KEY (`member_num`) REFERENCES `member`(`member_num`) ON DELETE CASCADE
);
select * from `board`;

CREATE TABLE `reply` (
                         `reply_num` int NOT NULL AUTO_INCREMENT,
                         `board_num` int NOT NULL,
                         `member_num` int NOT NULL,
                         `reply_contents` varchar(1000) NULL,
                         `create_date`	timestamp default current_timestamp,
                         PRIMARY KEY (`reply_num`),
                         FOREIGN KEY (`board_num`) REFERENCES `board`(`board_num`) ON DELETE CASCADE,
                         FOREIGN KEY (`member_num`) REFERENCES `member`(`member_num`) ON DELETE CASCADE
);
select * from `reply`;

CREATE TABLE `saved_recipe` (
                                `index_num` int NOT NULL AUTO_INCREMENT,
                                `member_num` int NOT NULL,
                                `food_name` varchar(100) NOT NULL,
                                `recipe_url` varchar(700) NOT NULL,
                                `saved_recipe` mediumtext NOT NULL,
                                PRIMARY KEY (`index_num`),
                                FOREIGN KEY (`member_num`) REFERENCES `member`(`member_num`) ON DELETE CASCADE
);
select * from `saved_recipe`;

CREATE TABLE `cal` (
                       `input_date` date NOT NULL,
                       `member_num` int NOT NULL,
                       `b_name` varchar(100) NULL,
                       `b_kcal` int DEFAULT 0,
                       `l_name` varchar(100) NULL,
                       `l_kcal` int DEFAULT 0,
                       `d_name` varchar(100) NULL,
                       `d_kcal` int DEFAULT 0,
                       `too_much` mediumtext NULL, -- 과하게 먹은 것.
                       `lack` mediumtext NULL, -- 적게 먹은 것.
                       `recom` mediumtext NULL, -- 권장 식재료 이름만
                       `total_kcal` int DEFAULT 0,
                       `score` int DEFAULT 0, -- 점수 → 마우스 오버시, 혹은 날짜 밑에 표시
                       PRIMARY KEY (`input_date`, `member_num`),
                       FOREIGN KEY (`member_num`) REFERENCES `member`(`member_num`) ON DELETE CASCADE
);
select * from `cal`;

CREATE TABLE `history` (
                           `ingredient_name` varchar(700) NOT NULL,
                           `member_num` int NOT NULL,
                           `date` date NULL,
                           `type` varchar(10) NOT NULL CHECK (`type` IN ('c', 'b')), -- c : 소비, b : 버림
                           PRIMARY KEY (`ingredient_name`, `member_num`),
                           FOREIGN KEY (`member_num`) REFERENCES `member`(`member_num`) ON DELETE CASCADE
);

select * from `history`;

CREATE TABLE `subscription` (
                                `subscription_id` int auto_increment primary key,
                                `member_num` int NOT NULL,
                                `start_date` date NOT NULL,
                                `end_date` date NOT NULL,
                                FOREIGN KEY (`member_num`) REFERENCES `member`(`member_num`) ON DELETE CASCADE
);

CREATE TABLE `complaint` (
                             `member_num` INT NOT NULL,  -- 회원 고유 번호 (FK)
                             `complaint_num` INT AUTO_INCREMENT PRIMARY KEY,  -- 문의사항 고유 번호
                             `title` VARCHAR(200) NOT NULL,  -- 제목
                             `category` VARCHAR(50) NOT NULL,  -- 분류
                             `contents` MEDIUMTEXT NOT NULL,  -- 내용
                             `input_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,-- 입력 날짜
                             `status` tinyint(1) DEFAULT 0 CHECK (`status` IN (0, 1)), -- 1 : 답변완료, 0 : 답변중
                             FOREIGN KEY (`member_num`) REFERENCES `member`(`member_num`) ON DELETE CASCADE -- 회원 테이블과의 관계
);


drop table  allergy ;
drop table  history  ;
drop table  user_body_info  ;
drop table  board  ;
drop table  reply  ;
drop table  saved_recipe  ;
drop table  cal ;
drop table member;
drop table  `stock`;
drop table  `complaint`;