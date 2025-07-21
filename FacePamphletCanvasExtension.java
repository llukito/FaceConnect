/*
 * File: FacePamphletCanvasExtension.java
 * -----------------------------
 * This class represents the canvas on which the profiles in the social
 * network are displayed.  NOTE: This class does NOT need to update the
 * display when the window is resized.
 */

import acm.graphics.*;
import acm.util.RandomGenerator;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class FacePamphletCanvasExtension extends GCanvas
		implements FacePamphletConstants, ComponentListener, KeyListener {

	/**
	 * Constructor This method takes care of any initialization needed for the
	 * display
	 */
	public FacePamphletCanvasExtension() {
		addComponentListener(this); // user can change dimensions
		chatCanvas.addKeyListener(this); // for chat scroll
		initializeWinterWallpaper();
	}

	/*
	 * This is a wallpaper of our chat It needs to be initialized before
	 * executed
	 */
	private void initializeWinterWallpaper() {
		// we need a separate thread for the winter mode
		Thread winterThread = new Thread(() -> {
			try {
				while (isRunning) { // canvas is in run time(not closed)
					if (approvedWinter) { // if button was pressed
						addFlake();
						moveFlakes();
					}
					Thread.sleep(DELAY); // makes flakes visible
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
		winterThread.start();
	}

	/*
	 * Adds a new snowflake to the canvas with a random position
	 */
	private void addFlake() {
		if (rgen.nextBoolean(0.1)) { // 10% of Probability to add a snowflake
			GImage flake = new GImage("snowFlake.jpg");
			flake.setSize(FLAKE_SIZE, FLAKE_SIZE);
			chatCanvas.add(flake, rgen.nextInt(0, getWidth() - FLAKE_SIZE), -FLAKE_SIZE);
			flakes.add(flake); // Add the flake to the list
		}
	}

	/*
	 * Moves all snowflakes downward and removes them when they reach the bottom
	 */
	private void moveFlakes() {
		Iterator<GImage> iterator = flakes.iterator();
		while (iterator.hasNext()) {
			GImage flake = iterator.next();
			flake.sendToBack(); // Ensure the flake stays behind other objects
			if (flake.getY() + FLAKE_SIZE > getHeight()) {
				chatCanvas.remove(flake); // Remove from canvas if it reaches the bottom
				iterator.remove(); // Remove from the list
			} else {
				flake.move(0, 3); // Move the snowflake downward
			}
		}

	}

	/*
	 * Stops the winter mode(snowflake) thread
	 */
	public void stop() {
		isRunning = false;
	}

	/*
	 * Switches back to classic mode
	 */
	public void resetToClassicMode() {
		approvedWinter = false; // Disable winter mode
		removeFlakes(); // clear canvas and flakes array
	}

	private void removeFlakes() {
		for (GImage flake : flakes) {
			chatCanvas.remove(flake);
		}
		flakes.clear();
	}

	/*
	 * This method displays all users
	 */
	public void displayUsers(int iteratorSize, Iterator<String> iterator) {
		remove(chatCanvas); // if it was displayed before
		canvasOfUsers.setBounds(0, 0, 2 * getWidth(), 2 * getHeight());
		add(canvasOfUsers);
		canvasOfUsers.removeAll();
		initializeUsersCanvas();
		if (iteratorSize != 0) {
			drawUsers(iteratorSize, iterator);
		} else {
			drawNoUser();
		}
	}

	/*
	 * This method draw initialization of canvas
	 */
	private void initializeUsersCanvas() {
		if (userCanvasLabel != null) {
			canvasOfUsers.remove(userCanvasLabel);
		}
		if (userCanvasGLine != null) {
			canvasOfUsers.remove(userCanvasGLine);
		}
		userCanvasLabel = new GLabel("USERS");
		userCanvasLabel.setFont(resizedFont(30, "-bold"));
		double labelWidth = userCanvasLabel.getWidth();
		double labelHeight = userCanvasLabel.getAscent();
		canvasOfUsers.add(userCanvasLabel, (getWidth() - labelWidth) / 2, labelHeight);
		double yforGLine = labelHeight + 2; // +2 so there is space between them
		userCanvasGLine = new GLine(userCanvasLabel.getX(), yforGLine, userCanvasLabel.getX() + labelWidth, yforGLine);
		canvasOfUsers.add(userCanvasGLine);
	}

	/*
	 * This method is executed only if there are users
	 */
	private void drawUsers(int iteratorSize, Iterator<String> iterator) {
		yoffsetOfUsers = getHeight() / 20;
		double y = userCanvasLabel.getY() + userCanvasLabel.getAscent();
		while (iterator.hasNext()) {
			GLabel label = new GLabel(iterator.next());
			label.setFont(resizedFont(20, "-bold"));
			canvasOfUsers.add(label, (getWidth() - label.getWidth()) / 2, y);
			y += yoffsetOfUsers;
		}
	}

	/*
	 * This method is executed if there are no users
	 */
	private void drawNoUser() {
		GLabel label = new GLabel("NO USERS");
		label.setFont(resizedFont(25, "-bold"));
		label.setColor(Color.BLUE);
		double x = (getWidth() - label.getWidth()) / 2;
		double y = (getHeight() - label.getAscent()) / 2;
		canvasOfUsers.add(label, x, y);
	}

	/*
	 * If chat is pressed, we show them our messaging portal
	 */
	public void addMessenger(String user) {
		remove(canvasOfUsers);// if it was displayed before
		chatCanvas.setBounds(0, 0, 2 * getWidth(), 2 * getHeight());
		add(chatCanvas);
		initializeChatCanvas(user);
		chat = new GCompound();
		chatCanvas.add(chat, getWidth() / 4, 0);
		displayMessages();
	}

	/*
	 * Initialization, in which only GLabel of "TOPIC" and below GLine is drawn
	 * ( both of them obey component Listener)
	 */
	private void initializeChatCanvas(String user) {
		removeLastDrawns();
		chatLabel = new GLabel("MESSAGES");
		chatLabel.setFont(resizedFont(30, "-bold"));
		double labelWidth = chatLabel.getWidth();
		double labelHeight = chatLabel.getAscent();
		chatCanvas.add(chatLabel, (getWidth() / 40), labelHeight);
		addchatLine(labelHeight, labelWidth);
		adduserLabel(user);
	}

	/*
	 * Clears canvas by removing previously drawn objects
	 */
	private void removeLastDrawns() {
		if (chatLabel != null) {
			chatCanvas.remove(chatLabel);
		}
		if (chatLine != null) {
			chatCanvas.remove(chatLine);
		}
		if (userLabel != null) {
			chatCanvas.remove(userLabel);
		}
	}

	private void addchatLine(double labelHeight, double labelWidth) {
		double yforGLine = labelHeight + 2; // +2 so there is space between them
		chatLine = new GLine(chatLabel.getX(), yforGLine, chatLabel.getX() + labelWidth, yforGLine);
		chatCanvas.add(chatLine);
	}

	private void adduserLabel(String user) {
		if (!user.equals("")) {
			userLabel = new GLabel("User:" + user);
		} else {
			userLabel = new GLabel("No User Chosen");
		}
		userLabel.setFont(resizedFont(25, "-bold"));
		userLabel.setColor(Color.RED);
		chatCanvas.add(userLabel, (getWidth() / 40), getHeight() / 4);
	}
	
	/*
	 * Stores messages and goes to other method which 
	 * displays them
	 */
	public void storeMessages(String message, String author) {
		GLabel newMessage = new GLabel(author + ": " + message);
		newMessage.setFont("Dialog-20");
		listOfMessages.add(newMessage);
		displayMessages();
	}
	
	/*
	 * Our messages are kept in GCoumpound which makes it
	 * easy to move them collectively
	 */
	private void displayMessages() {
		yOffset = getHeight() / 10;
		xOffset = getWidth() / 5;
		yInterval = getHeight() / 18;
		for (GLabel label : listOfMessages) {
			yOffset += yInterval;
			chat.add(label, xOffset, yOffset);
			if (label.getY() > getHeight()) {
				chat.setLocation(chat.getX(), getHeight() - (chat.getHeight() + getHeight() / 7));
			}
		}
	}

	public void backToHomePage() {
		remove(chatCanvas);
		remove(canvasOfUsers);
	}

	/**
	 * This method displays a message string near the bottom of the canvas.
	 * Every time this method is called, the previously displayed message (if
	 * any) is replaced by the new message text passed in.
	 */
	public void showMessage(String msg) {
		if (labelOfMessage != null) {
			remove(labelOfMessage);
		}
		labelOfMessage = new GLabel(msg);
		labelOfMessage.setFont(resizedFont(18, ""));
		double x = (getWidth() - labelOfMessage.getWidth()) / 2;
		double y = getHeight() - BOTTOM_MESSAGE_MARGIN * (getHeight() + getWidth()) / baseDimensions;
		add(labelOfMessage, x, y);
		lastMessage = msg;
	}

	/**
	 * This method displays the given profile on the canvas. The canvas is first
	 * cleared of all existing items (including messages displayed near the
	 * bottom of the screen) and then the given profile is displayed. The
	 * profile display includes the name of the user from the profile, the
	 * corresponding image (or an indication that an image does not exist), the
	 * status of the user, and a list of the user's friends in the social
	 * network.
	 */
	public void displayProfile(FacePamphletProfile profile) {
		// Setting values of dimensions used by all below methods
		// since we added component listeners, now we use (getHeight() + getWidth()) / baseDimensions
		double imageWidth = IMAGE_WIDTH * (getHeight() + getWidth()) / baseDimensions;
		double imageHeight = IMAGE_HEIGHT * (getHeight() + getWidth()) / baseDimensions;
		double newLeftMargin = LEFT_MARGIN * (getHeight() + getWidth()) / baseDimensions;
		double newImageYCoordinate = IMAGE_Y_COORDINATE * (getHeight() + getWidth()) / baseDimensions;
		double newTopMargin = TOP_MARGIN * (getHeight() + getWidth()) / baseDimensions;
		double newStatusMargin = STATUS_MARGIN * (getHeight() + getWidth()) / baseDimensions;
		removeAll();
		addName(profile.getName(), newLeftMargin, newTopMargin);
		addPicture(profile, imageWidth, imageHeight, newLeftMargin, newImageYCoordinate);
		addStatus(profile, newStatusMargin, imageHeight, newLeftMargin, newImageYCoordinate);
		addFriends(profile.getFriends(), newImageYCoordinate);
		lastProfile = profile;
	}

	private void addName(String name, double leftMargin, double topMargin) {
		GLabel labelOfName = new GLabel(name);
		labelOfName.setColor(Color.BLUE);
		labelOfName.setFont(resizedFont(24, ""));
		add(labelOfName, leftMargin, topMargin + labelOfName.getAscent());
	}

	/*
	 * This method adds picture if there is any or just a frame(No Image)
	 */
	private void addPicture(FacePamphletProfile profile, double imWidth, double imHeight, double leftMargin, double y) {
		GImage image = profile.getImage();
		if (image != null) {
			image.setSize(imWidth,imHeight);
			add(image, leftMargin, y);
		} else {
			GRect rect = new GRect(imWidth, imHeight);
			add(rect, leftMargin, y);
			addLabelInside("No Image", imWidth, imHeight, leftMargin, y);
		}
	}

	private void addLabelInside(String text, double imWidth, double imHeight, double newLeftMargin, double y) {
		GLabel label = new GLabel(text);
		label.setFont(resizedFont(24, ""));
		double labelX = newLeftMargin + (imWidth - label.getWidth()) / 2;
		double labelY = y + (imHeight + label.getHeight()) / 2;
		add(label, labelX, labelY);
	}

	private void addStatus(FacePamphletProfile profile, double statusMargin, double imHeight, double leftMargin,
			double y) {
		GLabel statusLabel = null; // initialize it
		if (!profile.getStatus().isEmpty()) {
			statusLabel = new GLabel(profile.getName() + " is " + profile.getStatus());
		} else {
			statusLabel = new GLabel("No current status");
		}
		statusLabel.setFont(resizedFont(16, "-bold"));
		double belowYOfImage = y + imHeight;
		double yOfStatus = belowYOfImage + statusMargin + statusLabel.getAscent();
		add(statusLabel, leftMargin, yOfStatus);
	}

	private void addFriends(Iterator<String> friends, double imageY) {
		double x = makeFriendsLabel(imageY);
		double y = imageY;
		while (friends.hasNext()) {
			String friend = friends.next();
			double heightOfFriendLabel = makeGLabelofFriend(friend, resizedFont(16, ""), x, y);
			y += heightOfFriendLabel;
		}
	}

	/*
	 * This method has two functions. 1) it adds Friends(title) Label on canvas
	 * 2) it return x coordinate of label, other labels can use it
	 */
	private double makeFriendsLabel(double imageY) {
		GLabel labelOfFriends = new GLabel("Friends:");
		labelOfFriends.setFont(resizedFont(16, "-bold"));
		double x = (getWidth() - labelOfFriends.getWidth()) / 2;
		add(labelOfFriends, x, imageY);
		return x;
	}

	/*
	 * This method has two functions. 1) it adds Friend Label on canvas 2) it
	 * return height of canvas, so Y coordinate of next label changes
	 */
	private double makeGLabelofFriend(String text, String font, double x, double y) {
		GLabel label = new GLabel(text);
		label.setFont(font);
		add(label, x, y + label.getHeight());
		return label.getHeight();
	}

	/*
	 * It receives information about default font and then returns string of
	 * resized font
	 */
	private String resizedFont(int num, String str) {
		int n = num * (getHeight() + getWidth()) / baseDimensions;
		String fontText = "Dialog-" + String.valueOf(n) + str;
		return fontText;
	}

	private void update() {
		if (lastProfile != null) {
			displayProfile(lastProfile);
		}
		showMessage(lastMessage);
	}

	private void setChatLabelToNull() {
		if (chatLabel != null) {
			chatCanvas.remove(chatLabel);
			chatLabel = null;
		}
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		backToHomePage();
		update();
		setChatLabelToNull();
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * This is a scroll function that enables users to scroll chat and see
	 * previously sent messages
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			if (chat.getY() < 0) {
				chat.move(0, 10);
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			if ((chat.getY() + chat.getHeight()) + getHeight() / 6 > getHeight()) {
				chat.move(0, -10); // Move 10 units down
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	/* Instance variables */
	private GLabel labelOfMessage;
	// Used for ComponentListener
	private String lastMessage = "";
	// lastProfile is public, because when it's removed
	// by other class its value needs to be changed to null
	public FacePamphletProfile lastProfile;
	// Addition of default getHeight and default width(for ComponentListener)
	private int baseDimensions = (621 + 444);

	/* Instance variables for displaying messages(chat) */
	// canvas for messaging
	private GCanvas chatCanvas = new GCanvas();
	// chatLabel(part of chatCanvas) is public because it becomes null in
	// resized method, and in FacePamphletExtension class we need that info
	public GLabel chatLabel, userLabel;
	// this GLine is part of chatCanvas
	private GLine chatLine;
	private GCompound chat;
	private int yInterval;
	private int yOffset;
	private int xOffset;
	private ArrayList<GLabel> listOfMessages = new ArrayList<>();

	/* Constants for winter wallpaper */
	/** The size of flakes, which fall when WINTER mode is on */
	private static final int FLAKE_SIZE = 20;
	/** Delay, which makes falling objects visible for us */
	private static final int DELAY = 20;
	/* Instance variables for winter wallpaper */
	public boolean approvedWinter = false; // becomes true if WINTER mode is on
	private boolean isRunning = true; // controls thread execution
	private ArrayList<GImage> flakes = new ArrayList<>(); // stores flakes
	private RandomGenerator rgen = RandomGenerator.getInstance();

	/* Instance variables of View User Canvas */
	private GCanvas canvasOfUsers = new GCanvas();
	private GLabel userCanvasLabel;
	private GLine userCanvasGLine;
	private int yoffsetOfUsers;
}
