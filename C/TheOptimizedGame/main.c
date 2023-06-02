#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/time.h>
#include <pthread.h>

#define WHITE 1
#define BLACK -1
#define EMPTY 0

#define WIDTH 4
#define HEIGHT 5

int field[HEIGHT][WIDTH] = {
    {BLACK, BLACK, BLACK, BLACK},
    {BLACK, BLACK, BLACK, BLACK},
    {EMPTY, EMPTY, EMPTY, EMPTY},
    {WHITE, WHITE, WHITE, WHITE},
    {WHITE, WHITE, WHITE, WHITE}
};

int algorithm(int player) {

    // Try to predict a win
    int targetRow = (1 - player) / 2 * (HEIGHT - 1);
    int p;
    for (p = 0; p < WIDTH; p++) {
        if (field[targetRow - player][p] == player) {
            if (p > 0 && field[targetRow][p - 1] == -1 * player) {
                return 1;
            }
            if (p < WIDTH - 1 && field[targetRow][p + 1] == -1 * player) {
                return 1;
            }
        }
    }

    int x, y;
    for (x = 0; x < HEIGHT; x++) {
        for (y = 0; y < WIDTH; y++) {
            if (field[x][y] == player) {
                int dir;
                for (dir = -1; dir <= 1; dir++) {
                    int xNew = x - player;
                    int yNew = y + dir;

                    if (xNew >= 0 && xNew < HEIGHT && yNew >= 0 && yNew < WIDTH) {
                        if (abs(dir) + (field[xNew][yNew] * player) == 0) {
                            if (xNew == (1 - player) / 2 * (HEIGHT - 1)) {
                                return 1;
                            }

                            int prevPlayerAtNewPos = field[xNew][yNew];
                            field[x][y] = EMPTY;
                            field[xNew][yNew] = player;

                            if (!algorithm(-player)) {
                                field[x][y] = player;
                                field[xNew][yNew] = prevPlayerAtNewPos;
                                return 1;
                            }
                            field[x][y] = player;
                            field[xNew][yNew] = prevPlayerAtNewPos;
                        }
                    }
                }
            }
        }
    }
    return 0;
}



int main() {

    long start, end;
    struct timeval timecheck;

    gettimeofday(&timecheck, NULL);
    start = (long)timecheck.tv_sec * 1000000 + (long)timecheck.tv_usec;

    int result = algorithm(WHITE);

    gettimeofday(&timecheck, NULL);
    end = (long)timecheck.tv_sec * 1000000 + (long)timecheck.tv_usec;

    if (result) {
        printf("White wins\n");
    } else {
        printf("Black wins\n");
    }
    printf("Duration: %ld microseconds\n", (end - start));
    return 0;
}



