package io;

import java.io.BufferedReader;                    //BufferedReader makes reading operation efficient
import java.io.InputStreamReader;         //InputStreamReader decodes a stream of bytes into a character set
import java.io.OutputStream;          //writes stream of bytes into serial port
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;            //deals with possible events in serial port (eg: data received)
import gnu.io.SerialPortEventListener; //listens to the a possible event on serial port and notifies when it does
import java.util.Enumeration;
import gnu.io.PortInUseException;           //all the exceptions.Never mind them for now
import java.io.IOException;
import gnu.io.UnsupportedCommOperationException;

import javax.print.DocFlavor;
import java.util.TooManyListenersException;
import java.util.Scanner;                                   //to get user input of name
import java.util.*;


public class serialComm implements SerialPortEventListener {

    private SerialPort serialPort ;         //defining serial port object
    private Enumeration ports = null;
    private CommPortIdentifier portId  = null;       //my COM port
    private static final int TIME_OUT = 2000;    //time in milliseconds
    private static final int BAUD_RATE = 115200; //baud rate to 9600bps
    public static BufferedReader input;               //declaring my input buffer
    public static OutputStream output;                //declaring output stream
    private String name;        //user input name string
    Scanner inputName;          //user input name
    private String portname = "COM4";
    private List<String> portList = new ArrayList<String>();


    //method initialize
    
    public void searchForPorts()
    {
        ports = CommPortIdentifier.getPortIdentifiers();
        portList.clear();

        
        while (ports.hasMoreElements()){
            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();
            
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL){
                //System.out.println(curPort.getName());
                portList.add(curPort.getName());
            }

        }
    }
    public void initialize(){
        CommPortIdentifier ports = null;      //to browse through each port identified
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers(); //store all available ports
        while(portEnum.hasMoreElements()){  //browse through available ports
            ports = (CommPortIdentifier)portEnum.nextElement();
            //following line checks whether there is the port i am looking for and whether it is serial
            if(ports.getPortType() == CommPortIdentifier.PORT_SERIAL&&ports.getName().equals(portname)){
                System.out.println(String.format("COM port found: %s",portname));
                //graphicsInfo.setConsole(String.format("Com port found: %s",portname));
                portId = ports;                  //initialize my port
                break;
            }

        }
        //if serial port am looking for is not found
        if(portId==null){
            System.out.println("COM port not found");
            System.exit(1);
        }

    }

    //end of initialize method

    //connect method

    public void portConnect(){
        //connect to port
        try{
            serialPort = (SerialPort)portId.open(this.getClass().getName(),TIME_OUT);
            //down cast the comm port to serial port
            //give the name of the application
            //time to wait
            System.out.println(String.format("Port open succesful: %s",portname));

            //set serial port parameters
            serialPort.setSerialPortParams(BAUD_RATE,SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);



        }
        catch(PortInUseException e){
            System.out.println("Port already in use");
            System.exit(1);
        }
        catch(NullPointerException e2){
            System.out.println("COM port may be disconnected");
        }
        catch(UnsupportedCommOperationException e3){
            System.out.println(e3.toString());
        }

        //input and output channels
        try{
            //defining reader and output stream
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output =  serialPort.getOutputStream();
            char ch = 1; // I added this
            output.write(ch);// and this
            //adding listeners to input and output streams
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            serialPort.notifyOnOutputEmpty(true);
        }
        catch(Exception e){
            System.out.println(e.toString());
        }

    }
    //end of portConnect method

    //readWrite method

    public synchronized void serialEvent(SerialPortEvent evt) {

        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) { //if data available on serial port
            try {
                byte singleData = (byte)input.read();
                String str = new String(new byte[] { singleData },"UTF-8");
                System.out.print(str);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }

    }
    //end of serialEvent method


    //closePort method
    public synchronized void close(){
        if(serialPort!=null){
            serialPort.close(); //close serial port
        }
        input = null;        //close input and output streams
        output = null;
    }

    public BufferedReader getInput(){
        return input;
    }

    public OutputStream getOutput(){
        return output;
    }
    public void setPortname(String portname){
        this.portname = portname;
    }


    public List getPorts(){
        return portList;
    }
    //main method

//end of main method
// end of  SerialTest class
}