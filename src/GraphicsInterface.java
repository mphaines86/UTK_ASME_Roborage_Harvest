

/**
 * Created by Michael Haines on 1/1/2015.
 */

//import com.sun.prism.*;
import audio.AudioFieldState;
import io.MessageReader;
import io.serialComm;
import io.MessageWriter;
import javafx.embed.swing.JFXPanel;
import messaging.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.*;


public class GraphicsInterface {

    private JFrame mainFrame, scoreFrame;
    private JPanel statePanel, controlPanel, timePanel, topPanel, bracketPanel, teamPanel, coverPanel, bottomPanel, redPanel, bluePanel, greenPanel,
            yellowPanel;

    private JPanelWithBackground picturePanelWithBackground;
    private serialComm comm;
    private MessageWriter messageWriter;
    private MessageReader messageReader;
    private JComboBox comboCommPorts, redTeamNameBox, blueTeamNameBox, greenTeamNameBox, yellowTeamNameBox;
    private JTextArea console;
    private JTextField textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8,
            textField9, textField10, textField11, textField12, textField13, textField14, textField15, textField16;
    private JSpinner redTeamScore, blueTeamScore, greenTeamScore, yellowTeamScore;
    private JCheckBox redTeamCheckBox, blueTeamCheckBox, greenTeamCheckBox, yellowTeamCheckBox, pointsCheckBox;

    private JButton activateButton, startMatchButton, redTeamPestilence, blueTeamPestilence, greenTeamPestilence, yellowTeamPestilence;
    private JLabel gameTime, redState, redButton, redPoints, blueState, blueButton, bluePoints, greenState, greenButton,
            greenPoints, yellowState, yellowButton, yellowPoints, redDisplayPoints, blueDisplayPoints,
            greenDisplayPoints, yellowDisplayPoints, timeDisplay, backgroundLabel;
    private boolean isConnected = false, matchStarted = false;
    private int teamsActive = 0, matchtime = 0, matchlength = 180000, teamSelection = 0, counter = 0;
    private AudioFieldState fieldStartSound, fieldMatchSound, fieldEndSound, pestilenceSound;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);


    public GraphicsInterface(){
        comm = new serialComm();
        createGraphicInterface();
        updateDashboard();
        matchStarted = false;

        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JFXPanel(); // initializes JavaFX environment
                latch.countDown();
            }
        });
        try {
            latch.await();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        fieldMatchSound = new AudioFieldState();

    }

    public void createGraphicInterface(){

        teamNameLoader teams = new teamNameLoader();

        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e) {
            // handle exception
        }
        catch (ClassNotFoundException e) {
            // handle exception
        }
        catch (InstantiationException e) {
            // handle exception
        }
        catch (IllegalAccessException e) {
            // handle exception
        }

        comm.searchForPorts();
        redirectSystemStreams();

        mainFrame = new JFrame("Field Control");
        mainFrame.setSize(800,600);
        mainFrame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        timePanel = new JPanel();
        timePanel.setLayout(new GridLayout(1,1));
        c.fill = GridBagConstraints.EAST;
        c.insets = new Insets(5,0,0,0);
        c.anchor = GridBagConstraints.PAGE_START;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        mainFrame.add(timePanel, c);


        gameTime = new JLabel();
        gameTime.setText("Match Time: 00:00:000");
        timePanel.add(gameTime);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.insets = new Insets(20,0,0,0);
        c.gridy = 1;
        c.ipady = 10;
        mainFrame.add(controlPanel, c);

        activateButton = new JButton();
        activateButton.setText("Initialize");
        activateButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        if(!isConnected) {
                            startMatchButton.setEnabled(true);
                            comm.setPortname(comboCommPorts.getSelectedItem().toString());
                            comm.initialize();
                            comm.portConnect();

                            messageReader = new MessageReader(comm.getInput());
                            (new Thread (messageReader)).start();
                            messageWriter = new MessageWriter(comm.getOutput());
                            (new Thread (messageWriter)).start();

                            messageReader.setClose(false);
                            messageWriter.setClose(false);
                            activateButton.setText("Disconnect");
                            messageWriter.writeMessage(new PingMessage(1));

                            //updateDashboard();

                        }
                        else if (isConnected) {
                            startMatchButton.setEnabled(false);
                            messageWriter.writeMessage(new PingMessage(0));
                            messageReader.setClose(true);
                            messageWriter.setClose(true);
                            comm.close();
                            activateButton.setText("Initialize");
                            //scheduler.shutdown();


                        }
                    }
                }
        );
        controlPanel.add(activateButton);

        comboCommPorts = new JComboBox(comm.getPorts().toArray());
        controlPanel.add(comboCommPorts);

        redTeamCheckBox = new JCheckBox();
        redTeamCheckBox.setText("Red Team");
        redTeamCheckBox.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (redTeamCheckBox.isSelected()){

                            teamSelection |= 1;
                            messageWriter.writeMessage(new StartMessage((byte) 1, (byte) 0, (byte) teamSelection));

                            System.out.println("Red Team is Active");
                            redState.setText("Team Red State: Active");

                            teamsActive++;
                            bottomPanel.setLayout(new GridLayout(1, teamsActive));

                            redPanel = new JPanel();
                            redPanel.setLayout(new GridBagLayout());
                            redPanel.setBackground(new Color(196, 27, 22));
                            bottomPanel.add(redPanel);

                            redDisplayPoints = new JLabel("<html>Red Team</html>");
                            redDisplayPoints.setFont(new Font("Papyrus", Font.BOLD, 30));
                            redDisplayPoints.setForeground(Color.BLACK);
                            redPanel.add(redDisplayPoints);

                            scoreFrame.revalidate();
                            scoreFrame.repaint();

                        }
                        else{

                            teamSelection &= ~0x01;
                            messageWriter.writeMessage(new StartMessage((byte) 1, (byte) 0, (byte) teamSelection));

                            System.out.println("Red Team is Inactive");
                            redState.setText("Team Red State: Inactive");

                            redPanel.remove(redDisplayPoints);
                            bottomPanel.remove(redPanel);

                            teamsActive--;
                            bottomPanel.setLayout(new GridLayout(1, teamsActive));

                            scoreFrame.revalidate();
                            scoreFrame.repaint();
                        }
                    }
                }
        );
        controlPanel.add(redTeamCheckBox);


        blueTeamCheckBox = new JCheckBox();
        blueTeamCheckBox.setText("Blue Team");
        blueTeamCheckBox.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (blueTeamCheckBox.isSelected()){
                            System.out.println("Blue Team is Active");
                            blueState.setText("Team Blue State: Active");

                            teamSelection |= 1 << 1;
                            messageWriter.writeMessage(new StartMessage((byte) 1, (byte) 0, (byte) teamSelection));


                            teamsActive++;
                            bottomPanel.setLayout(new GridLayout(1, teamsActive));

                            bluePanel = new JPanel();
                            bluePanel.setLayout(new GridBagLayout());
                            bluePanel.setBackground(new Color(0, 108, 147));
                            bottomPanel.add(bluePanel);

                            blueDisplayPoints = new JLabel("<html>Blue Team</html>");
                            blueDisplayPoints.setFont(new Font("Papyrus", Font.BOLD, 30));
                            blueDisplayPoints.setForeground(Color.BLACK);
                            bluePanel.add(blueDisplayPoints);

                            scoreFrame.revalidate();
                            scoreFrame.repaint();

                        }
                        else{
                            System.out.println("Blue Team is Inactive");
                            blueState.setText("Team Blue State: Inactive");

                            teamSelection &= ~(1 << 1);
                            System.out.println((byte) teamSelection);

                            messageWriter.writeMessage(new StartMessage((byte) 1, (byte) 0, (byte) teamSelection));

                            bluePanel.remove(blueDisplayPoints);
                            bottomPanel.remove(bluePanel);

                            teamsActive--;
                            bottomPanel.setLayout(new GridLayout(1, teamsActive));

                            scoreFrame.revalidate();
                            scoreFrame.repaint();
                        }
                    }
                }
        );
        controlPanel.add(blueTeamCheckBox);

        greenTeamCheckBox = new JCheckBox();
        greenTeamCheckBox.setText("Green Team");
        greenTeamCheckBox.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (greenTeamCheckBox.isSelected()){
                            System.out.println("Green Team is Active");
                            greenState.setText("Team Green State: Active");

                            teamSelection |= 1 << 2;
                            messageWriter.writeMessage(new StartMessage((byte) 1, (byte) 0, (byte) teamSelection));

                            teamsActive++;
                            bottomPanel.setLayout(new GridLayout(1, teamsActive));


                            greenPanel = new JPanel();
                            greenPanel.setLayout(new GridBagLayout());
                            greenPanel.setBackground(new Color(0, 116, 111));
                            bottomPanel.add(greenPanel);

                            greenDisplayPoints = new JLabel("<html>Green Team</html>");
                            greenDisplayPoints.setFont(new Font("Papyrus", Font.BOLD, 30));
                            greenDisplayPoints.setForeground(Color.BLACK);
                            greenPanel.add(greenDisplayPoints);

                            scoreFrame.revalidate();
                            scoreFrame.repaint();
                        }
                        else{
                            System.out.println("Green Team is Inactive");
                            greenState.setText("Team Green State: Inactive");

                            teamSelection &= ~(1 << 2);
                            messageWriter.writeMessage(new StartMessage((byte) 1, (byte) 0, (byte) teamSelection));

                            greenPanel.remove(greenDisplayPoints);
                            bottomPanel.remove(greenPanel);

                            teamsActive--;
                            bottomPanel.setLayout(new GridLayout(1, teamsActive));

                            scoreFrame.revalidate();
                            scoreFrame.repaint();
                        }
                    }
                }
        );
        controlPanel.add(greenTeamCheckBox);

        yellowTeamCheckBox = new JCheckBox();
        yellowTeamCheckBox.setText("Yellow Team");
        yellowTeamCheckBox.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (yellowTeamCheckBox.isSelected()){
                            System.out.println("Yellow Team is Active");
                            yellowState.setText("Team Yellow State: Active");

                            teamSelection |= 1 << 3;
                            messageWriter.writeMessage(new StartMessage((byte) 1, (byte) 0, (byte) teamSelection));

                            teamsActive++;
                            bottomPanel.setLayout(new GridLayout(1, teamsActive));

                            yellowPanel = new JPanel();
                            yellowPanel.setLayout(new GridBagLayout());
                            yellowPanel.setBackground(new Color(174, 131, 254));
                            bottomPanel.add(yellowPanel);

                            yellowDisplayPoints = new JLabel("<html>Purple Team</html>");
                            yellowDisplayPoints.setFont(new Font("Papyrus", Font.BOLD, 30));
                            yellowDisplayPoints.setForeground(Color.BLACK);
                            yellowPanel.add(yellowDisplayPoints);

                            scoreFrame.revalidate();
                            scoreFrame.repaint();

                        }
                        else{
                            System.out.println("Yellow Team is Inactive");
                            yellowState.setText("Team Yellow State: Inactive");

                            teamSelection &= ~(1 << 3);
                            messageWriter.writeMessage(new StartMessage((byte) 1, (byte) 0, (byte) teamSelection));

                            yellowPanel.remove(yellowDisplayPoints);
                            bottomPanel.remove(yellowPanel);

                            teamsActive--;
                            bottomPanel.setLayout(new GridLayout(1, teamsActive));

                            scoreFrame.revalidate();
                            scoreFrame.repaint();

                        }
                    }
                }
        );
        controlPanel.add(yellowTeamCheckBox);

        pointsCheckBox = new JCheckBox();
        pointsCheckBox.setText("No Points");
        pointsCheckBox.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (pointsCheckBox.isSelected()){
                            redTeamScore.setEnabled(false);
                            blueTeamScore.setEnabled(false);
                            greenTeamScore.setEnabled(false);
                            yellowTeamScore.setEnabled(false);

                            redTeamPestilence.setEnabled(false);
                            blueTeamPestilence.setEnabled(false);
                            greenTeamPestilence.setEnabled(false);
                            yellowTeamPestilence.setEnabled(false);
                        }
                        else {
                            redTeamScore.setEnabled(true);
                            blueTeamScore.setEnabled(true);
                            greenTeamScore.setEnabled(true);
                            yellowTeamScore.setEnabled(true);

                            redTeamPestilence.setEnabled(true);
                            blueTeamPestilence.setEnabled(true);
                            greenTeamPestilence.setEnabled(true);
                            yellowTeamPestilence.setEnabled(true);

                        }
                    }
                }
        );
        controlPanel.add(pointsCheckBox);

        startMatchButton = new JButton();
        startMatchButton.setText("Start Match");
        startMatchButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(!matchStarted) {
                            System.out.println("Match is starting!");
                            startMatchButton.setEnabled(false);
                            activateButton.setEnabled(false);
                            comboCommPorts.setEnabled(false);
                            redTeamCheckBox.setEnabled(false);
                            blueTeamCheckBox.setEnabled(false);
                            greenTeamCheckBox.setEnabled(false);
                            yellowTeamCheckBox.setEnabled(false);

                            redTeamScore.setValue(0);
                            blueTeamScore.setValue(0);
                            greenTeamScore.setValue(0);
                            yellowTeamScore.setValue(0);

                            messageWriter.writeMessage(new StartMessage((byte) 1, (byte) 0, (byte) teamSelection));

                            fieldStartSound = new AudioFieldState();
                            fieldStartSound.matchCountdown();


                            Timer timer = new Timer(50, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    matchtime = 0;
                                    messageWriter.writeMessage(new StartMessage((byte) 0, (byte) 1,(byte) 0));
                                    matchStarted = true;
                                    fieldMatchSound.matchMusic();
                                    startMatchButton.setEnabled(true);
                                }
                            });
                            timer.setRepeats(false);
                            timer.setInitialDelay(3000);
                            timer.start();
                            final Timer timer_repeat = new Timer(100, null);

                            timer_repeat.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if(!matchStarted) {
                                        messageWriter.writeMessage(new StartMessage((byte) 0, (byte) 0,(byte) 0));
                                        timer_repeat.stop();
                                    }
                                    else {
                                        matchtime += 100;
                                        //System.out.println(matchtime);
                                        int timeleft = matchlength - matchtime;
                                        if (timeleft <= 0) {
                                            fieldEndSound = new AudioFieldState();
                                            fieldEndSound.endSound();
                                            fieldMatchSound.fadeOutSound();
                                            matchStarted = false;

                                            redTeamCheckBox.setEnabled(true);
                                            blueTeamCheckBox.setEnabled(true);
                                            greenTeamCheckBox.setEnabled(true);
                                            yellowTeamCheckBox.setEnabled(true);
                                            activateButton.setEnabled(true);
                                            comboCommPorts.setEnabled(true);
                                            startMatchButton.setText("Start Match");
                                        }
                                        int minute = timeleft / 60000;
                                        int second = (int) ((timeleft % 60000) * .001);
                                        int millis = timeleft % 1000;
                                        //System.out.println(timeleft);
                                        timeDisplay.setText(String.format("%02d:%02d:%03d", minute, second, millis));
                                        gameTime.setText(String.format("Match Time: %02d:%02d:%03d", minute, second, millis));
                                    }
                                }
                            });
                            timer_repeat.setInitialDelay(3000);
                            timer_repeat.start();

                            startMatchButton.setText("Abort Match");
                        }
                        else{
                            matchStarted = false;
                            System.out.println("Match is Aborting!");
                            //fieldMatchSound.killSounds();
                            fieldEndSound = new AudioFieldState();
                            fieldEndSound.abortSound();
                            fieldMatchSound.fadeOutSound();
                            messageWriter.writeMessage(new StartMessage((byte) 0, (byte) 1, (byte) 0));
                            matchtime = 120000;
                            startMatchButton.setText("Start Match");

                            redTeamCheckBox.setEnabled(true);
                            blueTeamCheckBox.setEnabled(true);
                            greenTeamCheckBox.setEnabled(true);
                            yellowTeamCheckBox.setEnabled(true);
                            activateButton.setEnabled(true);
                            comboCommPorts.setEnabled(true);

                        }

                    }
                }
        );
        startMatchButton.setEnabled(false);
        controlPanel.add(startMatchButton);

        statePanel = new JPanel();
        statePanel.setLayout(new GridBagLayout());
        //c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.gridy = 2;
        //c.weighty = .5;
        mainFrame.add(statePanel, c);

        redState = new JLabel();
        redState.setText("Red Team Active:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.gridy = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 0);
        c.ipadx = 50;
        c.ipady = 2;
        statePanel.add(redState, c);

        redTeamScore = new JSpinner();
        c.gridy = 0;
        c.gridx = 2;
        statePanel.add(redTeamScore, c);
        redTeamScore.addChangeListener(
                new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        //if(!matchStarted){
                            redDisplayPoints.setText("<html>Red Team Points:<br>" + redTeamScore.getValue() + "<br></html>");
                            messageWriter.writeMessage(new TeamMessage(TeamMessage.Teams.Red_TEAM, 1, ((Integer) redTeamScore.getValue()).byteValue(),
                                    0, 0, 0, 0, 0, 0x04));
                        //}
                    }
                }
        );

        redTeamPestilence = new JButton();
        redTeamPestilence.setText("Red Team Pestilence");
        c.gridx = 3;
        statePanel.add(redTeamPestilence, c);
        redTeamPestilence.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        pestilenceSound = new AudioFieldState();
                        pestilenceSound.pestilenceSound();
                        redTeamScore.setValue((Integer) redTeamScore.getValue() - 20);
                        messageWriter.writeMessage(new TeamMessage(TeamMessage.Teams.Red_TEAM, 1, ((Integer) redTeamScore.getValue()).byteValue(),
                                0, 0, 0, 0, 0, 0x04));

                    }
                }
        );

        redTeamNameBox = new JComboBox(teams.getTeamName().toArray());
        c.gridx = 4;
        statePanel.add(redTeamNameBox, c);
        redTeamNameBox.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String displayText = "<html><b>Team Name:</b> &emsp &#160 " + teams.getTeamName(redTeamNameBox.getSelectedIndex())
                                +"<br>School Name: &emsp " + teams.getSchoolName(redTeamNameBox.getSelectedIndex())
                                +"<br>Robot Name: &emsp &#160 " + teams.getRobotName(redTeamNameBox.getSelectedIndex())
                                +"<br>Robot Weight: &emsp " + teams.getRobotWeight(redTeamNameBox.getSelectedIndex());
                        if (!pointsCheckBox.isSelected()){
                            displayText+= "<br>Points: &emsp &emsp &emsp &emsp " + redTeamScore.getValue() + "<br></html>";
                        }
                        else
                            displayText+="</html>";
                        redDisplayPoints.setText(displayText);
                    }
                }
        );

        blueState = new JLabel();
        blueState.setText("Blue Team Score:");
        c.gridy = 1;
        c.gridx = 0;
        statePanel.add(blueState, c);

        blueTeamScore = new JSpinner();
        c.gridx = 2;
        statePanel.add(blueTeamScore, c);
        blueTeamScore.addChangeListener(
                new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        //if(!matchStarted){
                            blueDisplayPoints.setText("<html>Blue Team Points:<br>" + blueTeamScore.getValue() + "<br></html>");
                            messageWriter.writeMessage(new TeamMessage(TeamMessage.Teams.BLUE_TEAM, 1, ((Integer) blueTeamScore.getValue()).byteValue(),
                                    0, 0, 0, 0, 0, 0x04));
                        //}
                    }
                }
        );

        blueTeamPestilence = new JButton();
        blueTeamPestilence.setText("Blue Team Pestilence");
        c.gridx = 3;
        statePanel.add(blueTeamPestilence, c);
        blueTeamPestilence.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        pestilenceSound = new AudioFieldState();
                        pestilenceSound.pestilenceSound();
                        blueTeamScore.setValue((Integer) blueTeamScore.getValue() - 20);
                        messageWriter.writeMessage(new TeamMessage(TeamMessage.Teams.BLUE_TEAM, 1, ((Integer) blueTeamScore.getValue()).byteValue(),
                                0, 0, 0, 0, 0, 0x04));
                        //messageWriter.writeMessage();
                    }
                }
        );

        blueTeamNameBox = new JComboBox(teams.getTeamName().toArray());
        c.gridx = 4;
        statePanel.add(blueTeamNameBox, c);
        blueTeamNameBox.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String displayText = "<html><b>Team Name:</b> &emsp &#160 " + teams.getTeamName(blueTeamNameBox.getSelectedIndex())
                                +"<br>School Name: &emsp " + teams.getSchoolName(blueTeamNameBox.getSelectedIndex())
                                +"<br>Robot Name: &emsp &#160 " + teams.getRobotName(blueTeamNameBox.getSelectedIndex())
                                +"<br>Robot Weight: &emsp " + teams.getRobotWeight(blueTeamNameBox.getSelectedIndex());
                        if (!pointsCheckBox.isSelected()){
                            displayText+= "<br>Points: &emsp &emsp &emsp &emsp " + blueTeamScore.getValue() + "<br></html>";
                        }
                        else
                            displayText+="</html>";
                        blueDisplayPoints.setText(displayText);
                    }
                }
        );

        greenState = new JLabel();
        greenState.setText("Green Team Score:");
        c.gridy = 2;
        c.gridx = 0;
        statePanel.add(greenState, c);

        greenTeamScore = new JSpinner();
        c.gridx = 2;
        statePanel.add(greenTeamScore, c);
        greenTeamScore.addChangeListener(
                new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        //if(!matchStarted){
                            greenDisplayPoints.setText("<html>Green Team Points:<br>" + greenTeamScore.getValue() + "<br></html>");
                            messageWriter.writeMessage(new TeamMessage(TeamMessage.Teams.GREEN_TEAM, 1, ((Integer) greenTeamScore.getValue()).byteValue(),
                                    0, 0, 0, 0, 0, 0x04));
                        //}
                    }
                }
        );

        greenTeamPestilence = new JButton();
        greenTeamPestilence.setText("Green Team Pestilence");
        c.gridx = 3;
        statePanel.add(greenTeamPestilence, c);
        greenTeamPestilence.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        pestilenceSound = new AudioFieldState();
                        pestilenceSound.pestilenceSound();
                        greenTeamScore.setValue((Integer) greenTeamScore.getValue() - 20);
                        messageWriter.writeMessage(new TeamMessage(TeamMessage.Teams.GREEN_TEAM, 1, ((Integer) greenTeamScore.getValue()).byteValue(),
                                0, 0, 0, 0, 0, 0x04));
                        //messageWriter.writeMessage();
                    }
                }
        );

        greenTeamNameBox = new JComboBox(teams.getTeamName().toArray());
        c.gridx = 4;
        statePanel.add(greenTeamNameBox, c);
        greenTeamNameBox.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String displayText = "<html><b>Team Name:</b> &emsp &#160 " + teams.getTeamName(greenTeamNameBox.getSelectedIndex())
                                +"<br>School Name: &emsp " + teams.getSchoolName(greenTeamNameBox.getSelectedIndex())
                                +"<br>Robot Name: &emsp &#160 " + teams.getRobotName(greenTeamNameBox.getSelectedIndex())
                                +"<br>Robot Weight: &emsp " + teams.getRobotWeight(greenTeamNameBox.getSelectedIndex());
                        if (!pointsCheckBox.isSelected()){
                            displayText+= "<br>Points: &emsp &emsp &emsp &emsp " + greenTeamScore.getValue() + "<br></html>";
                        }
                        else
                            displayText+="</html>";
                        greenDisplayPoints.setText(displayText);
                    }
                }
        );

        yellowState = new JLabel();
        yellowState.setText("Purple Team Score:");
        c.gridy = 3;
        c.gridx = 0;
        statePanel.add(yellowState, c);

        yellowTeamScore = new JSpinner();
        c.gridx = 2;
        statePanel.add(yellowTeamScore, c);
        yellowTeamScore.addChangeListener(
                new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        //if(!matchStarted){
                            yellowDisplayPoints.setText("<html>Purple Team Points:<br>" + yellowTeamScore.getValue() + "<br></html>");
                            messageWriter.writeMessage(new TeamMessage(TeamMessage.Teams.YELLOW_TEAM, 1, ((Integer) yellowTeamScore.getValue()).byteValue(),
                                    0, 0, 0, 0, 0, 0x04));
                        //}
                    }
                }
        );

        yellowTeamPestilence = new JButton();
        yellowTeamPestilence.setText("Purple Team Pestilence");
        c.gridx = 3;
        statePanel.add(yellowTeamPestilence, c);
        yellowTeamPestilence.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        pestilenceSound = new AudioFieldState();
                        pestilenceSound.pestilenceSound();
                        yellowTeamScore.setValue((Integer) yellowTeamScore.getValue() - 20);
                        messageWriter.writeMessage(new TeamMessage(TeamMessage.Teams.YELLOW_TEAM, 1, ((Integer) yellowTeamScore.getValue()).byteValue(),
                                0, 0, 0, 0, 0, 0x04));
                        //messageWriter.writeMessage();
                    }
                }
        );

        yellowTeamNameBox = new JComboBox(teams.getTeamName().toArray());
        c.gridx = 4;
        statePanel.add(yellowTeamNameBox, c);
        yellowTeamNameBox.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String displayText = "<html><b>Team Name:</b> &emsp &#160 " + teams.getTeamName(yellowTeamNameBox.getSelectedIndex())
                                +"<br>School Name: &emsp " + teams.getSchoolName(yellowTeamNameBox.getSelectedIndex())
                                +"<br>Robot Name: &emsp &#160 " + teams.getRobotName(yellowTeamNameBox.getSelectedIndex())
                                +"<br>Robot Weight: &emsp " + teams.getRobotWeight(yellowTeamNameBox.getSelectedIndex());
                        if (!pointsCheckBox.isSelected()){
                            displayText+= "<br>Points: &emsp &emsp &emsp &emsp " + yellowTeamScore.getValue() + "<br></html>";
                        }
                        else
                            displayText+="</html>";
                        yellowDisplayPoints.setText(displayText);
                    }
                }
        );

        console = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(console,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //console.setWrapStyleWord(true);
        console.setEditable(false);
        console.setForeground(new Color(230, 41, 0));
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_END;
        c.weighty = 1;
        //c.gridheight = 40;
        c.gridy = 3;
        c.gridx = 0;
        mainFrame.add(scrollPane, c);

        //statusLabel.setSize(350,100);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });

        /*bracketPanel = new JPanel();
        bracketPanel.setLayout(new GridLayout(2, 2));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 200;
        //c.insets = new Insets(20,0,0,0);
        c.gridx = 1;
        c.ipadx = 200;
        mainFrame.add(bracketPanel, c);

        teamPanel = new JPanel();
        teamPanel.setLayout(new GridLayout(0, 16));
        bracketPanel.add(teamPanel);

        bracketPanel.add(teamPanel);
        textField1 = new JTextField("Team Name");
        teamPanel.add(textField1);*/


        mainFrame.setVisible(true);
        try {
            fieldPanel();
        } catch (IOException e) {
            e.printStackTrace();
        }


        /*
        Thread writerThread = new Thread(messageWriter);
        writerThread.start();

        refresh = new JButton();
        refresh.setText("Refresh Ports");
        refresh.setSize(130,50);
        refresh.setLocation(190, 50);
        refresh.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (initialize.getText().equals("Initialize")){
                            comm.searchForPorts();
                            commPorts.removeAllItems();
                            for (int i = 0; i < comm.getPorts().size(); i++){
                                commPorts.addItem(comm.getPorts().get(i));
                            }

                            controllerPorts.removeAllItems();
                            //searchForControllers();
                            for (String allTheController : allTheControllers) {
                                controllerPorts.addItem(allTheController);
                            }

                        }
                    }
                }
        );
        GraphicsInterfacePane.add(refresh);*/

        //setTitle("RoboSmokey 3000 Control Interface");
        //setSize(800,600);
        //setVisible(true);
    }

    private void fieldPanel() throws IOException {
        scoreFrame = new JFrame("Field Control");
        scoreFrame.setSize(960,640);
        JPanelWithBackground picturePanelWithBackground = new JPanelWithBackground(System.getProperty("user.dir") + "/src/n-sumowomen-a-20180501_v3.png");
        scoreFrame.getContentPane().add(picturePanelWithBackground);
        picturePanelWithBackground.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new GridBagLayout());
        c.fill = GridBagConstraints.HORIZONTAL;
        //c.anchor = GridBagConstraints.PAGE_START;
        c.weighty = 1;
        c.weightx = 1;
        c.gridheight = 3;
        c.gridy = 0;
        picturePanelWithBackground.add(topPanel, c);

        timeDisplay = new JLabel("00:00:000", SwingConstants.CENTER);
        timeDisplay.setOpaque(false);
        timeDisplay.setFont(new Font("Papyrus", Font.BOLD, 100));
        timeDisplay.setForeground(Color.BLACK);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_START;
        c.gridwidth=2;
        c.weightx = 1;
        c.weighty = 0;
        topPanel.add(timeDisplay, c);

        c = new GridBagConstraints();
        bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new GridLayout(0, 4));
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.LAST_LINE_START;
        c.gridheight = 1;
        c.gridwidth = 40;
        c.weighty = 0.1;
        c.weightx = 0;
        c.gridy = 3;
        picturePanelWithBackground.add(bottomPanel, c);

        scoreFrame.setVisible(true);

    }

    private void updateTextArea(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                console.append(text);
            }
        });
    }


    private void updateDashboard(){
        final Runnable dashboardUpdate = new Runnable() {
            @Override
            public void run(){
                if(!matchStarted) {
                    try {
                        //System.out.println("sleeping");
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                else {
                    //System.out.println("printing");
                    //messageWriter.writeMessage(new PingMessage(1));
                    //System.out.println(counter);
                    switch (counter%=4) {
                        case 0:
                            messageWriter.writeMessage(new TeamMessage(TeamMessage.Teams.Red_TEAM, (byte) 1,(byte) 0, (byte) 0,
                                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0));
                            break;
                        case 1:
                            messageWriter.writeMessage(new TeamMessage(TeamMessage.Teams.BLUE_TEAM, (byte) 1,(byte) 0, (byte) 0,
                                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0));
                            break;
                        case 2:
                            messageWriter.writeMessage(new TeamMessage(TeamMessage.Teams.GREEN_TEAM, (byte) 1,(byte) 0, (byte) 0,
                                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0));
                            break;
                        case 3:
                            messageWriter.writeMessage(new TeamMessage(TeamMessage.Teams.YELLOW_TEAM, (byte) 1,(byte) 0, (byte) 0,
                                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0));
                            break;
                    }

                    if (teamsActive == 2){
                        counter+=2;
                    }
                    else {
                        counter++;
                    }

                    if (messageReader.getMessageReady()) {
                        byte[] data = messageReader.getMessage();
                        IMessage msg = MessageParser.parse(data);

                        if(msg instanceof PingMessage){
                            System.out.println(((PingMessage) msg).getPing());
                        }
                        if (msg instanceof TeamMessage){
                            switch (((TeamMessage) msg).getTeamsId().getValue()){
                                case 0:
                                    redTeamScore.setValue(((TeamMessage) msg).getTeamsPoints());
                                    redDisplayPoints.setText("<html>Red Team Points:<br>" + String.valueOf(((TeamMessage) msg).getTeamsPoints()) + "<br></html>");
                                    break;
                                case 1:
                                    blueTeamScore.setValue(((TeamMessage) msg).getTeamsPoints());
                                    blueDisplayPoints.setText("<html>Blue Team Points:<br>" + String.valueOf(((TeamMessage) msg).getTeamsPoints()) + "<br></html>");
                                    break;
                                case 2:
                                    greenTeamScore.setValue(((TeamMessage) msg).getTeamsPoints());
                                    greenDisplayPoints.setText("<html>Green Team Points:<br>" + String.valueOf(((TeamMessage) msg).getTeamsPoints()) + "<br></html>");
                                    break;
                                case 3:
                                    yellowTeamScore.setValue(((TeamMessage) msg).getTeamsPoints());
                                    yellowDisplayPoints.setText("<html>Purple Team Points:<br>" + String.valueOf(((TeamMessage) msg).getTeamsPoints()) + "<br></html>");
                                    break;
                            }
                        }

                    }
                }
            }

        };

        //final ScheduledFuture<?> dashboardUpdater =
        scheduler.scheduleAtFixedRate(dashboardUpdate, 100, 100, TimeUnit.MILLISECONDS);

        /*if(matchStarted) {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    dashboardUpdater.cancel(true);
                }
            },0,TimeUnit.SECONDS);
        }*/
    }


    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                updateTextArea(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateTextArea(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        //System.setErr(new PrintStream(out, true));
    }


    /*private void writeMessage(IMessage msg){
        try {
            byte[] test = msg.getBytes();
            //for(int i = 0; i < msg.getBytes().length; i++){
                //int positive = test[i] & 0xff;
                //System.out.print(positive);
                //System.out.print(' ');
            //}
            //System.out.println(' ');
            serialComm.output.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public static void main(String[] args){

        GraphicsInterface graphics = new GraphicsInterface();
        //graphics.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
