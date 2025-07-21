
/*
 * File: FacePamphletExtension.java
 * -----------------------
 * When it is finished, this program will implement a basic social network
 * management system.
 */

import acm.io.IODialog;
import acm.program.*;
import acm.graphics.*;
import acm.util.*;

import java.awt.event.*;
import java.util.Iterator;
import javax.swing.*;

public class FacePamphletExtension extends Program implements FacePamphletConstants {

	/* Instance variables of North-located JComponents */
	private JTextField nameField;
	private JButton add, delete, lookup, viewUsers;

	/* Instance variables of South-located JComponents */
	private JTextField chatField;
	private JButton homePage, chat, winterWallpaper, defaultWallpaper;
	public boolean isChatOpened = false; // it is changed by other classes too

	/* Instance variables of West-located JComponents */
	private JTextField statusField, pictureField, addFriendField;
	private JButton changeStatus, changePicture, addFriend;

	/* Instance variable of user-related communication */
	private IODialog dialog = new IODialog();

	/* Instance variables of objects of other classes */
	private FacePamphletDatabase database;
	private FacePamphletProfile currentProfile = null;
	private FacePamphletCanvasExtension canvas;

	/* Constructor of our class */
	public FacePamphletExtension() {
		canvas = new FacePamphletCanvasExtension();
		add(canvas);
	}

	/**
	 * This method has the responsibility for initializing the interactors in
	 * the application, and taking care of any other initialization that needs
	 * to be performed.
	 */
	public void init() {
		addNorthJComponents();
		addWestJComponents();
		addSouthJComponents();
		addActionListeners();
		database = new FacePamphletDatabase();
	}

	private void addNorthJComponents() {
		homePage = new JButton("Home Page");
		add(homePage, NORTH);
		add(new JLabel(EMPTY_LABEL_TEXT_NORTH), NORTH);
		add(new JLabel("Name"), NORTH);
		nameField = new JTextField(TEXT_FIELD_SIZE);
		add(nameField, NORTH);
		addRestNorthButtons();
		add(new JLabel(EMPTY_LABEL_TEXT_NORTH), NORTH);
	}

	/*
	 * Rest of buttons are added
	 */
	private void addRestNorthButtons() {
		add = new JButton("Add");
		add(add, NORTH);
		delete = new JButton("Delete");
		add(delete, NORTH);
		lookup = new JButton("Lookup");
		add(lookup, NORTH);
		viewUsers = new JButton("View All Users");
		add(viewUsers, NORTH);
	}

	private void addWestJComponents() {
		addStatusComponents();
		addPictureComponents();
		addFriendComponents();
	}

	private void addStatusComponents() {
		statusField = new JTextField(TEXT_FIELD_SIZE);
		add(statusField, WEST);
		statusField.addActionListener(this);
		changeStatus = new JButton("Change Status");
		add(changeStatus, WEST);
		add(new JLabel(EMPTY_LABEL_TEXT), WEST);
	}

	private void addPictureComponents() {
		pictureField = new JTextField(TEXT_FIELD_SIZE);
		add(pictureField, WEST);
		pictureField.addActionListener(this);
		changePicture = new JButton("Change Picture");
		add(changePicture, WEST);
		add(new JLabel(EMPTY_LABEL_TEXT), WEST);
	}

	private void addFriendComponents() {
		addFriendField = new JTextField(TEXT_FIELD_SIZE);
		add(addFriendField, WEST);
		addFriendField.addActionListener(this);
		addFriend = new JButton("Add Friend");
		add(addFriend, WEST);
	}

	private void addSouthJComponents() {
		chat = new JButton("Open Chat");
		add(chat, SOUTH);
		chatField = new JTextField(TEXT_FIELD_SIZE);
		chatField.addActionListener(this);
		add(chatField, SOUTH);
		winterWallpaper = new JButton("Winter Wallpaper");
		add(winterWallpaper, SOUTH);
		defaultWallpaper = new JButton("Default Wallpaper");
		add(defaultWallpaper, SOUTH);
	}

	/**
	 * This class is responsible for detecting when the buttons are clicked or
	 * interactors are used, so you will have to add code to respond to these
	 * actions.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == add) {
			displayNorthButtons(add);
		} else if (e.getSource() == delete) {
			displayNorthButtons(delete);
		} else if (e.getSource() == lookup) {
			displayNorthButtons(lookup);
		} else if (e.getSource() == viewUsers) {
			displayUsers();
		} else if (e.getSource() == changeStatus || e.getSource() == statusField) {
			displayWestButtons(changeStatus, statusField);
		} else if (e.getSource() == changePicture || e.getSource() == pictureField) {
			displayWestButtons(changePicture, pictureField);
		} else if (e.getSource() == addFriend || e.getSource() == addFriendField) {
			displayWestButtons(addFriend, addFriendField);
		} else if (e.getSource() == homePage) {
			goToHomePage();
		} else if (e.getSource() == chat) {
			goToChat();
		} else if (e.getSource() == chatField) {
			checkSendingMessage();
		} else if (e.getSource() == winterWallpaper) {
			setWinterWallpaper();
		} else if (e.getSource() == defaultWallpaper) {
			setDefaultWallpaper();
		}
		emptyTextFields();
	}

	private void displayNorthButtons(JButton button) {
		if (nameField.getText().isEmpty()) {
			dialog.showErrorMessage("Text Field is empty");
		} else {
			canvas.backToHomePage();
			if (button == add) {
				addUser();
			} else if (button == delete) {
				deleteUser();
			} else { // lookup
				lookupUser();
			}
			isChatOpened = false;
		}
	}

	private void addUser() {
		String name = nameField.getText().trim();
		if (!database.containsProfile(name)) {
			FacePamphletProfile profile = new FacePamphletProfile(name);
			database.addProfile(profile);
			currentProfile = profile;
			canvas.displayProfile(currentProfile);
			canvas.showMessage("New profile created");
		} else { // already in database
			currentProfile = database.getProfile(name);
			canvas.displayProfile(currentProfile);
			canvas.showMessage("A Profile with the name " + name + " already exists");
		}
	}

	private void deleteUser() {
		String name = nameField.getText().trim();
		if (database.containsProfile(name)) {
			database.deleteProfile(name);
			canvas.removeAll();
			canvas.showMessage("Profile of " + name + " deleted");
			canvas.lastProfile = null;
			currentProfile = null;
		} else {
			canvas.showMessage("A Profile with the name " + name + " does not exist");
		}
	}

	private void lookupUser() {
		String name = nameField.getText().trim();
		FacePamphletProfile profile = database.getProfile(name);
		if (profile != null) {
			currentProfile = profile;
			canvas.displayProfile(currentProfile);
			canvas.showMessage("Displaying " + name);
		} else {
			canvas.removeAll();
			canvas.showMessage("A Profile with the name " + name + " does not exist");
			currentProfile = null;
			canvas.lastProfile = null;
		}
	}

	private void displayUsers() {
		int sizeOfKeys = database.profilesMap.keySet().size();
		Iterator<String> iteratorOfKeys = database.profilesMap.keySet().iterator();
		canvas.displayUsers(sizeOfKeys, iteratorOfKeys);
		isChatOpened = false;
	}

	private void displayWestButtons(JButton button, JTextField field) {
		if (field.getText().isEmpty()) {
			dialog.showErrorMessage("Text Field is empty");
		} else {
			canvas.backToHomePage();
			if (button == changeStatus) {
				changeStatus();
			} else if (button == changePicture) {
				changePicture();
			} else { // addFriend
				addFriend();
			}
			isChatOpened = false;
		}
	}

	private void changeStatus() {
		if (currentProfile != null) {
			currentProfile.setStatus(statusField.getText().trim());
			canvas.displayProfile(currentProfile);
			canvas.showMessage("Status updated to " + statusField.getText().trim());
		} else {
			canvas.showMessage("Please select a profile to change status");
		}
	}

	private void changePicture() {
		if (currentProfile != null) {
			GImage image = null;
			try {
				image = new GImage(pictureField.getText().trim());
				currentProfile.setImage(image);
				canvas.displayProfile(currentProfile);
				canvas.showMessage("Picture updated");
			} catch (ErrorException ex) {
				canvas.showMessage("Unable to open image file: " + pictureField.getText().trim());
			}
		} else {
			canvas.showMessage("Please select a profile to change picture");
		}
	}

	private void addFriend() {
		if (currentProfile != null) {
			String friend = addFriendField.getText().trim();
			if (database.containsProfile(friend)) {
				if (!alreadyFriends(friend)) {
					if (friend.equals(currentProfile.getName())) {
						canvas.showMessage("Can't add yourself as a friend");
						return;
					}
					currentProfile.addFriend(friend);
					database.getProfile(friend).addFriend(currentProfile.getName()); // being friends is bilateral
					canvas.displayProfile(currentProfile);
					canvas.showMessage(friend + " added as a friend");
				} else {
					canvas.showMessage(currentProfile.getName() + " already has " + friend + " as a friend");
				}
			} else {
				canvas.showMessage(friend + " does not exist");
			}
		} else {
			canvas.showMessage("Please select a profile to add friend");
		}
	}

	/*
	 * Apparently this method is a second check for friends, since addFriend
	 * (which is FacePamphletProfile class method) itself adds friend only if
	 * not being friends before (still let's leave this method here, The more
	 * check , The better:) )
	 */
	private boolean alreadyFriends(String friend) {
		Iterator<String> it = currentProfile.getFriends();
		while (it.hasNext()) {
			if (it.next().equals(friend)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Home page is the page we see first when we run program
	 */
	private void goToHomePage() {
		canvas.backToHomePage();
		isChatOpened = false;
	}

	/*
	 * Opens chat(messenger) if there are people
	 */
	private void goToChat() {
		if (!database.profilesMap.isEmpty()) {
			if (currentProfile != null) {
				canvas.addMessenger(currentProfile.getName());
			} else {
				canvas.addMessenger("");
			}
			isChatOpened = true;
		} else {
			dialog.showErrorMessage("No people to Chat");
		}
	}

	/*
	 * This method checks if chat is open to send message.
	 * chatLabel should not be null if chat is open and
	 * our chatOpened boolean should not be false
	 */
	private void checkSendingMessage() {
		if (canvas.chatLabel == null || isChatOpened == false) {
			dialog.showErrorMessage("Chat is not opened");
		} else {
			checkMessage();
		}
	}

	/*
	 * Checks if there is content in message
	 */
	private void checkMessage() {
		if (chatField.getText().isEmpty()) {
			dialog.showErrorMessage("Text Field is empty");
		} else {
			checkProfile();
		}
	}

	/*
	 * Message can not be sent if currentProfile is null
	 */
	private void checkProfile() {
		if (currentProfile != null) {
			sendMessage();
		} else {
			dialog.showErrorMessage("Please select a profile to send message");
		}
	}

	/*
	 * Finally, message is valid and will be sent to canvas class
	 */
	private void sendMessage() {
		String message = chatField.getText();
		canvas.storeMessages(message, currentProfile.getName());
	}

	/*
	 * This is wallpaper for chat
	 */
	private void setWinterWallpaper() {
		if (canvas.chatLabel == null || isChatOpened == false) {
			dialog.showErrorMessage("Chat is not opened");
		} else {
			canvas.approvedWinter = true;
		}
	}

	/*
	 * This is default(white) wallpaper for chat
	 */
	private void setDefaultWallpaper() {
		if (canvas.chatLabel == null || isChatOpened == false) {
			dialog.showErrorMessage("Chat is not opened");
		} else {
			canvas.resetToClassicMode();
		}
	}

	private void emptyTextFields() {
		nameField.setText("");
		statusField.setText("");
		pictureField.setText("");
		addFriendField.setText("");
		addFriendField.setText("");
		chatField.setText("");
	}

}
