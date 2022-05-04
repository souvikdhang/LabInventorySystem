select * from user_profile left join credentials on user_profile.user_id=credentials.profile_obj_user_id;
select * from equipment_details left join equipment_availability on equipment_details.equipment_id=equipment_availability.equipment_equipment_id;

select * from user_profile;
select * from credentials;
select * from equipment_details;
select * from equipment_availability;
select * from requests;

--drop table credentials;
--drop table user_profile;
--drop table equipment_availability;
--drop table equipment_details;
--drop table requests;
--ALTER USER HR IDENTIFIED BY hr;


insert into credentials (login_status,type,password,profile_obj_user_id) values('1','customer','admin2','27');
insert into user_profile (user_id,address,dob,email,gender,name,phone_number) values ('27','address',TO_DATE('01/01/2000', 'DD/MM/YYYY'),'email@domain.com','male','admin2','123456789');

update credentials set type='customer' where profile_obj_user_id = '28'; 
delete FROM user_profile WHERE user_profile.user_id= 27;
delete FROM credentials WHERE profile_obj_user_id= 27;
delete FROM requests WHERE userid= 32;