package com.battleship.game.model.DTO;

import com.battleship.game.model.entity.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameResponse {
    String gameId;
    String gameCode;
    String status;
    String player1Id;
    String player2Id;
    String error;

    public static GameResponse from(Game game) {
        return new GameResponse(
                game.getGameId(),
                game.getGameCode(),
                game.getStatus().toString(),
                game.getPlayer1Id(),
                game.getPlayer2Id(),
                null
        );
    }
}
