## Subsystem 2
This system has 2 cores, one Ready queue and none Waiting queue. scheduler for this system uses Shortest Remaining Time First Algorithm. Because there isn't a waiting queue, we might encounter deadlock, so in order to prevent that (deadlock prevention) , on a core if a task is chosen to run on it but there are not enough resources for it, the core will be stalled until resources are available. in real world this might not be a good idea, however since in our project, we must allocate all needed resources at first, this was definitely the right choice. we could also have that task to be moved to the end of ready queue but that would be against SRTF algorithm.
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