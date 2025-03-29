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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author Le Quang Minh
 */
@Entity
@Table(name = "grade_detail")
@NamedQueries({
    @NamedQuery(name = "GradeDetail.findAll", query = "SELECT g FROM GradeDetail g"),
    @NamedQuery(name = "GradeDetail.findById", query = "SELECT g FROM GradeDetail g WHERE g.id = :id"),
    @NamedQuery(name = "GradeDetail.findByFinalGrade", query = "SELECT g FROM GradeDetail g WHERE g.finalGrade = :finalGrade"),
    @NamedQuery(name = "GradeDetail.findByMidtermGrade", query = "SELECT g FROM GradeDetail g WHERE g.midtermGrade = :midtermGrade"),
    @NamedQuery(name = "GradeDetail.findByUpdatedDate", query = "SELECT g FROM GradeDetail g WHERE g.updatedDate = :updatedDate")})
public class GradeDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Max(value=10)  @Min(value=0)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "final_grade")
    private Double finalGrade;
    @Column(name = "midterm_grade")
    private Double midtermGrade;
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "gradeDetail")
    private Set<ExtraGrade> extraGradeSet;
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Course course;
    @JoinColumn(name = "semester_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Semester semester;
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Student student;

    public GradeDetail() {
    }

    public GradeDetail(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(Double finalGrade) {
        this.finalGrade = finalGrade;
    }

    public Double getMidtermGrade() {
        return midtermGrade;
    }

    public void setMidtermGrade(Double midtermGrade) {
        this.midtermGrade = midtermGrade;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Set<ExtraGrade> getExtraGradeSet() {
        return extraGradeSet;
    }

    public void setExtraGradeSet(Set<ExtraGrade> extraGradeSet) {
        this.extraGradeSet = extraGradeSet;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
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
        if (!(object instanceof GradeDetail)) {
            return false;
        }
        GradeDetail other = (GradeDetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mh.pojo.GradeDetail[ id=" + id + " ]";
    }
    
}
