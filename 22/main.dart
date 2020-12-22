import 'dart:collection';
import 'dart:io';

const int PLAYER_A = 0;
const int PLAYER_B = 1;
int GAME_ROUNDS = 1;

class Game {
    int game = 0;
    Queue<int> playerA;
    Queue<int> playerB;

    Game clone({int takeNFromA = -1, int takeNFromB = -1}) {
        var newGame = Game();
        newGame.game = GAME_ROUNDS;
        newGame.playerA = Queue.from(playerA);
        newGame.playerB = Queue.from(playerB);
        if(takeNFromA != -1) {
            newGame.playerA = Queue.from(newGame.playerA.take(takeNFromA));
        }
        if(takeNFromB != -1) {
            newGame.playerB = Queue.from(newGame.playerB.take(takeNFromB));
        }

        GAME_ROUNDS++;
        return newGame;
    }

    int score(Queue<int> deck) {
        return deck.toList().asMap().entries.fold(0, (acc, e) => 
            acc + e.value * (deck.length - e.key)
        );
    }

    int scorePlayerA() {
        return score(playerA);
    }

    int scorePlayerB() {
        return score(playerB);
    }
}
class GameResult {
    int winner;
    int score;
    GameResult({this.winner, this.score = 0});
}

void main() {
    var game = parse("input.txt");

    print("Part 01: ${part01(game.clone())}");
    print("Part 02: ${part02(game.clone(), verbose: false)}");
}

int part01(Game game) {

    var playerA = game.playerA;
    var playerB = game.playerB;

    // Play game
    while(playerA.length > 0 && playerB.length > 0) {

        var playA = playerA.removeFirst();
        var playB = playerB.removeFirst();

        if(playA > playB) {
            playerA.addLast(playA);
            playerA.addLast(playB);
        } else {
            playerB.addLast(playB);
            playerB.addLast(playA);
        }
    }

    if(playerA.length > 0) {
        // Player A wins
        return game.scorePlayerA();

    } else {
        // Player B wins
        return game.scorePlayerB();
    }
}

int part02(Game game, {bool verbose = false}) {

    var gameResult = recursiveCombat(game, verbose: verbose);

    if(verbose) {
        print("Winner: ${gameResult.winner} - Score: ${gameResult.score}");
        print("Number of games played: ${GAME_ROUNDS-1}");
    }

    return gameResult.score;
}

GameResult recursiveCombat(Game game, {bool verbose = false}) {

    var history = Set<String>();


    var playerA = game.playerA;
    var playerB = game.playerB;

    // Play game
    int round = 1;
    while(playerA.length > 0 && playerB.length > 0) {

        // Check if round has repeated
        var historyPrint = playerA.join(",") + "-" + playerB.join(",");
        if(history.contains(historyPrint)) {
            if(verbose) {
                print("Game ${game.game} just finished after $round rounds due to history!");
            }
            return GameResult(winner: PLAYER_A);
        }

        // Add history
        history.add(historyPrint);

        var playA = playerA.removeFirst();
        var playB = playerB.removeFirst();

        var playerAWinsRound = playA > playB;

        if(playA <= playerA.length && playB <= playerB.length) {
            var subGameResult = recursiveCombat(game.clone(takeNFromA: playA, takeNFromB: playB), verbose: verbose);
            playerAWinsRound = subGameResult.winner == PLAYER_A;
        }

        if(playerAWinsRound) {
            playerA.addLast(playA);
            playerA.addLast(playB);
        } else {
            playerB.addLast(playB);
            playerB.addLast(playA);
        }

        round++;
    }

    if(verbose) {
        print("Game ${game.game} just finished after $round rounds!");
    }

    if(playerA.length > 0) {
        // Player A wins
        return GameResult(winner: PLAYER_A, score: game.scorePlayerA());

    } else {
        // Player B wins
        return GameResult(winner: PLAYER_B, score: game.scorePlayerB());
    }
}



Game parse(String filename) {

    var game = Game();

    var playerLines = File(filename)
        .readAsStringSync()
        .split("\n\n");

    var playerA = playerLines[0].split("\n").skip(1).map((e) => int.parse(e));
    var playerB = playerLines[1].split("\n").skip(1).map((e) => int.parse(e));

    game.playerA = Queue.from(playerA);
    game.playerB = Queue.from(playerB);
    return game;
}