/*
 * File: FacePamphletCanvas.java
 * -----------------------------
 * This class represents the canvas on which the profiles in the social
 * network are displayed.  NOTE: This class does NOT need to update the
 * display when the window is resized.
 */

import acm.graphics.*;

import java.awt.*;
import java.util.*;

public class FacePamphletCanvas extends GCanvas implements FacePamphletConstants {

	/* Instance variable, which displays messages on canvas */
	private GLabel labelOfMessage;

	/**
	 * Constructor This method takes care of any initialization needed for the
	 * display
	 */
	public FacePamphletCanvas() {
		// no need to fill it
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
		labelOfMessage.setFont(MESSAGE_FONT);
		double x = (getWidth() - labelOfMessage.getWidth()) / 2;
		double y = getHeight() - BOTTOM_MESSAGE_MARGIN;
		add(labelOfMessage, x, y);
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
		removeAll();
		addName(profile.getName());
		addPicture(profile);
		addStatus(profile.getStatus(), profile.getName());
		addFriends(profile.getFriends());
	}

	private void addName(String name) {
		GLabel labelOfName = new GLabel(name);
		labelOfName.setColor(Color.BLUE);
		labelOfName.setFont(PROFILE_NAME_FONT);
		add(labelOfName, LEFT_MARGIN, TOP_MARGIN + labelOfName.getAscent());
	}

	/*
	 * This method adds picture if there is any or just a frame(No Image)
	 */
	private void addPicture(FacePamphletProfile profile) {
		GImage image = profile.getImage();
		if (image != null) {
			image.setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
			add(image, LEFT_MARGIN, IMAGE_Y_COORDINATE);
		} else {
			GRect rect = new GRect(IMAGE_WIDTH, IMAGE_HEIGHT);
			add(rect, LEFT_MARGIN, IMAGE_Y_COORDINATE);
			addLabelInside("No Image");
		}
	}

	private void addLabelInside(String text) {
		GLabel label = new GLabel(text);
		label.setFont(PROFILE_IMAGE_FONT);
		double labelX = LEFT_MARGIN + (IMAGE_WIDTH - label.getWidth()) / 2;
		double labelY = IMAGE_Y_COORDINATE + (IMAGE_HEIGHT + label.getHeight()) / 2;
		add(label, labelX, labelY);
	}

	private void addStatus(String status, String name) {
		GLabel statusLabel = null; // initialize it
		if (!status.isEmpty()) {
			statusLabel = new GLabel(name + " is " + status);
		} else {
			statusLabel = new GLabel("No current status");
		}
		statusLabel.setFont(PROFILE_STATUS_FONT);
		double belowYOfImage = IMAGE_Y_COORDINATE + IMAGE_HEIGHT;
		double y = belowYOfImage + STATUS_MARGIN + statusLabel.getAscent();
		add(statusLabel, LEFT_MARGIN, y);
	}

	private void addFriends(Iterator<String> friends) {
		double x = makeFriendsLabel();
		double y = IMAGE_Y_COORDINATE;
		while (friends.hasNext()) {
			String friend = friends.next();
			double heightOfFriendLabel = makeGLabelofFriend(friend, PROFILE_FRIEND_FONT, x, y);
			y += heightOfFriendLabel;
		}
	}

	/*
	 * This method has two functions. 1) it adds Friends(title) Label on canvas
	 * 2) it return x coordinate of label, other labels can use it
	 */
	private double makeFriendsLabel() {
		GLabel labelOfFriends = new GLabel("Friends:");
		labelOfFriends.setFont(PROFILE_FRIEND_LABEL_FONT);
		double x = (getWidth() - labelOfFriends.getWidth()) / 2;
		add(labelOfFriends, x, IMAGE_Y_COORDINATE);
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

}
