package ui;

import facade.ServerFacade;
import model.GameData;

import java.util.*;

public class PostloginClient {
    private final ServerFacade server;
    private final Session session;
    private List<GameData> lastListed = List.of();

    public PostloginClient(ServerFacade server, Session session) {
        this.server = server;
        this.session = session;
    }

    public void run() {
        System.out.println(postloginHelp());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout")) {
            System.out.print("[postlogin] > ");
            String line = scanner.nextLine();

            try {
                result = eval(line);
                if (!result.isBlank() && !result.equals("logout")) {
                    System.out.println(result);
                }
            } catch (Throwable e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


    public String eval(String input) {
        // Preserve param case; only lowercase the command
        String[] raw = input.trim().split("\\s+");
        String cmd = (raw.length > 0 && !raw[0].isBlank()) ? raw[0].toLowerCase() : "help";
        String[] params = java.util.Arrays.copyOfRange(raw, 1, raw.length);

        return switch (cmd) {
            case "h", "help" -> postloginHelp();

            case "logout" -> {
                try {
                    server.logout(session.authToken());
                } catch (Exception e) {
                    // Return a user-friendly error so it prints in the REPL
                    yield "Logout error: " + e.getMessage();
                }
                yield "logout"; // signal the loop to exit postlogin
            }

            // "create game <name>"
            case "c", "create" -> createGame(params);

            // "list games"
            case "lg", "list" -> listGames(params);

            // "play game <#> [WHITE|BLACK]"
            case "pg", "play" -> playGame(params);

            // "observe game <#>"
            case "og", "observe" -> observeGame(params);

            case "quit", "exit" -> "logout"; // optional alias

            default -> "Unknown command. Type 'help'.";
        };
    }

    private String createGame(String[] params) {
        if (params.length < 2 || !"game".equalsIgnoreCase(params[0])) {
            return "usage: create game <gameName>";
        }
        String gameName = String.join(" ", java.util.Arrays.copyOfRange(params, 1, params.length));
        int id = server.createGame(session.authToken(), gameName);
        if (id <= 0) {
            return "Could not create game. Try again.";
        }
        return "Created game: " + gameName;
    }

    private String listGames(String[] params) {
        if (params.length > 0 && !"games".equalsIgnoreCase(params[0])) {
            return "usage: list games";
        }
        var games = server.listGames(session.authToken());
        if (games == null) {
            lastListed = java.util.List.of();
            return "No games created yet. Create one with: create game <name>";
        }
        lastListed = games;
        var sb = new StringBuilder("Games:\n");

        for (int i = 0; i < lastListed.size(); i++) {
            var g = lastListed.get(i);
            String white = Objects.toString(g.whiteUsername(), "-");
            String black = Objects.toString(g.blackUsername(), "-");
            sb.append(String.format("  %d) %s  [white=%s | black=%s]%n",
                    i + 1, g.gameName(), white, black));
        }

        return sb.toString();
        //later: server.createGame(session.authToken(), gameName);
    }

    private String playGame(String[] params) {
        if (params.length < 2 || !"game".equalsIgnoreCase(params[0])) {
            return "usage: play game <#fromList> [WHITE|BLACK]";
        }

        if (lastListed == null || lastListed.isEmpty()) {
            return "No games selected. Run 'list games' first.";
        }

        // index into lastListed
        int index;
        try {
            index = Integer.parseInt(params[1]) - 1; // 1-based -> 0-based
        } catch (NumberFormatException e) {
            return "Choose a number from the last 'list games' output.";
        }

        if (index < 0 || index >= lastListed.size()) {
            return "Choose a number between 1 and " + lastListed.size() + " from 'list games'.";
        }

        // Color (default WHITE)
        String color = (params.length >= 3) ? params[2].toUpperCase() : "Color must be WHITE or Black";
        if (!"WHITE".equals(color) && !"BLACK".equals(color)) {
            return "Color must be WHITE or BLACK.";
        }

        var game = lastListed.get(index);
        String me = session.username(); // or however you get the logged-in username

        // Who (if anyone) already has this color?
        String takenBy = switch (color) {
            case "WHITE" -> game.whiteUsername();
            case "BLACK" -> game.blackUsername();
            default -> null; // should never happen
        };

        // Block if someone else already has that seat
        if (takenBy != null && !takenBy.equals(me)) {
            return "That game's " + color + " seat is already taken by " + takenBy + ".";
        }

        server.joinGame(session.authToken(), color, game.gameID());

        boolean whitePerspective = !"BLACK".equals(color);
        System.out.println(BoardDrawer.drawInitial(whitePerspective));
        return "Joined '" + game.gameName() + "' as " + color + ".";
    }

    private String observeGame(String[] params) {
        if (params.length < 2 || !"game".equalsIgnoreCase(params[0])) {
            return "usage: observe game <#fromList>";
        }
        int numChosen;
        try {
            numChosen = Integer.parseInt(params[1]);
        } catch (Exception e) {
            return "Choose game that is actually listed in: 'list;.";
        }
        var chosen = lastListed.get(numChosen - 1);
        System.out.println(BoardDrawer.drawInitial(true)); // observer is from the white perspective
        return "Observing '" + chosen.gameName() + "'.";
    }

    public String postloginHelp() {

        return """
                - Postlogin Commands:
                - create game <gameName>
                - list games
                - play game <#fromList> [WHITE|BLACK]
                - observe game<#fromList>
                - logout
                - help
                """;
    }

}
