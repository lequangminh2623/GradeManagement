/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "semester")
@NamedQueries({
    @NamedQuery(name = "Semester.findAll", query = "SELECT s FROM Semester s"),
    @NamedQuery(name = "Semester.findById", query = "SELECT s FROM Semester s WHERE s.id = :id"),
    @NamedQuery(name = "Semester.findBySemesterType", query = "SELECT s FROM Semester s WHERE s.semesterType = :semesterType")})
public class Semester implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "semester_type")
    private String semesterType;
    @OneToMany(mappedBy = "semester")
    @JsonIgnore
    private Set<GradeDetail> gradeDetailSet;
    @OneToMany(mappedBy = "semester")
    @JsonIgnore
    private Set<Classroom> classroomSet;
    @JoinColumn(name = "academic_year_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private AcademicYear academicYear;

    public Semester() {
    }

    public Semester(Integer id) {
        this.id = id;
    }

    public Semester(Integer id, String semesterType) {
        this.id = id;
        this.semesterType = semesterType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSemesterType() {
        return semesterType;
    }

    public void setSemesterType(String semesterType) {
        this.semesterType = semesterType;
    }

    public Set<GradeDetail> getGradeDetailSet() {
        return gradeDetailSet;
    }

    public void setGradeDetailSet(Set<GradeDetail> gradeDetailSet) {
        this.gradeDetailSet = gradeDetailSet;
    }

    public Set<Classroom> getClassroomSet() {
        return classroomSet;
    }

    public void setClassroomSet(Set<Classroom> classroomSet) {
        this.classroomSet = classroomSet;
    }

    public AcademicYear getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AcademicYear academicYear) {
        this.academicYear = academicYear;
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
        if (!(object instanceof Semester)) {
            return false;
        }
        Semester other = (Semester) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mh.pojo.Semester[ id=" + id + " ]";
    }
    
}
