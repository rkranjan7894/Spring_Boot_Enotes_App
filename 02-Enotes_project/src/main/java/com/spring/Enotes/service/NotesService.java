package com.spring.Enotes.service;

import com.spring.Enotes.entity.Notes;
import com.spring.Enotes.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NotesService {
 public Notes saveNotes(Notes notes);
 public Notes getNotesById(int id);
 //public List<Notes> getNotesByUser(User user);
 public Page<Notes> getNotesByUser(User user, int pageNo);

 public Notes updateNotes(Notes notes);
 public boolean deleteNotes(int id);
}
