package com.poll.services;

import com.poll.data.model.Poll;
import com.poll.security.UserPrincipal;
import com.poll.web.payload.request.PollRequest;
import com.poll.web.payload.request.VoteRequest;
import com.poll.web.payload.response.PagedResponse;
import com.poll.web.payload.response.PollResponse;

public interface PollService {
     PagedResponse<PollResponse> getAllPolls(UserPrincipal currentUser, int page, int size);
     PagedResponse<PollResponse> getPollsCreatedBy(String username, UserPrincipal currentUser, int page, int size);
     PagedResponse<PollResponse> getPollsVotedBy(String username, UserPrincipal currentUser, int page, int size);
     Poll createPoll(PollRequest pollRequest);
     PollResponse getPollById(Long pollId, UserPrincipal currentUser);
     PollResponse castVoteAndGetUpdatedPoll(Long pollId, VoteRequest voteRequest, UserPrincipal currentUser);
}
