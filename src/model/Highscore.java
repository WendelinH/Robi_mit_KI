package model;

import java.util.Date;
/**
 * contains data for a single highscore
 * @author Sven.Schirmer
 *
 */
public class Highscore {
	private String name;
	private Date date;
	private int score;
	
	/**
	 * create a new Highscore object
	 * @param name
	 * @param score
	 */
	public Highscore(String name, int score) {
		this.name = name;
		this.score = score;
		this.date = new Date();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}
	
}
