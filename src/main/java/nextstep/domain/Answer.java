package nextstep.domain;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Objects;

import static nextstep.CannotDeleteException.ALREADY_DELETED_EXCEPTION;

@Entity
public class Answer extends AbstractEntity implements UrlGeneratable {
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_writer"))
    private User writer;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_to_question"))
    private Question question;

    @Size(min = 5)
    @Lob
    private String contents;

    private boolean deleted = false;

    public Answer() {
    }

    public Answer(User writer, String contents) {
        this.writer = writer;
        this.contents = contents;
    }

    public Answer(long id, String contents) {
        super(id);
        this.contents = contents;
    }

    public Answer(User writer, String contents, boolean deleted) {
        this(writer, contents);
        this.deleted = deleted;
    }

    public Answer(Long id, User writer, Question question, String contents) {
        super(id);
        this.writer = writer;
        this.question = question;
        this.contents = contents;
        this.deleted = false;
    }

    public User getWriter() {
        return writer;
    }

    public Question getQuestion() {
        return question;
    }

    public String getContents() {
        return contents;
    }

    public Answer setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public void toQuestion(Question question) {
        this.question = question;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void delete(User loginUser) throws CannotDeleteException {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        if(isDeleted()) {
            throw new CannotDeleteException(ALREADY_DELETED_EXCEPTION);
        }

        this.deleted = true;
    }

    public void update(User loginUser, Answer target) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        this.contents = target.contents;
    }

    @Override
    public String generateUrl() {
        return String.format("%s/answers/%d", question.generateUrl(), getId());
    }

    @Override
    public String toString() {
        return "Answer [id=" + getId() + ", writer=" + writer + ", contents=" + contents + "]";
    }

    public boolean equalsContents(Answer target) {
        if (Objects.isNull(target)) {
            return false;
        }
        return this.contents.equals(target.getContents());
    }
}
