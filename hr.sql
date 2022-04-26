select * from user_profile left join credentials on user_profile.user_id=credentials.profile_obj_user_id;

select * from user_profile;
select * from credentials;

drop table user_profile;
drop table credentials;

ALTER USER HR IDENTIFIED BY hr;



ALTER SEQUENCE HIBERNATE_SEQUENCE INCREMENT BY -2;
SELECT HIBERNATE_SEQUENCE.NEXTVAL FROM dual;
ALTER SEQUENCE HIBERNATE_SEQUENCE INCREMENT BY 1;

ALTER SEQUENCE HIBERNATE_SEQUENCE RESTART WITH 1;

insert into credentials (login_status,type,password,profile_obj_user_id) values('1','customer','admin2','2');
insert into user_profile (user_id,address,dob,email,gender,name,phone_number) values ('2','address',TO_DATE('01/01/2000', 'DD/MM/YYYY'),'email@domain.com','male','admin2','123456789');
update credentials set login_status='0' where user_id = '2'; 