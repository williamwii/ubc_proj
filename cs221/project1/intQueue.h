#ifndef QUEUE_H_
#define QUEUE_H_

class Queue{

	public:
	
		Queue();
		bool is_empty();
		bool is_full();
		void expand_queue();
		int first();
		int last();
		void enqueue(int item);
		int dequeue();

	private:
	
		int* ary;
		int ary_size;
		int item_num;
		int front;
		int back;
		
};

#endif
