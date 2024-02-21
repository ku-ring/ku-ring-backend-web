package com.kustacks.kuring.user.domain;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "update user set deleted = true where id = ?")
@Where(clause = "deleted = false")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Getter(AccessLevel.PUBLIC)
    @Column(name = "token", unique = true, length = 256, nullable = false)
    private String token;

    @Embedded
    private Feedbacks feedbacks = new Feedbacks();

    @Embedded
    private Departments departments = new Departments();

    @Embedded
    private Categories categories = new Categories();

    @Embedded
    private Bookmarks bookmarks = new Bookmarks();

    private boolean deleted = false;

    public User(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void addBookmark(String noticeId) {
        this.bookmarks.add(noticeId);
    }

    public List<String> lookupAllBookmarkIds() {
        return this.bookmarks.lookupAllId();
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

    public void subscribeCategory(CategoryName categoryName) {
        this.categories.add(categoryName);
    }

    public void unsubscribeCategory(CategoryName categoryName) {
        this.categories.delete(categoryName);
    }

    public void subscribeDepartment(DepartmentName departmentName) {
        this.departments.add(departmentName);
    }

    public void unsubscribeDepartment(DepartmentName departmentName) {
        this.departments.delete(departmentName);
    }

    public List<CategoryName> getSubscribedCategoryList() {
        Set<CategoryName> categoryNamesSet = this.categories.getCategoryNamesSet();
        return new ArrayList<>(categoryNamesSet);
    }

    public List<DepartmentName> getSubscribedDepartmentList() {
        Set<DepartmentName> departmentNamesSet = this.departments.getDepartmentNamesSet();
        return new ArrayList<>(departmentNamesSet);
    }

    public List<CategoryName> filteringNewCategoryName(List<CategoryName> newCategoryNames) {
        return newCategoryNames.stream()
                .filter(newCategoryName -> !this.categories.contains(newCategoryName))
                .toList();
    }

    public List<CategoryName> filteringOldCategoryName(List<CategoryName> newCategoryNames) {
        return this.categories.getCategoryNamesSet().stream().
                filter(oldCategoryName -> !newCategoryNames.contains(oldCategoryName))
                .toList();
    }

    public List<DepartmentName> filteringNewDepartmentName(List<DepartmentName> newDepartmentNames) {
        return newDepartmentNames.stream()
                .filter(newDepartmentName -> !this.departments.contains(newDepartmentName))
                .toList();
    }

    public List<DepartmentName> filteringOldDepartmentName(List<DepartmentName> newDepartmentNames) {
        return this.departments.getDepartmentNamesSet().stream()
                .filter(oldDepartmentName -> !newDepartmentNames.contains(oldDepartmentName))
                .toList();
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
