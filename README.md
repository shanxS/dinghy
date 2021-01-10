## raft
### build steps

gradle clean

gradle build

gradle :installdist


### current state

Leader selection works for some definition of "works"

### next steps

1. write tests for leader election
2. figure better log management

### testing

#### single node [DONE]
 >starts as Follower and flip flops b/n Candidate and Follower

#### 3 node system
>Election
>> 1. elects a leader in X time
>> 2. elected leader remains leader 
>> 3. leader sends heartbeat and followers acknowledge it

>High Consistency
>>Leader failure
>>> 1. followers flip flop b/n Candidate and Follower
>>> 2. leader comes online and system returns to steady state
>>
>>Follower failure
>>> 1. leader becomes follower
>>> 2. followers flip flop b/n Candidate and Follower
>>> 3. failed node comes online and system returns to steady state after election

>High Availability
>>Leader failure
>>> 1. followers figure leader is down, do `partial election` and system returns to steady state
>>> 2. leader comes online, becomes follower, catches up with work and system returns to steady state
>>
>>Follower failure
>>> 1. leader continues
>>> 2. failed node comes online, catches up with work and system returns to steady state

>High Availability and Consistency
>>Leader failure
>>> 1. followers flip flop b/n Candidate and Follower for X time
>>> 2. followers decide to spin another node and system returns to steady state
>>> 3. leader comes online, figures it is out of date and that system is in steady state, dumps it's state and shutsdown
>>
>>Follower failure
>>> 1. leader figures a follower is down and spins up a node after X time and system returns to steady state
>>> 2. failed node comes online, figures it is out of date and that system is in steady state, dumps it's state and shutsdown
 

### future ideas

1. can we have a system which looks at longs and figure what state system is in?
 - how many nodes are up

- are we voting or are we in steady state
- what term is going on who is leader
- are the histories of state of all nodes same  