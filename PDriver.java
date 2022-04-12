
//  JOHN ZHAO
//  CS 4348.003
//  PROJECT TWO (03/17/2022)

//  Driver Program File

//  VERSION 1.0

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class PDriver
{
    public static void main(String[] args) throws InterruptedException
    {
        LinkedList<PTeller> tellers = new LinkedList<>();
        LinkedList<PCustomer> customers = new LinkedList<>();

        Semaphore tellersAvailable = new Semaphore(3, true);
        Semaphore manager = new Semaphore(1, true);
        Semaphore safe = new Semaphore(2, true);

        Semaphore numberOfCustomers = new Semaphore(100);

        Semaphore tellerZeroAvailable = new Semaphore(1, true);
        Semaphore tellerOneAvailable = new Semaphore(1, true);
        Semaphore tellerTwoAvailable = new Semaphore(1, true);

        tellers.add(new PTeller(manager, safe, numberOfCustomers));
        tellers.add(new PTeller(manager, safe, numberOfCustomers));
        tellers.add(new PTeller(manager, safe, numberOfCustomers));

        for(PTeller teller : tellers)
            teller.start();

        for(int index = 0; index < 100; index++)
            customers.add(new PCustomer(tellers, tellersAvailable,
                    tellerZeroAvailable, tellerOneAvailable, tellerTwoAvailable));

        for(PCustomer customer : customers)
           customer.start();

        //  Not completely sure what .join() does, but it waits for the
        //  tellers to leave before the bank closes.
        tellers.get(0).join();  tellers.get(1).join();  tellers.get(2).join();

        System.out.println("The bank closes for the day.");

    }
}
