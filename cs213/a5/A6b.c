#include <stdio.h>

int a[10];
int* ap = &a;

int main(){
	af(3,4);
	/*int i = 0;
	while ( i<10 ){
		printf("%d\n",a[i]);
		i++;
	}*/
	return 0;
}

void af(int p, int q){
	int i = 1;
	int j = 2;
	bf(p,q);
	bf(i,j);
}

void bf(int m, int n){
	m += *(ap+n);
	*(ap+n) = m;	
}
