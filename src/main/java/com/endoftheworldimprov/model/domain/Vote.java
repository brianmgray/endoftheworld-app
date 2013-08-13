package com.endoftheworldimprov.model.domain;

import com.endoftheworldimprov.model.dto.VoteDto;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * A vote by an audience member at a show
 * @author bgray
 **/
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long key;

    @NonNull private Long showKey;
    @NonNull private String ip;

    // needed to use 2 booleans to make the SQL for counting easier
    private int saveVote;
    private int destroyVote;

    public void setSaveVote(int saveVote) {
        Preconditions.checkArgument(saveVote == 0 || saveVote == 1);
        this.saveVote = saveVote;
        this.destroyVote = 1 - saveVote;
    }

    public void setDestroyVote(int destroyVote) {
        Preconditions.checkArgument(destroyVote == 0 || destroyVote == 1);
        this.destroyVote = destroyVote;
        this.saveVote = 1 - destroyVote;
    }

    public static Vote createVote(VoteDto voteDto) {
        Vote vote;
        switch (voteDto.getVote()) {
           case SAVE:
               vote = createSaveVote(voteDto.getShowKey(), voteDto.getIp());
               break;
           case DESTROY:
               vote = createDestroyVote(voteDto.getShowKey(), voteDto.getIp());
               break;
           default:
               throw new IllegalArgumentException("Cannot create vote from VoteDto: " + voteDto);
        }
        vote.setKey(voteDto.getKey());
        return vote;
    }

    public static Vote createSaveVote(Long showKey, String ip) {
        Vote vote = new Vote(showKey, ip);
        vote.setSaveVote(1);
        return vote;
    }

    public static Vote createDestroyVote(Long showKey, String ip) {
        Vote vote = new Vote(showKey, ip);
        vote.setDestroyVote(1);
        return vote;
    }
}
