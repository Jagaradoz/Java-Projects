import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class Calculator extends JFrame {
    public static int WIDTH = 275;

    private final Color DARK_BG = new Color(33, 33, 33);
    private final Color DARK_BUTTON_BG_1 = new Color(66, 66, 66);
    private final Color DARK_BUTTON_BG_2 = new Color(88, 88, 88);
    private final Color DARK_TEXT = Color.WHITE;
    private final Color DARK_PLACEHOLDER = new Color(180, 180, 180);

    private final Color LIGHT_BG = Color.WHITE;
    private final Color LIGHT_BUTTON_BG_1 = new Color(238, 238, 238);
    private final Color LIGHT_BUTTON_BG_2 = new Color(220, 220, 220);
    private final Color LIGHT_TEXT = Color.BLACK;
    private final Color LIGHT_PLACEHOLDER = Color.GRAY;

    private final JPanel mainPanel;
    private JPanel buttonsPanel;
    private JTextField displayBar;
    private JTextField displayPlaceHolderBar;
    private final String[] characters = {
            "C", "CE", "←", "%",
            "7", "8", "9", "÷",
            "4", "5", "6", "×",
            "1", "2", "3", "-",
            "0", ".", "=", "+"
    };

    private String num1;
    private String num2;
    private String result;
    private String operator;
    private boolean calculated;
    private boolean isDarkMode = false;

    private Font mainFont;
    public Calculator() {
        // RESET VALUES
        clearCalculator();

        // LOAD FONT
        loadFont();

        // SET MENU BAR
        setJMenuBar(calculatorMenuBar());

        // SET MAIN PANEL
        mainPanel = calculatorPanel();
        add(mainPanel);

        pack();
        setVisible(true);
        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("images/icon.png"))).getImage());
        setTitle("Calculator");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void switchTheme() {
        isDarkMode = !isDarkMode;

        // CHANGE MAIN PANEL BACKGROUND COLOR
        mainPanel.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);

        // CHANGE DISPLAY BAR AND DISPLAY PLACEHOLDER BAR BACKGROUND COLOR
        displayBar.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
        displayBar.setForeground(isDarkMode ? DARK_TEXT : LIGHT_TEXT);
        displayPlaceHolderBar.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
        displayPlaceHolderBar.setForeground(isDarkMode ? DARK_PLACEHOLDER : LIGHT_PLACEHOLDER);

        // CHANGE BUTTON BACKGROUND COLOR
        buttonsPanel.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
        for (int i = 0; i < buttonsPanel.getComponents().length; i++) {
            if (buttonsPanel.getComponent(i) instanceof JButton button) {
                if ((i + 1) % 4 == 0 || i < 4) {
                    button.setBackground(isDarkMode ? DARK_BUTTON_BG_2 : LIGHT_BUTTON_BG_2);
                } else {
                    button.setBackground(isDarkMode ? DARK_BUTTON_BG_1 : LIGHT_BUTTON_BG_1);
                }

                button.setForeground(isDarkMode ? DARK_TEXT : LIGHT_TEXT);
            }
        }

        // CHANGE MENU BAR BACKGROUND COLOR
        JMenuBar menuBar = getJMenuBar();
        menuBar.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            menu.setForeground(isDarkMode ? DARK_TEXT : LIGHT_TEXT);
            for (int j = 0; j < menu.getItemCount(); j++) {
                JMenuItem item = menu.getItem(j);
                if (item != null) {
                    item.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
                    item.setForeground(isDarkMode ? DARK_TEXT : LIGHT_TEXT);
                }
            }
        }


        repaint();
    }

    private void loadFont(){
        try {
            InputStream fontStream = getClass().getResourceAsStream("/fonts/Itim-Regular.ttf");
            if (fontStream == null) {
                throw new IOException("Font resource not found.");
            }

            mainFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(16f);
        } catch (IOException | FontFormatException e) {
            mainFont = new Font("Arial", Font.PLAIN, 16);
        }
    }

    private void resetNum() {
        if (num2.equals("0")) {
            // IF NUM2 IS 0, RESET NUM1
            num1 = "0";
            displayBar.setText(num1);

        } else {
            // RESET NUM2
            num2 = "0";
            displayBar.setText(num2);

        }
    }

    private void calculate() {
        Double number1DoubleValue = Double.parseDouble(num1);
        Double number2DoubleValue = Double.parseDouble(num2);

        switch (operator) {
            case "+":
                result = Double.toString(number1DoubleValue + number2DoubleValue);
                break;
            case "-":
                result = Double.toString(number1DoubleValue - number2DoubleValue);
                break;
            case "×":
                result = Double.toString(number1DoubleValue * number2DoubleValue);
                break;
            case "÷":
                result = Double.toString(number1DoubleValue / number2DoubleValue);
                break;
            case "%":
                result = Double.toString((number1DoubleValue / 100) * number2DoubleValue);
                break;
        }
    }

    private void clearCalculator() {
        num1 = "0";
        num2 = "0";
        result = "0";
        operator = "";
        calculated = false;

        if (displayBar != null && displayPlaceHolderBar != null) {
            displayBar.setText("0");
            displayPlaceHolderBar.setText("0");
        }
    }

    private void setNum2(String displayBarText) {
        num2 = displayBarText;
    }

    private void backspace(String displayBarText) {
        if (displayBarText.length() <= 1) {
            displayBar.setText("0");
        } else {
            displayBar.setText(displayBarText.substring(0, displayBarText.length() - 1));
        }
    }

    private void numberInput(String text, String displayBarText) {
        if (displayBarText.equals("0")) {
            displayBar.setText(text);
        } else {
            displayBar.setText(displayBarText + text);
        }
    }

    private void setNum1(String text, String displayBarText) {
        // ONLY INPUT NUMBER1 ONCE
        if (num1.equals("0")) {
            num1 = displayBarText;
        }

        // INPUT PLACEHOLDER BAR WHETHER CALCULATED OR NOT
        if (calculated) {
            result = getRemovedZeroDecimal(result);
            num1 = result;

            displayPlaceHolderBar.setText(result + " " + text);
        } else {
            displayPlaceHolderBar.setText(num1 + " " + text);
        }

    }

    private String getRemovedZeroDecimal(String number) {
        if (number.contains(".")) {
            if (number.substring(number.indexOf(".") + 1).equals("0")) {
                return number.substring(0, number.indexOf("."));
            }
        }

        return number;
    }

    private JMenuBar calculatorMenuBar() {
        //ASSIGN FONTS
        Font font14px = mainFont.deriveFont(14f);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(LIGHT_BG);
        menuBar.setBorder(new EmptyBorder(0, 0, 0, 0));

        // MENU ITEMS
        JMenu viewMenu = new JMenu("View");
        JMenu helpMenu = new JMenu("Help");

        // SET FONT
        viewMenu.setFont(mainFont);
        helpMenu.setFont(mainFont);

        // URL IMAGES
        URL lightModeUrl = getClass().getResource("/images/light-mode.png");
        URL nightModeUrl = getClass().getResource("/images/night-mode.png");

        // IMAGE INSTANCES
        ImageIcon lightIcon = null;
        ImageIcon darkIcon = null;

        if (lightModeUrl != null && nightModeUrl != null) {
            lightIcon = new ImageIcon(lightModeUrl);
            darkIcon = new ImageIcon(nightModeUrl);

            lightIcon = resizeIcon(lightIcon);
            darkIcon = resizeIcon(darkIcon);
        }

        // VIEW ITEMS
        JMenuItem whiteTheme = new JMenuItem("White Theme", lightIcon);
        JMenuItem darkTheme = new JMenuItem("Dark Theme", darkIcon);

        // HELP ITEMS
        JMenuItem sourceCode = new JMenuItem("Source Code");

        // SET FONT
        whiteTheme.setFont(font14px);
        darkTheme.setFont(font14px);
        sourceCode.setFont(font14px);

        // SET HAND CURSOR
        viewMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        helpMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        whiteTheme.setCursor(new Cursor(Cursor.HAND_CURSOR));
        darkTheme.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sourceCode.setCursor(new Cursor(Cursor.HAND_CURSOR));

        viewMenu.add(whiteTheme);
        viewMenu.addSeparator();
        viewMenu.add(darkTheme);

        helpMenu.add(sourceCode);

        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        whiteTheme.addActionListener(e -> {
            if (isDarkMode) {
                switchTheme();
            }
        });

        darkTheme.addActionListener(e -> {
            if (!isDarkMode) {
                switchTheme();
            }
        });

        sourceCode.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/Jagaradoz/Java-Projects"));
            } catch (IOException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        });

        return menuBar;
    }

    private JPanel calculatorPanel() {
        //ASSIGN FONTS
        Font font30px = mainFont.deriveFont(30f);

        // MAIN PANEL (Border Layout)
        JPanel panel = new JPanel();
        panel.setBackground(LIGHT_BG);
        panel.setLayout(new BorderLayout(0, 0));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // DISPLAY BAR (NUMBER)
        displayBar = new JTextField("0");
        displayBar.setEditable(false);
        displayBar.setBackground(LIGHT_BG);
        displayBar.setHorizontalAlignment(JLabel.RIGHT);
        displayBar.setCursor(null);
        displayBar.setFocusable(false);
        displayBar.setCaretPosition(0);
        displayBar.setHighlighter(null);
        displayBar.setPreferredSize(new Dimension(WIDTH, 50));
        displayBar.setFont(font30px);
        displayBar.setBorder(new EmptyBorder(0, 0, 0, 0));

        // DISPLAY PLACEHOLDER BAR (NUMBER)
        displayPlaceHolderBar = new JTextField("0");
        displayPlaceHolderBar.setForeground(Color.GRAY);
        displayPlaceHolderBar.setBackground(LIGHT_BG);
        displayPlaceHolderBar.setEditable(false);
        displayPlaceHolderBar.setHorizontalAlignment(JLabel.RIGHT);
        displayPlaceHolderBar.setCursor(null);
        displayPlaceHolderBar.setFocusable(false);
        displayPlaceHolderBar.setCaretPosition(0);
        displayPlaceHolderBar.setHighlighter(null);
        displayPlaceHolderBar.setPreferredSize(new Dimension(WIDTH, 25));
        displayPlaceHolderBar.setFont(font30px);
        displayPlaceHolderBar.setBorder(new EmptyBorder(0, 0, 0, 0));

        // BUTTONS PANEL
        buttonsPanel = new JPanel();
        buttonsPanel.setBackground(LIGHT_BG);
        buttonsPanel.setPreferredSize(new Dimension(WIDTH, 325));
        buttonsPanel.setLayout(new GridLayout(5, 4, 3, 3));

        for (int i = 0; i < characters.length; i++) {
            JButton button = getJButton(i);

            buttonsPanel.add(button);
            button.addActionListener(new ButtonListener());
        }

        panel.add(displayPlaceHolderBar, BorderLayout.NORTH);
        panel.add(displayBar, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private ImageIcon resizeIcon(ImageIcon icon) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    private JButton getJButton(int i) {
        //ASSIGN FONTS
        Font font24px = mainFont.deriveFont(24f);

        JButton button = new JButton(characters[i]);

        button.setBorder(null);
        button.setFont(font24px);
        button.setFocusable(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // SET SPECIFIC COLORS
        if ((i + 1) % 4 == 0 || i < 4) {
            button.setBackground(isDarkMode ? DARK_BUTTON_BG_2 : LIGHT_BUTTON_BG_2);
        } else {
            button.setBackground(isDarkMode ? DARK_BUTTON_BG_1 : LIGHT_BUTTON_BG_1);
        }
        return button;
    }

    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // TEXT FROM BUTTON
            // TEXT FROM DISPLAY BAR
            String text = e.getActionCommand();
            String displayBarText = displayBar.getText();

            try {
                if (text.charAt(0) >= '0' && text.charAt(0) <= '9') {
                    // INPUT NUMBER 0-9
                    numberInput(text, displayBarText);
                    calculated = false;

                } else if (text.equals(".") && !displayBarText.contains(".")) {
                    // INPUT DECIMAL POINT
                    displayBar.setText(displayBarText + text);
                    calculated = false;

                } else if (text.equals("+") || text.equals("-") || text.equals("×") || text.equals("÷") || text.equals("%")) {
                    // SET NUM1
                    setNum1(text, displayBarText);

                    // RESET AFTER INPUT OPERATOR
                    calculated = false;
                    operator = text;
                    displayBar.setText("0");

                } else if (text.equals("=")) {
                    // SET NUM2
                    setNum2(displayBarText);

                    // CALCULATE
                    calculate();

                    // DISPLAY RESULT
                    calculated = true;
                    displayBar.setText(getRemovedZeroDecimal(result));
                    displayPlaceHolderBar.setText(String.format("%s %s %s =", num1, operator, num2));

                } else if (text.equals("←")) {
                    // BACKSPACE
                    backspace(displayBarText);

                } else if (text.equals("C")) {
                    // CLEAR CALCULATOR
                    clearCalculator();

                } else if (text.equals("CE")) {
                    // RESET NUM1 OR NUM2
                    resetNum();
                }
            } catch (NumberFormatException _) {
                System.out.println("Can't format the number!");
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }

        }
    }

    public static void main(String[] args) {
        new Calculator();
    }
}