
/**
 * PaymentHeaderDAO.java
 * mafs-core-data
 * Mar 5, 2013
 * 5:03:27 PM
 */
package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.PaymentHeader;

/**
 * @author anand.mohan
 *
 */
public interface PaymentHeaderDAO extends JpaRepository<PaymentHeader, Long> {

}
