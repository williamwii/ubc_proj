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

	irmovl $10,  %eax         # %eax = 10
	irmovl $fib, %edx		 # %edx = address of function fib
	call (%edx)				 # call fib(10), %eax = 55
	halt


.pos 0x1000
#
# compute the fibonnaci number of %eax
# %eax >= 0
# result is stored in %eax
#
fib:
	pushl %esi				# store %esi
	irmovl $random, %esi	# %esi = memory location of random
	rrmovl %eax, %ecx		# %ecx = %eax
	irmovl $0, %eax			# %eax = 0, the first fib number
	irmovl $1, %ebx			# %ebx = 1, the second fib number
loop:
	call (%esi)				# call random, does nothing
	andl %ecx, %ecx
	jle loop_done
	rrmovl %eax, %edx		# %edx = %eax
	rrmovl %ebx, %eax		# %eax = %ebx
	addl %edx, %ebx			# %ebx += %edx, %ebx = the next fib number
	isubl $1, %ecx			# %ecx --
	jmp loop
loop_done:
	popl %esi				# restore %esi
	ret
	
.pos 0x2000
# Does nothing
random:
	ret

#
# Stack (32 thirty-two bit words is more than enough here).
#
.pos 0x3000
top:	            .long 0x00000000,0x20     # top of stack.
bottom:             .long 0x00000000          # bottom of stack.
