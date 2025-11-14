package ui;


import java.util.Arrays;
import java.util.Scanner;

//import Exception.ResponseException;
import facade.ServerFacade;


public class PreloginClient {
    private String userName = null;
    private final ServerFacade server;
    //private final WebSocketFacade ws;
    //private State state = State.SIGNEDOUT;

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
        String[] tokens = input.trim().toLowerCase().split("\\s+");
        String cmd = (tokens.length > 0 && !tokens[0].isBlank()) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

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
        // call server.register later; for now just stub a message:
        System.out.println("Registered & logged in as " + params[0]);
        //if successful register {
            var auth = server.register(params[0], params[1], params[2]);
            //var session = new Session(params[0], /*authtoken from facade later*/ "token");
            new PostloginClient(server, new Session(auth.username(), auth.authToken())).run();
        return "";
    }

    public String login(String... params) {
        if (params.length < 2) {
            return "usage: login <USERNAME> <PASSWORD>";
        }
        System.out.println("Logged in as " + params[0]);
        // if successful login {
            var auth = server.login(params[0], params[1]);
            var session = new Session(params[0], /*authtoken from facade later*/ "token");
            new PostloginClient(server, new Session(auth.username(), auth.authToken())).run();
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

