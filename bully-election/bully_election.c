/*
 * Bully election algorithm — simplified C simulation.
 * Coordinator = highest ID among alive processes.
 * Compile: gcc -o bully bully_election.c -std=c99
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_P 16

typedef struct {
    int id;
    int alive;
} Process;

static Process processes[MAX_P];
static int nproc;
static int coordinator = -1;

static int find_highest_alive(void) {
    int best = -1;
    for (int i = 0; i < nproc; i++) {
        if (processes[i].alive && processes[i].id > best) {
            best = processes[i].id;
        }
    }
    return best;
}

static void print_state(void) {
    printf("\n=== Cluster state ===\n");
    for (int i = 0; i < nproc; i++) {
        printf("  P%d %s\n", processes[i].id,
               processes[i].alive ? "" : "(dead)");
    }
    if (coordinator >= 0) {
        int ok = 0;
        for (int i = 0; i < nproc; i++) {
            if (processes[i].id == coordinator && processes[i].alive) {
                ok = 1;
                break;
            }
        }
        if (ok) {
            printf("Current coordinator: P%d\n", coordinator);
        } else {
            printf("No coordinator (election needed).\n");
        }
    } else {
        printf("No coordinator (election needed).\n");
    }
}

static void run_election_from(int starter_id) {
    int si = -1;
    for (int i = 0; i < nproc; i++) {
        if (processes[i].id == starter_id) {
            si = i;
            break;
        }
    }
    if (si < 0 || !processes[si].alive) {
        printf("Starter P%d is not active.\n", starter_id);
        return;
    }

    printf("\n--- Election started by P%d ---\n", starter_id);

    int higher[MAX_P];
    int nh = 0;
    for (int i = 0; i < nproc; i++) {
        if (processes[i].alive && processes[i].id > starter_id) {
            higher[nh++] = processes[i].id;
        }
    }

    if (nh == 0) {
        coordinator = starter_id;
        printf("No higher-ID alive processes. P%d becomes COORDINATOR.\n", starter_id);
        return;
    }

    printf("P%d sends ELECTION to higher processes: ", starter_id);
    for (int i = 0; i < nh; i++) {
        printf("P%d%s", higher[i], (i < nh - 1) ? ", " : "\n");
    }

    int winner = higher[nh - 1];
    printf("Highest respondent P%d takes over election.\n", winner);
    run_election_from(winner);
}

static void fail_coordinator(void) {
    for (int i = 0; i < nproc; i++) {
        if (processes[i].id == coordinator) {
            printf("\n>>> Coordinator P%d FAILED.\n", coordinator);
            processes[i].alive = 0;
            break;
        }
    }
    coordinator = -1;
}

int main(void) {
    /* Default: processes 1..5 */
    nproc = 5;
    for (int i = 0; i < nproc; i++) {
        processes[i].id = i + 1;
        processes[i].alive = 1;
    }
    coordinator = find_highest_alive();

    printf("Bully Election Algorithm — C simulation\n");
    print_state();

    printf("\nDemo: fail coordinator, then elect from P1\n");
    fail_coordinator();
    print_state();
    run_election_from(1);
    print_state();

    return 0;
}
