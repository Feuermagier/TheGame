#include <stdio.h>
#include <pthread.h>

struct thread_data {
    int id;
};

static void *threadRunner(void* data) {
    printf("Id: %d", (*((struct thread_data*) data)).id);
}

void test() {
    struct thread_data data;
    data.id = 42;

    pthread_t thread;
    pthread_create(&thread, NULL, &threadRunner, (void *)&data);
    pthread_join(thread, NULL);
}
