## raft
### build steps

gradle clean

gradle build

gradle :installdist


### current state

follower tansitions to candidate and candidate requests vote

### next steps

1. impl timeout in follower
2. read input from cmd line to start multiple nodes
3. test that system goes from init -> voting -> steady state


### future ideas

1. can we have a system which looks at longs and figure what state system is in?
 - how many nodes are up

- are we voting or are we in steady state
- what term is going on who is leader
- are the histories of state of all nodes same  