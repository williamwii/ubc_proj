//
//  electleader.c
//  ass3
//
//  Wei You
//  77610095
//
//  Created by William You on 2014-03-16.
//  Copyright (c) 2014 William You. All rights reserved.
//

#include <time.h>
#include <stdio.h>
#include <limits.h>
#include <stdlib.h>
#include <string.h>

#ifdef __APPLE__
    #include <mpi.h>
#else
    #include "mpi.h"
#endif

#define LEADER_ELECTED  0x1
#define LEADER_ELECTION 0x12
#define SUMMATION       0x123

typedef enum {
    INITIATOR   = 0,
    SLEEP       = 1,
    LOST        = 2,
    CANDIDATE   = 3,
    LEADER      = 4,
} NODE_STATE;

int main(int argc, const char * argv[])
{
    int rank, left, right, num, pnum, r, debug,
        uid, recv_uid, min_uid, mrcvd, msent, leader;
    if (argc < 2) {
        printf("Invalid number of arguments\n");
        return -1;
    }
    pnum = atoi(argv[1]);
    if (pnum == 0) {
        printf("Invalid pnum\n");
        return -1;
    }

    // Debug flag
    debug = 0;
    if (argc > 2 && strcmp(argv[2], "d")==0) {
        debug = 1;
    }

    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &num);

    mrcvd = 0;
    msent = 0;
    leader = 0;
    min_uid = INT_MAX;

    MPI_Status status;
    MPI_Status send_statuses[2];
    MPI_Request send_requests[2];
    MPI_Request recv_requests[3];

    // The id of the process
    uid = (rank + 1) * pnum % num;

    left = rank - 1;
    if (left < 0) left = num - 1;
    right = rank + 1;
    if (right >= num) right = 0;

    NODE_STATE state = SLEEP;
    // Set partial of the set to be initiator
    srand(time(NULL));
    if (1) state = INITIATOR;

    // Register for elected message
    MPI_Irecv(&leader, 1, MPI_INT, left, LEADER_ELECTED, MPI_COMM_WORLD, &recv_requests[0]);

    // Candidate
    if (state == INITIATOR) {
        min_uid = uid;
        state = CANDIDATE;
        if (debug) printf("[%i] Initiating...\n", rank);

        // Send id's to both left and right
        if (debug) printf("[%i] Sending token %i to %i and %i\n", rank, uid, left, right);
        MPI_Isend(&uid, 1, MPI_INT, left, LEADER_ELECTION, MPI_COMM_WORLD, &send_requests[0]);
        MPI_Isend(&uid, 1, MPI_INT, right, LEADER_ELECTION, MPI_COMM_WORLD, &send_requests[1]);
        MPI_Waitall(2, send_requests, send_statuses);
        msent += 2;

        while (state != LEADER) {
            // Receive id's from left or right or elected message
            MPI_Irecv(&recv_uid, 1, MPI_INT, left, LEADER_ELECTION, MPI_COMM_WORLD, &recv_requests[1]);
            MPI_Irecv(&recv_uid, 1, MPI_INT, right, LEADER_ELECTION, MPI_COMM_WORLD, &recv_requests[2]);
            MPI_Waitany(3, recv_requests, &r, &status);
            mrcvd++;

            // Some one is elected
            if (status.MPI_TAG == LEADER_ELECTED) {
                if (debug) printf("[%i] Selected %i as leader\n", rank, leader);

                // Has sent elected message to all node, done
                if (leader == rank) {
                    state = LEADER;
                }
                // Forward the elected message to next node
                else {
                    MPI_Isend(&leader, 1, MPI_INT, right, LEADER_ELECTED, MPI_COMM_WORLD, &send_requests[0]);
                    MPI_Wait(&send_requests[0], &status);
                    msent++;
                    break;
                }
            }
            // Doing election
            else if (status.MPI_TAG == LEADER_ELECTION) {
                if (debug) printf("[%i] Received token %i from %i\n", rank, recv_uid, status.MPI_SOURCE);

                // Has pass through all nodes, this node is elected as leader
                if (recv_uid == uid) {
                    leader = rank;

                    // Notify other nodes
                    MPI_Isend(&leader, 1, MPI_INT, right, LEADER_ELECTED, MPI_COMM_WORLD, &send_requests[0]);
                    MPI_Wait(&send_requests[0], &status);
                    msent++;
                }
                // Current minimum loses, forward the new minimum id
                else if (recv_uid <= min_uid) {
                    min_uid = recv_uid;
                    if (state == CANDIDATE) state = LOST;

                    int dest;
                    if (status.MPI_SOURCE == left) dest = right;
                    else dest = left;

                    if (debug) printf("[%i] Forwarding token %i to %i\n", rank, recv_uid, dest);
                    MPI_Isend(&recv_uid, 1, MPI_INT, dest, LEADER_ELECTION, MPI_COMM_WORLD, &send_requests[0]);
                    MPI_Wait(&send_requests[0], &status);
                    msent++;
                }
            }
            if (r == 2)
                MPI_Cancel(&recv_requests[1]);
            else if (r == 1)
                MPI_Cancel(&recv_requests[2]);
        }
    }
    // Not candidate
    else {
        while (1) {
            // Receive id's from left or right or elected message
            MPI_Irecv(&recv_uid, 1, MPI_INT, left, LEADER_ELECTION, MPI_COMM_WORLD, &recv_requests[1]);
            MPI_Irecv(&recv_uid, 1, MPI_INT, right, LEADER_ELECTION, MPI_COMM_WORLD, &recv_requests[2]);
            MPI_Waitany(3, recv_requests, &r, &status);
            mrcvd++;

            // Someone is elected, forward the message
            if (status.MPI_TAG == LEADER_ELECTED) {
                if (debug) printf("[%i] Selected %i as leader\n", rank, leader);
                
                MPI_Isend(&leader, 1, MPI_INT, right, LEADER_ELECTED, MPI_COMM_WORLD, &send_requests[0]);
                MPI_Wait(&send_requests[0], &status);
                msent++;
                break;
            }
            // Doing election, forward the message
            else if (status.MPI_TAG == LEADER_ELECTION) {
                if (debug) printf("[%i] Received token %i from %i\n", rank, recv_uid, status.MPI_SOURCE);

                if (state == SLEEP) state = LOST;

                if (recv_uid <= min_uid) {
                    min_uid = recv_uid;
                    int dest;
                    if (status.MPI_SOURCE == left) dest = right;
                    else dest = left;

                    if (debug) printf("[%i] Forwarding token %i to %i\n", rank, recv_uid, dest);
                    MPI_Isend(&recv_uid, 1, MPI_INT, dest, LEADER_ELECTION, MPI_COMM_WORLD, &send_requests[0]);
                    MPI_Wait(&send_requests[0], &status);
                    msent++;
                }
            }
            if (r == 2)
                MPI_Cancel(&recv_requests[1]);
            else if (r == 1)
                MPI_Cancel(&recv_requests[2]);
        }
    }
    printf("rank=%d, id=%d, leader=%d, mrcvd=%d, msent=%d\n", rank, uid, leader, mrcvd, msent);

    int total[2];
    MPI_Request send, recv;
    // Leader initiates call of summation
    // The sum includes messages sent/received during the summation
    if (state == LEADER) {
        total[0] = mrcvd + 1;
        total[1] = msent + 1;

        MPI_Isend(total, 2, MPI_INT, right, SUMMATION, MPI_COMM_WORLD, &send);
        MPI_Wait(&send, &status);

        MPI_Irecv(total, 2, MPI_INT, left, SUMMATION, MPI_COMM_WORLD, &recv);
        MPI_Wait(&recv, &status);

        printf("\n");
        printf("rank=%d, id=%d, trcvd=%d, tsent=%d\n", rank, uid, total[0], total[1]);
    }
    // Non-leader calculate and forward new sum
    else {
        MPI_Irecv(total, 2, MPI_INT, left, SUMMATION, MPI_COMM_WORLD, &recv);
        MPI_Wait(&recv, &status);

        total[0] += mrcvd + 1;
        total[1] += msent + 1;

        MPI_Isend(total, 2, MPI_INT, right, SUMMATION, MPI_COMM_WORLD, &send);
        MPI_Wait(&send, &status);
    }

    MPI_Finalize();
    return 0;
}