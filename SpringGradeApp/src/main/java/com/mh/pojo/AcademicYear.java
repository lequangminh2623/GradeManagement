/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author Le Quang Minh
 */
@Entity
@Table(name = "academic_year")
@NamedQueries({
    @NamedQuery(name = "AcademicYear.findAll", query = "SELECT a FROM AcademicYear a"),
    @NamedQuery(name = "AcademicYear.findById", query = "SELECT a FROM AcademicYear a WHERE a.id = :id"),
    @NamedQuery(name = "AcademicYear.findByYear", query = "SELECT a FROM AcademicYear a WHERE a.year = :year")})
public class AcademicYear implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 9)
    @Column(name = "year")
    private String year;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "academicYear")
    private Set<Semester> semesterSet;

    public AcademicYear() {
    }

    public AcademicYear(Integer id) {
        this.id = id;
    }

    public AcademicYear(Integer id, String year) {
        this.id = id;
        this.year = year;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Set<Semester> getSemesterSet() {
        return semesterSet;
    }

    public void setSemesterSet(Set<Semester> semesterSet) {
        this.semesterSet = semesterSet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AcademicYear)) {
            return false;
        }
        AcademicYear other = (AcademicYear) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mh.pojo.AcademicYear[ id=" + id + " ]";
    }
    
}
