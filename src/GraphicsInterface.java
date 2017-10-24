

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
    private JPanel statePanel, controlPanel, timePanel, topPanel, bottomPanel, redPanel, bluePanel, greenPanel,
            yellowPanel;

    private serialComm comm;
    private MessageWriter messageWriter;
    private MessageReader messageReader;
    private JComboBox comboCommPorts;
    private JTextArea console;
    private JSpinner redTeamScore, blueTeamScore, greenTeamScore, yellowTeamScore;
    private JCheckBox redTeamCheckBox, blueTeamCheckBox, greenTeamCheckBox, yellowTeamCheckBox;

    private JButton activateButton, startMatchButton, redTeamPestilence, blueTeamPestilence, greenTeamPestilence, yellowTeamPestilence;;
    private JLabel gameTime, redState, redButton, redPoints, blueState, blueButton, bluePoints, greenState, greenButton,
            greenPoints, yellowState, yellowButton, yellowPoints, redDisplayPoints, blueDisplayPoints,
            greenDisplayPoints, yellowDisplayPoints, timeDisplay;
    private boolean isConnected = false, matchStarted = false;
    private int teamsActive = 0, matchtime = 0, matchlength = 120000, teamSelection = 0;
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
                            redPanel.setBackground(new Color(196, 27, 22));
                            bottomPanel.add(redPanel);

                            redDisplayPoints = new JLabel("<html>Red Team Points: <br> 0</html>");
                            redDisplayPoints.setFont(new Font("Monospace", Font.BOLD, 24));
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
                            bluePanel.setBackground(new Color(0, 108, 147));
                            bottomPanel.add(bluePanel);

                            blueDisplayPoints = new JLabel("<html>Blue Team Points: <br> 0</html>");
                            blueDisplayPoints.setFont(new Font("Monospace", Font.BOLD, 24));
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
                            greenPanel.setBackground(new Color(0, 116, 111));
                            bottomPanel.add(greenPanel);

                            greenDisplayPoints = new JLabel("<html>Green Team Points: <br> 0</html>");
                            greenDisplayPoints.setFont(new Font("Monospace", Font.BOLD, 24));
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
                            yellowPanel.setBackground(new Color(254, 213, 53));
                            bottomPanel.add(yellowPanel);

                            yellowDisplayPoints = new JLabel("<html>Yellow Team Points: <br> 0</html>");
                            yellowDisplayPoints.setFont(new Font("Monospace", Font.BOLD, 24));
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
                                            fieldMatchSound.fadeSound();
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
                            fieldMatchSound.killSounds();
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
        statePanel.setLayout(new GridLayout(4,3));
        //c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.gridy = 2;
        //c.weighty = .5;
        mainFrame.add(statePanel, c);

        redState = new JLabel();
        redState.setText("Red Team Score:");
        statePanel.add(redState);

        redTeamScore = new JSpinner();
        statePanel.add(redTeamScore);

        redTeamPestilence = new JButton();
        redTeamPestilence.setText("Red Team Pestilence");
        statePanel.add(redTeamPestilence);
        redTeamPestilence.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        pestilenceSound = new AudioFieldState();
                        pestilenceSound.pestilenceSound();
                        redTeamScore.setValue((Integer) redTeamScore.getValue() - 10);
                        messageWriter.writeMessage(new TeamMessage(TeamMessage.Teams.Red_TEAM, 1, ((Integer) redTeamScore.getValue()).byteValue(),
                                0, 0, 0, 0, 0, 0x04));

                    }
                }
        );

        blueState = new JLabel();
        blueState.setText("Blue Team Score:");
        statePanel.add(blueState);

        blueTeamScore = new JSpinner();
        statePanel.add(blueTeamScore);

        blueTeamPestilence = new JButton();
        blueTeamPestilence.setText("Blue Team Pestilence");
        statePanel.add(blueTeamPestilence);
        blueTeamPestilence.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        pestilenceSound = new AudioFieldState();
                        pestilenceSound.pestilenceSound();
                        //messageWriter.writeMessage();
                    }
                }
        );

        greenState = new JLabel();
        greenState.setText("Green Team Score:");
        statePanel.add(greenState);

        greenTeamScore = new JSpinner();
        statePanel.add(greenTeamScore);

        greenTeamPestilence = new JButton();
        greenTeamPestilence.setText("Green Team Pestilence");
        statePanel.add(greenTeamPestilence);
        greenTeamPestilence.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        pestilenceSound = new AudioFieldState();
                        pestilenceSound.pestilenceSound();
                        //messageWriter.writeMessage();
                    }
                }
        );

        yellowState = new JLabel();
        yellowState.setText("Purple Team Score:");
        statePanel.add(yellowState);

        yellowTeamScore = new JSpinner();
        statePanel.add(yellowTeamScore);

        yellowTeamPestilence = new JButton();
        yellowTeamPestilence.setText("Purple Team Pestilence");
        statePanel.add(yellowTeamPestilence);
        yellowTeamPestilence.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        pestilenceSound = new AudioFieldState();
                        pestilenceSound.pestilenceSound();
                        //messageWriter.writeMessage();
                    }
                }
        );

        console = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(console,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //console.setWrapStyleWord(true);
        console.setEditable(false);
        console.setForeground(new Color(230, 89, 51));
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_END;
        c.weighty = 1;
        //c.gridheight = 40;
        c.gridy = 3;
        mainFrame.add(scrollPane, c);

        //statusLabel.setSize(350,100);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });

        mainFrame.setVisible(true);
        fieldPanel();


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

    private void fieldPanel(){
        scoreFrame = new JFrame("Field Control");
        scoreFrame.setSize(960,640);
        scoreFrame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();



        topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        c.fill = GridBagConstraints.BOTH;
        //c.anchor = GridBagConstraints.PAGE_START;
        c.weighty = 1;
        c.weightx = 1;
        c.gridheight = 3;
        c.gridy = 0;
        scoreFrame.add(topPanel, c);

        c = new GridBagConstraints();
        timeDisplay = new JLabel("00:00:000", SwingConstants.CENTER);
        timeDisplay.setFont(new Font("Monospace", Font.BOLD, 100));
        timeDisplay.setForeground(Color.BLACK);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth=2;
        c.weightx = 1;
        c.weighty = 0;
        topPanel.add(timeDisplay, c);

        c = new GridBagConstraints();
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(0, 4));
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_END;
        c.gridheight = 1;
        c.gridwidth = 40;
        c.weighty = .25;
        c.weightx = 0;
        c.gridy = 3;
        scoreFrame.add(bottomPanel, c);

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
            public void run() {

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
                    messageWriter.writeMessage(new TeamMessage(TeamMessage.Teams.Red_TEAM, (byte) 1,(byte) 0, (byte) 0,
                            (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0));
                    if (messageReader.getMessageReady()) {
                        byte[] data = messageReader.getMessage();
                        IMessage msg = MessageParser.parse(data);

                        if(msg instanceof PingMessage){
                            System.out.println(((PingMessage) msg).getPing());
                        }
                        if (msg instanceof TeamMessage){
                            switch (((TeamMessage) msg).getTeamsId().getValue()){
                                case 0:
                                    System.out.println("Update Red Team");
                                    //redButton.setText(String.format("Team Red Button State: %d",
                                    //        ((TeamMessage) msg).getTeamsActive()));
                                    //redPoints.setText(String.format("Team Red Points: %d",
                                    //        ((TeamMessage) msg).getTeamsPoints()));
                                    System.out.println(String.valueOf(((TeamMessage) msg).getTeamsPoints()));
                                    //redTeamScore.setValue(String.valueOf(((TeamMessage) msg).getTeamsPoints()));
                                    //redDisplayPoints.setText("<html>Red Team Points:<br>" + redTeamScore.getValue() + "<br></html>");
                                    System.out.println("Finish Update");
                                    break;
                                case 1:
                                    blueButton.setText(String.format("Team Blue Button State: %d",
                                            ((TeamMessage) msg).getTeamsActive()));
                                    bluePoints.setText(String.format("Team Blue Points: %d",
                                            ((TeamMessage) msg).getTeamsPoints()));
                                    blueDisplayPoints.setText("<html>Blue Team Points:<br>" + String.valueOf(((TeamMessage) msg).getTeamsPoints() + "</html>"));
                                    break;
                                case 2:
                                    greenButton.setText(String.format("Team Green Button State: %d",
                                            ((TeamMessage) msg).getTeamsActive()));
                                    greenPoints.setText(String.format("Team Green Points: %d",
                                            ((TeamMessage) msg).getTeamsPoints()));
                                    greenDisplayPoints.setText(String.format("\"<html>Yellow Team Points: <br> %d</html>\"",
                                            ((TeamMessage) msg).getTeamsPoints()));
                                    break;
                                case 3:
                                    yellowButton.setText(String.format("Team Yellow Button State: %d",
                                            ((TeamMessage) msg).getTeamsActive()));
                                    yellowPoints.setText(String.format("Team Yellow Points: %d",
                                            ((TeamMessage) msg).getTeamsPoints()));
                                    yellowDisplayPoints.setText(String.format("\"<html>Yellow Team Points: <br> %d</html>\"",
                                            ((TeamMessage) msg).getTeamsPoints()));
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
