#include <math.h>
#include <iostream>
#include <stdlib.h>
#include <vector>

#include "cuda_runtime.h"
#include "kernel.h"
#include "dev_array.h"
#include "constants.h"

using namespace std;


// Expensive, use not that often
// Returns a pointer to a newly allocated array for the field
__device__
int* getFieldFromList(int* list, int index, int xDim, int yDim) {
    int* field = (int*) malloc(xDim * yDim * sizeof(int));
    memcpy(field, &list[xDim * yDim * index], xDim * yDim * sizeof(int));
    return field;
}


__device__
int getValueAt(int x, int y, int* field, int xDim, int yDim) {
    return field[x * yDim + y];
}

__device__ 
void setValueAt(int x, int y, int newValue, int* field, int xDim, int yDim) {
    field[x * yDim + y] = newValue;
}

// Returns true if the given player can win
__device__
bool executeTurn(int* field, int xDim, int yDim, int player, int depth) {

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

                            // Set new positions
                            setValueAt(x, y, EMPTY, field, xDim, yDim);
                            setValueAt(xNew, yNew, player, field, xDim, yDim);


                            // Check if a win condition is reached
                            if (xNew == (1 - player)/2 * (xDim - 1)) {
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
                                return true;
                            }
                        }
                    }
                }
            }
        }
    }
    return false;
}

///////////////////////////// KERNEL ////////////////////////////////////////
__global__
void gameRunnerKernel(int* fieldList, int fieldCount, int xDim, int yDim, int player, int* results) {
    int index = blockIdx.x * blockDim.x + threadIdx.x;
    int stride = blockDim.x * gridDim.x;

    for (int i = index; i < fieldCount; i += stride) {
        // Copy field to local memory
        int* field = getFieldFromList(fieldList, i, xDim, yDim);
        // Run game simulation
        bool canWin = executeTurn(field, xDim, yDim, player, 0);
        // Set result array according to the result
        if (canWin) {
            results[i] = 1;
            printf("Wins: %d\n", i);
        } else {
            results[i] = 0;
        }
    }
}

vector<int> gameRunner(vector<int> fieldList, int fieldCount, int xDim, int yDim, int player) {

    // Copy field data to the device
    dev_array<int> devFieldList(fieldList.size());
    devFieldList.set(&fieldList[0], fieldList.size());

    // Create and copy result array
    vector<int> results(fieldCount);
    dev_array<int> deviceResults(fieldCount);
    deviceResults.set(&results[0], fieldCount);


    int blockSize = 256;
    int numBlocks = (fieldCount + blockSize - 1) / blockSize;
    gameRunnerKernel<<<numBlocks, blockSize>>>(devFieldList.getData(), fieldCount, xDim, yDim, player, deviceResults.getData());

    // Copy data back to the host
    deviceResults.set(&results[0], fieldCount);

    cudaDeviceSynchronize();

    return results;
}