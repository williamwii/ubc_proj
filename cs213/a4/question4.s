.address 0x100
                 ld   $0x0, r0            # r0 = temp_i = 0
                 ld   $a, r1              # r1 = address of a[0]
                 ld   $min, r2            # r2 = address of min
                 ld   0x0(r2), r2         # r2 = 1000000
				 ld   $0xfffffff6, r4     # r4 = -10

loop:            mov  r0, r5              # r5 = temp_i
                 add  r4, r5              # r5 = temp_i-10
                 beq  r5, end_loop        # if temp_i=10 goto +10
                 ld   (r1, r0, 4), r3     # r3 = a[temp_i]
                 not  r3                  # r3 = ~r3
                 inc  r3                  # r3++
				 mov  r2, r6              # r6 = temp_min
                 add  r3, r6              # temp_min = temp_min - a[temp_i]
                 bgt  r6, then            # temp_min = a[temp_i](if a[temp_i]<temp_min)
				 br   inc                 # goto +1
then:            ld   (r1, r0, 4), r2     # temp_min = a[temp_i]
inc:             inc  r0                  # temp_i++
                 br   loop                # goto -11
end_loop:        ld   $min, r1              # r1 = address of s
                 st   r2, 0x0(r1)         # min = temp_min
                 st   r0, 0x4(r1)         # i = temp_i
                 halt                     
.address 0x1000
min:             .long 0x000f4240         # min
i:               .long 0x00000000         # i
a:               .long 0x00000002         # a[0]
                 .long 0x00000028         # a[1]
                 .long 0x00000006         # a[2]
                 .long 0x00000050         # a[3]
                 .long 0x0000000a         # a[4]
                 .long 0x00000078         # a[5]
                 .long 0x0000000e         # a[6]
                 .long 0x00000010         # a[7]
                 .long 0x00000012         # a[8]
                 .long 0x00000014         # a[9]
				 