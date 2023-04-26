package com.kustacks.kuring.user.domain;

import com.kustacks.kuring.feedback.domain.Feedback;
import com.kustacks.kuring.worker.DepartmentName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "token", unique = true, length = 256, nullable = false)
    private String token;

    @Embedded
    private Feedbacks feedbacks = new Feedbacks();

    @Embedded
    private Departments departments = new Departments();

    public User(String token) {
        this.token = token;
    }

    public void addFeedback(String content) {
        this.feedbacks.add(new Feedback(content, this));
    }

    public List<Feedback> getAllFeedback() {
        return this.feedbacks.getAllFeedback();
    }

    public void clearFeedbacks() {
        this.feedbacks.clear();
    }

    public void subscribeDepartment(DepartmentName departmentName) {
        this.departments.add(departmentName);
    }

    public void unsubscribeDepartment(DepartmentName departmentName) {
        this.departments.delete(departmentName);
    }

    public List<DepartmentName> getSubscribedDepartmentList() {
        Set<DepartmentName> departmentNamesSet = this.departments.getDepartmentNamesSet();
        return new ArrayList<>(departmentNamesSet);
    }

    public List<DepartmentName> filteringNewDepartmentName(List<DepartmentName> newDepartmentNames) {
        return newDepartmentNames.stream()
                .filter(newDepartmentName -> !this.departments.contains(newDepartmentName))
                .collect(Collectors.toList());
    }

    public List<DepartmentName> filteringOldDepartmentName(List<DepartmentName> newDepartmentNames) {
        return this.departments.getDepartmentNamesSet().stream()
                .filter(oldDepartmentName -> !newDepartmentNames.contains(oldDepartmentName))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
