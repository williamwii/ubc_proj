#include <stdio.h>

int a_size = 5;
int a[5] = {2,4,16,7,1};
int max = 0;

void A6a(){
	int temp_max = -1;
	int i = 0;
	while ( i<a_size ){
		if ( a[i]>temp_max )
			temp_max = a[i];
		i++;
	}
	max = temp_max;
}

int main(){
	A6a();
	printf("%d\n",max);
	return 0;
}