package model;

import java.util.Collection;

public record ListGamesRequest(Collection<GameSummary> games) {
}
