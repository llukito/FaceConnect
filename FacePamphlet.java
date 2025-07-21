
/*
 * File: FacePamphlet.java
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

public class FacePamphlet extends Program implements FacePamphletConstants {

	/* Instance variables of North-located JComponents */
	private JTextField nameField;
	private JButton add, delete, lookup;

	/* Instance variables of West-located JComponents */
	private JTextField statusField, pictureField, addFriendField;
	private JButton changeStatus, changePicture, addFriend;

	/* Instance variable of user-related communication */
	private IODialog dialog = new IODialog();

	/* Instance variables of objects of other classes */
	private FacePamphletCanvas canvas;
	private FacePamphletDatabase database;
	private FacePamphletProfile currentProfile;

	/* Constructor of our class */
	public FacePamphlet() {
		canvas = new FacePamphletCanvas();
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
		addActionListeners();
		database = new FacePamphletDatabase();
	}

	private void addNorthJComponents() {
		add(new JLabel("Name"), NORTH);
		nameField = new JTextField(TEXT_FIELD_SIZE);
		add(nameField, NORTH);
		addNorthButtons();
	}

	private void addNorthButtons() {
		add = new JButton("Add");
		add(add, NORTH);
		delete = new JButton("Delete");
		add(delete, NORTH);
		lookup = new JButton("Lookup");
		add(lookup, NORTH);
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
		} else if (e.getSource() == changeStatus || e.getSource() == statusField) {
			displayWestButtons(changeStatus, statusField);
		} else if (e.getSource() == changePicture || e.getSource() == pictureField) {
			displayWestButtons(changePicture, pictureField);
		} else if (e.getSource() == addFriend || e.getSource() == addFriendField) {
			displayWestButtons(addFriend, addFriendField);
		}
		emptyTextFields();
	}

	private void displayNorthButtons(JButton button) {
		if (nameField.getText().isEmpty()) {
			dialog.showErrorMessage("Text Field is empty");
		} else {
			if (button == add) {
				addUser();
			} else if (button == delete) {
				deleteUser();
			} else { // lookup
				lookupUser();
			}
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
		}
	}

	private void displayWestButtons(JButton button, JTextField field) {
		if (field.getText().isEmpty()) {
			dialog.showErrorMessage("Text Field is empty");
		} else {
			if (button == changeStatus) {
				changeStatus();
			} else if (button == changePicture) {
				changePicture();
			} else { // addFriend
				addFriend();
			}
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

	/*
	 * This method checks multiple things before adding friend Every if
	 * statement is check for specific info. Else statements show what we are
	 * checking exactly
	 */
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

	private void emptyTextFields() {
		nameField.setText("");
		statusField.setText("");
		pictureField.setText("");
		addFriendField.setText("");
		addFriendField.setText("");
	}

}
