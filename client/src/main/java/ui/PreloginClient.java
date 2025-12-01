package ui;


import java.util.Arrays;
import java.util.Scanner;

import facade.ServerFacade;


public class PreloginClient {
    private String userName = null;
    private final ServerFacade server;

    public PreloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        //ws = new WebSocketFacade(serverUrl, this);
    }

    public void run() {
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print("> ");
            String line = scanner.nextLine();

            try {
                result = eval(line);
                if (!"quit".equals(result)) {
                    System.out.println(result);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }


    public String eval(String input) {
        String[] rawString = input.trim().split("\\s+");
        String cmd = (rawString.length > 0 && !rawString[0].isBlank()) ? rawString[0].toLowerCase() : "help";
        String[] params = Arrays.copyOfRange(rawString, 1, rawString.length);

        switch (cmd) {
            case "q", "quit" -> { return "quit"; }
            case "h", "help" -> { return help(); }
            case "l", "login" -> { return login(params); }
            case "r", "register" -> { return register(params); }
            default -> { return "Unknown command. Type 'help'."; }
        }
    }


    public String register(String... params) {
        if (params.length < 3) {
            return "usage: register <USERNAME> <PASSWORD> <EMAIL>";
        }
        var auth = server.register(params[0], params[1], params[2]);
        if (auth == null) {
            return "Error: could not register (username may already be taken).";
        }

        System.out.println("Registered & logged in as " + auth.username());
        var session = new Session(auth.username(), auth.authToken());
        new PostloginClient(server, session).run();
        return "";
    }

    public String login(String... params) {
        if (params.length < 2) {
            return "usage: login <USERNAME> <PASSWORD>";
        }
        // Asks the server to log in
        var auth = server.login(params[0], params[1]);

        if (auth == null) {
            return "Error: invalid username or password.";
        }

        System.out.println("Logged in as " + auth.username());

        // Enter postlogin with the token!!!!!
        var session = new Session(auth.username(), auth.authToken());
        new PostloginClient(server, session).run();

        // After postlogin returns, just drop back to prelogin loop
        //if you are feeling crazy you could reprint help here but i think it's fine
        return "";
    }

    public String help() {
        return """
                - Options:
                - Login as an existing user "l", "login" <USERNAME> <PASSWORD>
                - Register a new user: "r", "register" <USERNAME> <PASSWORD> <EMAIL>
                - Exit the program: "q", "quit"
                - Print this message: "h", "help"
                """;
    }
}

