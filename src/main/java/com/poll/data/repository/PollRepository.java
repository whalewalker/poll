package com.poll.data.repository;

import com.poll.data.model.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    Page<Poll> findByCreatedBy(Long id, Pageable pageable);
    long countByCreatedBy(long id);
    List<Poll> findByIdIn(List<Long> pollIds);
    List<Poll> findByIdIn(List<Long> pollIds, Sort sort);
}
