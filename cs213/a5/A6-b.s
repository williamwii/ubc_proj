# .pos is a pseudonym for .address
.pos 0x0
                 ld   $0x2028, r5         
                 ld   $0x300, r0          
                 gpc  r6                  
                 inca r6                  
                 j    0x0(r0)             
                 halt                     
.pos 0x100
                 .long 0x00001000         
.pos 0x200
                 ld   0x0(r5), r0         
                 ld   0x4(r5), r1         
                 ld   $0x100, r2          
                 ld   0x0(r2), r2         
                 ld   (r2, r1, 4), r3     
                 add  r3, r0              
                 st   r0, (r2, r1, 4)     
                 j    0x0(r6)             
.pos 0x300
                 ld   $0xfffffff4, r0     
                 add  r0, r5              
                 st   r6, 0x8(r5)         
                 ld   $0x1, r0            
                 st   r0, 0x0(r5)         
                 ld   $0x2, r0            
                 st   r0, 0x4(r5)         
                 ld   $0xfffffff8, r0     
                 add  r0, r5              
                 ld   $0x3, r0            
                 st   r0, 0x0(r5)         
                 ld   $0x4, r0            
                 st   r0, 0x4(r5)         
                 ld   $0x200, r0          
                 gpc  r6                  
                 inca r6                  
                 j    0x0(r0)             
                 ld   $0x8, r0            
                 add  r0, r5              
                 ld   0x0(r5), r1         
                 ld   0x4(r5), r2         
                 ld   $0xfffffff8, r0     
                 add  r0, r5              
                 st   r1, 0x0(r5)         
                 st   r2, 0x4(r5)         
                 ld   $0x200, r0          
                 gpc  r6                  
                 inca r6                  
                 j    0x0(r0)             
                 ld   $0x8, r0            
                 add  r0, r5              
                 ld   0x8(r5), r6         
                 ld   $0xc, r0            
                 add  r0, r5              
                 j    0x0(r6)             
.pos 0x1000
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
.pos 0x2000
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
                 .long 0x00000000         
