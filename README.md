# Virtual Memory Management
Simulate Virtual Memory Management and Concurrency Control on a Multicore Computer

## Documentation
- Uses Limited Main Memory and Unlimited Virtual Disk Space
- Divided into Pages, Storing Variables Consisting of Id and Value
    

- Virtual Memory Manager (VMM) Offers 3 Commands
- Store - Adds Variable to a Page in Memory or Disk If Main Memory is Full
- Release - Removes then Returns Value from Memory or Disk Given a Variable Id
- Lookup - Searches then Returns Value from Memory or Disk Given a Variable Id
- If Present in Disk and Memory is Full, Swap with LRU Data
  

- Reads Number of Cores and Processes
- Outputs Process and Command Execution Log

