#include <iostream>
#include <vector>
#include <stdlib.h>
#include <time.h>
#include <cuda_runtime.h>
#include <math.h>
#include<fstream>

#include "kernel.h"
#include "kernel.cu"

using namespace std;

int main(void) {

    ifstream file;
    file.open("output.txt");
    if (!file.is_open()) {
        cerr << "Could not read file" << endl;
        return -1;
    }

    char separator;
    char playerChar;
    file >> playerChar;
    int player = (playerChar == 'W' ? WHITE : BLACK);
    file >> separator;
    if (separator != INIT_SEPARATOR) {
        cerr << "File has an invalid format. Continuing parsing." << endl;
    } 

    int xDim, yDim;
    file >> xDim;
    file >> separator;
    if (separator != INIT_SEPARATOR) {
        cerr << "File has an invalid format. Continuing parsing." << endl;
    } 
    file >> yDim;
    file >> separator;
    if (separator != INIT_SEPARATOR) {
        cerr << "File has an invalid format. Continuing parsing." << endl;
    } 

    vector<int> fieldList(0);
    int fieldCount = 0;

    char pos;
    while(file >> pos) {
        if (pos == FIELD_SEPARATOR)
            fieldCount++;
        else if (pos == 'W')
            fieldList.push_back(WHITE);
        else if (pos == 'S')
            fieldList.push_back(BLACK);
        else if (pos == '0')
            fieldList.push_back(EMPTY);
        else {
            cerr << "File has wrong format" << endl;
            return(-1);
        }    
    }

    file.close();
    cout << "Field count: " << fieldCount << endl;
    cout << "XDim: " << xDim << ", YDim: " << yDim << endl;
    cout << "Starting player: " << player << endl;

    // Run simulation on device
    vector<int> results = gameRunner(fieldList, fieldCount, xDim, yDim, player);

    
    for (int i = 0; i < results.size(); i++) {
        cout << i << ": " << results[i] << endl;
    }

    return 0;
}