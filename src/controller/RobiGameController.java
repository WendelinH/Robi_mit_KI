package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import model.Highscore;
import model.Robi;
import model.Stern;
import processing.core.PApplet;
/**
 * Start und Controller unseres Processing-Games,
 * "extends PApplet" erm√∂glicht Zugriff auf Processing-Funktionalit√§t
 * @author sven
 *
 */
public class RobiGameController extends PApplet{

	Robi r1;
	ArrayList<Robi> enemies;
	float speed = 3;
	ArrayList<Stern> sterne;
	ArrayList<Highscore> highscores;
	boolean highscoreAdded = false;
	enum SpielZustand{
        Start, Spielen, SpielEnde;

    }

    SpielZustand state = SpielZustand.Start;
	
	public static void main(String[] args) {
		//startet das Fenster
		PApplet.main("controller.RobiGameController");
	}
	
	public void settings() {
		size(800,600);
	}
	
	@Override
	public void setup() {
		r1 = new Robi(100,100, 0xFF0000FF);
		sterne = new ArrayList<Stern>();
		for (int i = 0; i < 40; i++){
		    Stern s1 = new Stern(random(width), random(height));
		    sterne.add(s1);
		}
		enemies = new ArrayList<Robi>();
		for (int i = 0;i < 2;i++) {
			Robi r2 = new Robi(random(width), random(height), 0xFFFF0000);
			r2.setSpeed(speed);
			enemies.add(r2);
		}
		readHighscore();
		
	}
	
	/**
	 * funktion, um alle Highscires einzulesen.
	 */
	private void readHighscore() {
		highscores = new ArrayList<Highscore>();
		File hsFile = new File("highscores");
		try {
			FileReader fReader = new FileReader(hsFile);
			BufferedReader bfr = new BufferedReader(fReader);
			
			try {
				while(bfr.ready() == true) {
					String line = bfr.readLine();
					System.out.println(line);
					String[] bestandteiele = line.split(";");
					Highscore hs = new Highscore(bestandteiele[0], Integer.parseInt(bestandteiele[1]));
					highscores.add(hs);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("File cannot be read :P");
;			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Highscore-File does not exists");
		}
	}
	
	/**
	 * schreibe Highscore liste
	 */
	private void writeHighscore() {
		PrintWriter prnt = null;
		File hsFile = new File("highscores");
		
		highscores.sort((a, b) -> b.getScore() - a.getScore());
		
		try {
			prnt = new PrintWriter(hsFile);
			
			for(Highscore hs : highscores) {
				prnt.write(hs.getName() + ";" + hs.getScore() + System.lineSeparator());
			}
			
			prnt.flush();
			prnt.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * bestimt n‰chstgelegenen Stern
	 * @param r
	 * @return
	 */
	private Stern getClosestStern(Robi r) {
		Stern result = null;
		float minDistance = Float.MAX_VALUE;
		for (Stern s : sterne) {
			float dist = dist(r.getX(), r.getY(), s.x, s.y);
			if (dist < minDistance) {//wenn aktueller Abstand kleiner ist als bisheriger kleinster Abstand
				result = s;//aktuellen Stern "merken"
				minDistance = dist;//Aktuellen Abstand "merken"
				
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param r
	 * @param s
	 */
	private void moveRobi2ClosestStar(Robi r, Stern s) {
		if (abs(r.getX() - s.x) > abs(r.getY() - s.y)) { // abs() giebt die zahl ohne vorzeichen zur¸ck 
			//move x-direction
			if (r.getX() > s.x) {
				r.setDirection(Robi.Direction.W);
			} else {
				r.setDirection(Robi.Direction.E);
			}
		} else {
			//move y-direction
			if (r.getY() > s.y) {
				r.setDirection(Robi.Direction.N);
			} else {
				r.setDirection(Robi.Direction.S);
			}
		}
		r.move(this);
	}
	
	@Override
	public void draw() {
		switch (state) {
		case Start: drawStartScreen(); break;
		case Spielen: drawSpiel(); break;
		case SpielEnde: drawEndScreen(); break;
		
		}
	}
	
	public void drawStartScreen() {
		background(0);
		fill(255);
		textSize(45);
		text("Start, press SPACE", 50, 50);
		int i = 0;
		for(Highscore hs : highscores) {
			text(hs.getName() + ": " + hs.getScore(), 50, 100 + i * 40);
			i++;
		}
		
	}
	
	public void drawEndScreen() {
		background(0);
		fill(255);
		textSize(60);
		text("Game Over, press SPACE", 50, 100);
		textSize(30);
		text("Dein Punktestand = "+r1.getScore(), 200,250);
		
		if(!highscoreAdded) {
			String name = JOptionPane.showInputDialog(null, "Bitte geben Sie Ihren Namen ein");
			if(name.length() > 0) {//Damit der name nicht lehr sein kan.
				Highscore neuerHighscore = new Highscore(name, r1.getScore());
				highscores.add(neuerHighscore);
				writeHighscore();
			}
			highscoreAdded = true;
		}
		int j = 0;
		for(Highscore hs : highscores) {
			text(hs.getName() + ": " + hs.getScore(), 200, 290 + j * 40);
			j++;
		}
		
	}
	
	public void drawSpiel() {
		background(0);
		r1.drawRobi(this);
		for (Stern s : sterne) {
			if (s.x >= 0) {
				s.draw(this);
			}
		}
		
		for (Robi r : enemies) {
			//Robi-KI
			Stern s = getClosestStern(r);
			if (s != null) {
				moveRobi2ClosestStar(r, s);
				checkCollisions(r);
			}
			//Robi zeichnen
			r.drawRobi(this);
		}
		
		fill(255);
		textSize(30);
		text("Punkte: "+r1.getScore(), 20,30);
	}
	
	@Override
	public void keyPressed() {
		switch (state) {
		case Start: keyPressedStartScreen(); break;
		case Spielen: keyPressedInGame(); break;
		case SpielEnde: keyPressedEndScreen(); break;
		
		}
	}
	
	
	public void keyPressedStartScreen() {
		if (keyCode == 32) {// KeyCode 32 ist die Lertaste
			state = SpielZustand.Spielen;
		}
	}
	
	public void keyPressedEndScreen() {
		if (keyCode == 32) {
			state = SpielZustand.Start;
		}
	}
	
	
	public void keyPressedInGame() {
		switch (key) {
			case 'w': r1.setDirection(Robi.Direction.N); break; 
			case 's': r1.setDirection(Robi.Direction.S); break; 
			case 'a': r1.setDirection(Robi.Direction.W); break; 
			case 'd': r1.setDirection(Robi.Direction.E); break; 			
		}
		r1.move(this);
		
		checkCollisions(r1);
		
		System.out.println("Anzahl Sterne: "+sterne.size());
	}
	
	private void checkCollisions(Robi r) {
		for (Stern s : sterne) {
			if (checkCollision(r, s)) {
				r.addScore(1);;
				//stern "verschwinden" lassen
				s.x = -100; s.y = -100;
			}
		}
		int i = 0;
		while(i < sterne.size()) {
			if (sterne.get(i).x < 0) {
				sterne.remove(i);
			} else {
				i++;
			}
		}
		
		if (sterne.size() == 0) {
			state = SpielZustand.SpielEnde;
		}
		
	}

	/**
	 * pr√ºft, on Robi r gerade auf Stern s trifft
	 * @param r
	 * @param s
	 * @return true wenn r auf s
	 */
	boolean checkCollision(Robi r, Stern s) {
		float a = dist(r.getX(), r.getY(), s.x, s.y);
		return a < 32;
	}
}
