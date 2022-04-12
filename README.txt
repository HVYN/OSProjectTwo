
[ READ ME TEXT FOR PROJECT TWO ]
[ JOHN ZHAO ]
[ jzz180000 ]

\! VERSION 1.0 !/
(03/22/2022)

[!] All Semaphores used (in this version) are intialized in the 'PDriver' program.

[!] The 'PDriver' (Driver) program uses '*.join()' function to wait for all 'PTeller' (Teller) 
	threads	to die before ending.

[!] The Tellers and 'PCustomer' (Customer(s)) are both stored in separate LinkedLists; they start
	in the Driver program, by iterating thru each LinkedList and performing '*.start()'
	on each separate Teller and Customer thread.

[!] The 'CustomerBehavior' enum is very small, and defines a custom data type for readability
	purposes.

[!] The Teller threads timeout after two seconds (2000 ms) of non-activity (no customer interaction).

\! VERSION 1.1 !/
(03/23/2022)

[!] All Synchronizing are now done by Semaphores.

[!] Both the Customer and Teller talk to each other, and release each other's Blocking Semaphores.

\! VERSION 1.2 !/
(03/31/2022)

[!] The Teller now quits when there are no customers left to serve, as opposed to a timeout function.

[!] Teller interaction semaphores were moved from the Driver class into the Teller classes.
 