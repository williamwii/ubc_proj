.address 0x100
start:           ld   $0x1028, r0         # base of stack
                 mov  r0, r5              # initialize stack pointer
                 ld   $foo, r0            # r0 = address of foo ()
                 gpc  r6                  # r6 = pc
                 inca r6                  # r6 = r6 + 4
                 j    0x0(r0)             # goto foo ()
                 halt                     
.address 0x200
foo:             deca r5                  # sp-=4
                 st   r6, 0x0(r5)         # save r6 to stack
                 ld   $0x1, r0            # arg0 (r0) = 1
                 ld   $0x2, r1            # arg1 (r1) = 2
                 ld   $add, r3            # address of add ()
                 gpc  r6                  # r6 = pc
                 inca r6                  # r6 = r6 + 4
                 j    0x0(r3)             # goto add ()
                 ld   $s, r1              # r1 = address of s
                 st   r0, 0x0(r1)         # s = add (1,2)
                 ld   0x0(r5), r6         # restore r6 from stack
                 inca r5                  # sp+=4
                 j    0x0(r6)             # return
.address 0x300
add:             add  r1, r0              # return (r0) = a (r0) + b (r1)
                 j    0x0(r6)             # return
.address 0x280
s:               .long 0x00000000         # s
.address 0x1000
stack:           .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
