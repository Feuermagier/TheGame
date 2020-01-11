#include <iostream>
#include <fstream>
#include <string>
#include <math.h>
#include <chrono>

bool executeTurn(int* field, int xDim, int yDim, int player, int depth);
int* determineTurns(int* field, int xDim, int yDim, int player, int* count);

int* arrayCopy(int* array, int xDim, int yDim);
bool fieldEquals(int* fieldOne, int* fieldTwo, int xDim, int yDim);
int* arrayCopy(int* array, int xDim, int yDim);
void copyArrayTo(int* field, int* loc, int xDim, int yDim);

int getValueAt(int x, int y, int* field, int xDim, int yDim);
void setValueAt(int x, int y, int newValue, int* field, int xDim, int yDim);
void writeValueToFieldList(int* list, int index, int x, int y, int xDim, int yDim, int newValue);
int* getFieldFromList(int* list, int index, int xDim, int yDim);
int* addFieldToList(int* list, int listSize, int* field, int xDim, int yDim);

void printField(int* field, int xDim, int yDim, int tabs);
void printTabs(int count);

int* readFile(char* filename);

const int WHITE = 1;
const int BLACK = -1;
const int EMPTY = 0;

const int LEFT = -1;
const int STRAIGHT = 0;
const int RIGHT = 1;

int turnCount = 0;
int gameCount = 0;

int main(void) {
    /*
    int xDim = 3, yDim = 3;
    int* field = (int*) malloc(xDim * yDim * sizeof(int));
    memset(field, 0, xDim * yDim * sizeof(int));        // Init array with zeros



    // Init player positions -> one row per player
    for (int i = 0; i < yDim; i++) {
        setValueAt(0, i, BLACK, field, xDim, yDim);
        //setValueAt(1, i, BLACK, field, xDim, yDim);
        //setValueAt(xDim - 2, i, WHITE, field, xDim, yDim);
        setValueAt(xDim - 1, i, WHITE, field, xDim, yDim);
    }

    printField(field, xDim, yDim, 0);
    printf("\n\n");
    */

    /*
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
    std::cout << " Game count: " << gameCount << std::endl;
    std::cout << " Turn count: " << turnCount << std::endl;

    // Delete field
    free(field);
    */


/*

    int turnCount = 0;
    int player = WHITE;
    int* turnList = determineTurns(field, xDim, yDim, player, &turnCount);

    for (int i = 0; i < 1; i++) {
        int* newList = (int*) malloc(1);
        int newListSize = 0;
        for (int j = 0; j < turnCount; j++) {
            std::cout << "Iteration" << std::endl;
            int count;
            int* tempField = getFieldFromList(turnList, j, xDim, yDim);
            std::cout << "Field created" << std::endl;
            printField(tempField, xDim, yDim, 1);
            int* turns = determineTurns(tempField, xDim, yDim, player, &count);
        
            std::cout << "I'm here!" << std::endl;
            int* tempList = (int*) malloc(1);
            int tempListSize = 0;
            for (int k = 0; k < newListSize + count; k++) {
                if (k < newListSize) {
                    addFieldToList(tempList, k, getFieldFromList(newList, k, xDim, yDim), xDim, yDim);
                } else {
                    addFieldToList(tempList, k, getFieldFromList(turns, k, xDim, yDim), xDim, yDim);
                }
                tempListSize++;
            }
            newList = tempList;
            newListSize = tempListSize;
            //free(tempList);
            std::cout << "I'm here! 2" << std::endl;
        }
        turnList = newList;
        free(newList);
        turnCount = newListSize;
        player = -player;
        std::cout << "I'm here! 3" << std::endl;
    }

    std::cout << "Turn count: " << turnCount << std::endl;
    for (int i = 0; i < turnCount; i++) {
        std::cout << std::endl;
        printField(getFieldFromList(turnList, i, xDim, yDim), xDim, yDim, 0);
    }
    */


    std::ifstream file("output.txt");
    if (!file.is_open()) {
        return -1;
    }
    int xDim = file.get() - '0';
    file.get();
    int yDim = file.get() - '0';
    //file.get();

    int* turnList = (int*) malloc(1);
    int totalFieldCount = 0;

    char pos;
    int x = 0, y = 0;
    int* field = turnList;
    while (file.get(pos)) {
        if (pos == ';') {
            turnList = (int*) realloc(turnList, (totalFieldCount + 1) * xDim * yDim * sizeof(int));
            x = 0;
            y = 0;
            field = turnList + totalFieldCount * xDim * yDim;
            totalFieldCount++;
        } else { 
            if (pos == '0') {
                setValueAt(x, y, EMPTY, field, xDim, yDim);
            } else if (pos == 'S') {
                setValueAt(x, y, BLACK, field, xDim, yDim);
            } else if (pos == 'W') {
                setValueAt(x, y, WHITE, field, xDim, yDim);
            }
            y++;
            if (y >= yDim) {
                x++;
                y = 0;
            }
            //printField(field, xDim, yDim, 0);
        }
    }
    file.close();
    totalFieldCount--;
    printf("Field count: %d\n", totalFieldCount);
    /*
    for (int i = 0; i < totalFieldCount; i++) {
        std::cout << std::endl;
        printField(getFieldFromList(turnList, i, xDim, yDim), xDim, yDim, 0);
    }
    */

    free(turnList);
}

// Returns true if the given player can win
bool executeTurn(int* field, int xDim, int yDim, int player, int depth) {
    /*
    if (depth <= 3) {
        printTabs(depth);
        printf("%d's turn:\n", player);
        printField(field, xDim, yDim, depth);
    }
    */

    int moveCount = 0;
    for (int x = 0; x < xDim; x++) {
        for (int y = 0; y < yDim; y++) {
            if (getValueAt(x, y, field, xDim, yDim) == player) {
                // Try all movements
                for (int dir = -1; dir <= 1; dir++) {
                    int xNew = x - player;
                    int yNew = y + dir;

                    // Check if the new position is inside the field boundaries
                    if (xNew >= 0 && xNew < xDim && yNew >= 0 && yNew < yDim) {

                        // Store state of the target position
                        int newPosition = getValueAt(xNew, yNew, field, xDim, yDim);

                        // Check if the turn is legal
                        int moveAllowed = abs(dir) + (newPosition * player);  // Zero if you move straight and the target position is empty 
                        // or you move to the right/left and the target position is occupied by an enemy

                        // True if moveAllowed == 0
                        if (!moveAllowed) {
                            moveCount++;

                            //printTabs(depth);
                            //printf("%d moving from %d,%d to %d,%d\n\n", player, x, y, xNew, yNew);

                            // Set new positions
                            setValueAt(x, y, EMPTY, field, xDim, yDim);
                            setValueAt(xNew, yNew, player, field, xDim, yDim);

                            // Check if a win condition is reached
                            if (xNew == (1 - player)/2 * (xDim - 1)) {
                                //printField(field, xDim, yDim, 1);
                                //std::cout << player << " wins" << std::endl;
                                //std::cout << std::endl;
                                //printTabs(depth);
                                //printf("--> %d wins\n\n", player);
                                gameCount++;
                                turnCount += depth;
                                // Revert changes
                                setValueAt(x, y, player, field, xDim, yDim);
                                setValueAt(xNew, yNew, newPosition, field, xDim, yDim);
                                return true;
                            }

                            // Check if the enemy cannot win after this turn, then return true: If you execute this turn, you will win
                            bool canEnemyWin = executeTurn(field, xDim, yDim, -player, depth + 1);
                            // Revert changes
                            setValueAt(x, y, player, field, xDim, yDim);
                            setValueAt(xNew, yNew, newPosition, field, xDim, yDim);

                            if (!canEnemyWin) {
                                //printTabs(depth);
                                //printf("--> %d wins\n\n", player);
                                return true;
                            }
                        }
                    }
                }
            }
        }
    }
    if (moveCount == 0) {
        gameCount++;
        turnCount += depth;
    }
    //printf("\n\n");
    return false;
}

int* determineTurns(int* field, int xDim, int yDim, int player, int* count) {

    printf("Before list init\n");
    int* list = (int*) malloc(1);
    printf("After after init\n");

    int i = 0;
    for (int x = 0; x < xDim; x++) {
        for (int y = 0; y < yDim; y++) {
            if (getValueAt(x, y, field, xDim, yDim) == player) {
                // Try all movements
                for (int dir = -1; dir <= 1; dir++) {
                    int xNew = x - player;
                    int yNew = y + dir;

                    // Check if the new position is inside the field boundaries
                    if (xNew >= 0 && xNew < xDim && yNew >= 0 && yNew < yDim) {

                        // Store state of the target position
                        int newPosition = getValueAt(xNew, yNew, field, xDim, yDim);

                        // Check if the turn is legal
                        int moveAllowed = abs(dir) + (newPosition * player);  // Zero if you move straight and the target position is empty 
                        // or you move to the right/left and the target position is occupied by an enemy

                        // True if moveAllowed == 0
                        if (!moveAllowed) {
                            printf("Moving\n");
                            // Set new positions
                            setValueAt(x, y, EMPTY, field, xDim, yDim);
                            setValueAt(xNew, yNew, player, field, xDim, yDim);

                            printf("Positions set\n");
                            list = (int*) realloc(list, (i+1) * xDim * yDim * sizeof(int));
                            printf("Memory allocated\n");
                            copyArrayTo(field, getFieldFromList(list, i, xDim, yDim), xDim, yDim);
                            i++;
                            printf("Array copied\n");

                            // Revert changes
                            setValueAt(x, y, player, field, xDim, yDim);
                            setValueAt(xNew, yNew, newPosition, field, xDim, yDim);
                            printf("Reverted changes\n\n");
                        }
                    }
                }
            }
        }
    }
    *count = i;
    return list;
}

///////////////////////// Helpers ////////////////////////////////////

void writeValueToFieldList(int* list, int index, int x, int y, int xDim, int yDim, int newValue) {
    *(list + xDim * yDim * index + x * yDim + y) = newValue;
}

int* getFieldFromList(int* list, int index, int xDim, int yDim) {
    return list + xDim * yDim * index;
}

int* addFieldToList(int* list, int listSize, int* field, int xDim, int yDim) {
    int* newList = (int*) realloc(list, (listSize + 1) * xDim * yDim * sizeof(int));
    int* tmp = getFieldFromList(list, listSize, xDim, yDim);
    printf("%p || %p || %d\n", newList, tmp, listSize * xDim * yDim * sizeof(int));
    copyArrayTo(field, tmp, xDim, yDim);
    return newList;
}

void copyArrayTo(int* field, int* loc, int xDim, int yDim) {
    for (int x = 0; x < xDim; x++) {
        for (int y = 0; y < yDim; y++) {
            setValueAt(x, y, getValueAt(x, y, field, xDim, yDim), loc, xDim, yDim);
        }
    }
}

int* arrayCopy(int* array, int xDim, int yDim) {
    int* copied = (int*) malloc(xDim * yDim * sizeof(int));
    for (int x = 0; x < xDim; x++) {
        for (int y = 0; y < yDim; y++) {
            setValueAt(x, y, getValueAt(x, y, array, xDim, yDim), copied, xDim, yDim);
        }
    }
    return copied;
}


int getValueAt(int x, int y, int* field, int xDim, int yDim) {
    return *(field + x * yDim + y);
}

void setValueAt(int x, int y, int newValue, int* field, int xDim, int yDim) {
    *(field + x * yDim + y) = newValue;
}

void printTabs(int count) {
    for (int i = 0; i < count ; i++) {
        printf("|  ");
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


bool fieldEquals(int* fieldOne, int* fieldTwo, int xDim, int yDim) {
    for (int x = 0; x < xDim; x++) {
        for (int y = 0; y < yDim; y++) {
            if (getValueAt(x, y,fieldOne, xDim, yDim) != getValueAt(x, y, fieldTwo, xDim, yDim))
                return false;
        }
    }
    return true;
}
