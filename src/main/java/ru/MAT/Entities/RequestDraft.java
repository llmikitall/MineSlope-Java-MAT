package ru.MAT.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.MAT.Enums.RequestStatus;
import ru.MAT.Enums.RequestType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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

    private Integer telegramMessageId;

    private List<Integer> mediaGroupId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String htmlMainText = "-";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String htmlStatusText = "-";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String box0 = "-";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String box1 = "-";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String box2 = "-";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String box3 = "-";

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RequestMediaDraft> box4 = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "TEXT")
    private String box5 = "-";
}
