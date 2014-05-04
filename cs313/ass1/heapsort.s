	.file	"heapsort.c"
	.text
	.p2align 4,,15
	.globl	heapsort
	.type	heapsort, @function
heapsort:
.LFB11:
	.cfi_startproc
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset 3, -16
	movl	%edi, %ebx			# int i; i=last; Initialize the counter i to last
	call	heapify_array		# heapify_array(last); last is in %rbx
	testl	%ebx, %ebx			# test whether %ebx is equal, less than, or greater than 0
	js	.L1						# if %ebx is less than 0,
								# jump to L1 and do not execute loop
	.p2align 4,,10
	.p2align 3
.L3:
	movl	%ebx, %edi			# start of the loop, copy the new i value into %edi
								# first i value equals to last
	call	extract_max			# heap[i] = extract_max(i); get max and store in %eax
	movslq	%ebx, %rdx			# sign extend the index i and store in %rdx
	subl	$1, %ebx			# i--; 
	cmpl	$-1, %ebx			# compare i with -1
	movl	%eax, heap(,%rdx,4)	# heap[i] = extract_max(i); copy the next max value into the ith position of the array
	jne	.L3						# continue the loop if i is not equal to -1
.L1:
	popq	%rbx				# restore rbx value
	.cfi_def_cfa_offset 8
	ret							# return
	.cfi_endproc
.LFE11:
	.size	heapsort, .-heapsort
	.ident	"GCC: (SUSE Linux) 4.6.2"
	.section	.comment.SUSE.OPTs,"MS",@progbits,1
	.string	"Ospwg"
	.section	.note.GNU-stack,"",@progbits
