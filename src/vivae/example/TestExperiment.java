/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution
 * written as a bachelor project
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */
package vivae.example;

import vivae.arena.Arena;
import vivae.controllers.FRNNController;
import vivae.controllers.VivaeController;
import vivae.fitness.AverageSpeed;
import vivae.fitness.FitnessFunction;
import vivae.robots.IRobotInterface;
import vivae.robots.VivaeRobot;
import vivae.sensors.OdometerSensor;
import vivae.sensors.ScalableDistanceSensor;
import vivae.util.Util;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

//import robot.VivaeRobotTmp;

public class TestExperiment extends BasicExperiment {

    Arena vivaeArena = null;
    JFrame f = null;
    private boolean isVisible;
    private boolean initialized = false;


    public TestExperiment() {
        robots = new ArrayList<IRobotInterface>();
        controllers = new ArrayList<VivaeController>();
    }

    public void setupExperiment(int sensors, double[][][] wm, String scenario, boolean visible) throws IOException {
        isVisible = visible;
        initialized = true;

        if (visible) {
            f = new JFrame("FRNN Experiment");
            vivaeArena = Arena.renewArena(f);

            f.setBounds(50, 0, vivaeArena.screenWidth, vivaeArena.screenHeight + 30);
            f.setResizable(false);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.getContentPane().add(vivaeArena);
            f.setVisible(true);

            vivaeArena.loadScenario(scenario);
            vivaeArena.isVisible = true;
            vivaeArena.setAllArenaPartsAntialiased(true);
        } else {
            vivaeArena = Arena.renewArena(f);
            vivaeArena.loadScenario(scenario);
            vivaeArena.isVisible = false;
            vivaeArena.setLoopSleepTime(0);
        }

        VivaeRobot va1 = new VivaeRobot("Vrobot1");
        VivaeRobot va2 = new VivaeRobot("Vrobot2");

        double sangle = -Math.PI / 2;
        double eangle = +Math.PI / 2;
//        double ai = Math.PI / (sensors_cnt / 2 - 1);
        double ai = (eangle - sangle) / (sensors - 1);

        va1.addSensor(new ScalableDistanceSensor(va1, sangle, eangle, sensors, 50));
//        va2.addSensor(new ScalableDistanceSensor(va2 ,sangle, eangle, sensors_cnt, 50));
//

//        va1.addSensor(new AsyncScalableDistanceSensor(va1, sangle, eangle, sensors_cnt, 50));

//        for (int i = 0; i < sensors_cnt; i++) {
//            va1.addDistanceSensor(sangle + i * ai, 50);
//            va2.addDistanceSensor(sangle + i * ai, 50);
//        }
//        for (int i = 0; i < sensors_cnt; i++) {
//            va1.addFrictionSensor(sangle + i * ai, 25);
//            va2.addFrictionSensor(sangle + i * ai, 25);
//        }
        va1.addSensor(new OdometerSensor(va1));
//        va2.addSensor(new OdometerSensor(va2));

//        HardwareRobot hr = new HardwareRobot("localhost", 6001);

//        KeyboardVivaeController kbc = new KeyboardVivaeController(va1, KeyboardLayout.ArrowsLayout());
        //KeyboardVivaeController kbc2 = new KeyboardVivaeController(hr, KeyboardLayout.AwdsLayout());
//          KeyboardVivaeController kbc2 = new KeyboardVivaeController(va2, KeyboardLayout.AwdsLayout());
        FRNNController frnnc1 = new FRNNController(va1, wm[0]);
//         DemoController dc = new DemoController(hr);
//        StopAtWallController stw = new StopAtWallController(va1);
//        FileReaderController frc = new FileReaderController(va2, "reply_run.txt");


//        FRNNController frnnc2 = new FRNNController(va2, wm[0]);
//        KeyboardVivaeController kbc = new KeyboardVivaeController(va2);
        controllers.add(frnnc1);
//        controllers.add(stw);
//        controllers.add(dc);
//        controllers.add(dc);
//        controllers.add(frnnc2);


//        robots.add(hr);
        robots.add(va1);
//        robots.add(va2);


//        KeyboardVivaeController kbhrc = new KeyboardVivaeController(hr);

//        hr.connect();
//        hr.setMaxWheelSpeed(10);


//        robots.add(hr);

//        hr.getSensorData();

//        hr.connect();
//        hr.setMaxWheelSpeed(10);

    }

    public void startExperiment() {
        System.out.println("Starting experiment, visible = " + isVisible);

        if (!initialized) {
            System.err.println("Experiment not initialized!");
            System.exit(1);
        }

        /**
         * Thread sleep time in milisecs.
         */
        int loopSleepTime = 10;
        /**
         * Number of steps the physical world takes in one iteration of the main loop.
         */
        int worldStepsPerLoop = 1;


        vivaeArena.setScreenSize(640, 480);
        vivaeArena.initWorld();

        for (int i = 1; i < 5000; i++) {

            ListIterator<VivaeController> ci = controllers.listIterator();
            while (ci.hasNext()) {
                ci.next().moveControlledObject();
            }

            vivaeArena.getWorld().step();
            vivaeArena.moveVivaes();

            if (isVisible) {
                vivaeArena.repaint();
                if (loopSleepTime > 0) {
                    try {
                        Thread.sleep(loopSleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            stepsDone++;
        }
    }

    public static void main(String[] args) {

        try {
            TestExperiment exp = new TestExperiment();

            String scen = "cfg/vivae/scenarios/arena1.svg";
//            String scen = "cfg/vivae/scenarios/distance2_h.svg";
            int sensors = 10; // 5 for distance and 5 for surface
            int neurons = 2;
            int robots = 2;
            double[][][] wm = Util.randomArray3D(robots, neurons, 2 * sensors + neurons + 1, -5, 5);
//        exp.setupExperiment(wm,50,25);
            exp.setupExperiment(sensors, wm, scen, true);
//        FitnessFunction mot = new MovablesOnTop(exp.arena);//initialize fitness
            FitnessFunction avg = new AverageSpeed(exp);
            exp.startExperiment();
            System.out.println("average speed fitness = " + avg.getFitness());
//        System.out.println("average ontop fitness = "+ mot.getFitness());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

