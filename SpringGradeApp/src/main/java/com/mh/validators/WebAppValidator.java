package com.mh.validators;

import com.mh.pojo.AcademicYear;
import com.mh.pojo.Classroom;
import com.mh.pojo.Course;
import com.mh.pojo.ForumPost;
import com.mh.pojo.ForumReply;
import com.mh.pojo.Semester;
import com.mh.pojo.User;
import jakarta.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class WebAppValidator implements Validator {

    @Autowired
    private jakarta.validation.Validator beanValidator;
    private Set<Validator> springValidators = new HashSet<>();

    @Override
    public boolean supports(Class<?> clazz) {
        for (org.springframework.validation.Validator v : springValidators) {
            if (v.supports(clazz)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Set<ConstraintViolation<Object>> constraintViolations = beanValidator.validate(target);
        for (ConstraintViolation<Object> violation : constraintViolations) {
            errors.rejectValue(violation.getPropertyPath().toString(), violation.getMessageTemplate(), violation.getMessage());
        }

        if (target instanceof Classroom) {
            for (Validator validator : springValidators) {
                if (validator instanceof ClassroomValidator) {
                    validator.validate(target, errors);
                }
            }
        } else if (target instanceof User) {
            for (Validator validator : springValidators) {
                if (validator instanceof UserValidator) {
                    validator.validate(target, errors);
                }
            }
        } else if (target instanceof Course) {
            for (Validator validator : springValidators) {
                if (validator instanceof CourseValidator) {
                    validator.validate(target, errors);
                }
            }
        } else if (target instanceof AcademicYear) {
            for (Validator validator : springValidators) {
                if (validator instanceof AcademicYearValidator) {
                    validator.validate(target, errors);
                }
            }
        } else if (target instanceof Semester) {
            for (Validator validator : springValidators) {
                if (validator instanceof SemesterValidator) {
                    validator.validate(target, errors);
                }
            }
        } else if (target instanceof ForumPost) {
            for (Validator validator : springValidators) {
                if (validator instanceof ForumPostValidator) {
                    validator.validate(target, errors);
                }
            }
        } else if (target instanceof ForumReply) {
            for (Validator validator : springValidators) {
                if (validator instanceof ForumReplyValidator) {
                    validator.validate(target, errors);
                }
            }
        }
    }

    public void setSpringValidators(
            Set<Validator> springValidators) {
        this.springValidators = springValidators;
    }
}
