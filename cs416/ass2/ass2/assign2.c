//
//  assign2.c
//  ass2
//
//  Created by William You on 2/23/2014.
//  Copyright (c) 2014 William You. All rights reserved.
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <time.h>

int op_num;
pthread_mutex_t lock;

typedef struct queue_obj {
    int value;
    struct queue_obj *next;
} queue_obj_t;

typedef struct queue_operation {
    int operation; // 1 to insert, 0 to remove
    int value;
} operation_t;

typedef struct generator_state {
    int state[4];
} generator_state_t;

void enqueue(queue_obj_t **queue, int value) {
    queue_obj_t *q_obj = (queue_obj_t*)malloc(sizeof(queue_obj_t));
    q_obj->value = value;
    q_obj->next = NULL;
    
//    printf("Enqueuing value: %i\n", q_obj->value);
    if (*queue == NULL) {
        *queue = q_obj;
    }
    else {
        queue_obj_t *q = *queue;
        while (q->next != NULL)
            q = q->next;
        q->next = q_obj;
    }
}

int dequeue(queue_obj_t **queue) {
    if (*queue == NULL) {
//        printf("Dequeuing empty queue\n");
        return -1;
    }
    else {
//        printf("Dequeuing value: %i\n", (*queue)->value);
        queue_obj_t *next = (*queue)->next;
        int value = (*queue)->value;
        free(*queue);
        *queue = next;
        return value;
    }
}

int load_generator(operation_t *op, generator_state_t **s)
{
    if ( *s == NULL )
    {
        *s = (generator_state_t *) malloc(sizeof(generator_state_t));
        // initialize state
    }
    // set value for op­>operation and op­>value
    if (random() % 2 == 0)
        op->operation = 1;
    else
        op->operation = 0;
    op->value = random();

    return 1; // 1 success ­­ return 0 for failure
}

void *child_labour(void *arg) {
    queue_obj_t **queue = (queue_obj_t **)arg;

    int s = pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
    if (s != 0) printf("Can not set cancel state\n");

    operation_t op;
    generator_state_t *state = NULL;
    while ( load_generator(&op, &state) ) {
        pthread_mutex_lock(&lock);
        if (op.operation == 0) {
            enqueue(queue, op.value);
        }
        else if (op.operation == 1) {
            int v = dequeue(queue);
        }

        op_num++;
        pthread_mutex_unlock(&lock);
        pthread_testcancel();
    }

    pthread_exit(NULL);
}

int main(int argc, const char * argv[])
{
    op_num = 0;
    pthread_mutex_init(&lock, NULL);

    pthread_t *t;
    int i, res, p_num, t_num;

    if (argc != 5) {
        printf("this program takes exactly 4 arguments\n");
        return -1;
    }

    p_num = 1;
    t_num = 5;

    i = 0;
    while (i < argc) {
        if (strcmp(argv[i], "-p") == 0) {
            i++;
            p_num = atoi(argv[i]);
            if (p_num < 1) p_num = 1;
            if (p_num > 100) p_num = 100;
        }
        else if (strcmp(argv[i], "-t") == 0) {
            i++;
            t_num = atoi(argv[i]);
            if (t_num < 1) t_num = 1;
            if (t_num > 300) t_num = 300;
        }
        i++;
    }

    // Open file for output
    FILE *file = fopen("result.txt", "a+");
    if (file == NULL) {
        printf("Cannot open output file");
        return -1;
    }

    // The queue object
    queue_obj_t *queue = NULL;

    // Create processes
    t = (pthread_t *)malloc(sizeof(pthread_t) * p_num);
    for (i=0; i<p_num; i++) {
        res = pthread_create(&t[i], NULL, child_labour, &queue);
    }

    clock_t end_time = clock() + t_num * CLOCKS_PER_SEC;
    while (clock() < end_time);

    for (i=0; i<p_num; i++) pthread_cancel(t[i]);
    for (i=0; i<p_num; i++) pthread_join(t[i], NULL);

    printf("# of threads=%i time (seconds)=%i total number of operations=%i\n", p_num, t_num, op_num);
    fprintf(file, "# of threads=%i time (seconds)=%i total number of operations=%i\n", p_num, t_num, op_num);
    while (queue != NULL) {
        queue_obj_t *next = queue->next;
        free(queue);
        queue = next;
    }
    free(t);
}

