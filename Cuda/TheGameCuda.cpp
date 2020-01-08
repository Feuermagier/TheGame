#include <iostream>
#include <math.h>

bool executeTurn(int* field, int xDim, int yDim, int player);
int* arrayCopy(int* array, int xDim, int yDim);
int getValueAt(int x, int y, int* field, int xDim, int yDim);
void setValueAt(int x, int y, int newValue, int* field, int xDim, int yDim);

const int WHITE = 1;
const int BLACK = -1;
const int EMPTY = 0;

const int LEFT = -1;
const int STRAIGHT = 0;
const int RIGHT = 1;

int main(void) {
    int* field = (int*) malloc(6 * sizeof(int));
    int player = BLACK;
    char* output = executeTurn(field, 2, 3, WHITE) ? " wins" : " loses";
    std::cout << player << output << std::endl;
    free (field);
}

bool executeTurn(int* field, int xDim, int yDim, int player) {
    for (int x = 0; x < xDim; x++) {
        for (int y = 0; y < yDim; y++) {
            if (getValueAt(x, y, field, xDim, yDim) == player) {

                // Try all movements
                for (int dir = -1; dir <= 1; dir++) {
                    int xNew = y - player;
                    int yNew = x + dir;

                    // Check if the new position is inside the field boundaries
                    if (xNew >= 0 && xNew < xDim && yNew >= 0 && yNew < yDim) {
                        // Check if the turn is legal
                        int moveAllowed = abs(dir) + (getValueAt(xNew, yNew, field, xDim, yDim) * player);  // Zero if you move straight and the target position is empty 
                        // or you move to the right/left and the target position is occupied by an enemy

                        // True if moveAllowed != 0
                        if (moveAllowed) {
                            // Store previous positions
                            int prevOldPos = getValueAt(x, y, field, xDim, yDim);
                            int prevNewPos = getValueAt(xNew, yNew, field, xDim, yDim);

                            // Set new positions
                            setValueAt(x, y, EMPTY, field, xDim, yDim);
                            setValueAt(xNew, yNew, player, field, xDim, yDim);

                            // Check if the enemy cannot win after this turn, then return true: If you do this turn, you will win
                            if (!executeTurn(field, xDim, yDim, -player)) {
                                return true;
                            }

                            // Revert changes
                            setValueAt(x, y, prevOldPos, field, xDim, yDim);
                            setValueAt(xNew, yNew, prevNewPos, field, xDim, yDim);
                        }
                    }
                }
            }
        }
    }
    return false;
}

///////////////////////// Helpers ////////////////////////////////////

int* arrayCopy(int* array, int xDim, int yDim) {
    int* copied = (int*) malloc(xDim * sizeof(int) + yDim * sizeof(int));
    for (int x = 0; x < xDim; x++) {
        for (int y = 0; y < yDim; y++) {
            setValueAt(x, y, getValueAt(x, y, array, xDim, yDim), copied, xDim, yDim);
        }
    }
    return copied;
}


int getValueAt(int x, int y, int* field, int xDim, int yDim) {
    return *(field + sizeof(int) * x * xDim + sizeof(int) * y);
}

void setValueAt(int x, int y, int newValue, int* field, int xDim, int yDim) {
    *(field + sizeof(int) * x * xDim + sizeof(int) * y) = newValue;
}