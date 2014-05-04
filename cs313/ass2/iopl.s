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
# iaddl V, rB; isubl V, rB; ixorl V, rB; iandl V, rB;
# imull V, rB; idivl V, rB; imodl V, rB;
#
main:	irmovl bottom,  %esp     # initialize stack

	xorl   %eax,%eax         # %eax = 0
	iaddl  $4,  %eax	 	 # %eax += 4, %eax = 4;
	iaddl  $-2, %eax		 # %eax -= 2, %eax = 2
	iaddl  $2147483647, %eax # %eax += 2147483647, overflow, %eax = -2147483647
	isubl  $2,  %eax		 # %eax -= 2, overflow, %eax = 2147483647
	isubl  $2147483647, %eax # %eax -= 2147483647, %eax = 0
	ixorl  $1,  %eax		 # %eax | 1, %eax = 1
	ixorl  $286331152, %eax  # %eax | 11111110, %eax = 11111111 ( bitwise )
	iandl  $4369, %eax       # %eax & 00001111, %eax = 00001111
	iandl  $286326785, %eax  # %eax & 11110001, %eax = 1
	imull  $100,%eax		 # %eax *= 100, %eax = 100
	imull  $-2,%eax		 	 # %eax *= -2, %eax = -200
	idivl  $4, %eax		     # %eax /= 4, %eax = -50
	idivl  $0, %eax			 # %eax /= 0, %eax = -50, dividing by 0
							 # %eax does not change
	idivl  $-5,%eax			 # %eax /= -5, %eax = 10
	imodl  $15,%eax			 # %eax = %eax % 15, %eax = 10
	imodl  $8, %eax			 # %eax = %eax % 8, %eax = 2
	imodl  $2, %eax 		 # %eax = %eax % 2, %eax = 0
	iaddl  $10,%eax			 # %eax += 10, %eax = 10
	imull  $0, %eax			 # %eax *= 0, %eax = 0
	halt


#
# Stack (32 thirty-two bit words is more than enough here).
#
.pos 0x3000
top:	            .long 0x00000000,0x20     # top of stack.
bottom:             .long 0x00000000          # bottom of stack.
