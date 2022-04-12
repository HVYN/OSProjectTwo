
//  JOHN ZHAO
//  CS 4348.003
//  PROJECT TWO (03/17/2022)

//  Representation of Teller
//  for Project TWo

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class PTeller extends Thread
{
    private static int tellerCounter = 0;

    private int tellerId;

    private Semaphore tellerCanAct;

    private Semaphore servingCustomer;

    private Semaphore manager;
    private Semaphore safe;

    private Semaphore numberOfCustomers;

    private PCustomer currentCustomer;

    //  UPON INSTANTIATION, THE STATIC COUNTER WILL INCREMENT,
    //  AND THE TELLER WILL INHERIT AN ID.
    public PTeller(Semaphore manager, Semaphore safe, Semaphore numberOfCustomers)
    {
        servingCustomer = new Semaphore(0);

        tellerCanAct = new Semaphore(0);

        currentCustomer = null;

        this.manager = manager;
        this.safe = safe;

        this.numberOfCustomers = numberOfCustomers;

        this.tellerId = tellerCounter++;
    }

    @Override
    public void run()
    {
        try
        {
            resetServing();

            //  HARDCODED - 100 CUSTOMERS
            //  IF THERE ARE STILL CUSTOMERS TO SERVE, TELLERS WILL
            //  CONTINUE TO WORK.
            while (numberOfCustomers.availablePermits() > 0)
            {
                while(!servingCustomer.tryAcquire())
                {
                    if(numberOfCustomers.availablePermits() == 0)
                        break;
                }

                //  IF NO MORE CUSTOMERS TO SERVE, TELLER CAN LEAVE.
                if(numberOfCustomers.availablePermits() == 0)
                    break;

                //  TELLER GOT A CUSTOMER, NOW SERVING.
                inquireRequest();

                //  TELLER WAITING TO ACT.
                tellerCanAct.acquire();

                System.out.println(debuggerTellerIndentation() + tellerInfo() +
                        " is handling the " + getCurrentCustomer().getBehavior() + " transaction.");

                if (getCurrentCustomer().getBehavior() == CustomerBehavior.WITHDRAWAL)
                    accessManager();

                //  BOTH INTERACTIONS NEED TO ACCESS THE SAFE.
                accessSafe();

                System.out.println(debuggerTellerIndentation() + tellerInfo() +
                        " finishes " + getCurrentCustomer().customerInfo() + "'s "
                        + getCurrentCustomer().getBehavior() + " transaction.");

                finalizeTransaction();
                
                tellerCanAct.acquire();

                //  RESET AFTER THE CUSTOMER IS SERVED.
                resetServing();
            }

        }
        catch(Exception E)
        {
            E.printStackTrace();
        }

        //  LOG: TELLER HAS LEFT THE BANK.
        System.out.println(debuggerTellerIndentation() + tellerInfo() + " is leaving for the day.");
    }

    //  METHOD THAT ALLOWS THE TELLER TO GO AHEAD WITH THEIR SEQUENCE OF ACTION(S)
    public void canAct()
    {
        tellerCanAct.release();
    }

    private void inquireRequest() throws InterruptedException
    {
        System.out.println(debuggerTellerIndentation() + tellerInfo() +
                " is serving " + getCurrentCustomer().customerInfo() + ".");

        getCurrentCustomer().canAct();
    }

    //  RESET METHOD; USED AFTER EVERY CUSTOMER/TRANSACTION.
    private void resetServing() throws InterruptedException
    {
        currentCustomer = null;

        //  LOG: TELLER CAN SERVE.
        System.out.println(debuggerTellerIndentation() + tellerInfo() +
                " is ready to serve.");
    }

    public void accessManager() throws InterruptedException
    {
        System.out.println(debuggerTellerIndentation() + tellerInfo() +
                " is going to the manager.");

        //  TRY TO GET MANAGER'S ATTENTION, WAIT (BLOCK) IF THEY'RE
        //  BUSY.
        manager.acquire();

        System.out.println(debuggerTellerIndentation() + tellerInfo() +
                " is getting the manager's permission.");

        sleep(new Random().nextInt(25) + 5);

        //  LET THE MANAGER BE AFTER DONE WORKING WITH THEM.
        manager.release();
    }

    public void accessSafe() throws InterruptedException
    {
        //  TRY TO USE THE SAFE, WAIT (BLOCK) IF IT'S TOO FULL.
        safe.acquire();

        System.out.println(debuggerTellerIndentation() + tellerInfo() +
                " is going to the safe, for a " + getCurrentCustomer().getBehavior() + ".");

        sleep(new Random().nextInt(40) + 10);

        System.out.println(debuggerTellerIndentation() + tellerInfo() +
                " is leaving the safe.");

        //  LEAVE THE SAFE AFTER DONE WORKING.
        safe.release();
    }

    public void finalizeTransaction() throws InterruptedException
    {
        //  ALLOW THE CUSTOMER LEAVE.
        currentCustomer.canAct();

        //  LOWER SEMAPHORE COUNTER.
        numberOfCustomers.acquire();
    }

    //  THIS METHOD GIVES THE TELLER WHAT CUSTOMER IT'S SERVING,
    //  AND GIVES THE CUSTOMER THE PERMISSION SEMAPHORE TO PREVENT IT
    //  FROM LEAVING EARLIER THAN THE TELLER'S WORK SPEED.
    public void introduce(PCustomer currentCustomer) throws InterruptedException
    {
        this.currentCustomer = currentCustomer;

        servingCustomer.release();

        //  LET THE CUSTOMER THE TELLER IS CURRENTLY SERVING KNOW
        //  THAT IT CANNOT LEAVE.
    }

    //  GETTER: TELLER ID
    public int getTellerId() {  return tellerId;    }

    //  GETTER: CURRENT CUSTOMER
    public PCustomer getCurrentCustomer()   {   return currentCustomer; }

    //  GETTER: PERMISSION TO LEAVE (WHETHER SEMAPHORE HAS A PERMIT OR NOT).
    public Semaphore getInteractionFlag()   {   return tellerCanAct; }

    //  HELPER: TELLER + ID
    public String tellerInfo()  {   return "TELLER " + getTellerId();   }

    //  DEBUG: PRINT AMOUNT OF INDENTS ACCORDING TO A TELLER.
    private String debuggerTellerIndentation()
    {
        if(getTellerId() == 0)
            return "\t";
        else if(getTellerId() == 1)
            return "\t\t";
        else
            return "\t\t\t";
    }
}
