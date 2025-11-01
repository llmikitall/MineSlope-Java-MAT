package ru.MAT.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.MAT.Entities.RequestDraft;

public interface RequestDraftRepository extends JpaRepository<RequestDraft, Long> {

}
