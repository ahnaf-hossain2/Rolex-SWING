import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent; // Add this import
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Displays a premium watch brand animation
 * with alternating text between ROLEX and SUBMARINER
 */
public class Test {
    // Resource paths
    private static final String IMAGE_PATH = "C:\\Users\\User\\Desktop\\Rolex SWING\\test.png";
    private static final String FONT_PATH = "C:\\Users\\User\\Desktop\\Rolex SWING\\xeolademoversionregular-8ogpd.otf";
    private static final String BACKGROUND_IMAGE_PATH = "C:\\Users\\User\\Desktop\\Rolex SWING\\background.jpg";

    // Visual constants
    private static final Color TEXT_COLOR = new Color(250, 250, 250);

    // Text constants
    private static final String ROLEX_TEXT = " ROLEX ";
    private static final String SUBMARINER_TEXT = " SUBMARINER ";

    // Animation settings
    private static final int ANIMATION_DELAY = 127; // milliseconds per character
    private static final int FADE_DURATION = 1500; // milliseconds for fade-in animation
    private static final int FADE_STEPS = 20; // number of steps in fade animation

    // UI components
    private final JFrame frame;
    private final JLabel textLabel;
    private Font customFont;

    // Font sizing
    private float baseFontSize = 145f; // Original font size
    private int baseWidth = 820;       // Original window width

    // Animation state
    private Timer animationTimer;
    private Timer fadeTimer;
    private boolean isTypingForward = true;
    private int currentCharIndex = 0;
    private boolean isRolexText = true;
    private String currentFullText = ROLEX_TEXT; // Start with ROLEX

    /**
     * Constructs and initializes the application
     */
    public Test() {
        // Initialize main frame
        frame = new JFrame("Premium Watch");

        // Initialize textLabel before any method calls
        textLabel = new JLabel(SUBMARINER_TEXT, JLabel.CENTER);

        // Set up UI components
        JPanel mainPanel = setupMainPanel();
        frame.add(mainPanel);

        // Configure and show frame
        configureFrame();

        // Start animation
        setupAnimationTimer();
    }

    /**
     * Creates and configures the main panel with all components
     */
    private JPanel setupMainPanel() {
        // Create main panel with background image instead of solid color
        BackgroundPanel panel = new BackgroundPanel(new ImageIcon(BACKGROUND_IMAGE_PATH).getImage(),
                                                  BackgroundPanel.SCALED, 0.5f, 0.5f);
        panel.setLayout(new BorderLayout());

        // Add image with fade-in effect
        FadingImageLabel imageLabel = createFadingImageLabel();
        panel.add(imageLabel, BorderLayout.CENTER);

        // Create and add text panel
        JPanel textPanel = createTextPanel();
        panel.add(textPanel, BorderLayout.NORTH);

        return panel;
    }

    /**
     * Creates the image label with the watch image and fade-in effect
     */
    private FadingImageLabel createFadingImageLabel() {
        ImageIcon image = new ImageIcon(IMAGE_PATH);
        FadingImageLabel imageLabel = new FadingImageLabel(image);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        return imageLabel;
    }

    /**
     * Creates and configures the text panel with the animated label
     */
    private JPanel createTextPanel() {
        // Load custom font - textLabel is already initialized in constructor
        loadCustomFont();

        textLabel.setForeground(TEXT_COLOR);

        // Create a fixed-height panel to contain the text label
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false); // Make panel transparent to show background image
        textPanel.add(textLabel, BorderLayout.CENTER);

        // Calculate and set fixed height using the longer text to ensure enough space
        int textHeight = textLabel.getPreferredSize().height;
        textPanel.setPreferredSize(new Dimension(0, textHeight));

        // Now empty the label for animation start
        textLabel.setText("");

        return textPanel;
    }

    /**
     * Loads the custom font for the application
     */
    private void loadCustomFont() {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_PATH));

            // Register the font with the graphics environment
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);

            textLabel.setFont(customFont.deriveFont(baseFontSize));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            // Fallback to system font if custom font fails to load
            customFont = new Font("Arial", Font.BOLD, 70);
            textLabel.setFont(customFont);
            baseFontSize = 70f;
        }
    }

    /**
     * Configures the main application frame
     */
    private void configureFrame() {
        frame.setSize(baseWidth, 740);

        // Add component listener to handle window resizing
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateFontSize();
            }
        });

        // Make frame visible
        frame.setLocationRelativeTo(null); // Center the frame on screen
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Updates font size based on window width
     */
    private void updateFontSize() {
        // Calculate new font size proportionally
        float newFontSize = baseFontSize * ((float)frame.getWidth() / baseWidth);
        textLabel.setFont(customFont.deriveFont(newFontSize));
    }

    /**
     * Sets up the timer for the typing animation
     */
    private void setupAnimationTimer() {
        animationTimer = new Timer(ANIMATION_DELAY, e -> updateAnimatedText());
        animationTimer.start();
    }

    /**
     * Updates the text for the animation effect, alternating between ROLEX and SUBMARINER
     */
    private void updateAnimatedText() {
        if (isTypingForward) {
            currentCharIndex++;
            if (currentCharIndex > currentFullText.length()) {
                // When we reach the end, start typing backward
                isTypingForward = false;
                currentCharIndex = currentFullText.length();
            }
        } else {
            currentCharIndex--;
            if (currentCharIndex < 0) {
                // When completely deleted, switch to the other text
                isRolexText = !isRolexText;
                currentFullText = isRolexText ? ROLEX_TEXT : SUBMARINER_TEXT;

                // Start typing forward again
                isTypingForward = true;
                currentCharIndex = 0;
            }
        }

        // Update the label text with the current substring
        textLabel.setText(currentFullText.substring(0, currentCharIndex));
    }

    /**
     * Panel with a background image that adjusts smartly
     */
    private class BackgroundPanel extends JPanel {
        public static final int SCALED = 0;
        public static final int TILED = 1;
        public static final int ACTUAL = 2;

        private Image image;
        private int style;
        private float alignmentX;
        private float alignmentY;

        public BackgroundPanel(Image image, int style, float alignmentX, float alignmentY) {
            this.image = image;
            this.style = style;
            this.alignmentX = alignmentX;
            this.alignmentY = alignmentY;
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (image == null) return;

            switch (style) {
                case SCALED:
                    drawScaled(g);
                    break;
                case TILED:
                    drawTiled(g);
                    break;
                case ACTUAL:
                    drawActual(g);
                    break;
            }
        }

        private void drawScaled(Graphics g) {
            int width = getWidth();
            int height = getHeight();
            g.drawImage(image, 0, 0, width, height, this);
        }

        private void drawTiled(Graphics g) {
            int width = getWidth();
            int height = getHeight();
            int imageWidth = image.getWidth(this);
            int imageHeight = image.getHeight(this);

            for (int x = 0; x < width; x += imageWidth) {
                for (int y = 0; y < height; y += imageHeight) {
                    g.drawImage(image, x, y, this);
                }
            }
        }

        private void drawActual(Graphics g) {
            int width = getWidth();
            int height = getHeight();
            int imageWidth = image.getWidth(this);
            int imageHeight = image.getHeight(this);

            int x = (int)(alignmentX * (width - imageWidth));
            int y = (int)(alignmentY * (height - imageHeight));

            g.drawImage(image, x, y, this);
        }
    }

    /**
     * Custom JLabel that shows an image with fade-in effect
     */
    private class FadingImageLabel extends JLabel {
        private float opacity = 0.0f;
        private ImageIcon originalIcon;
        private Timer fadeTimer;

        public FadingImageLabel(ImageIcon icon) {
            this.originalIcon = icon;
            // Start fully transparent
            setOpaque(false);

            // Start fade-in timer when added to container
            addHierarchyListener(e -> {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                    startFadeIn();
                }
            });
        }

        private void startFadeIn() {
            int delay = FADE_DURATION / FADE_STEPS;
            fadeTimer = new Timer(delay, e -> {
                opacity += 1.0f / FADE_STEPS;
                if (opacity >= 1.0f) {
                    opacity = 1.0f;
                    fadeTimer.stop();
                }
                repaint();
            });
            fadeTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            // Paint the transparent background
            super.paintComponent(g);

            if (originalIcon == null) return;

            // Create a composite with current opacity for the image
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.SrcOver.derive(opacity));

            // Draw the image
            Image img = originalIcon.getImage();
            int x = (getWidth() - originalIcon.getIconWidth()) / 2;
            int y = (getHeight() - originalIcon.getIconHeight()) / 2;
            g2d.drawImage(img, x, y, this);

            g2d.dispose();
        }
    }

    /**
     * Application entry point
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Test());
    }
}
