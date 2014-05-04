.address 0x100
                 ld   $0x0, r0            # r0 = 0
                 ld   $a, r1              # r1 = address of a
                 st   r0, 0x0(r1)         # a = 0
                 ld   $b, r0              # r0 = address of b
                 ld   $a, r1              # r1 = address of a
                 ld   0x0(r1), r2         # r2 = a
                 st   r2, (r0, r2, 4)     # b[a] = a
				 ld   $0x1, r3            # r3 = 1
				 st   r3, (r0, r3, 4)     # b[1] = r3
				 ld   (r0, r3, 4), r1     # r1 = b[1]
                 halt                     # halt
.address 0x1000
a:               .long 0xffffffff         # a
.address 0x2000
b:               .long 0xffffffff         # b[0]
                 .long 0xffffffff         # b[1]
                 .long 0xffffffff         # b[2]
                 .long 0xffffffff         # b[3]
                 .long 0xffffffff         # b[4]
                 .long 0xffffffff         # b[5]
                 .long 0xffffffff         # b[6]
                 .long 0xffffffff         # b[7]
                 .long 0xffffffff         # b[8]
                 .long 0xffffffff         # b[9]
