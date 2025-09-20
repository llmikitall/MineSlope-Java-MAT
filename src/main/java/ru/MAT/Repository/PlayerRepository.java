package ru.MAT.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.MAT.Entities.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByTgId(Long tgId);
}
