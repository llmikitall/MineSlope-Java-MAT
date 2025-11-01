package ru.MAT.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.MAT.Entities.RequestMedia;
import ru.MAT.Entities.RequestMediaDraft;

public interface RequestMediaDraftRepository extends JpaRepository<RequestMediaDraft, Long> {

}
