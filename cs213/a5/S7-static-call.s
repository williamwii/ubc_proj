.address 0x100
foo:             ld   $ping, r0           # r0 = address of ping ()
                 gpc  r6                  # r6 = pc of next instruction
                 inca r6                  # r6 = pc + 4
                 j    0x0(r0)             # goto ping ()
                 halt                     # halt
.address 0x500
ping:            j    0x0(r6)             # return
