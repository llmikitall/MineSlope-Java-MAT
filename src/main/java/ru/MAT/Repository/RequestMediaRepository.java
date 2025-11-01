package ru.MAT.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.MAT.Entities.RequestMedia;

public interface RequestMediaRepository extends JpaRepository<RequestMedia, Long> {
    public RequestMedia findByFileId(String fileId);
}
