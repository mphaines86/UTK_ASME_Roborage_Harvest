

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
import java.util.*;
import java.util.List;
import java.util.concurrent.*;


public class GraphicsInterface extends JFrame {

    private serialComm comm;
    private MessageWriter messageWriter;
    //final MessageReader messageReader = new MessageReader();
    // final xboxControllerTest connectController = new xboxControllerTest();
    private JComboBox commPorts, controllerPorts;
    private JTextArea console;
    private JButton initialize, refresh, killcontrol;
    private JLabel batteryVoltage, limitSwitchZero, limitSwitchOne, encoderLeftFront,
            encoderLeftRear,encoderRightFront,encoderRightRear;
    private String[] portList = {};
    private boolean isConnected = false;
    private double voltage = 0;
    private byte firstInit = 0;
    private boolean canceldashboard = false;
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

        Container GraphicsInterfacePane = getContentPane();
        GraphicsInterfacePane.setLayout(null);

        comm.searchForPorts();
        //redirectSystemStreams();

        Thread writerThread = new Thread(messageWriter);
        writerThread.start();

        console = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(console,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //console.setWrapStyleWord(true);
        console.setEditable(false);
        console.setForeground(Color.BLUE);
        //console.setRows(80);
        //console.setColumns(60);
        scrollPane.setSize(700,250);
        scrollPane.setLocation(50,280);

        GraphicsInterfacePane.add(scrollPane);
        //GraphicsInterfacePane.add(console);

        //searchForControllers();

        commPorts = new JComboBox(comm.getPorts().toArray());
        commPorts.setLocation(350,50);
        commPorts.setSize(100,30);
        //commPorts.setSelectedIndex(0);
        GraphicsInterfacePane.add(commPorts);

        controllerPorts = new JComboBox();
        controllerPorts.setLocation(500,50);
        controllerPorts.setSize(200, 30);
        GraphicsInterfacePane.add(controllerPorts);
        for (String allTheController : allTheControllers) {
            controllerPorts.addItem(allTheController);
        }

        killcontrol = new JButton();
        killcontrol.setText("KILL CONTROLS!!!");
        killcontrol.setSize(130,50);
        killcontrol.setLocation(600,150);
        killcontrol.addActionListener(
               new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent e) {
                       /*if (!abuse){
                           abuse = true;
                           //initiateController.kill();
                           System.out.println("Robot Controls Suspended");
                       }
                       else{
                           abuse = false;
                           //initiateController.enable();
                           System.out.println("Robot Controls Enabled");
                       }*/
                       messageWriter.writeMessage(new PingMessage(1));
                   }
               }
        );
        GraphicsInterfacePane.add(killcontrol);

        initialize = new JButton();
        initialize.setText("Initialize");
        initialize.setSize(130,50);
        initialize.setLocation(50,50);
        initialize.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        if(!isConnected) {
                            comm.setPortname(commPorts.getSelectedItem().toString());
                            comm.initialize();
                            comm.portConnect();
                            
                            initialize.setText("Disconnect");
                            messageWriter.writeMessage(new PingMessage(1));
                            isConnected = true;
                            canceldashboard = false;
                            //updateDashboard();

                        }
                        else if (isConnected) {
                            canceldashboard = true;
                            messageWriter.writeMessage(new PingMessage(0));
                            comm.close();
                            initialize.setText("initialize");
                            //scheduler.shutdown();
                            isConnected = false;

                        }
                    }
                }
        );
        GraphicsInterfacePane.add(initialize);

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
        GraphicsInterfacePane.add(refresh);

        batteryVoltage = new JLabel();
        batteryVoltage.setText(String.format("Battery Voltage: %s", voltage)+'v');
        batteryVoltage.setSize(140,30);
        batteryVoltage.setLocation(50,120);
        GraphicsInterfacePane.add(batteryVoltage);

        limitSwitchZero = new JLabel();
        limitSwitchZero.setText("Top Limit Switch On: false");
        limitSwitchZero.setSize(200, 30);
        limitSwitchZero.setLocation(50, 150);
        GraphicsInterfacePane.add(limitSwitchZero);

        limitSwitchOne = new JLabel();
        limitSwitchOne.setText("Bottom Limit Switch On: false");
        limitSwitchOne.setSize(200, 30);
        limitSwitchOne.setLocation(50, 170);
        GraphicsInterfacePane.add(limitSwitchOne);

        encoderLeftFront = new JLabel();
        encoderLeftFront.setText("Front Left RPM: 0");
        encoderLeftFront.setSize(150, 30);
        encoderLeftFront.setLocation(270, 150);
        GraphicsInterfacePane.add(encoderLeftFront);

        encoderRightFront = new JLabel();
        encoderRightFront.setText("Front Right RPM: 0");
        encoderRightFront.setSize(150, 30);
        encoderRightFront.setLocation(420, 150);
        GraphicsInterfacePane.add(encoderRightFront);

        encoderLeftRear = new JLabel();
        encoderLeftRear.setText("Rear Left RPM: 0");
        encoderLeftRear.setSize(150, 30);
        encoderLeftRear.setLocation(270, 170);
        GraphicsInterfacePane.add(encoderLeftRear);

        encoderRightRear = new JLabel();
        encoderRightRear.setText("Rear Right RPM: 0");
        encoderRightRear.setSize(150, 30);
        encoderRightRear.setLocation(420, 170);
        GraphicsInterfacePane.add(encoderRightRear);

        setTitle("RoboSmokey 3000 Control Interface");
        setSize(800,600);
        setVisible(true);
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
                    writeMessage(new PingMessage(1));
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
        graphics.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
