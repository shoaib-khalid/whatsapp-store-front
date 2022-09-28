package com.kalsym.whatsapp.service.repository;

import com.kalsym.whatsapp.service.model.UserPayment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sarosh
 */
@Repository
public interface UserPaymentRepository extends JpaRepository<UserPayment, String>, PagingAndSortingRepository<UserPayment, String> {
 
}
