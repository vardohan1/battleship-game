package com.battleship.game.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "games")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Game {
    @Id
    private String gameId;

    @Column(nullable = false)
    private String player1Id;

    @Column
    private String player2Id;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Column
    private String currentTurn;

    @Column
    private String winnerId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(unique = true)
    private String gameCode;
}
