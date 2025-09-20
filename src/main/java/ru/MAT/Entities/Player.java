package ru.MAT.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.MAT.Enums.RequestType;
import ru.MAT.Enums.UserStatus;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private long tgId;

    @Column(unique = true)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private UserStatus status = UserStatus.MAIN_MENU;

    @Enumerated(EnumType.ORDINAL)
    private RequestType requestType;

    private int requestId;

    @Column(nullable = false)
    private boolean messageMainMenu = true;

}
