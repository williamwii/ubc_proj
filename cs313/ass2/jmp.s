#
# Calling conventions:
#     %eax, %ebx, %ecx, %edx can be used by subroutines without saving.
#     %ebp, %esi, %edi must be saved and restored if they are used.
#     %esp can not be used except for its normal use as a stack pointer.
#
#     argument are passed through registers %eax, %ebx, except for subroutine
#     heapify_node_helper (see its documentation).
#
#     values are returned through %eax
#
.pos 0x100

#
# Testing for the new instructions
# call (rA)
#
main:	irmovl bottom,  %esp     # initialize stack

	irmovl $1, %eax         # %eax = 1
	irmovl $0, %ebx			# %ebx = 0

loop:
	jmp *switch(%ebx, 4)	# switch table
	iaddl $4, %eax			# %eax += 4
	jmp end_switch			# break
	isubl $-5, %eax			# %eax -= -5
	jmp end_switch			# break
	imull $4, %eax			# %eax *= 4
	jmp end_switch			# break
end_switch:
	iaddl $1, %ebx			# %ebx ++
	rrmovl %ebx, %ecx		# %ecx = %ebx
	isubl $2, %ecx			# compare %ecx to 2
	jle loop				# jump and continue loop if %ecx is less than 2
	
	halt					# expected value of %eax is 40

.pos 0x1000
# Switch table of function locations
switch:
	.long 0x118				# addl
	.long 0x123				# subl
	.long 0x12e				# mull
	

#
# Stack (32 thirty-two bit words is more than enough here).
#
.pos 0x3000
top:	            .long 0x00000000,0x20     # top of stack.
bottom:             .long 0x00000000          # bottom of stack.
