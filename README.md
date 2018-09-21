# Ticket-Service
This TicketServiceImplementation is written in Java and uses Maven as its build tool.

The generated jar file is located in the target folder under the name *ticket-service-1.0-SNAPSHOT.jar*

The groupId of this maven project is *com.walmart.app*

The main method is in the *App.java* file at *src/main/java/com/walmart/app*

JUnit and Mockito are the testing frameworks that are used.
### Design
Finding the perfect solution is a NP-hard problem. For an exact solution, backtracking can be used (takes a lot of time though), checking all the possibilities 
and choosing the best one. Greedy solution can be used, but it will not be optimized.

This program allocates maximum number of continuous seats to an user. At any given point, this program tries to find a continuous block of space. 
If a continuous space is not found, it splits and tries to find the maximum block that can accommodate most of the people in the reservation. 
In case of release of tickets, the gaps created in between filled blocks are also considered the next time a user lodges a request.

Two HashMaps (heldTickets and rowSpaceMap) and one TreeMap (continuousSpaceMap) is used to track the number of seats available in the venue.

The **heldTickets** map stores the seatHoldId and its corresponding seatHold object. 
This allows to keep track of the tickets currently being held.

The **rowSpaceMap** holds the row number and the list of continuous seat blocks in that row.
For example, if the venue has 2 rows and 6 columns and if seat numbers 2 and 3 are booked in row 0, then the rowSpaceMap would look like:
```
{
    0: [[0 1] [4 5]]
    1: [[0 5]]
}
```
The keys 0 and 1 indicate the row number. Each block in the list of blocks contains the start and end positions of the continuous seat block.
Over here, there is one continuous seat block in row 1 having 6 continuous seats from seat 0 to seat 5.
Row 0 has two seat blocks that have 2 continuous seats in seat 0 and seat 1 & seat 4 and seat 5 respectively.

The **continuousSpaceMap** contains the number of continuous seats and the rows in which they are available.
If seats 2 & 3 in row 0 are booked, continuousSpaceMap would indicate that 6 continuous seats are available in row 1, and row 0 has two blocks that have 2 continuous seats.
```
{
    2: [0 0]
    6: [1]
}
```
### Implementation
#### Hold Tickets
The user can place a request to hold seats by sending a valid integer.
A valid integer is greater than 0 and less than or equal to the number of seats available in the venue.
If not, a SeatsUnavailableException is thrown.

If the requested number of seats is available as one continuous block, that block is assigned to that customer.
This information is retrieved from continuousSpaceMap, where it says if a block is available and if yes, in which row.

If the requested seats is not available as one continuous block, a loop runs, each time assigning the maximum continuous seat block to the user until the seats that need to be allocated is less than the maximum continuous block.
Then, maximum seat block is modified to accommodate the remaining number of seats.

```
initial row space map and       User requesting for 8 seats         6 seats assigned and 2 more seats needed
continuous space map            Max block is first assigned         Max block modified
{                               {                                   {
    0: [[0 5]]                      1: [[0 5]]                          1: [[2 5]]
    1: [[0 5]]                  }                                   }
}                                   
{                               {                                   {
    6 : [0 1]                       6: [1]                              4: [1]
}                               }                                   }
```

After a block has been assigned, it is removed from both the rowSpaceMap and the continuousSpaceMap and if it is partially assigned, then the corresponding block is modified accordingly in both the places.

A seatHoldId is generated and pushed to the heldTickets map along with the assigned seatHold object.
The **SeatHold** object contains a List of **SeatsBlock** that says in which row that block is and from where it starts and ends.
##### Release Tickets
A timer task is scheduled at the end of Hold Tickets and the default time in the constants file is set to 120 seconds.
The task checks for if the seatHold object has been reserved or is still present in the heldTickets map.

If present, it iterates through each SeatsBlock present in that SeatHold object, and adds that block to the rowSpaceMap and its corresponding space to continuousSpaceMap.
After adding, it checks the previous and next block in rowSpaceMap to see if they are continuous and merges into one single continuous block and accordingly updates the continuousSpaceMap too.
```
initial row space map and       A new block in row 0            new block merged with left block       Then checks with
continuous space map            seat 2 and 3 is released        {                                      right side block
{                               {                                   0: [[0 3] [4 5]]                   {
    0: [[0 1] [4 5]]                0: [[0 1] [2 3] [4 5]]          1: [[0 5]]                             0: [[0 5]]
    1: [[0 5]]                      1: [[0 5]]                  }                                          1: [[0 5]]
}                               }                               {                                      }
{                               {                                   2: [0]                             {
    2: [0 0]                        2: [0 0 0]                      4: [0]                                 6: [0 1]
    6: [1]                          6: [1]                          6: [1]                             }
}                               }                               }                                      Flow of the maps
```
##### Confirm Tickets
Checks if the requested seatHold object is present in the heldTickets map and if present, removes it from the map and generates and returns a code.
If not, the wait time on the blocks has expired and the object has been removed from the map by a scheduled task and a SessionExpiredException is thrown.
### Testing
Tested the business logic of the application to ensure that the code behaves as expected and that it does not break at any point during its execution.
Wrote unit tests to check if the rowSpaceMap and continuousSpaceMap get modified correctly during the event of hold and release of tickets.
### Run the application
The jar is built and packaged and located in the target folder.

The executable java file is the App.java, a single threaded simulation of the TicketServiceImplementation to test the functionality of the application by interacting with the user.
### Expected Output
```
Enter the number of rows in the venue: 3
Enter the number of columns in the venue: 4

Press x if you want to stop booking tickets.

Menu 
1. Availability 
2. Hold Tickets 
3. Book Tickets
Your choice (1, 2 or 3)? 1

Available seats: 12                 //rows*columns

Press any key to proceed.

Menu 
1. Availability 
2. Hold Tickets 
3. Book Tickets
Your choice (1, 2 or 3)? 2

Number of seats to hold: 3
Email ID: qwerty

Your hold ID is: 672221262          //3 tickets are being held by user qwerty
Seats assigned in row: 0            //continuous 3 tickets have been assigned in row 0
Seat Number: 0
Seat Number: 1
Seat Number: 2

Press any key to proceed.

Menu 
1. Availability 
2. Hold Tickets 
3. Book Tickets
Your choice (1, 2 or 3)? 1

Available seats: 9                  //previous (12) - held (3)

Press any key to proceed.

Menu 
1. Availability 
2. Hold Tickets 
3. Book Tickets
Your choice (1, 2 or 3)? 2

Number of seats to hold: 5
Email ID: abcd

Your hold ID is: 1479662593         //5 tickets are being held by user abcd
Seats assigned in row: 1            //4 continuous tickets are assigned in row1
Seat Number: 0                      //Max possible allocation in one row is 4 in this venue
Seat Number: 1
Seat Number: 2
Seat Number: 3
Seats assigned in row: 0            //Allocating the left out person in row 0 seat 3
Seat Number: 3                      //seat 0, 1 and 2 is being held by user qwerty

Press any key to proceed.

Menu 
1. Availability 
2. Hold Tickets 
3. Book Tickets
Your choice (1, 2 or 3)? 3

Enter hold ID: 1479662593           //user abcd is confirming his seats
Enter email: abcd

Your confirmation code is: a2d83e3a-a689-4d68-8d3c-0aa18ba4c8cf

Press any key to proceed.

Menu 
1. Availability 
2. Hold Tickets 
3. Book Tickets
Your choice (1, 2 or 3)? 1

Available seats: 7                  //previous availability was 9 (12-3) and after that abcd held 5
                                    //so it should effectively be 4 but time has expired on qwerty's hold
Press any key to proceed.           

Menu 
1. Availability 
2. Hold Tickets 
3. Book Tickets
Your choice (1, 2 or 3)? 2

Number of seats to hold: 8          //trying to hold more seats than available
Email ID: aqws

The requested number of seats is not available.

Press any key to proceed.

Menu 
1. Availability 
2. Hold Tickets 
3. Book Tickets
Your choice (1, 2 or 3)? 3

Enter hold ID: 672221262            //qwerty is confirming his hold request
Enter email: qwerty                 //time has expired and those seats have been added back

The wait time on the tickets that were being held has expired.

Press any key to proceed.

Menu 
1. Availability 
2. Hold Tickets 
3. Book Tickets
Your choice (1, 2 or 3)? x

Process finished with exit code 0
```