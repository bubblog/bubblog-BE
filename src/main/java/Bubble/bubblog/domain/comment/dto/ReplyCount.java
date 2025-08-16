package Bubble.bubblog.domain.comment.dto;

/** replyCount 집계용 Projection */
public interface ReplyCount {
    Long getRootId();
    Long getCnt();
}
