package com.lis.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import com.lis.model.Credentials;
import org.springframework.stereotype.Repository;


@Repository
public interface Credentials_repo extends JpaRepository<Credentials, Integer> {
	void deleteById(int id);
//	void deleteByUserId(int id);
//
	Credentials getById(int id);
//	boolean existsByUserId(int id);
}
