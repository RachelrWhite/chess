package model;
import chess.*;

import java.util.Collection;

public record ListGamesResult(Collection<ChessGame> games) {
}
