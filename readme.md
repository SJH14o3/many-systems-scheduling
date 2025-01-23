## Subsystem 1
This system has 3 cores, Ready queue for each core and a Waiting queue. scheduler for this system uses weighted round-robin. before system runs cores it would assign each task a quantum based of it's burst time. minimum burst time gets one time unit as quantum and the maximum burst time get the highest quantum that can be adjusted in function setQuantums. also system is a soft affinity meaning we must let the task run in their desired core.
### how does it work?
first in every time unit, sub system will check if new processes have arrived, if there are, they will be added to the ready queue of the task's desired core.
this is how cores works:
[note that for allocating for this system we added a function that would check if task can be assigned and if it can, it will be allocated there, the whole process is inside a synchronized block]
if core is not idle then it would run for a time unit since task is already allocated.
if a core is idle, it will get a process from waiting queue but in this specific way: first it will only iterate over the tasks that wants to run on it. if we found a task, but it cannot be allocated, we continue iterating. if waiting queue is empty or no task that had affinity for the core or no task could be allocated then null will be returned. if a task is found then it will be assigned to core, and it will be removed from waiting queue. note that new tasks will be added to end of waiting queue to reduce starvation.
if task is null then we try to get a task from ready queue only if a task it can be allocated. first it would iterate over ready queue. if ready queue is empty then null value will be returned. when iterating, if task cannot be allocated, it will be removed from ready queue, and it will be added to waiting queue. if we found a task that can be allocated it would be allocated. null will be returned if queue is empty, or it got empty (no task could be allocated so all tasks are in waiting queue now)
again if task is null then we try pulling process from other cores ready queue which is against affinity but this is a soft affinity and for speed purpose we can ignore affinity. task pulling is done with checking other cores circularly. if a task is found then it will be assigned to this core. note that if task is finished on this core, it will return to the original ready queue to increase affinity. same thing happens a task cannot be allocated it will move to waiting queue. null will be returned if no task could be found or allocated.
finally as the last resort if task is null still then we iterate over all of waiting queue.
if task is still null then no task could be allocated so core will be idle.
also when a task arrives it will be added to the end of ready queue.
a task will be deallocated if it ran out of quantum, or it's burst time is over.
system will be finished when all task are finished.

## Subsystem 2
This system has 2 cores, one Ready queue and no Waiting queue. scheduler for this system uses Shortest Remaining Time First Algorithm. Because there isn't a waiting queue, we might encounter deadlock, so in order to prevent that (deadlock prevention) , on a core if a task is chosen to run on it but there are not enough resources for it, the core will be stalled until resources are available. in real world this might not be a good idea, however since in our project, we must allocate all needed resources at first, this was definitely the right choice. we could also have that task to be moved to the end of ready queue but that would be against SRTF algorithm.
### how does it work?
first in every time unit, sub system will check if new processes have arrived, if there are, they will be added to the ready queue.
this is how cores works:
if a core is idle, it will get a process from ready queue.
if not idle it means either it was stalled or it is running.
if a new process have arrived, it would check if new task has shorter remaining time, the new task will enter the core and old task will be added to ready queue. if new task is not better, then nothing will be changed.
last scenario is when core was stalled, it will check if it can be allocated.
if a core can't allocate resources to a task because of insufficient resources, it will be stalled and caught in an exception block.
if ready queue is empty and core tried to get a task from it, it will be idle and empty queue exception is caught in an exception block.
if task is allocated successfully, then it will be run for one time unit.
if remain time of task reaches 0, it will be deallocated be removed from the core.
system will be finished when all task are finished.

## Subsystem 3

### how does it work?

## Subsystem 4
This system has 2 cores, a Ready queue a Waiting queue. scheduler for this system uses FCFS. In this system, some task has a prerequisite and if their prerequisite is not finished, they cannot run.

### how does it work?
first in every time unit, sub system will check if new processes have arrived, if there are, they will be added to the ready queue if their prerequisite is complete
if not it will be added to waiting queue instead. both queues use a priority queue with lower arrival time as priority.
[note that for allocating for this system we added a function that would check if task can be assigned and if it can, it will be allocated there, the whole process is inside a synchronized block]
if core is not idle then it would run for a time unit since task is already allocated.
if a core is idle, it will get a process from ready queue by polling a process as long as a process can be allocated or queue get's empty. all the removed processes that could not be allocated will be moved to waiting queue. if we could poll a process from ready queue we check waiting queue. we clone it's priority queue first then start polling out processes until a process that can be allocated and it's prerequisite is finished is found. if no such process is found then core will be idle. if we have a process then it will ran until is finished. when is finished, all of the task that had the finished task as prerequisite will come back to ready queue. system will finished when all tasks are done and no new task is coming and queues are empty.