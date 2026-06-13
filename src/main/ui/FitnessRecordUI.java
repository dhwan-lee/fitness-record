package ui;

import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import model.Exercise;
import model.Logbook;
import model.Muscles;
import model.WorkoutSession;
import model.PrintEventLog;

import java.util.List;

/*
* Represent application's main window frame
* This class now acts as the VIEW and CONTROLLER
* It sends all user actions to the LOGBOOK (the model)
*/
public class FitnessRecordUI extends JFrame {
    private static final int WIDTH = 350;
    private static final int HEIGHT = 700;
    private static final String IMAGE_STORE = "./image/background.png";
    
    // MODEL
    private Logbook logbook;

    // View components
    private JFrame parentFrame;
    private JComboBox<Muscles> muscleComboBox;
    private JTextArea logDisplay;
    private JScrollPane scrollPane;
    private JTextField yearField;
    private JTextField monthField;
    private JTextField dayField;
    private JTextField nameField;
    private JTextField weightField;
    private JTextField setsField;
    private JTextField repsField;
    private String[] labels = {
        "Exercise Name", 
        "Muscle Type", 
        "Weight (kg)", 
        "Number of Reps", 
        "Number of Sets", 
        "Date yyyy/mm/dd"
    };
    

    /*
     * MODIFIES: this
     * EFFECTS: creates the main application window and initialize components
     */
    public FitnessRecordUI() {
        // initialize the LogBook. This is the Model
        // it automatically knows where to save/load from.
        logbook = new Logbook("./data/fitness_log.json");

        parentFrame = new JFrame("Fitness Record");
        parentFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        parentFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        parentFrame.setSize(WIDTH, HEIGHT);
        parentFrame.setLayout(new BorderLayout());

        try {
            logbook.loadLogBook();
        } catch (IOException e) {
            System.out.println("No existing log file found. Starting fresh.");
        }

        JRootPane rootPane = parentFrame.getRootPane();
    
        // get the input map for when the window or any of its children are focused
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        // map the physical ESCAPE key stroke to an action identifier string
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        inputMap.put(escapeKey, "CONFIRM_EXIT");

        // define what action happens when that identifier string is triggered
        actionMap.put("CONFIRM_EXIT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // reuses your existing safe custom exit dialogue sequence
                confirmExit();
            }
        });
        
        parentFrame.add(new ImagePanel(IMAGE_STORE), BorderLayout.CENTER);
        addButtonPanel();
        centreOnScreen();
        parentFrame.setVisible(true);
    }

    /*
     * Displays a confirmation dialog before exiting this application.
     * Handles user's "Yes" (Enter), "No", or ESC key presses.
     */
    private void confirmExit() {
        // If user clicks "No", presses ESC, or hits Enter (on default "No")
        // the dialog simply closes and nothing happens
        showCustomConfirmExit();
    }

    /**
     * Creates and displays a custom, foolproof confirmation dialog.
     * This method builds a JDialog from scratch to ensure keyboard
     * navigation (Tab, Arrows, Enter, ESC) works perfectly.
     */
    private void showCustomConfirmExit() {
        // create the modal dialog
        JDialog dialog = new JDialog(parentFrame, "Confirm Exit", true); // 'true' makes it modal
        dialog.setSize(350, 150);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // add the message
        JLabel messageLabel = new JLabel("Just double check! would you like to close this application?");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBorder(new EmptyBorder(20, 20, 10, 20)); // Add padding
        dialog.add(messageLabel, BorderLayout.CENTER);

        //  reate the buttons
        JButton yesButton = new JButton("Yes");
        JButton noButton = new JButton("No");

        // create the button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 20, 0)); // Bottom padding
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // add Action Listeners
        yesButton.addActionListener(e -> {
            PrintEventLog.printEventLog();
            System.exit(0);
        });
        
        noButton.addActionListener(e -> {
            dialog.dispose();
        });

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                yesButton.requestFocusInWindow();
            }
        });

        // Add the ESC key binding *to this dialog
        JRootPane rootPane = dialog.getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        // Arrow Key Navigation: Move focus to "No"
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "FOCUS_NO");
        actionMap.put("FOCUS_NO", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.getRootPane().setDefaultButton(noButton);
                noButton.requestFocusInWindow();
            }
        });

        // Arrow Key Navigation: Move focus to "Yes"
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "FOCUS_YES");
        actionMap.put("FOCUS_YES", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.getRootPane().setDefaultButton(yesButton);
                yesButton.requestFocusInWindow();
            }
        });

        // 🚀 THE FIX FOR TERMINATION: Force Enter key to select the highlighted button
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "TRIGGER_ENTER");
        actionMap.put("TRIGGER_ENTER", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (yesButton.isFocusOwner()) {
                    yesButton.doClick(); // Simulates clicking 'Yes' -> terminates app
                } else if (noButton.isFocusOwner()) {
                    noButton.doClick();  // Simulates clicking 'No' -> closes popup
                }
            }
        });
        // =========================================================================

        // map the ESC key binding to close the dialog
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        inputMap.put(escapeKey, "CLOSE_DIALOG");
        actionMap.put("CLOSE_DIALOG", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        // request initial starting focus on the "no" button when opened
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                dialog.getRootPane().setDefaultButton(noButton);
                noButton.requestFocusInWindow();
            }
        });

        // Show the dialog
        dialog.setLocationRelativeTo(parentFrame); // Center it
        dialog.setVisible(true);
    }

    /*
     * MODIFIES: this
     * EFFECTS: creates and adds the option button panel to the main frame
     */
    private void addButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(8, 1));
        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        buttonPanel.add(createButton("Add an exercise", e -> addExercise()));
        buttonPanel.add(createButton("Remove an exercise", e -> removeExercise()));
        buttonPanel.add(createButton("Update the log", e -> updateLog()));
        buttonPanel.add(createButton("Filter workout log", e -> filteredLog()));
        buttonPanel.add(createButton("View all exercises you added", 
                                            e -> displayAllLogs()));
        buttonPanel.add(createButton("Save logs to file", e -> saveLogsToFile()));
        buttonPanel.add(createButton("Load logs from file", e -> loadLogsFromFile()));
        buttonPanel.add(createButton("Exit", e -> confirmExit()));
    
        parentFrame.add(buttonPanel, BorderLayout.SOUTH);
    }

    /*
     * REQUIRES: text != null, action != null
     * EFFECTS: creates and returns a button with the text and action listener
     */
    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        return button;
    }

    /*
     * MODIFIES: logDisplay, scrollPane, parentFrame
     * EFFECTS: if logDisplay is null, initializes a new JTextArea and wraps it in a JScrollPane.
     */
    private void createDisplayLog() {
        if (logDisplay == null) {
            logDisplay = new JTextArea();
            logDisplay.setEditable(false);
            scrollPane = new JScrollPane(logDisplay);
            scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
            parentFrame.add(scrollPane, BorderLayout.CENTER);
            centreOnScreen();
            parentFrame.setVisible(true);
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: open a window to add a new exercise log
     */
    private void addExercise() {
        createDisplayLog();
        JDialog dialog = createDialog(parentFrame, "Add Exercise", 400, 320);
        
        JPanel addExercisePanel = new JPanel();
        // use BorderLayout for the main panel (10px horizontal gap)
        addExercisePanel.setLayout(new BorderLayout(10, 0));

        // call the helper to build and add the sub-panels
        addExerciseFormat(addExercisePanel);
        
        dialog.add(addExercisePanel, BorderLayout.CENTER);
        dialog.add(exerciseButtonPanel(dialog, "addEx"), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /*
     * REQUIRES: title != null, w > 0, h > 0
     * EFFECTS: creates a new window with the specified title, width, and height
     */
    private JDialog createDialog(Window owner, String title, int w, int h) {
        JDialog dialog;
        if (owner instanceof JFrame) {
            dialog = new JDialog((JFrame) owner, title, true);
        } else {
            dialog = new JDialog((JDialog) owner, title, true);
        }

        dialog.setSize(w, h);
        dialog.setLocationRelativeTo(owner);
        dialog.setLayout(new BorderLayout());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // get the dialog's root pane (its main content area)
        JRootPane rootPane = dialog.getRootPane();
        // use the more robust "WHEN_ANCESTOR" binding
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = rootPane.getActionMap();
        // define the ESC key
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

        // map the ESC key to an "action name"
        inputMap.put(escapeKey, "CLOSE_DIALOG");

        // map the "action name" to an actual action
        actionMap.put("CLOSE_DIALOG", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        return dialog;
    }

    /*
     * REQUIRES: purpose is one of "addEx", "removeEx", "updateEx"
     * EFFECTS: creates a panel with buttons for saving and canceling
     */
    private JPanel exerciseButtonPanel(JDialog dialog, String purpose) {
        JPanel buttonPanel = new JPanel();
        JButton button = new JButton();
        JButton cancelButton = createCancelButton(dialog, "cancel");


        if (purpose.equals("addEx")) {
            button = createSaveButton(dialog);
        } else if (purpose.equals("removeEx")) {
            button = createSaveButtonForRemove(dialog);
        } else if (purpose.equals("updateEx")) {
            button = createSaveButtonForUpdate(dialog);
        }

        buttonPanel.add(button);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    /*
     * REQUIRES: dialog != null
     * MODIFIES: this, Log.exercises
     * EFFECTS: creates and adds a new exercise to the logs with the success message
     */
    private JButton createSaveButton(JDialog dialog) {
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                String exerciseName = nameField.getText();
                Muscles muscleType =  (Muscles) muscleComboBox.getSelectedItem();
                int weight = Integer.parseInt(weightField.getText());
                int reps = Integer.parseInt(repsField.getText());
                int sets = Integer.parseInt(setsField.getText());
                String date = String.format("%s/%s/%s", yearField.getText(), monthField.getText(), dayField.getText());
                
                // creates the exercise object
                Exercise exercise = new Exercise(exerciseName, muscleType, weight, reps, sets);
                // finds the session for that date
                WorkoutSession session = logbook.getSessionByDate(date);
                
                if (session == null) {
                    // if no session exists for that date, create one
                    session = new WorkoutSession(date);
                    logbook.addSession(session);
                }

                // adds the exercise to that day's session
                session.addExercise(exercise);
                
                // updates display and close
                displayLog(exercise, date, "Exercise Added");
                JOptionPane.showMessageDialog(dialog, "Exercise added successfully");
                dialog.dispose();

                // refreshes the main view to show the new exercise
                displayAllLogs();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter vaild numbers for weight, reps, and sets:)");
            }
        });

        return saveButton;
    }

    /*
     * REQUIRES: dialog != null
     * MODIFIES: this
     * EFFECTS: creates and returns a cancel button that closes the window
     */
    private JButton createCancelButton(JDialog dialog, String title) {
        JButton cancelButton = new JButton(title);
        cancelButton.addActionListener(e -> dialog.dispose());
        return cancelButton;
    }

    /*
     * REQUIRES: e != null, date != null, title != null
     * MODIFIES: this
     * EFFECTS: appends exercise to the log display area
     */
    private void displayLog(Exercise e, String date, String title) {
        int volume = e.getTotalVolume();

        logDisplay.append(String.format(
                "\n" + title + ": %s\n Muscle: %s\n Weight: %d kg\n Reps: %d\n Sets: %d\n Volume: %d kg\n Date: %s\n", 
                e.getExerciseName(), 
                e.getMuscleType(), 
                e.getWeightLifted(), 
                e.getNumReps(), 
                e.getNumSets(), 
                volume,
                date
            )
        );
    }

    /*
     * REQUIRES: addExercisePanel != null
     * MODIFIES: this, addExercisePanel
     * EFFECTS: creates two panels(one for labels, one for fields) and 
     * adds them to the main addExercisePanel
     */
    private void addExerciseFormat(JPanel addExercisePanel) {

        // creates the panel for labels (on the west)
        // (GridLayout with 1 column, and a 10px vertical gap for padding)
        JPanel labelPanel = new JPanel(new GridLayout(labels.length, 1, 0, 10));
        labelPanel.setBorder(new EmptyBorder(10, 5, 10, 5)); //10px padding all around
        addExercisePanel.add(labelPanel, BorderLayout.WEST);

        // creates the panel for input fields (in the center)
        // (GridLayout with 1 column, and a 10px vertical gap for padding)
        JPanel fieldPanel = new JPanel(new GridLayout(labels.length, 1, 0, 10));
        fieldPanel.setBorder(new EmptyBorder(10, 5, 10, 10)); // 10px padding
        addExercisePanel.add(fieldPanel, BorderLayout.CENTER);

        // creates and add all the labels to the labelPanel
        for (String labelText : labels) {
            JLabel label = new JLabel(labelText);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            labelPanel.add(label);
        }

        // creates and add all the input fields to the fieldPanel
        // Exercise Name
        nameField = new JTextField();
        fieldPanel.add(nameField);

        // Muscle Type
        muscleComboBox = createMuscleCombo();
        fieldPanel.add(muscleComboBox);

        // Weight
        weightField = new JTextField();
        fieldPanel.add(weightField);

        // Reps
        repsField = new JTextField();
        fieldPanel.add(repsField);

        // Sets
        setsField = new JTextField();
        fieldPanel.add(setsField);

        // Date 
        fieldPanel.add(createDatePanel());
    }

    /*
     * EFFECTS: creates and returns a date panel with placeholder text
     */
    private JPanel createDatePanel() {
        yearField = new JTextField();
        monthField = new JTextField();
        dayField = new JTextField();

        // apply the placeholder behavior to each field
        addPlaceHolderFocusListener(yearField, "YYYY");
        addPlaceHolderFocusListener(monthField, "MM");
        addPlaceHolderFocusListener(dayField, "DD");

        JPanel datePanel = new JPanel();
        // 1 row, 3 columns, 5px horizontal gap, 0px vertical gap
        datePanel.setLayout(new GridLayout(1, 3, 5, 0));
        datePanel.add(yearField);
        datePanel.add(monthField);
        datePanel.add(dayField);

        return datePanel;
    }

    /*
     * adds placeholder behavior to a JTextField.
     * When the user clicks in, the placeholder disappears.
     * When the user clicks out, it reappears if the field is empty.
     * @param field       The text field to modify.
     * @param placeholder The placeholder text(e.g., "YYYY")
     */
    private void addPlaceHolderFocusListener(JTextField field, String placeholder) {
        // sets the field's starting text and color
        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        // adds the FocusListener to watch for clicks
        field.addFocusListener(new FocusListener() {
            
            @Override
            public void focusGained(FocusEvent e) {
                // When the user clicks IN:
                // check if the text is still the placeholder
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                // When the user clicks OUT:
                // check if the field is now empty
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    /*
     * MODIFIES: this
     * EFFECTS: creates a new window for user to remove a specific exercise log
     */
    private void removeExercise() {
        createDisplayLog();
        JDialog dialog = createDialog(parentFrame, "Remove Exercise", 400, 170);

        // use BorderLayout for the main panel
        JPanel removeExercisePanel = new JPanel(new BorderLayout(10, 0));

        // call the helper to build the panel
        removeExerciseFormat(removeExercisePanel);

        dialog.add(removeExercisePanel, BorderLayout.CENTER);
        dialog.add(exerciseButtonPanel(dialog, "removeEx"), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /*
     * REQUIRES: removeExercisePanel != null (must have BorderLayout)
     * MODIFIES: this, removeExercisePanel
     * EFFECTS: adds input fields for removing an exercise (exercise name, date)
     */
    private void removeExerciseFormat(JPanel removeExercisePanel) {
        // creates label panel (WEST)
        JPanel labelPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        labelPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

        JLabel nameLabel = new JLabel("Exercise Name");
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        labelPanel.add(nameLabel);

        JLabel dateLabel = new JLabel("Date yyyy/mm/dd");
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        labelPanel.add(dateLabel);

        // creates field panel(CENTER)
        JPanel fieldPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        fieldPanel.setBorder(new EmptyBorder(10, 5, 10, 10));

        nameField = new JTextField();
        fieldPanel.add(nameField);
        fieldPanel.add(createDatePanel());

        // Adds Panels to the main dialog panel
        removeExercisePanel.add(labelPanel, BorderLayout.WEST);
        removeExercisePanel.add(fieldPanel, BorderLayout.CENTER);
    }

    /*
     * REQUIRES: dialog != null
     * MODIFIES: this, logbook
     * EFFECTS: creates a button that removes an exercise from the correct workout sessionin the logbook
     */
    private JButton createSaveButtonForRemove(JDialog dialog) {
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> {
            try {
                // gets exerciseName and date from fields
                String exerciseName = nameField.getText();
                String date = String.format("%s/%s/%s", yearField.getText(), monthField.getText(), dayField.getText());

                // finds the session from logbook
                WorkoutSession session = logbook.getSessionByDate(date);

                // checks if the session for that date even exists
                if (session == null) {
                    JOptionPane.showMessageDialog(dialog, "No workout session found for date: " + date);
                } else {
                    // the session exists, so tell it to remove the exercise
                    boolean removed = session.removeExercise(exerciseName);

                    // checks if the exercise was successfully found and removed
                    if (removed) {
                        JOptionPane.showMessageDialog(dialog, "Exercise '" + exerciseName + "' removed successfully.");

                        // refreshes the main display to show the change
                        displayAllLogs();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Exercise '" + exerciseName + "' not found on this date.");
                    }
                }
                
                // closes the pop-up dialog
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "An error occured. Please check your inputs.");
            }
        });

        return removeButton;
    }

    /*
     * MODIFIES: this
     * EFFECTS: find exercise log for updating. This just opens the "find" dialog.
     */
    private void updateLog() {
        createDisplayLog();
        JDialog dialog = createDialog(parentFrame, "Update Exercise: (Find)", 400, 170);
        
        JPanel updateExercisePanel = new JPanel(new BorderLayout(10, 0));
        updateExericseFormat(updateExercisePanel);

        dialog.add(updateExercisePanel, BorderLayout.CENTER);
        dialog.add(exerciseButtonPanel(dialog, "updateEx"), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /*
     * REQUIRES: updateExercisePanel != null
     * MODIFIES: this
     * EFFECTS: adds input field for finding an exercise log to update
     */
    private void updateExericseFormat(JPanel updateExercisePanel) {
        // creates label panel (WEST)
        JPanel labelPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        labelPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

        JLabel nameLabel = new JLabel("Exercise Name");
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        labelPanel.add(nameLabel);

        JLabel dateLabel = new JLabel("Date yyyy/mm/dd");
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        labelPanel.add(dateLabel);

        // creates field panel (CENTER)
        JPanel fieldPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        fieldPanel.setBorder(new EmptyBorder(10, 5, 10, 10));

        nameField = new JTextField();
        fieldPanel.add(nameField);
        fieldPanel.add(createDatePanel());

        // adds panels to the main dialog panel
        updateExercisePanel.add(labelPanel, BorderLayout.WEST);
        updateExercisePanel.add(fieldPanel, BorderLayout.CENTER);
    }

    /*
     * REQURIES: dialog != null
     * MODIFIES: this
     * EFFECTS: finds a matching exercise log based on the input name and date, and open a new window.
     */
    private JButton createSaveButtonForUpdate(JDialog dialog) {
        JButton saveButton = new JButton("Find");
        saveButton.addActionListener(e -> {
            try {
                // gets user input
                String exerciseName = nameField.getText();
                String date = String.format("%s/%s/%s", yearField.getText(), monthField.getText(), dayField.getText());

                // finds the session
                WorkoutSession session = logbook.getSessionByDate(date);
                if (session == null) {
                    JOptionPane.showMessageDialog(dialog, "No workout session found for date: " + date);
                    return;
                }

                // finds the exercise within the session
                Exercise exerciseToUpdate = null;
                for (Exercise ex : session.getExercises()) {
                    if (ex.getExerciseName().equalsIgnoreCase(exerciseName)) {
                        exerciseToUpdate = ex;
                        break;
                    }
                }

                // opens the update dialog if found, otherwise show error
                if (exerciseToUpdate != null) {
                    // We found it! Pass both the session and the exercise to the next step
                    updateOptions(session, exerciseToUpdate);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Exercise '" + exerciseName + "' not found on this date.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter vaild numbers for date:)");
            }
        });

        return saveButton;
    }
    
    /*
     * REQUIRES: session != null, exercise != null
     * MODIFIES: session, exercise
     * EFFECTS: open a new window for updating the found exercise's info
     */
    private void updateOptions(WorkoutSession session, Exercise exercise) {
        JDialog dialog = createDialog(parentFrame, "Update Exercise: Step 2 (Edit)", 400, 400);

        JPanel updateExercisePanel = new JPanel();
        updateExercisePanel.setLayout(new GridLayout(0, 1));
        updateExercisePanel.setBorder(new EmptyBorder(10, 5, 10, 5));

        JComboBox<String> updateFieldComboBox = new JComboBox<>(labels);
        
        updateExercisePanelWithFields(updateExercisePanel, updateFieldComboBox, session, exercise);

        JPanel datePanel = new JPanel(new GridLayout(1, 3));
        datePanel.add(yearField);
        datePanel.add(monthField);
        datePanel.add(dayField);
    
        updateExercisePanel.add(datePanel);

        updateFieldComboBoxEventHandler(updateFieldComboBox, dialog, updateExercisePanel);

        // sets up the buttons
        JPanel buttonPanel = new JPanel();
        JButton updateButton = new JButton("Update");
        JButton cancelButton = createCancelButton(dialog, "Cancel");

        // This helper adds the final update logic to the button
        updateEventHandler(updateButton, updateFieldComboBox, session, exercise, dialog);

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        dialog.add(updateExercisePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    /*
     * REQUIRES: updateFieldComboBox != null, dialog != null
     * MODIFIES: this
     * EFFECTS: adds event handler to the combo box to visualize the input field by the option users choose
     */
    private void updateFieldComboBoxEventHandler(JComboBox<String> updateFieldComboBox, JDialog dialog, JPanel updateExercisePanel) {
        updateFieldComboBox.addActionListener(e -> {
            String selected = (String) updateFieldComboBox.getSelectedItem();
            nameField.setVisible("Exercise Name".equals(selected));
            muscleComboBox.setVisible("Muscle Type".equals(selected));
            weightField.setVisible("Weight (kg)".equals(selected));
            repsField.setVisible("Number of Reps".equals(selected));
            setsField.setVisible("Number of Sets".equals(selected));
            boolean isDate = "Date yyyy/mm/dd".equals(selected);
            yearField.setVisible(isDate);
            monthField.setVisible(isDate);
            dayField.setVisible(isDate);
            updateExercisePanel.revalidate();
            updateExercisePanel.repaint();
            dialog.pack();
        });
    }

    /*
     * REQUIRES: all params != null
     * MODIFIES: session, exercise
     * EFFECTS: updates the selected field in the exercise or session with the new input value
     */
    private void updateEventHandler(JButton updateButton, JComboBox<String> updateFieldComboBox, 
                                            WorkoutSession session, Exercise exercise, JDialog dialog) {
        updateButton.addActionListener(e -> {
            try {
                String selected = (String) updateFieldComboBox.getSelectedItem();
                if ("Exercise Name".equals(selected)) {
                    exercise.setExerciseName(nameField.getText());
                } else if ("Muscle Type".equals(selected)) {
                    exercise.setMuscleType((Muscles) muscleComboBox.getSelectedItem());
                } else if ("Weight (kg)".equals(selected)) {
                    exercise.setWeightLifted(Integer.parseInt(weightField.getText()));
                } else if ("Number of Reps".equals(selected)) {
                    exercise.setNumReps(Integer.parseInt(repsField.getText()));
                } else if ("Number of Sets".equals(selected)) {
                    exercise.setNumSets(Integer.parseInt(setsField.getText()));
                } else if ("Date yyyy/mm/dd".equals(selected)) {
                    String updatedDate = String.format("%s/%s/%s", 
                                            yearField.getText(), monthField.getText(), dayField.getText());
                    session.setDate(updatedDate);
                }
                // Show the success confirmation popup
                JOptionPane.showMessageDialog(dialog, "Exercise updated successfully!");
                
                // Close edit dialog immediately so it doesn't linger on screen
                dialog.dispose();
                
                // Re-run the main log assembly method to clear the old text area
                // and re-render the logs with your newly updated values!
                displayAllLogs();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input. Please check your values!");
            }
        });
    }

    /*
     * REQUIRES: all params != null
     * MODIFIES: this
     * EFFECTS: adds fields to update an exercise and hide all input fields initially
     */
    private void updateExercisePanelWithFields(JPanel updateExercisePanel, JComboBox<String> updateFieldComboBox, 
                                                    WorkoutSession session, Exercise exercise) {
        
        // creates components and populate with existing data
        nameField = new JTextField(exercise.getExerciseName());
        muscleComboBox = createMuscleCombo();
        muscleComboBox.setSelectedItem(exercise.getMuscleType());
        weightField = new JTextField(String.valueOf(exercise.getWeightLifted()));
        repsField = new JTextField(String.valueOf(exercise.getNumReps()));
        setsField = new JTextField(String.valueOf(exercise.getNumSets()));
        
        String[] dateParts = session.getDate().split("/");
        yearField = new JTextField(dateParts[0]);
        monthField = new JTextField(dateParts[1]);
        dayField = new JTextField(dateParts[2]);

        // adds components to the panel
        updateExercisePanel.add(new Label("Choose Fields to Update:"));
        updateExercisePanel.add(updateFieldComboBox);
        updateExercisePanel.add(muscleComboBox);
        updateExercisePanel.add(nameField);
        updateExercisePanel.add(weightField);
        updateExercisePanel.add(repsField);
        updateExercisePanel.add(setsField);

        nameField.setVisible(false);
        muscleComboBox.setVisible(false);
        weightField.setVisible(false);
        repsField.setVisible(false);
        setsField.setVisible(false);
        yearField.setVisible(false);
        monthField.setVisible(false);
        dayField.setVisible(false);
    }

    /*
     * MODIFIES: this
     * EFFECTS: iterates through all logs and display the details in the main panel
     */
    private void displayAllLogs() {
        createDisplayLog();
        logDisplay.setText(""); // clear the display

        List<WorkoutSession> sessions = logbook.getAllSessions();

        if (sessions.isEmpty()) {
            logDisplay.setText("No exercises have been logged yet.");
        } else {
            // This is your updated loop that calculates and displays the session volume
            for (WorkoutSession session : sessions) {
                
                // 1. Calculate aggregate session volume manually
                int totalSessionVolume = 0;
                for (Exercise ex : session.getExercises()) {
                    totalSessionVolume += ex.getTotalVolume();
                }

                // 2. Add the polished header displaying BOTH the Date and the Total Session Volume
                logDisplay.append("\n===================================\n");
                logDisplay.append("    DATE: " + session.getDate() + "\n");
                logDisplay.append("    TOTAL VOLUME: " + totalSessionVolume + " kg\n");
                logDisplay.append("===================================\n");

                // 3. Print out each individual exercise inside this session
                List<Exercise> exercises = session.getExercises();
                if (exercises.isEmpty()) {
                    logDisplay.append("  (Rest Day / No exercises logged)\n");
                } else {
                    for (Exercise ex : exercises) {
                        displayLog(ex, session.getDate(), "Exercise");
                    }
                }
            }
        }
    }

    /*
     * REQUIRES: sessions != null, title != null
     * MODIFIES: this
     * EFFECTS: Display a *filtered* list of sessions in the main display
     */
    private void displayAllLogs(List<WorkoutSession> sessions, String title) {
        createDisplayLog();
        logDisplay.setText("");
        logDisplay.append("--- " + title + " ---\n");
        if (sessions.isEmpty()) {
            logDisplay.append("No workout found matching this filter.");
            return;
        }

        for (WorkoutSession session : sessions) {
            logDisplay.append("\n--- " + session.getDate() + " --- \n");
            if (session.getExercises().isEmpty()) {
                logDisplay.append("  (No exercises for this session)\n");
            } else {
                for (Exercise ex : session.getExercises()) {
                    displayLog(ex, session.getDate(), "Exercise");
                }
            }
        }
    }
    /*
     * MODIFIES: this
     * EFFECTS: gives options to users for exercise to be filtered by date or muscle type
     *          and sets up event handlers for filtering
     */
    private void filteredLog() {
        createDisplayLog();
        JDialog dialog = createDialog(parentFrame, "Filter log", 400, 75);

        JPanel buttonPanel = new JPanel();
        JButton dateButton = new JButton("Filtered by Date");
        JButton exerciseMuscleTypeButton = new JButton("Filtered by Muscle Type");

        buttonPanel.add(dateButton);
        buttonPanel.add(exerciseMuscleTypeButton);
        
        filteredByDateEventHandler(dateButton, dialog);
        filteredByMuscleTypeEventHandler(exerciseMuscleTypeButton, dialog);

        dialog.add(buttonPanel);

        centreOnScreen();
        dialog.setVisible(true);
        dialog.pack();
    }

    /*
     * REUQIRES: dataButton != null
     * MODIFIES: this
     * EFFECTS: pops up a window to filter exercises by date
     */
    private void filteredByDateEventHandler(JButton dateButton, JDialog parentDialog) {
        dateButton.addActionListener(e -> {
        
            JDialog subDialog = new JDialog(parentDialog, "Filtered By Date", true); 
            subDialog.setSize(400, 150);

            JPanel datePanel = createDatePanel();
            JButton filterButton = new JButton("filter");
            JButton cancelButton = createCancelButton(subDialog, "cancel");
            
            filteredByDateEventHandlerHelper(filterButton, subDialog);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(filterButton);
            buttonPanel.add(cancelButton);

            subDialog.add(datePanel, BorderLayout.CENTER);
            subDialog.add(buttonPanel, BorderLayout.SOUTH);

            subDialog.pack();
            subDialog.setLocationRelativeTo(null);
            subDialog.setVisible(true);
        });
    }

    /*
     * REQURIES: filterButton and subDialog != null
     * MODIFIES: this
     * EFFECTS: filters and displays exercises by the inputted date using logbook
     */
    private void filteredByDateEventHandlerHelper(JButton filterButton, JDialog subDialog) {
        filterButton.addActionListener(event -> {
            try {
                String date = String.format("%s/%s/%s", 
                                        yearField.getText(), monthField.getText(), dayField.getText());

                List<WorkoutSession> filteredSessions = logbook.filterSessionsByDate(date);

                displayAllLogs(filteredSessions, "Workouts on " + date);
                subDialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(subDialog, "Invalid data format. Please try again.");
            }
        });
    }

    /*
     * REUQIRES: muscleTypeButton != null
     * MODIFIES: this
     * EFFECTS: pops up a window to filter exercises by muscle type
     */
    private void filteredByMuscleTypeEventHandler(JButton muscleTypeButton, JDialog parentDialog) {
        muscleTypeButton.addActionListener(e -> {
            
            JDialog subDialog = new JDialog(parentDialog, "Filtered By Muscle Type", true); 
            subDialog.setSize(400, 150);
            subDialog.setLocationRelativeTo(parentDialog); // Center it over the first popup
            subDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
            // Setup ESC key close binding for this sub-dialog
            JRootPane rootPane = subDialog.getRootPane();
            InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            ActionMap actionMap = rootPane.getActionMap();
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CLOSE_DIALOG");
            actionMap.put("CLOSE_DIALOG", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) { subDialog.dispose(); }
            });

            JPanel muscleTypePanel = new JPanel();
            muscleTypePanel.setLayout(new GridLayout(2, 1));
            JLabel muscleTypeLabel = new JLabel("Select Muscle Type: ");
            muscleComboBox = createMuscleCombo();

            muscleTypePanel.add(muscleTypeLabel);
            muscleTypePanel.add(muscleComboBox);

            JButton filterButton = new JButton("filter");
            JButton cancelButton = createCancelButton(subDialog, "cancel");

            filteredByMuscleTypeEventHandlerHelper(filterButton, subDialog);
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(filterButton);
            buttonPanel.add(cancelButton);

            subDialog.add(muscleTypePanel, BorderLayout.CENTER);
            subDialog.add(buttonPanel, BorderLayout.SOUTH);

            subDialog.setVisible(true);
        });
    }

    /*
     * REQURIES: filterButton and subDialog != null
     * MODIFIES: this
     * EFFECTS: filteres and displays exercises by the inputted muscle type using logbook
     */
    private void filteredByMuscleTypeEventHandlerHelper(JButton filterButton, JDialog subDialog) {
        filterButton.addActionListener(event -> {
            Muscles selectedMuscleType = (Muscles) muscleComboBox.getSelectedItem();
            List<WorkoutSession> filteredSessions = logbook.filterSessionsByMuscle(selectedMuscleType);

            displayAllLogs(filteredSessions, "Workouts for " + selectedMuscleType.toString());
            subDialog.dispose();
        });
    }

    /*
     * MODIFIES: a file
     * EFFECTS: saves all logs from logbook to its desginated file
     */
    private void saveLogsToFile() {
        try {
            logbook.saveLogBook();
            JOptionPane.showMessageDialog(this, "Logs saved successfully!");
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Unable to write logs to the file: " + e.getMessage());
        }
    }
    
    /*
     * EFFECTS: loads logs from a file
     */
    private void loadLogsFromFile() {
        try {
            logbook.loadLogBook();
            displayAllLogs(); // refreshes the view after loading
            JOptionPane.showMessageDialog(this, "Logs successfully loaded!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to read from file: " + e.getMessage());
        }
    }

    /*
     * MODIFIES: muscleComboBox
     * EFFECTS: creates and returns a combo box with muscle types
     */
    private JComboBox<Muscles> createMuscleCombo() {
        muscleComboBox = new JComboBox<>();

        for (Muscles muscle : Muscles.values()) {
            muscleComboBox.addItem(muscle);
        }

        return muscleComboBox;
    }

    /*
     * MODIFIES: this
     * EFFECTS: centers the parent frame on the screen
     */
    private void centreOnScreen() {
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        parentFrame.setLocation((width - parentFrame.getWidth()) / 2, (height - parentFrame.getHeight()) / 2);
    }

}