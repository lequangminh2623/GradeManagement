/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Le Quang Minh
 */
@Entity
@Table(name = "extra_grade")
@NamedQueries({
    @NamedQuery(name = "ExtraGrade.findAll", query = "SELECT e FROM ExtraGrade e"),
    @NamedQuery(name = "ExtraGrade.findById", query = "SELECT e FROM ExtraGrade e WHERE e.id = :id"),
    @NamedQuery(name = "ExtraGrade.findByGrade", query = "SELECT e FROM ExtraGrade e WHERE e.grade = :grade")})
public class ExtraGrade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Max(value = 10)
    @Min(value = 0)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "grade")
    private Double grade;
    @JoinColumn(name = "grade_detail_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @JsonIgnore
    private GradeDetail gradeDetail;
    @Column(name = "grade_index", nullable = false)
    private Integer gradeIndex;

    public ExtraGrade() {
    }

    public ExtraGrade(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public GradeDetail getGradeDetail() {
        return gradeDetail;
    }

    public void setGradeDetail(GradeDetail gradeDetail) {
        this.gradeDetail = gradeDetail;
    }

    public Integer getGradeIndex() {
        return gradeIndex;
    }

    public void setGradeIndex(Integer gradeIndex) {
        this.gradeIndex = gradeIndex;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ExtraGrade other = (ExtraGrade) object;

        return Objects.equals(this.id, other.id)
                && Objects.equals(this.gradeIndex, other.gradeIndex)
                && Objects.equals(this.grade, other.grade);
    }

    @Override
    public int hashCode() {
        // Include multiple fields for better uniqueness
        return Objects.hash(id, gradeIndex, grade);
    }

    @Override
    public String toString() {
        return "com.mh.pojo.ExtraGrade[ id=" + id + " ]";
    }

}
