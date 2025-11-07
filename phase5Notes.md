# Phase 5 Chess Client notes:
1) Create the Chess Client (Console App)
* Register
* Login
* Logout
* List games
* Create games
* Join games (play)
* Observe games
* Display help
* Exit
2) Implement the ServerFacade class
   This class wraps all your HTTP calls to the server.

* Each method should send and receive JSON via HTTP, using the same URLs, methods, and body formats as your server API (from Phase 3).
It lives in:
📁 client/src/main/java/client/ServerFacade.java

Methods to include:

Method	Purpose
register(username, password, email)	POST /user
login(username, password)	POST /session
logout(authToken)	DELETE /session
listGames(authToken)	GET /game
createGame(authToken, gameName)	POST /game
joinGame(authToken, gameID, color)	PUT /game
clear() (optional, but useful for tests)	DELETE /db

Your client should call these methods instead of directly making HTTP requests.
3) Build the REPL User Interface

A Read-Eval-Print Loop (REPL) is the interactive loop where the user types commands.

Split your UI into two “modes”:

🟢 Prelogin UI

Commands available before logging in:

help

quit

login

register

🔵 Postlogin UI

Commands available after logging in:

help

logout

create game

list games

play game

observe game

Each should call the appropriate method in your ServerFacade.

4. Draw the Chessboard

When a user chooses Play Game or Observe Game, draw the starting chessboard in the terminal:

Use ASCII or Unicode chess symbols.

Alternate light/dark squares.

Include ranks (1–8) and files (a–h) around the board.

If playing as white or observer → show from white’s perspective (a1 bottom-left).

If playing as black → show from black’s perspective (a1 top-right).

(You don’t need to handle moves yet — just display the initial board.)

5. Handle Errors and Bad Input

Your client should never crash or freeze.
Handle all of these gracefully:

Invalid commands

Too many/few arguments

Bad user input types

Server errors (bad request, unauthorized, already taken, etc.)

Never show JSON, HTTP codes, or stack traces to the user — just simple messages.

6. Write Unit Tests for ServerFacade

File:
📁 client/src/test/java/client/ServerFacadeTests.java

Each ServerFacade method needs:

✅ One positive test (works as expected)

❌ One negative test (fails correctly)

Example:

@Test
void registerPositive() throws Exception {
var authData = facade.register("player1", "password", "p1@email.com");
assertTrue(authData.authToken().length() > 10);
}

@Test
void registerNegative() throws Exception {
assertThrows(Exception.class, () -> {
facade.register("player1", null, "p1@email.com");
});
}


Be sure to:

Clear your database (/db) before each test (@BeforeEach).

Use the sample ServerFacadeTests structure provided in the starter.

7. Code Quality + Commits

You’ll be graded on:

Functionality

Code quality

Unit test coverage

Commit history (12+ meaningful commits spread out)

8. No New Pass Off Tests

There are no new autograder tests for Phase 5 — but your client and facade tests must pass locally before you demo to a TA.




//take all the input and sent it to the prelogin or postlogin

    //make sure the post can access the authtoken - can store in reple
    //last tips:
    //how do i plan on sending the https requests:
        //3 different mods client, shared, server
        //my server can access the server code and shared
        //client can access the shared and client code
        //client goes through the cloud to access server code


    //add server facade in the java folder probably in its own folder



}