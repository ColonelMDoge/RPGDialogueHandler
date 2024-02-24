# RPGDialogueHandler
> ### This free-to-use class allows you to implement dialogue scrolling capabilities into your Java project
## How to use it:
The RPGDialogueHandler class requires a JPanel, and must include two JLabels to represent the character sprite boxes, and one JTextArea to represent the dialogue.
## Example
An example implementation for setting the handler up would be as shown:
```java
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
class DialoguePanel extends JPanel {
    JPanel characterPanel;
    JLabel leftCharacter, rightCharacter;
    JTextArea dialogue;
    
    RPGDialogueHandler rpgdh;
    
    public DialoguePanel() {
        this.setBackground(Color.WHITE);
        this.setLayout(new BorderLayout());

        characterPanel = new JPanel(new BorderLayout(250,10));
        characterPanel.setBackground(Color.WHITE);

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
    public Insets getInsets() {
        return new Insets(40,50,25,50);
    }
    
    public void run() {
        rpgdh = new RPGDialogueHandler(this, leftCharacter, rightCharacter, dialogue);
        rpgdh.setResourcePath("src/resources");
        rpgdh.setDialogueKey('Z');
        rpgdh.loadDialogue("SampleDialogue.txt");
        rpgdh.runDialogue();
    }
    
}
```
Another way of implementing this would be directly intializing the class in the JPanel's end of its constructor and run its methods.
## How to use custom resources
First, in order to use custom resources, the hierarchy for the folder that contains everything should be:
1. resources
   - characters
     - character1
     - character2
       - image1
   - dialogue
   - sounds

Each character should be a subpackage inside the "characters" package, and the character's images should be in its respective folder. (.png files only!)

The dialogue text files should be located in the "dialogue" package.

Character's sound effects should be inside the "sounds" package. (.wav files only!)
## How to setup text file
For the RPGDialogueHandler to properly load character sprites and text into the program, you must create a valid text file. Below is a template with all explanations:
```
Name|Sound|Facial|Position|enableLeft|enableRight|Dialogue
Name|Sound|Facial|Position|enableLeft|enableRight|Dialogue
Name|Sound|Facial|Position|enableLeft|enableRight|Dialogue
```
1. Name: The character's name
2. Sound: The name of the sound file used without the extension
3. Facial: The name of the image file of the character without the extension
4. Position (Either left or right): Decides if the image will be placed on the left or the right JPanel box
5. enableLeft (Either true or false): Enables or disables left JPanel box
6. enableRight (Either true or false): Enables or disables right JPanel box
7. Dialogue: The line of dialogue that the character will say. Any consecutive text after the dialogue separated with the "|" delimiter will be counted as the next part of the dialogue after scrolling.

NOTE: The data before the dialogue MUST BE CASE SENSITIVE, as file names and such are determined by this data!
## How to use methods
###  setResourcePath()
The setResourcePath() method allows you to specify the package or folder path that all your resources will go into.
```java
public void setResourcePath(String directory) {
    this.directory = directory + "/";
}
```
###  setDialogueKey()
The setDialogueKey() method allows you to choose the letter on the keyboard that you want to use to scroll through dialogue.
```java
public void setDialogueKey(char key) {
    dialogueKey = Character.toUpperCase(key);
}
```
###  loadDialogue(String pathToResource)
The loadDialogue() method allows you to load your custom textfile that includes all information about characters and their dialogue.
```java
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
```
### runDialogue() 
The runDialogue() method will actually allow dialogue and character sprites to be shown. This should be the last method to be called.
```java
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
```
## Now you just learned how to successfuly setup the RPGDialogueHandler! Have fun with this class and thanks for using it!
