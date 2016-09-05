

/**
 * Created by Michael Haines on 1/1/2015.
 */

//import com.sun.prism.*;
import io.serialComm;
import io.MessageWriter;
import messaging.*;
//import net.java.games.input.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Time;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;


public class GraphicsInterface {

    private JFrame mainFrame, scoreFrame;
    private JPanel statePanel, controlPanel, timePanel, topPanel, bottomPanel, redPanel, bluePanel, greenPanel,
            yellowPanel;

    private serialComm comm;
    private MessageWriter messageWriter;
    //final io.MessageReader messageReader = new io.MessageReader();
    // final xboxControllerTest connectController = new xboxControllerTest();
    private JComboBox comboCommPorts;
    private JTextArea console;
    private JCheckBox redTeamCheckBox, blueTeamCheckBox, greenTeamCheckBox, yellowTeamCheckBox;
    private JButton activateButton, startMatchButton;
    private JLabel gameTime, redState, redButton, redPoints, blueState, blueButton, bluePoints, greenState, greenButton,
            greenPoints, yellowState, yellowButton, yellowPoints, redDisplayPoints, blueDisplayPoints,
            greenDisplayPoints, yellowDisplayPoints, timeDisplay;
    private String[] portList = {};
    private boolean isConnected = false;
    private boolean canceldashboard = false;
    private int teamsActive = 0;
    //SerialWorker initiateController;
    //inputControl xboxController;
    //Controller[] allControllers;
    //Controller control;
    private List<String> allTheControllers = new ArrayList<String>();
    int[] controllerLocation = new int[10];
    private boolean abuse = false;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public GraphicsInterface(){
        comm = new serialComm();
        messageWriter = new MessageWriter(comm.getOutput());
        createGraphicInterface();
        updateDashboard();
        canceldashboard = true;

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
                            comm.setPortname(comboCommPorts.getSelectedItem().toString());
                            comm.initialize();
                            comm.portConnect();

                            activateButton.setText("Disconnect");
                            messageWriter.writeMessage(new PingMessage(1));
                            isConnected = true;
                            canceldashboard = false;
                            //updateDashboard();

                        }
                        else if (isConnected) {
                            canceldashboard = true;
                            messageWriter.writeMessage(new PingMessage(0));
                            comm.close();
                            activateButton.setText("Initialize");
                            //scheduler.shutdown();
                            isConnected = false;

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
                            System.out.println("Red Team is Active");
                            redState.setText("Team Red State: Active");

                            teamsActive++;
                            bottomPanel.setLayout(new GridLayout(1, teamsActive));

                            redPanel = new JPanel();
                            redPanel.setBackground(Color.RED);
                            bottomPanel.add(redPanel);

                            redDisplayPoints = new JLabel("<html>Red Team Points: <br> 0</html>");
                            redDisplayPoints.setFont(new Font("Monospace", Font.BOLD, 24));
                            redDisplayPoints.setForeground(Color.BLACK);
                            redPanel.add(redDisplayPoints);

                            scoreFrame.revalidate();
                            scoreFrame.repaint();

                        }
                        else{
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

                            teamsActive++;
                            bottomPanel.setLayout(new GridLayout(1, teamsActive));

                            bluePanel = new JPanel();
                            bluePanel.setBackground(Color.BLUE);
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

                            teamsActive++;
                            bottomPanel.setLayout(new GridLayout(1, teamsActive));


                            greenPanel = new JPanel();
                            greenPanel.setBackground(Color.GREEN);
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

                            teamsActive++;
                            bottomPanel.setLayout(new GridLayout(1, teamsActive));

                            yellowPanel = new JPanel();
                            yellowPanel.setBackground(Color.YELLOW);
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
                        System.out.println("Match is starting!");

                        messageWriter.writeMessage(new StartMessage((byte)2, (byte)1));
                    }
                }
        );
        controlPanel.add(startMatchButton);

        statePanel = new JPanel();
        statePanel.setLayout(new GridLayout(4,3));
        //c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.gridy = 2;
        //c.weighty = .5;
        mainFrame.add(statePanel, c);

        redState = new JLabel();
        redState.setText("Team Red State: Inactive");
        statePanel.add(redState);

        redButton = new JLabel();
        redButton.setText("Team Red Button State: 0");
        statePanel.add(redButton);

        redPoints = new JLabel();
        redPoints.setText("Team Red Points: 0");
        statePanel.add(redPoints);

        blueState = new JLabel();
        blueState.setText("Team Blue State: Inactive");
        statePanel.add(blueState);

        blueButton = new JLabel();
        blueButton.setText("Team Blue Button State: 0");
        statePanel.add(blueButton);

        bluePoints = new JLabel();
        bluePoints.setText("Team Blue Points: 0");
        statePanel.add(bluePoints);

        greenState = new JLabel();
        greenState.setText("Team Green State: Inactive");
        statePanel.add(greenState);

        greenButton = new JLabel();
        greenButton.setText("Team Green Button State: 0");
        statePanel.add(greenButton);

        greenPoints = new JLabel();
        greenPoints.setText("Team Green Points: 0");
        statePanel.add(greenPoints);

        yellowState = new JLabel();
        yellowState.setText("Team Green State: Inactive");
        statePanel.add(yellowState);

        yellowButton = new JLabel();
        yellowButton.setText("Team Yellow Button State: 0");
        statePanel.add(yellowButton);

        yellowPoints = new JLabel();
        yellowPoints.setText("Team Yellow Points: 0");
        statePanel.add(yellowPoints);

        console = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(console,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //console.setWrapStyleWord(true);
        console.setEditable(false);
        console.setForeground(Color.BLUE);
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

        /*redPanel = new JPanel();
        redPanel.setBackground(Color.RED);
        bottomPanel.add(redPanel);

        redDisplayPoints = new JLabel("<html>Red Team Points: <br> 0</html>");
        redDisplayPoints.setFont(new Font("Monospace", Font.BOLD, 24));
        redDisplayPoints.setForeground(Color.BLACK);
        redPanel.add(redDisplayPoints);

        bluePanel = new JPanel();
        bluePanel.setBackground(Color.BLUE);
        bottomPanel.add(bluePanel);

        blueDisplayPoints = new JLabel("<html>Blue Team Points: <br> 0</html>");
        blueDisplayPoints.setFont(new Font("Monospace", Font.BOLD, 24));
        blueDisplayPoints.setForeground(Color.BLACK);
        bluePanel.add(blueDisplayPoints);

        greenPanel = new JPanel();
        greenPanel.setBackground(Color.GREEN);
        bottomPanel.add(greenPanel);

        greenDisplayPoints = new JLabel("<html>Green Team Points: <br> 0</html>");
        greenDisplayPoints.setFont(new Font("Monospace", Font.BOLD, 24));
        greenDisplayPoints.setForeground(Color.BLACK);
        greenPanel.add(greenDisplayPoints);

        yellowPanel = new JPanel();
        yellowPanel.setBackground(Color.YELLOW);
        bottomPanel.add(yellowPanel);

        yellowDisplayPoints = new JLabel("<html>Yellow Team Points: <br> 0</html>");
        yellowDisplayPoints.setFont(new Font("Monospace", Font.BOLD, 24));
        yellowDisplayPoints.setForeground(Color.BLACK);
        yellowPanel.add(yellowDisplayPoints);*/

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
                if(canceldashboard) {
                    try {
                        //System.out.println("sleeping");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                else {
                    //messageWriter.writeMessage(new PingMessage(2));
                    //System.out.println("Java Ping");
                    /*if (messageReader.messageReady()) {
                        byte[] data = messageReader.getMessage();
                        IMessage msg = MessageParser.parse(data);

                        if (msg instanceof BatteryMessage) {
                            voltage = ((BatteryMessage) msg).getVoltage();
                            batteryVoltage.setText(String.format("Battery Voltage: %s", voltage +'v'));
                        }
                        if (msg instanceof LimitSwitchMessage){
                            if(((LimitSwitchMessage)msg).getLimitSwitchId() == 0  ){
                                limitSwitchZero.setText((String.format("Top Limit Switch on: %s",
                                        ((LimitSwitchMessage)msg).getIsPressed())));
                            }
                            else if(((LimitSwitchMessage)msg).getLimitSwitchId() == 1  ){
                                limitSwitchOne.setText((String.format("Top Limit Switch on: %s",
                                        ((LimitSwitchMessage) msg).getIsPressed())));
                            }
                        }

                        if (msg instanceof EncoderMessage){

                            byte MSB = ((EncoderMessage)msg).getMostSignificantBit();
                            byte LSB = ((EncoderMessage)msg).getLeastSignificantBit();
                            int combined = (MSB << 8 ) | (LSB & 0xff);

                            if(((EncoderMessage)msg).getEncoderMessageId() == 0){
                                encoderLeftFront.setText((String.format("Front Left RPM: %s", combined)));
                            }
                            else if(((EncoderMessage)msg).getEncoderMessageId() == 1){
                                encoderLeftRear.setText((String.format("Front Left RPM: %s", combined)));
                            }
                            else if(((EncoderMessage)msg).getEncoderMessageId() == 2){
                                encoderRightFront.setText((String.format("Front Left RPM: %s", combined)));
                            }
                            else if(((EncoderMessage)msg).getEncoderMessageId() == 3){
                                encoderRightRear.setText((String.format("Front Left RPM: %s", combined)));
                            }
                        }
                    }*/
                }
            }

        };

        final ScheduledFuture<?> dashboardUpdater =
                scheduler.scheduleAtFixedRate(dashboardUpdate, 200 , 200, TimeUnit.MILLISECONDS);

        /*if(canceldashboard) {
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
        System.setErr(new PrintStream(out, true));
    }

    /*private void searchForControllers(){
        xboxController = new inputControl();
        allTheControllers.clear();
        allControllers = xboxController.getName();
        byte a = 0;
        for (int i = 0; i < xboxController.getName().length; i++){
            Controller[] temp = xboxController.getName();
            if (!temp[i].toString().toLowerCase().contains("mouse") &&
                    !temp[i].toString().toLowerCase().contains("keyboard") &&
                    !temp[i].toString().toLowerCase().contains("receiver")) {
                allTheControllers.add(temp[i].toString());
                controllerLocation[a] = i;
                a++;

            }
        }
    }*/

    private void writeMessage(IMessage msg){
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
    }

    public static void main(String[] args){


        GraphicsInterface graphics = new GraphicsInterface();
        //graphics.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
