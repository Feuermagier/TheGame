#include <iostream>
#include <math.h>
#include <chrono>

bool executeTurn(int* field, int xDim, int yDim, int player, int depth);

//int* arrayCopy(int* array, int xDim, int yDim);

int getValueAt(int x, int y, int* field, int xDim, int yDim);
void setValueAt(int x, int y, int newValue, int* field, int xDim, int yDim);

void printField(int* field, int xDim, int yDim, int tabs);
void printTabs(int count);

const int WHITE = 1;
const int BLACK = -1;
const int EMPTY = 0;

const int LEFT = -1;
const int STRAIGHT = 0;
const int RIGHT = 1;

int main(void) {
    int xDim = 5, yDim = 4;
    int* field = (int*) malloc(xDim * yDim * sizeof(int) * sizeof(int));
    memset(field, 0, xDim * yDim * sizeof(int) * sizeof(int));        // Init array with zeros

    // Init player positions -> one row per player
    for (int i = 0; i < yDim; i++) {
        setValueAt(0, i, BLACK, field, xDim, yDim);
        setValueAt(1, i, BLACK, field, xDim, yDim);
        setValueAt(xDim - 2, i, WHITE, field, xDim, yDim);
        setValueAt(xDim - 1, i, WHITE, field, xDim, yDim);
    }

    printField(field, xDim, yDim, 0);
    printf("\n\n");

    int player = WHITE;

    auto startTime = std::chrono::high_resolution_clock::now();
    bool result = executeTurn(field, xDim, yDim, WHITE, 1);
    auto endTime = std::chrono::high_resolution_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::microseconds>(endTime - startTime).count();

    // Outputs

    char* playerText = (player == WHITE) ? "White" : "Black";
    char* resultText = result ? " wins" : " loses";

    std::cout << std::endl << std::endl;
    std::cout << playerText << resultText << std::endl << std::endl;

    std::cout << "Metrics:" << std::endl;
    std::cout << " Elapsed time: " << duration << " microseconds" << std::endl;

    // Delete field
    free(field);
}

// Returns true if the given player can win
bool executeTurn(int* field, int xDim, int yDim, int player, int depth) {
    //printTabs(depth);
    //printf("%d's turn:\n", player);
    //printField(field, xDim, yDim, depth);

    for (int x = 0; x < xDim; x++) {
        for (int y = 0; y < yDim; y++) {
            if (getValueAt(x, y, field, xDim, yDim) == player) {
                // Try all movements
                for (int dir = -1; dir <= 1; dir++) {
                    int xNew = x - player;
                    int yNew = y + dir;

                    // Check if the new position is inside the field boundaries
                    if (xNew >= 0 && xNew < xDim && yNew >= 0 && yNew < yDim) {
                        //printTabs(depth);
                        //printf("%d moving from %d,%d to %d,%d\n\n", player, x, y, xNew, yNew);

                        // Check if the turn is legal
                        int moveAllowed = abs(dir) + (getValueAt(xNew, yNew, field, xDim, yDim) * player);  // Zero if you move straight and the target position is empty 
                        // or you move to the right/left and the target position is occupied by an enemy

                        // True if moveAllowed == 0
                        if (!moveAllowed) {

                            // Check if a win condition is reached
                            if (xNew == (1 - player)/2 * xDim) {
                                //printTabs(depth);
                                //printf("--> %d wins\n\n", player);
                                return true;
                            }

                            // Store previous positions
                            int prevOldPos = getValueAt(x, y, field, xDim, yDim);
                            int prevNewPos = getValueAt(xNew, yNew, field, xDim, yDim);

                            // Set new positions
                            setValueAt(x, y, EMPTY, field, xDim, yDim);
                            setValueAt(xNew, yNew, player, field, xDim, yDim);

                            // Check if the enemy cannot win after this turn, then return true: If you execute this turn, you will win
                            if (!executeTurn(field, xDim, yDim, -player, depth + 1)) {
                                //printTabs(depth);
                                //printf("--> %d wins\n\n", player);
                                return true;
                            }

                            // Revert changes
                            setValueAt(x, y, prevOldPos, field, xDim, yDim);
                            setValueAt(xNew, yNew, prevNewPos, field, xDim, yDim);
                        }
                        else {
                            //printTabs(depth);
                            //printf("Move not allowed\n");
                        }
                    }
                }
            }
        }
    }
    //printf("\n\n");
    return false;
}

///////////////////////// Helpers ////////////////////////////////////

/*
int* arrayCopy(int* array, int xDim, int yDim) {
    int* copied = (int*) malloc(xDim * sizeof(int) + yDim * sizeof(int));
    for (int x = 0; x < xDim; x++) {
        for (int y = 0; y < yDim; y++) {
            setValueAt(x, y, getValueAt(x, y, array, xDim, yDim), copied, xDim, yDim);
        }
    }
    return copied;
}
*/


int getValueAt(int x, int y, int* field, int xDim, int yDim) {
    return *(field + sizeof(int) * x * yDim + sizeof(int) * y);
}

void setValueAt(int x, int y, int newValue, int* field, int xDim, int yDim) {
    *(field + sizeof(int) * x * yDim + sizeof(int) * y) = newValue;
}

void printTabs(int count) {
    for (int i = 0; i < count ; i++) {
        printf("  ");
    }
}

void printField(int* field, int xDim, int yDim, int tabs) {
    for (int x = 0; x < xDim; x++) {
        printTabs(tabs);
        printf("|");
        for (int y = 0; y < yDim; y++) {
            int position = getValueAt(x, y, field, xDim, yDim);
            printf("  % d", position);
        }
        printf("  |\n");
    }
}