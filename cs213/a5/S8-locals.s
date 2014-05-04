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
                 ld   $b, r0              # address of b ()
                 gpc  r6                  # r6 = pc
                 inca r6                  # r6 = r6 + 4
                 j    0x0(r0)             # goto b ()
                 ld   0x0(r5), r6         # restore r6 from stack
                 inca r5                  # sp+=4
                 j    0x0(r6)             # return
.address 0x300
b:               ld   $0xfffffff8, r0     # r0 = -8 = -(size of activation frame)
                 add  r0, r5              # create activation frame on stack
                 ld   $0, r0              # r0 = 0
                 st   r0, 0x0(r5)         # l0 = 0
                 ld   $0x1, r0            # r0 = 1
                 st   r0, 0x4(r5)         # l1 = 1
                 ld   $0x8, r0            # r0 = 8 = size of activation frame
                 add  r0, r5              # teardown activation frame
                 j    0x0(r6)             # return
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
