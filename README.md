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


### future ideas

1. can we have a system which looks at longs and figure what state system is in?
 - how many nodes are up

- are we voting or are we in steady state
- what term is going on who is leader
- are the histories of state of all nodes same  