package ui;

public class BoardDrawer {
    private BoardDrawer() {}

    public static String drawInitial(boolean whitePerspective) {
        char[][] board = new char[8][8];
        // Puts the pieces down with simple letters
        String back = "rnbqkbnr";
        for (int c=0;c<8;c++){ board[0][c]=back.charAt(c); board[1][c]='p'; board[6][c]='P'; board[7][c]=Character.toUpperCase(back.charAt(c)); }
        StringBuilder sb = new StringBuilder();
        String files = "  a b c d e f g h";
        if (whitePerspective) {
            for (int r=7;r>=0;r--){
                sb.append(r+1).append(' ');
                for (int c=0;c<8;c++) sb.append(symbol(board[r][c])).append(' ');
                sb.append(r+1).append('\n');
            }
            sb.append(files);
        } else {
            for (int r=0;r<8;r++){
                sb.append(r+1).append(' ');
                for (int c=7;c>=0;c--) sb.append(symbol(board[r][c])).append(' ');
                sb.append(r+1).append('\n');
            }
            sb.append("  h g f e d c b a");
        }
        return sb.toString();
    }
    private static char symbol(char p){ return (p==0)?'.':p; }
}

