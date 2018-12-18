package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.Contact;

public interface MaintenanceContactsDAO extends MaintenanceContactsDAOCustom, JpaRepository<Contact, Long> {

}
