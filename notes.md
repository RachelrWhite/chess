## THESE ARE THE **NOTES!!**


### How to add (a new file at least) to github: 
* make your changes or create a file 
``` cpp 
echo "# My notes" > notes.md
git add notes.md
git commit -am "initial(notes) creation"
git push
```
## What do the 3 differetn moduels do? 
## Client: 
* going to interface fro the user 
* talks with teh server moduel 
## Server 
* this is what does all then work adn returns info to the users 
## Shared: 
* just code tha tis used as a library to represent the logic of the game 

## Phase 0: 
this is in teh shared code  - how to set up the boar dand what pieces are avaliable 
* test folder has all the tests i need to pass to make sure I got phase 0 working 




ok so in the chessboard file i might need to add some helpers like: 
```java 
placeBackRank(1, chessGame.TeamColor.WHITE);
placePawns(2, chessGame.TeamColor.WHITE);
placeBackRank(8, chessGame.TeamColor.BLACK);
placePawns(7, chessGame.TeamColor.BLACK); 

private void placePawns(int row, ChessGame.TeamColor color) {
for (int col=1; col<=8; col++) {
addPiece(new ChessPosition(row, col), new ChessPiece(color, ChessPiece.PieceType.PAWN));
}
}
private void placeBackRank(int row, ChessGame.TeamColor color) {
ChessPiece.PieceType[] order = {
ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP,
ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING,
ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK
};
for (int col=1; col<=8; col++) {
addPiece(new ChessPosition(row, col), new ChessPiece(color, order[col-1]));
}
```


you can do the knight and the king the same way - they can only move one step at a time 

and tehn there are three that can move until theyh hit something 


**maybe make an abstract class that like has an arrow that points to the inerface - this is where i can put all the methods 
