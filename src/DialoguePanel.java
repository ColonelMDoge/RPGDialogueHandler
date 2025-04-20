import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;

public class DialoguePanel extends JPanel {
    JPanel characterPanel;
    JLabel leftCharacter, rightCharacter;
    JTextArea dialogue;
    RPGDialogueHandler rpgdh;
    private Image backgroundImage;

    public DialoguePanel() {
        this.setBackground(Color.WHITE);
        this.setLayout(new BorderLayout());

        characterPanel = new JPanel(new BorderLayout(250,10));
        characterPanel.setOpaque(false);

        leftCharacter = new JLabel();
        leftCharacter.setBorder(BorderFactory.createLineBorder(Color.BLACK,5,true));
        characterPanel.add(leftCharacter, BorderLayout.WEST);

        rightCharacter = new JLabel();
        rightCharacter.setBorder(BorderFactory.createLineBorder(Color.BLACK,5,true));
        characterPanel.add(rightCharacter, BorderLayout.EAST);

        dialogue = new JTextArea(3,0);
        dialogue.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        dialogue.setFocusable(true);
        dialogue.setLineWrap(true);
        dialogue.setWrapStyleWord(true);
        dialogue.setHighlighter(null);
        dialogue.setEditable(false);
        dialogue.setCaretColor(this.getBackground());
        dialogue.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 5), BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        characterPanel.add(dialogue,BorderLayout.SOUTH);
        this.add(characterPanel,BorderLayout.SOUTH);

    }

    public void setBackgroundImage(Image image) {
        backgroundImage = image;
    }

    public Insets getInsets() {
        return new Insets(40,50,25,50);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int imageWidth = backgroundImage.getWidth(this);
        int imageHeight = backgroundImage.getHeight(this);

        double scale = Math.max((double) panelWidth / imageWidth, (double) panelHeight / imageHeight);
        int scaledWidth = (int) (imageWidth * scale);
        int scaledHeight = (int) (imageHeight * scale);

        int x = (panelWidth - scaledWidth) / 2;
        int y = (panelHeight - scaledHeight) / 2;
        g.drawImage(backgroundImage, x, y, scaledWidth, scaledHeight, this);
    }

    public void run() {
        rpgdh = new RPGDialogueHandler(this, leftCharacter, rightCharacter, dialogue);
        rpgdh.setResourcePath("src/resources");
        rpgdh.setDialogueKey('Z');
        rpgdh.setDialogueScrollSound("Scroll.wav");
        rpgdh.loadDialogue("SampleDialogue.txt");
        rpgdh.runDialogue();
    }
}