

//  JOHN ZHAO
//  CS 4348.003
//  PROJECT TWO (03/17/2022)

//  Representation of Customer
//  for Project Two

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class PCustomer extends Thread
{
    private static int customerCounter = 0;

    private int customerId;

    private LinkedList<PTeller> tellers;

    private Semaphore tellersAvailable;
    private Semaphore tellerZeroAvailable, tellerOneAvailable, tellerTwoAvailable;

    private Semaphore customerCanAct;

    private CustomerBehavior behavior;

    private PTeller currentTeller;

    public PCustomer(LinkedList<PTeller> tellers, Semaphore tellersAvailable,
                     Semaphore tellerZeroAvailable, Semaphore tellerOneAvailable, Semaphore tellerTwoAvailable)
    {
        //  RANDOMIZED BEHAVIOR DECIDER: WITHDRAW OR DEPOSIT.
        switch(new Random().nextInt(2) + 1)
        {
            case 1:
                behavior = CustomerBehavior.WITHDRAWAL;
                break;
            case 2:
                behavior = CustomerBehavior.DEPOSIT;
                break;

            default:
                System.out.println("OTHER BEHAVIOR");
                break;
        }

        //  ACTING SEMAPHORE - 'BLOCKING' DEFAULT STATE
        customerCanAct = new Semaphore(0);

        //  THE CUSTOMER WILL BE AWARE OF A LIST OF TELLERS.
        this.tellers = tellers;

        this.tellersAvailable = tellersAvailable;

        this.tellerZeroAvailable = tellerZeroAvailable;
        this.tellerOneAvailable = tellerOneAvailable;
        this.tellerTwoAvailable = tellerTwoAvailable;

        //  UPON INSTANTIATION, THE STATIC COUNTER WILL INCREMENT,
        //  AND THE CUSTOMER WILL INHERIT AN ID.
        this.customerId = customerCounter++;

        //  INDICATE WHAT THE CUSTOMER WANTS TO DO, WHEN THEY GO TO THE BANK.
        System.out.println(customerInfo() + " wants to perform a " + getBehavior() + " transaction.");
    }

    @Override
    public void run()
    {
        try
        {
            //  CHOOSE RANDOM TIME DURING THE DAY TO GO TO THE BANK.
            sleep(new Random().nextInt(5000));

            //  LOG: CUSTOMER ENTERS THE BANK.
            System.out.println(customerInfo() + " is going to the bank.");

            // System.out.println("DEBUG: TELLERS AVAILABLE: " + tellersAvailable.availablePermits());

            tellersAvailable.acquire();

            // System.out.println("DEBUG: TELLER ACQUIRED!");

            if(tellerZeroAvailable.tryAcquire())
                currentTeller = tellers.get(0);
            else if(tellerOneAvailable.tryAcquire())
                currentTeller = tellers.get(1);
            else if(tellerTwoAvailable.tryAcquire())
                currentTeller = tellers.get(2);

            //  CUSTOMER RECEIVES PERMISSION SEMAPHORE FROM TELLER.
            System.out.println(customerInfo() + " goes to " + currentTeller.tellerInfo());
            System.out.println(customerInfo() + " introduces itself to " + currentTeller.tellerInfo());

            currentTeller.introduce(this);

            customerCanAct.acquire();

            answerInquiry();

            customerCanAct.acquire();

            allowedToLeave();
        }
        catch(Exception E)
        {
            E.printStackTrace();
        }
    }

    public void allowedToLeave() throws InterruptedException
    {
        System.out.println(customerInfo() + " thanks " + currentTeller.tellerInfo() + " and leaves.");

        currentTeller.canAct();

        if(currentTeller.getTellerId() == 0)
            tellerZeroAvailable.release();
        else if(currentTeller.getTellerId() == 1)
            tellerOneAvailable.release();
        else if(currentTeller.getTellerId() == 2)
            tellerTwoAvailable.release();

        tellersAvailable.release();
    }

    public void canAct()
    {
        customerCanAct.release();
    }

    //  PRINT THE CUSTOMER'S WANTED TRANSACTION.
    public void answerInquiry() throws InterruptedException
    {
        System.out.println(customerInfo() + " asks for a " + getBehavior() + " transaction.");

        currentTeller.canAct();
    }

    //  GETTER: CUSTOMER ID
    private int getCustomerId()  {   return customerId;  }

    //  GETTER: CUSTOMER BEHAVIOR
    public CustomerBehavior getBehavior()  {   return behavior;   }

    //  HELPER: PRINT CUSTOMER + THEIR ID
    public String customerInfo()   {   return "CUSTOMER " + getCustomerId();   }
}
