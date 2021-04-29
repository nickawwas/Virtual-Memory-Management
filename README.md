# Virtual Memory Manager
Simulate Virtual Memory Management and Concurrency Control on a Multicore Computer

## Documentation
### Overview
> Uses Limited Main Memory and Unlimited Virtual Disk Space
> 
> Divides Memory into Pages, Storing Variables (Id, Value)

> Virtual Memory Manager (VMM) Offers 3 Commands
> - Store - Adds Variable to a Page in Memory or Disk If Main Memory is Full
> - Release - Removes then Returns Value from Memory or Disk Given a Variable Id
> - Lookup - Searches then Returns Value from Memory or Disk Given a Variable Id
>   - If Present in Disk and Memory is Full, Create Page Fault and Swap with LRU Data

> Reads Main Memory Size, Number of Cores, and Processes
> 
> Outputs Process and Command Execution Log

### Scheduler
> Schedules Processes on Multiple Cores using FIFO Algorithm
> - Moves Ready Processes to Ready Queue
> - Schedules Processes on Multiple Cores using Semaphore Representing Cores Available
>   - Creates a Thread for Ready Processes When Cores Become Available (Acquires Permit)
> - Removes Terminated Processes from Ready Queue 

### Process Runnable
> Runs Process Using Thread Runnable to Simulate Process Scheduling
> - Assigns Process a Command Using Try Acquire to Get Semaphore Command Permit
> - Changes Memory Manager State to Run Command
> - Waits for MMU Thread to Complete Running Command
> - Sleeps Process to Give Other Process an Opportunity to Access Memory
> - Releases Semaphore Core Permit Upon Termination of Process

### Memory Manager (MMU)
> Maps Logical to Physical Addresses and Runs Commands
>  - Stores Page Id and Value in Main Memory Using LRU Algorithm and in Virtual Memory if Main is Full
>      - Updates Pages with Same Id and Different Value if Page Already Exists
>
>  - Releases Page from Main and Virtual Memory Given Page Id
>       - Attempt 1: Searches Page Id in Main Memory
>       - Attempt 2: Searches Page Id in Large Disk
> 
>  - Looks Up Page in Main and Virtual Memory Given Page Id
>       - Attempt 1: Searches Page Id in Main Memory, Accesses Page Updating LRU List
>       - Attempt 2: Searches Page Id in Large Disk, Accesses Page Resulting in Page Fault
>           - Releases Page from Large Disk
>           - Stores Page in Main Memory if Not Full
>           - Swaps Page into Main Memory with LRU Otherwise

### Disk Space
> Simulate Access to Virtual Memory by Reading and Writing Pages to File
> - Store Page in Disk by Writing to File
> - Lookup Page on Disk by Scanning File
> - Release Page in Disk by Scanning File, Clearing its Content, and Rewriting Pages Without Specified Page

## Dependencies
Used log4j to Log Output to File and Maven to Build the Java Project
