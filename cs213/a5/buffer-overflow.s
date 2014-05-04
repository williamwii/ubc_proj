.address 0x100
main:     	ld $0x3028, r5    	#r5 = bottom of stack
			ld $copy, r0      	#r0 = copy
			gpc r6            	#r6 = pc
			inca r6           	#r6 = ra
			j  (r0)           	#copy()
			halt

.address 0x200
copy:    	deca r5            	#sp--
			st   r6, (r5)      	#store return address on stack
			ld   $src, r0      	#r0 = src
			deca r5            	#dst[2]
			deca r5
			ld   $0x0, r1      	#r1 = i = 0
while:   	ld   (r0,r1,4), r3 	#r3 = src[i]
			beq  r3, end_while 	#goto end_while if src[i]==0
			st   r3, (r5,r1,4) 	#dst[i] = src[i]
			inc  r1            	#i++
			br while           	# goto while
end_while: 	ld $0x1500, r0   	#r0 = address of i
			st r1, (r0)      	#store i in memory
			inca r5          	#deactivate frame
			inca r5
			ld (r5), r6      	# r6 = return address
			inca r5          	#sp++
			j  (r6)          	#return


.address 0x300
attack:     ld $0xffffffff, r0    #r0 = -1
			ld $0xffffffff, r1    #r1 = -1
			ld $0xffffffff, r2    #r2 = -1
			ld $0xffffffff, r3    #r3 = -1
			ld $0xffffffff, r4    #r4 = -1
			ld $0xffffffff, r5    #r5 = -1
			ld $0xffffffff, r6    #r6 = -1
			ld $0xffffffff, r7    #r7 = -1
			halt

.address 0x1000
src:        .long 0x1          	#src
			.long 0x1
			.long 0x300


.address 0x1500
			.long 0x0          	#i

# the runtime stack          
.address 0x3000
			.long 0           	#stack
			.long 0
			.long 0
			.long 0
			.long 0
			.long 0
			.long 0
			.long 0
			.long 0
			.long 0

