create table ai_test
(
    id           bigint                             not null
        primary key,
    test        varchar(30)                        null comment '标题'

)
    comment '测试' charset = utf8mb3
                   row_format = DYNAMIC;

