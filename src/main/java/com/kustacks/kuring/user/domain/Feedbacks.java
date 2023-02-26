package com.kustacks.kuring.user.domain;

import com.kustacks.kuring.feedback.domain.Feedback;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
@NoArgsConstructor
public class Feedbacks {

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Feedback> feedbacks = new ArrayList<>();

    public void add(Feedback feedback) {
        this.feedbacks.add(feedback);
    }

    public List<Feedback> getAllFeedback() {
        if (feedbacks.isEmpty()) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(feedbacks);
    }

    public void clear() {
        this.feedbacks.clear();
    }
}
