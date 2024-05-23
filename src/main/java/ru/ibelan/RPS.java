package ru.ibelan;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RPS {
	ROCK,
	PAPER,
	SCISSORS;

	public static int compare(RPS a, RPS b) {
		if (a == b) {
			return 0;
		} else if (ROCK == a && SCISSORS == b || SCISSORS == a && PAPER == b || PAPER == a && ROCK == b) {
			return 1;
		} else {
			return -1;
		}
	}
}
