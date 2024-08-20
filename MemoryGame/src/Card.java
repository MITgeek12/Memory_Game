import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Display;

public class Card {
    private int x, y, width, height;
    private boolean faceUp; // false with filled box, true with unfilled box including animal
    private boolean visible = true;
    private int cardNum;
    private String animal; // used to identify cards so I could check if 2 cards chosen by the user are the same
    private Image animalImgUp;
    private Image animalImgDown;

    public Card(int x, int y, String animal, int cardNum, Image animalImgDown, Display display) {
        // initialize instance variables
        width = 150;
        height = 150;
        this.x = x;
        this.y = y;
        faceUp = false; // face down
        this.animal = animal;
        this.cardNum = cardNum;
        this.animalImgDown = animalImgDown;
        
        // image face up
        String filename = animal + ".png";
        /* 
        Finds the resource with the given name. 
        A resource is some data (images, audio, text, etc) that can be accessed by class code in a way that is independent of the location of the code.
        The name of a resource is a '/'-separated path name that identifies the resource.
		This method will first search the parent class loader for the resource. 
		If the parent is null the path of the class loader built-in to the virtual machine is searched. 
        That failing, this method will invoke findResource(String) to find the resource.
        */
        animalImgUp = new Image(display, Card.class.getResourceAsStream(filename));
    }
    
    /*
       if the card is face up, draw that side
       if the card is face down, draw a rectangle with red, green, blue values 
     */
    public void drawMe(PaintEvent e) {
        if (faceUp) {
            //System.out.println("...drawing face up " + cardNum);
            e.gc.drawImage(animalImgUp, x, y);
        } else { 
            //System.out.println("...drawing face down " + cardNum);
        	e.gc.drawImage(animalImgDown, x, y);
        }
    }

    // find out if mouse is inside card
    public boolean isInside(int mouseX, int mouseY) {
        double yMax = y + height;
        double xMax = x + width;
        return mouseX >= x && mouseX <= xMax && mouseY >= y && mouseY <= yMax;
    }

    /*
       cover up the card to make it look face down
    */
    public void eraseMe(PaintEvent e) {
    	e.gc.setForeground(new Color(255, 255, 255)); // white color
    	e.gc.fillRectangle(x, y, width/2, height/2);
    }
    
    // toString
    public String toString() {
    	return "CardNum: " + cardNum + ", Coordinates: (" + x + ", " + y + "), Animal: " + animal;
    }

    // mutators
    public void flipCard() {
        faceUp = !faceUp;
    }
    
    public void setCoordinates(Point pt) {
    	this.x = pt.x;
    	this.y = pt.y;
    }
    
    public void setCardNum(int cardNum) {
    	this.cardNum = cardNum;
    }
    
    // used to hide or show cards
    public void setVisible(boolean visible) {
    	this.visible = visible;
    }

    // accessors
    public String getAnimal() { return animal; }
    
    public Image getAnimalImgUp() { return animalImgUp; }
    
    public Image getAnimalImgDown() { return animalImgDown; }

    public int getCardNum() { return cardNum; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public boolean isFaceUp() { return faceUp; }
    
    public boolean isVisible() { return visible; }
}