package ru.MAT.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.MAT.Enums.RequestStatus;
import ru.MAT.Enums.RequestType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long tgId;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private RequestType requestType;

    @Column(nullable = false)
    private int requestId;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private RequestStatus requestStatus = RequestStatus.CREATING;

    @Column(nullable = false)
    private boolean editable = true;

    @Column(nullable = false)
    private String telegramMessageId = "-";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mediaGroupId = "-";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String htmlText = "-";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String box0 = "-";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String box1 = "-";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String box2 = "-";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String box3 = "-";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String box4 = "-";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String box5 = "-";
}
