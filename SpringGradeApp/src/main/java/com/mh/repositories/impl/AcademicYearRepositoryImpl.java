/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.repositories.impl;

import com.mh.pojo.AcademicYear;
import com.mh.repositories.AcademicYearRepository;
import jakarta.persistence.Query;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Le Quang Minh
 */
@Repository
@Transactional
public class AcademicYearRepositoryImpl implements AcademicYearRepository {
    
    @Autowired
    private LocalSessionFactoryBean factory;
    
    public List<AcademicYear> getAcademicYears() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM AcademicYear", AcademicYear.class);
        return q.getResultList();
    }
}
