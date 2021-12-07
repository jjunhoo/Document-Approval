-- 문서 > Table Schema
drop table documents if exists;
create table documents(
    document_id bigint auto_increment primary key, -- 문서 ID
    title varchar(100) not null, -- 문서 제목
    content varchar(255) not null, -- 문서 내용
    classification varchar(10) not null, -- 문서 분류
    document_approval_status varchar(10) not null, -- 문서 결재 상태
    create_id varchar(20), -- 작성자
    create_date datetime, -- 작성일
    update_id varchar(20), -- 수정자
    update_date datetime -- 수정일
);

-- 문서 > Table Schema
drop table documents_approval if exists;
create table documents_approval(
    document_approval_id bigint auto_increment primary key, -- 문서 결재 ID
    document_id bigint not null, -- 문서 ID
    document_approval_order integer not null, -- 문서 결재 순서
    document_approval_status varchar(10) not null, -- 문서 결재자의 문서 결재 상태
    document_approver_id varchar(20) not null, -- 문서 결재자 ID
    document_approver_name varchar(30) not null, -- 문서 결재자 이름
    document_approval_opinion varchar(255), -- 문서 결재 의견
    create_id varchar(20), -- 작성자
    create_date datetime, -- 작성일
    update_id varchar(20), -- 수정자
    update_date datetime -- 수정일
);

-- 사용자 정보 > Table Schema
drop table user_info if exists;
create table user_info(
    user_id varchar(20) primary key, -- 사용자 ID
    password varchar(80) not null, -- 패스워드
    user_name varchar(30) not null, -- 사용자 이름
    use_yn varchar(1) not null, -- 사용 여부
    create_id varchar(20), -- 작성자
    create_date datetime, -- 작성일
    update_id varchar(20), -- 수정자
    update_date datetime -- 수정일
)