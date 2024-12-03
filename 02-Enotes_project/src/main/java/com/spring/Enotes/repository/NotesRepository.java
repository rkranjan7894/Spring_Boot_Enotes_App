package com.spring.Enotes.repository;

import com.spring.Enotes.entity.Notes;
import com.spring.Enotes.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotesRepository extends JpaRepository<Notes,Integer> {
    public Page<Notes> findByUser(User user, Pageable pageable);
}
