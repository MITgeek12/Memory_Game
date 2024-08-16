// JDK
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

// SWT
import org.eclipse.swt.SWT;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.swt.layout.FillLayout;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MemoryGame {
	static Card chosenCard = null;
	static Display display;
	static Canvas canvas;
	static Shell shell;
	int numCards = 12; // 12 cards displayed
	static ArrayList<String> animals = new ArrayList<>(Arrays.asList("beachCorgi", "beachCorgi", "happyCorgi", "happyCorgi", "flowerCorgi", "flowerCorgi", "couchCorgi", "couchCorgi", "sleepyCorgi", "sleepyCorgi", "funnyCorgi", "funnyCorgi"));
	static ArrayList<Point> coordinates = new ArrayList<Point>(12); // arraylist of coordinates for the cards
    static ArrayList<Card> cards = new ArrayList<Card>(12); // arrayList of card objects

    // Game state data structures.
    static int matches = 0;
    static int[] gameState = {0, 0}; // 2 spots for card numbers
    
    // Buttons
    static Button startButton;
    static boolean startButtonPressed = false;
    static Button endButton;
    static Button replayButton;
    
    // Shell size
    static Point canvasSize;
    
	public static void main(String[] args) {
		display = new Display(); // Object that talks to the OS
		shell = new Shell(display, SWT.DIALOG_TRIM); // Main window
		canvas = new Canvas(shell, SWT.NO_REDRAW_RESIZE);
		//canvas.setVisible(true); // used to hide or show windows
		
		shell.setLayout(new FillLayout()); // fill the screen
		
		// The default behavior for a Canvas is that before it paints itself the entire
		// client area is filled with the current background color.
		canvas.setBackground(new Color(219, 201, 230)); // the background color of the canvas	
			
		// Button initialization
		startButton = new Button(canvas, SWT.PUSH);
		endButton = new Button(canvas, SWT.PUSH);
		replayButton = new Button(canvas, SWT.PUSH);
		
		// The application always get a paint event after the underlying OS has drawn
		// the control,
		// so any drawing done to the paint event's GC will be shown on top of the
		// control.

		// When a widget is resized a paint event occurs. This can create screen flicker
		// as repeated repainting of the client area occurs.
		// Flicker is also known as flash, and the style bit SWT.NO_REDRAW_RESIZE can be
		// used to reduce this.
		// When NO_REDRAW_RESIZE is used and the control's size is reduced no paint
		// event is generated.
		// This means there will be no flash as the control is not unnecessarily
		// redrawn, and if the size is increased
		// then the paint event's GC is clipped set to just the area that needs
		// repainting.
		// This is the newly revealed bottom and right edge rectangles in a shape like a
		// backwards L.
		
		// DisposeListener class for shell disposal
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				//System.out.println("Widget disposed");
				for (int i = 0; i < cards.size(); i++) {
					Card card = cards.get(i);
					// disposing of cards
					card.getAnimalImgUp().dispose();
					card.getAnimalImgDown().dispose();
				}
			}
		});
		
		// SelectionListener class for Start Button
		startButton.addSelectionListener(new SelectionListener() {
			// never called
			public void widgetDefaultSelected(SelectionEvent e) {
				//System.out.println("widget default selected");
			}
			
			public void widgetSelected(SelectionEvent e) {
				//System.out.println("widget selected");
				startButtonPressed = true;
				startButton.setVisible(false); // hiding button
				// asking OS for repaint message which then calls repaintWindow(e)
				canvas.redraw();
			}
		});
		
		// SelectionListener class for End Button
		endButton.addSelectionListener(new SelectionListener() {
			// never called
			public void widgetDefaultSelected(SelectionEvent e) {
				//System.out.println("widget default selected");
			}
			
			public void widgetSelected(SelectionEvent e) {
				shell.close(); // close and dispose
			}
		});
		
		// SelectionListener class for Replay Button
		replayButton.addSelectionListener(new SelectionListener() {
			// never called
			public void widgetDefaultSelected(SelectionEvent e) {
				//System.out.println("widget default selected");
			}
			
			public void widgetSelected(SelectionEvent e) {
				//System.out.println("replay selected");
				// shuffling cards
				Collections.shuffle(cards);
				int cardNum = 1;
				for (int i = 0; i < cards.size(); i++) {
					Card card = cards.get(i);
					card.setCardNum(cardNum);
					card.setCoordinates(coordinates.get(i));
					cardNum = cardNum + 1;
				}
				
				// resetting game
				matches = 0;
				endButton.setVisible(false);
				replayButton.setVisible(false);
				
				// making cards visible again
				for (int i = 0; i < cards.size(); i++) {
					Card card = cards.get(i);
					card.setVisible(true);
					card.flipCard();
				}
				
				// canvas redraw
				asyncUpdateDisplay(display, canvas);
				//System.out.println(cards);
			}
		});
		
		// PaintListener class
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				//System.out.println("repaint......");
				// Place your redraw method here...
				repaintWindow(e);
			}
		});
		
		// MouseListener class
		canvas.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
				//System.out.println("MouseDown");
				for (int i = 0; i < cards.size(); i++) {
					Card chosen = cards.get(i);
					if (!chosen.isFaceUp() && chosen.isInside(e.x, e.y)) { // checking which card was clicked
						chosenCard = chosen;
						chosen.flipCard(); // flip card
						canvas.redraw();
						break;
					}
				}
			}

			public void mouseUp(MouseEvent e) {
				//System.out.println("Mouse Up");
				if (chosenCard != null) {
					updateGameState(chosenCard.getCardNum());
					chosenCard = null;
				}
			}

			public void mouseDoubleClick(MouseEvent e) {
				//System.out.println("Double Click");
			}
		});

		// this shows the window
		shell.open();
		
		// Canvas Size
		canvasSize = canvas.getSize();
		//System.out.println("Size of canvas = " + canvasSize);
				
		// x and y values of buttons
		int xStartButton = canvasSize.x / 2 - 150 / 2;
		int yStartButton = canvasSize.y - 70 - 30;
		int xEndButton = canvasSize.x / 2 - 150 - 20;
		int yEndButton = canvasSize.y - 70 - 30;
		int xReplayButton = xEndButton + 150 + 40;
		int yReplayButton = yEndButton;
				
		// start button
		startButton.setBackground(new Color(221, 241, 241));
		startButton.setBounds(xStartButton, yStartButton, 150, 70);
		startButton.setFont(new Font(display, "Arial", 25, SWT.BOLD));
		startButton.setText("Start Game");
				
		// end button
		endButton.setBackground(new Color(221, 241, 241));
		endButton.setBounds(xEndButton, yEndButton, 150, 70);
		endButton.setFont(new Font(display, "Arial", 25, SWT.BOLD));
		endButton.setText("End Game");
		endButton.setVisible(false);
				
		// replay button
		replayButton.setBackground(new Color(221, 241, 241));
		replayButton.setBounds(xReplayButton, yReplayButton, 150, 70);
		replayButton.setFont(new Font(display, "Arial", 25, SWT.BOLD));
		replayButton.setText("Replay");
		replayButton.setVisible(false);

		// figuring out the starting x coordinate
		int width = canvasSize.x;
		int height = canvasSize.y;
		int cardsTotalWidth = 150 * 4 + 80 * 3;
		int cardsTotalHeight = 150 * 3 + 40 * 2;
		int startingX = (width - cardsTotalWidth) / 2;
		int startingY = (height - cardsTotalHeight) / 2;
		
		// initializing coordinates ArrayList
		int y = startingY;
		for (int i = 0; i < 3; i++) {
			int x = startingX;
			for (int j = 0; j < 4; j++) {
				coordinates.add(new Point(x, y));
				x = x + 150 + 80; // 150 is the width of 1 card and 80 is the amount of space I want to have between the cards within one row
			}
			y = y + 150 + 40; // 150 is the height of 1 card and 40 is the amount of space I want to have between rows of cards
		}
		//System.out.println(coordinates);

		// initializing cards ArrayList
		int cardNum = 1; // card number
		for (int i = 0; i < 12; i++) {
			Image imgDown = new Image(display, MemoryGame.class.getResourceAsStream("Card_FaceDown.png"));
			//System.out.println("face down image retrieved");
			Image scaledImgDown = new Image(display, imgDown.getImageData().scaledTo(150, 150));
			int animalIndex = (int) (Math.random() * animals.size()); // get random animal
			String animal = animals.remove(animalIndex);
			cards.add(new Card(coordinates.get(i).x, coordinates.get(i).y, animal, cardNum, scaledImgDown, display));
			cardNum = cardNum + 1; // increment card number
		}		

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		// disposes all associated windows and their components
		display.dispose();
	}
	
	/*
	 * My methods
	 */
	
	// Redraw the screen
	static void repaintWindow(PaintEvent e) {
		if (!startButtonPressed) {
			//System.out.println("drawing welcome");
			drawWelcome(e);
		} else {
			//System.out.println("drawing cards");
			drawCards(e);
		}
		if (matches == 6) {
			drawEndScreen(e);
		}
	}
	
	// Welcome Screen
	static void drawWelcome(PaintEvent e) {
		// Gradient rectangle
		e.gc.setForeground(new Color(247, 241, 255)); // lilac color
		e.gc.fillGradientRectangle(0, 0, canvasSize.x, canvasSize.y, true);
				
		e.gc.setForeground(new Color(11, 138, 138)); // dark teal color
		e.gc.setLineWidth(8);
		e.gc.setFont(new Font(e.display, "Arial", 50, SWT.BOLD)); // setting font
		
		// welcome text
		Point pt = textCenterXY(e.gc, "Welcome to my Memory Game!");
		e.gc.drawText("Welcome to my Memory Game!", pt.x, 50, true); // true for transparency
		
		// instructions background shapes
		Point ptRect = shapeCenterXY(400, 300);
		Point ptOval = shapeCenterXY(340, 300);
		e.gc.setBackground(new Color(221, 241, 241));
		e.gc.drawOval(ptOval.x, 150, 340, 300);
		e.gc.drawRectangle(ptRect.x, 250, 400, 300);
		e.gc.fillRectangle(ptRect.x, 250, 400, 300);
		e.gc.fillOval(ptOval.x, 150, 340, 300);
		
		// instructions title
		e.gc.setFont(new Font(e.display, "Arial", 40, SWT.ITALIC));
		Point ptInstruct = textCenterXY(e.gc, "Instructions:");
		e.gc.drawText("Instructions:", ptInstruct.x, 190, true); 
		
		// instruction details
		e.gc.setFont(new Font(e.display, "Arial", 30, SWT.NORMAL));
		
		Point pt1 = textCenterXY(e.gc, "1. Flip 2 cards");
		e.gc.drawText("1. Flip 2 cards", pt1.x, 300, true);
		Point pt2 = textCenterXY(e.gc, "2. Try to get all the matches");
		e.gc.drawText("2. Try to get all the matches", pt2.x, 420, true);
	}
	
	// Update game state
	static void updateGameState(int cardNum) {
		if (gameState[0] == 0) { // no cards in gameState
			gameState[0] = cardNum;
		} else if (gameState[1] == 0) { // 1 card in gameState
			gameState[1] = cardNum;
		}
		
		// updating matches if there are 2 cards in gameState
		if (gameState[0] != 0 && gameState[1] != 0) {
			updateMatches();
		}
	}
	
	// Updating matches
	static void updateMatches() {
		int index1 = findCardIndex(gameState[0]);
		int index2 = findCardIndex(gameState[1]);
		Card card1 = cards.get(index1);
		Card card2 = cards.get(index2);
		if (card1.getAnimal().equals(card2.getAnimal())) { // if cards are the same
			matches = matches + 1; // update matches
			// hiding cards
			card1.setVisible(false);
			card2.setVisible(false);
			//System.out.println(card1.isVisible() + " " + card2.isVisible());
		} else {
			card1.flipCard();
			card2.flipCard();
		}
		resetState();
		//System.out.println("...updating matches..");
		timerUpdateDisplay(700, display, canvas);
	}
	
	// Find the index of a card in 'cards' arraylist
	static int findCardIndex(int cardNum) {
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).getCardNum() == cardNum) {
				return i;
			}
		}
		return -1;
	}
	
	// Reset gameState
	static void resetState() {
		gameState[0] = 0;
		gameState[1] = 0;
	}
	
	// Display Cards
	static void drawCards(PaintEvent e) {
		// teal background color
		canvas.setBackground(new Color(221, 240, 238));
		
		// painting cards
		for (int i = 0; i < cards.size(); i++) {
			Card card = cards.get(i);
			if (card.isVisible()) {
				cards.get(i).drawMe(e);
			}
		}
	}
	
	// End screen
	static void drawEndScreen(PaintEvent e) {
		// Background color
		canvas.setBackground(new Color(219, 201, 230)); // purple lilac color
		
		// Font
		e.gc.setFont(new Font(display, "Arial", 80, SWT.BOLD));
		
		// Gradient rectangle
		e.gc.setForeground(new Color(247, 241, 255));
		e.gc.fillGradientRectangle(0, 0, canvasSize.x, canvasSize.y, true);
		
		// Center Rectangle
		e.gc.setForeground(new Color(11, 138, 138));
		e.gc.setLineWidth(6);
		e.gc.setBackground(new Color(221, 240, 238));
		e.gc.fillRectangle(canvasSize.x / 2 - 500 / 2, canvasSize.y / 2 - 200 / 2, 500, 200);
		e.gc.drawRectangle(canvasSize.x / 2 - 500 / 2, canvasSize.y / 2 - 200 / 2, 500, 200);
		
		// Game Over text
		Point point = textCenterXY(e.gc, "Game Over!");
		e.gc.drawText("Game Over!", point.x, point.y, true);
		
		// End Button
		endButton.setVisible(true);
		
		// Replay Button
		replayButton.setVisible(true);
	}
	
	// used to draw text exactly in the middle of the screen
	static Point textCenterXY(GC gc, String text) {
    	Point extent = gc.stringExtent(text);
    	int textWidth = extent.x;
    	int textHeight = extent.y;
    	int x = (canvasSize.x - textWidth) / 2;
    	int y = (canvasSize.y - textHeight) / 2;
    	return new Point(x, y);
    }
	
	// used to draw shapes exactly in the middle of the screen
	static Point shapeCenterXY(int width, int height) {
    	int x = (canvasSize.x - width) / 2;
    	int y = (canvasSize.y - height) / 2 - height / 2;
    	return new Point(x, y);
    }
	
	// Force a redraw. Do this when the state changes and the display needs update.
	static void asyncUpdateDisplay(Display display, Canvas canvas) {
		// This forces a the canvas to repaint.
		display.asyncExec(new Runnable() {
			public void run() {
				canvas.redraw(); // ask OS to send us a fresh paint!
			}
		});
	}
	
	// Force a redraw. Do this when the state changes and the display needs update.
	static void timerUpdateDisplay(int delay, Display display, Canvas canvas) {
		// This forces a the canvas to repaint with a delay.
		display.timerExec(delay, new Runnable() {
			public void run() {
				canvas.redraw(); // ask OS to send us a fresh paint!
			}
		});
	}
}