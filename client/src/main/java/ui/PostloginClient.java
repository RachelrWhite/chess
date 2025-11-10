package ui;

import Facade.ServerFacade;
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
        if (params.length < 2 || "game".equalsIgnoreCase(params[0])) {
            return "usage: create game <gameName";
        }
        String gameName = String.join(" ", Arrays.copyOfRange(params, 1, params.length));
        // eventually we are going to create server.createGame(session.authToken(), gameName);
        return "Created game: " + gameName + " (stub)";
    }

    private String listGames(String[] params) {
        if (params.length > 0 && !"games".equalsIgnoreCase(params[0])) {
            return "usage: list games";
        }
        //later: server.createGame(session.authToken(), gameName);
        return "No games yet. (stub)";
    }

    private String playGame(String[] params) {
        if (params.length < 2 || !"game".equalsIgnoreCase(params[0])) {
            return "usage: play game <#fromList> [WHITE|BLACK]";
        }
        // later: parse number/color, join, draw board
        return "Playing game (stub)";
    }

    private String observeGame(String[] params) {
        if (params.length < 2 || !"game".equalsIgnoreCase(params[0])) {
            return "usage: observe game <#fromList>";
        }
        // later: draw board from white perspective
        return "Observing game (stub)";
    }

    public String postloginHelp() {
//        if (state == State.SIGNEDOUT) {
//            return """
//                    - signIn <yourname>
//                    - quit
//                    """;
//        }
        return """
                - Postlogin Commands:
                - create game <gameName>
                - list games
                - play game <#fromList> [WHITE|BLACK]
                - observe game <#fromList>
                - logout
                - help
                """;
    }

}
