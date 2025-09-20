package ru.MAT.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.MAT.Entities.Request;
import ru.MAT.Enums.RequestType;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByTgIdAndRequestType(Long tgId, RequestType requestType);

    @Query("SELECT MAX(r.requestId) FROM Request r WHERE r.requestType = :requestType")
    Integer findMaxRequestId(@Param("requestType") RequestType requestType);

    Request findByRequestIdAndRequestType(int requestId, RequestType requestType);
}
