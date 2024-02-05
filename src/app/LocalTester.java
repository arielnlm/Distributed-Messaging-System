package app;

import networking.Address;
import networking.SystemState;
import networking.architecture.Organizer;
import servent.message.MessageHelper;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class LocalTester {
    public static void main(String[] args) {
        int id = 1;
        ServentInfo sib = new ServentInfo("localhost", id++, 900, null, false);
        AppConfig.myServentInfo = sib;
        SystemState.getInstance().setBootstrap(sib);
        Organizer.getInstance().addServentToSystem(sib);
        ServentInfo test = null;
        for(int i=1000; i<=2000; i+=100){
            ServentInfo si = new ServentInfo("localhost", id++, i, null, false);
            if(i == 1700)
                test = si;
            Organizer.getInstance().addServentToSystem(si);
        }
        Address a1 = new Address("localhost", 1100);
        Address a2 = new Address("localhost", 1600);
        Queue<ServentInfo> shortes = new ConcurrentLinkedDeque<>(MessageHelper.findShortestPath(sib, test));
        //shortes.poll();
        System.out.println("Shortest: " + shortes);
    }
}
