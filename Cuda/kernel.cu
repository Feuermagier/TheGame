#include <math.h>
#include <iostream>
#include <stdlib.h>
#include <vector>

#include "cuda_runtime.h"
#include "kernel.h"
#include "dev_array.h"
#include "constants.h"

using namespace std;


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
                //printf("Found one at %d, %d\n", x, y);
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
                            //printf("Can move from %d, %d to %d, %d\n", x, y, xNew, yNew);

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
                            bool canEnemyWin = executeTurn(field, xDim, yDim, -1 * player, depth + 1);
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
    extern __shared__ int fieldCache[];
    //int* field = (int*) malloc(xDim * yDim * sizeof(int));

    for (int i = index; i < fieldCount; i += stride) {
        // Copy field to local memory
        memcpy(&fieldCache[threadIdx.x * xDim * yDim], &fieldList[xDim * yDim * i], xDim * yDim * sizeof(int));
        /*
        printf("%d %d\n", field[0], field[1]);
        printf("%d %d\n", field[2], field[3]);
        printf("%d %d\n", field[4], field[5]);
        printf("%d %d\n", field[6], field[7]);
        printf("%d %d\n", field[8], field[9]);
        printf("%d %d\n\n", field[10], field[11]);    
        */

        // Run game simulation
        bool canWin = executeTurn(&fieldCache[threadIdx.x * xDim * yDim], xDim, yDim, player, 0);
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

    /*
    // Copy field data to the device
    dev_array<int> devFieldList(fieldList.size());
    devFieldList.set(&fieldList[0], fieldList.size());

    // Create and copy result array
    vector<int> results(fieldCount);
    dev_array<int> deviceResults(fieldCount);
    deviceResults.set(&results[0], fieldCount);
    */

    int* devFieldList;
    cudaMallocManaged(&devFieldList, fieldList.size() * sizeof(int));
    cudaMemcpy(devFieldList, &fieldList[0], fieldList.size() * sizeof(int), cudaMemcpyHostToDevice);

    vector<int> results(fieldCount);
    int* deviceResults;
    cudaMallocManaged(&deviceResults, fieldCount * sizeof(int));
    cudaMemcpy(deviceResults, &results[0], fieldCount * sizeof(int), cudaMemcpyHostToDevice);

    int blockSize = 256;
    int blockCount = (fieldCount + blockSize - 1) / blockSize;
    gameRunnerKernel<<<blockCount, blockSize, blockSize * xDim * yDim * sizeof(int)>>>(devFieldList, fieldCount, xDim, yDim, player, deviceResults);
    //gameRunnerKernel<<<1, 32>>>(devFieldList, fieldCount, xDim, yDim, player, deviceResults);

    error = cudaDeviceSynchronize();
    cout << error << endl;

    // Copy data back to the host
    //deviceResults.set(&results[0], fieldCount);
    cudaMemcpy(&results[0], deviceResults, fieldCount * sizeof(int), cudaMemcpyDeviceToHost);

    return results;
}