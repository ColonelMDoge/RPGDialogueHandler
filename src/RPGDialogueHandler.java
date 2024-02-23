import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.Timer;
import java.util.*;
import java.util.concurrent.CountDownLatch;
public class RPGDialogueHandler implements KeyListener {
    RPGDialogueHandler(JPanel dp, JLabel left, JLabel right, JTextArea dialogue) {
        this.dialoguePanel = dp;
        this.leftBox = left;
        this.rightBox = right;
        this.dialogueBox = dialogue;
        dialogue.addKeyListener(this);
    }
    private final Queue<String> dialogue = new LinkedList<>();
    private final List<List<String>> characterInformationList = new ArrayList<>();
    private final JPanel dialoguePanel;
    private final JLabel leftBox, rightBox;
    private List<String> charInfo;
    private CountDownLatch cdl;
    private JTextArea dialogueBox;
    private File voiceFile;
    private String fullSentence;
    private String directory;
    private char[] sentence;
    private short pointer = 0;

    private final Timer timer = new Timer(80, e -> {
        dialogueBox.setText(dialogueBox.getText() + sentence[pointer]);
        if (pointer == sentence.length - 1) {
            pointer = 0;
            stopTimer();
        } else {
            pointer++;
            checkForDelays();
        }
    });

    public void setResourcePath(String directory) {
        this.directory = directory + "/";
    }

    public void loadDialogue(String pathToResource) {
        try (BufferedReader br = new BufferedReader(new FileReader(directory + "dialogue/" + pathToResource))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                String[] info = line.split("\\|");
                characterInformationList.add(
                        new ArrayList<>() {{
                            addAll(Arrays.asList(info));
                        }}
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void runDialogue() {
        SwingWorker<Void, Void> sw = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (List<String> information : characterInformationList) {
                    charInfo = information;
                    dialogue.addAll(information.subList(6, information.size()));
                    cdl = new CountDownLatch(dialogue.size());
                    sayDialogue(charInfo.get(0), charInfo.get(1), charInfo.get(2), charInfo.get(3), charInfo.get(4), charInfo.get(5), dialogue.poll());
                    cdl.await();
                }
                return null;
            }
            @Override
            protected void done() {
                Timer timer = new Timer(5, e -> {
                    int leftCharacterX = leftBox.getX();
                    int rightCharacterX = rightBox.getX();
                    int dialogueY = dialogueBox.getY();
                    dialogueBox.setLocation(dialogueBox.getX(), dialogueY + 10);
                    leftBox.setLocation(leftCharacterX - 10, leftBox.getY());
                    rightBox.setLocation(rightCharacterX + 10, rightBox.getY());
                    if (dialogueY > dialoguePanel.getHeight()) {
                        dialogueBox.setVisible(false);
                        leftBox.setVisible(false);
                        rightBox.setVisible(false);
                        ((Timer) e.getSource()).stop();
                    }
                });
                timer.start();
            }
        };
        sw.execute();
    }

    private void sayDialogue(String characterName, String voiceFile, String facial, String position, String leftPosition, String rightPosition, String dialogue) {
        this.voiceFile = new File(directory + "sounds/" + voiceFile + ".wav");
        String image = directory + "characters/" + characterName.toLowerCase() + "/" + facial + ".png";
        fullSentence = dialogue;
        sentence = fullSentence.toCharArray();
        if (position.equals("left")) {
            leftBox.setIcon(new ImageIcon(image));
        } else if (position.equals("right")){
            rightBox.setIcon(new ImageIcon(image));
        }
        if ((Boolean.parseBoolean(leftPosition) && Boolean.parseBoolean(rightPosition))) {
            leftBox.setVisible(true);
            rightBox.setVisible(true);
        } else if (Boolean.parseBoolean(leftPosition)) {
            leftBox.setVisible(true);
            rightBox.setVisible(false);
        } else if (Boolean.parseBoolean(rightPosition)) {
            leftBox.setVisible(false);
            rightBox.setVisible(true);
        } else {
            leftBox.setVisible(false);
            rightBox.setVisible(false);
        }
        stopTimer();
        dialogueBox.setText("");
        timer.restart();
    }

    private void checkForDelays() {
        if (!(sentence[pointer] == ' ')) {
            try {
                AudioInputStream ais = AudioSystem.getAudioInputStream(voiceFile);
                Clip soundEffects = AudioSystem.getClip();
                soundEffects.open(ais);
                ((FloatControl) soundEffects.getControl(FloatControl.Type.MASTER_GAIN)).setValue(-5);
                soundEffects.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.out.println("Could not load audio file!");
            }
        }
        if (sentence[pointer] == '.' || sentence[pointer] == '!' || sentence[pointer] == '?') {
            timer.setDelay(750);
        } else if (sentence[pointer] == ',') {
            timer.setDelay(325);
        } else {
            timer.setDelay(45);
        }
    }

    private void stopTimer() {
        timer.stop();
        pointer = 0;
        timer.setDelay(45);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_Z) return;
        if (timer.isRunning()) {
            stopTimer();
            dialogueBox.setText(fullSentence);
        } else if (!dialogue.isEmpty()) {
            sayDialogue(charInfo.get(0), charInfo.get(1), charInfo.get(2), charInfo.get(3), charInfo.get(4), charInfo.get(5), dialogue.poll());
            cdl.countDown();
        } else {
            cdl.countDown();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}