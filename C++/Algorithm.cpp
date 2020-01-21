#include <array>

#include "constants.h"

using namespace std;

int getValueAt(int x, int y, int* field, int xDim, int yDim) {
    return *(field + x * yDim + y);
}

void setValueAt(int x, int y, int newValue, int* field, int xDim, int yDim) {
    *(field + x * yDim + y) = newValue;
}

AlgorithmResult executeTurn(int* field, int xDim, int yDim )