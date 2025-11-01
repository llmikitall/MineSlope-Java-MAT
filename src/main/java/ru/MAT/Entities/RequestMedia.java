package ru.MAT.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.MAT.Enums.RequestStatus;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String fileId;
    private String localPath;
}
