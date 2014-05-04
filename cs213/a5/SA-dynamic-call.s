.address 0x100
start:    ld   $0x2028, r5    # r5 = bottom of stack
          ld   $bar, r0       # r0 = bar
          gpc  r6             # r6 = pc
          inca r6             # r6 = ra
          j    (r0)           # bar ()
          halt
.address 0x200
bar:      deca r5           # sp--
          st   r6, (r5)     # store r6 on stack
          deca r5           # sp--
          ld   $a, r0       # r0 = &a
          ld   (r0), r0     # r0 = a
          st   r0, (r5)     # arg0 = a
          ld   $foo, r0     # r0 = foo
          gpc  r6           # r6 = pc
          inca r6           # r6 = ra
          j    (r0)         # foo (a)
          ld   $b, r0       # r0 = &b
          ld   (r0), r0     # r0 = b
          st   r0, (r5)     # arg0 = b
          ld   $foo, r0     # r0 = foo
          gpc  r6           # r6 = pc
          inca r6           # r6 = ra
          j    (r0)         # foo (b)
          inca r5           # sp++
          ld   (r5), r6     # restore r6 from stack
          inca r5           # sp ++
          j    (r6)         # return
.address 0x300
foo:      deca r5           # sp--
          st   r6, (r5)     # save r6 on stack
          ld   4(r5), r0    # r0 = a, arg0 of foo(a)
          gpc  r6           # r6 = pc
          inca r6           # r6 = ra
          j    *0(r0)       # a->ping ()
          gpc  r6           # r6 = pc
          inca r6           # r6 = ra
          j    *4(r0)       # a->pong ()
          ld   (r5), r6     # restore r6 from stack
          inca r5           # sp++
          j    (r6)         # return

.address 0x400
A_ping:   j (r6)            # return
A_pong:   j (r6)            # return
B_ping:   j (r6)            # return
B_wiff:   j (r6)            # return


# static variables with snapshot of their dynamic values
.address 0x500
a:        .long anA         # a: assigned dynamically by new_A
b:        .long aB          # b: assigned dynamically by new_B 

# snapshot of two objects that would be dynamically alloced in heap
.address 0x1000
anA:      .long A_ping      # allocated dynamically by new_A
          .long A_pong
aB:       .long B_ping      # allocated dynamically by new_B
          .long A_pong
          .long B_wiff

# the runtime stack          
.address 0x2000
stack:    .long 0
          .long 0
          .long 0
          .long 0
          .long 0
          .long 0
          .long 0
          .long 0
          .long 0
          .long 0
          