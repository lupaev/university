-- liquibase formatted sql

-- changeset sergej:1
CREATE TABLE STUDENT_GROUP
(
    ID              BIGINT AUTO_INCREMENT   PRIMARY KEY,
    GROUP_NUMBER    VARCHAR(255)            NOT NULL,
    CREATED_AT      TIMESTAMP               NOT NULL
);

-- changeset sergej:2
CREATE TABLE STUDENT
(
    ID                  BIGINT AUTO_INCREMENT   PRIMARY KEY,
    FULL_NAME           VARCHAR(255)            NOT NULL,
    DATE_RECEIPT        DATE                    NOT NULL,
    STUDENT_GROUP_ID    BIGINT,
    CONSTRAINT FK_STUDENT_GROUP FOREIGN KEY (STUDENT_GROUP_ID) REFERENCES STUDENT_GROUP (ID)
);