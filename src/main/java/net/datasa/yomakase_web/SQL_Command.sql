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
                           `history_id` int NOT NULL AUTO_INCREMENT, -- 자동 증가하는 기본 키
                           `ingredient_name` varchar(700) NOT NULL,
                           `member_num` int NOT NULL,
                           `date` date NOT NULL, -- 날짜
                           `type` varchar(10) NOT NULL CHECK (`type` IN ('c', 'b')), -- c : 소비, b : 버림
                           PRIMARY KEY (`history_id`),
                           FOREIGN KEY (`ingredient_name`, `member_num`) REFERENCES `stock`(`ingredient_name`, `member_num`) ON DELETE CASCADE -- 외래 키 제약 조건
);


select * from `history`;

CREATE TABLE `subscription` (
                                `subscription_id` int auto_increment primary key,
                                `member_num` int NOT NULL,
                                `start_date` date NOT NULL,
                                `end_date` date NOT NULL,
                                FOREIGN KEY (`member_num`) REFERENCES `member`(`member_num`) ON DELETE CASCADE
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

-- history 테이블에 더미 데이터 추가
INSERT INTO `history` (`ingredient_name`, `member_num`, `date`, `type`) VALUES
                                                                            ('토마토', 8, '2024-09-12', 'c'),  -- 소비
                                                                            ('당근', 8, '2024-09-13', 'b'),    -- 버림
                                                                            ('감자', 8, '2024-09-14', 'c'),    -- 소비
                                                                            ('상추', 8, '2024-09-15', 'b'),    -- 버림
                                                                            ('양파', 8, '2024-09-16', 'c'),    -- 소비
                                                                            ('마늘', 8, '2024-09-17', 'b'),    -- 버림
                                                                            ('오이', 8, '2024-09-18', 'c'),     -- 소비
                                                                            ('피망', 8, '2024-09-19', 'b'),    -- 버림
                                                                            ('시금치', 8, '2024-09-20', 'c'),  -- 소비
                                                                            ('브로콜리', 8, '2024-09-21', 'c'), -- 소비
                                                                            ('콜리플라워', 8, '2024-09-22', 'b'), -- 버림
                                                                            ('애호박', 8, '2024-09-23', 'c'),  -- 소비
                                                                            ('가지', 8, '2024-09-24', 'b'),    -- 버림
                                                                            ('호박', 8, '2024-09-25', 'c'),    -- 소비
                                                                            ('고구마', 8, '2024-09-26', 'b');  -- 버림

-- stock 테이블에 더미 데이터 추가
INSERT INTO `stock` (`ingredient_name`, `member_num`, `is_having`, `use_by_date`, `update_date`) VALUES
                                                                                                     ('토마토', 8, 1, '2024-10-01', CURRENT_TIMESTAMP),  -- 재고 있음
                                                                                                     ('당근', 8, 0, '2024-09-20', CURRENT_TIMESTAMP),    -- 재고 없음
                                                                                                     ('감자', 8, 1, '2024-10-05', CURRENT_TIMESTAMP),    -- 재고 있음
                                                                                                     ('상추', 8, 0, '2024-09-18', CURRENT_TIMESTAMP),    -- 재고 없음
                                                                                                     ('양파', 8, 1, '2024-10-02', CURRENT_TIMESTAMP),    -- 재고 있음
                                                                                                     ('마늘', 8, 0, '2024-09-15', CURRENT_TIMESTAMP),    -- 재고 없음
                                                                                                     ('오이', 8, 1, '2024-10-07', CURRENT_TIMESTAMP),    -- 재고 있음
                                                                                                     ('피망', 8, 0, '2024-09-22', CURRENT_TIMESTAMP),    -- 재고 없음
                                                                                                     ('시금치', 8, 1, '2024-10-09', CURRENT_TIMESTAMP),  -- 재고 있음
                                                                                                     ('브로콜리', 8, 1, '2024-10-11', CURRENT_TIMESTAMP), -- 재고 있음
                                                                                                     ('콜리플라워', 8, 0, '2024-09-25', CURRENT_TIMESTAMP), -- 재고 없음
                                                                                                     ('애호박', 8, 1, '2024-10-12', CURRENT_TIMESTAMP),  -- 재고 있음
                                                                                                     ('가지', 8, 0, '2024-09-19', CURRENT_TIMESTAMP),    -- 재고 없음
                                                                                                     ('호박', 8, 1, '2024-10-15', CURRENT_TIMESTAMP),    -- 재고 있음
                                                                                                     ('고구마', 8, 0, '2024-09-28', CURRENT_TIMESTAMP);  -- 재고 없음

-- 게시판 공지사항 데이터
INSERT INTO `board` (`member_num`,`title`, `category`, `contents`) values	(14, '[공지사항] 서비스 이용 관련 안내', '공지사항', '서비스 이용 시 아래 내용을 참고하시기 바랍니다.'),	-- member_num은 관리자 계정 번호로 수정해주세요
                                                                             (14, '[공지사항] 9/25 변경사항 알림', '공지사항', '서비스의 내용이 일부 변경되었으니 첨부파일을 참고바랍니다.'),
                                                                             (14, '[공지사항] 10/8 서버점검 예정', '공지사항', '23:00~23:59에 서버점검으로 인해 서비스 이용이 불가하니 참고바랍니다.')

-- 게시판 나만의레시피 데이터
    INSERT INTO `board` (`member_num`,`title`, `category`, `contents`) values	(12, '나만의 김치볶음밥 맛있게 만드는 방법', '나만의레시피', '팬에 간장과 설탕을 태우듯 볶고 밥, 김치, 마요네즈 넣어 볶으면 됌'),
                                                                                (12, '비빔국수 깔끔하게 만들려면', '나만의레시피', '고추장 넣지 말고 고춧가루로 만들면 안 텁텁하고 깔끔해요!'),
                                                                                (13, '이렇게 만들면 엽떡이랑 맛 똑같음', '나만의레시피', '카레가루랑 후추를 넣어보세요'),
                                                                                (13, '엄마한테 전수받은 김치찌개 레시피', '나만의레시피', '고기, 김치 먼저 볶고 끓일 때 국물에 액젓을 넣음');

INSERT INTO `saved_recipe` (`member_num`, `food_name`, `recipe_url`, `saved_recipe`) VALUES
                                                                                         (8, '김치찌개', 'https://www.youtube.com/watch?v=qWbHSOplcvY', '재료: 김치, 돼지고기, 두부, 대파\n1. 김치를 잘게 썰어 냄비에 넣고 볶아주세요.\n2. 돼지고기를 넣고 함께 볶다가 물을 넣고 끓여줍니다.\n3. 두부와 대파를 넣고 한소끔 더 끓여 마무리합니다.'),
                                                                                         (8, '된장찌개', 'https://www.youtube.com/watch?v=ffuakdFmuh4', '재료: 된장, 감자, 두부, 애호박, 표고버섯\n1. 냄비에 된장을 풀고 물을 넣어 끓입니다.\n2. 감자, 두부, 애호박, 표고버섯을 넣고 익을 때까지 끓입니다.\n3. 필요하면 소금으로 간을 맞추고 마무리합니다.'),
                                                                                         (8, '비빔밥', 'https://www.youtube.com/watch?v=Jq2SwKMw8vI', '재료: 밥, 고사리, 시금치, 당근, 계란, 고추장\n1. 밥 위에 다양한 나물과 볶은 야채를 올립니다.\n2. 계란 후라이를 올리고 고추장을 넣어 비벼 드세요.');