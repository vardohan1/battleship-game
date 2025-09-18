package com.battleship.game.service;

import com.battleship.game.model.entity.Game;
import com.battleship.game.model.entity.GameStatus;
import com.battleship.game.model.entity.Player;
import com.battleship.game.repository.GameRepository;
import com.battleship.game.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    @Transactional
    public Game createGame(String name) {
        //create new game
        Game game = new Game();
        game.setGameId(UUID.randomUUID().toString());
        game.setStatus(GameStatus.WAITING_FOR_PLAYER);
        game.setCreatedAt(LocalDateTime.now());

        //create first player
        Player player1 = createPlayer(name, game.getGameId());

        game.setPlayer1Id(player1.getId());

        game.setGameCode(generateGameCode());

        return gameRepository.save(game);
    }

    @Transactional
    public Game joinGame(String gameId, String name) {
        Game game = getGame(gameId);

        // Check if game is joinable
        if (game.getStatus() != GameStatus.WAITING_FOR_PLAYER) {
            throw new RuntimeException("Game is not available to join");
        }

        Player player2 = createPlayer(name, gameId);
        game.setPlayer2Id(player2.getId());
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setCurrentTurn(game.getPlayer1Id());



        return gameRepository.save(game);
    }

    public Game getGame(String gameId) {
        return gameRepository.findByGameCode(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
    }

    private Player createPlayer(String name, String gameId) {
        Player player = new Player();
        player.setName(name);
        player.setGameId(gameId);
        player.setReady(true);
        return playerRepository.save(player);
    }

    private String generateGameCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    public List<Game> getAvailableGames() {
        return gameRepository.findAll().stream()
                .filter(game -> game.getStatus() == GameStatus.WAITING_FOR_PLAYER)
                .toList();
    }

    @Transactional
    public Game leaveGame(String gameId, String playerId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        // If game hasn't started yet (waiting for player)
        if (game.getStatus() == GameStatus.WAITING_FOR_PLAYER) {
            gameRepository.delete(game);
            return game; // Return deleted game for response
        }

        // If game is in progress
        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            // Determine winner (the player who didn't leave)
            if (game.getPlayer1Id().equals(playerId)) {
                game.setWinnerId(game.getPlayer2Id());
            } else if (game.getPlayer2Id().equals(playerId)) {
                game.setWinnerId(game.getPlayer1Id());
            }
            game.setStatus(GameStatus.COMPLETED);
        }

        return gameRepository.save(game);
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }
}
