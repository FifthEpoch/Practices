int cellSize = 5;

float aliveAtStart = 18;

int interval = 100;
int lastRecordedTime = 0;

color dead = color(0); //main: 0, under: 0, 5, 6
//main colors
color birth_8 = color(214, 90, 36);
color alive_7 = color(157, 85, 36);
color alive_6 = color(111, 81, 35);
color alive_5 = color(85, 78, 35);
color alive_4 = color(66, 76, 34);
color alive_3 = color(57, 71, 28);
color alive_2 = color(43, 52, 17);
color alive_1 = color(25, 30, 7);

color [] colorArray = {dead, alive_1, alive_2, alive_3, alive_4, alive_5, alive_6, alive_7, birth_8};

// Arrays: main cells
int[][] cells; 
int[][] cellsBuffer; 
boolean[][] isUrban;

// Pause boolean
boolean pause = false;

void setup() {
  size (600, 600);

  // Instantiate main arrays 
  cells = new int[width/cellSize][height/cellSize];
  cellsBuffer = new int[width/cellSize][height/cellSize];
  isUrban = new boolean[width/cellSize][height/cellSize];
  
  // draw background grid
  stroke(20);
  noSmooth();

  // Initialization of cells
  for (int x = 0; x < width/cellSize; x++) {
    for (int y = 0; y < height/cellSize; y++) {
      float state = random (100);
      if (state > aliveAtStart) { 
        state = 0;
      }
      else {
        state = 8;
      }
      cells[x][y] = int(state); 
    }
  }
  background(0); // Fill in black in case cells don't cover all the windows
}

void draw() {
  //fill grid
  for (int x = 0; x < width/cellSize; x++) {
    for (int y = 0; y < height/cellSize; y++) {
      
      fill(colorArray[cells[x][y]]);
      rect (x * cellSize, y * cellSize, cellSize, cellSize);
    }
  }
  // Iterate if timer ticks
  if (millis()-lastRecordedTime>interval) {
    if (!pause) {
      iteration();
      lastRecordedTime = millis();
    }
  }

  // Create new cells manually on pause
  if (pause && mousePressed) {
    // Map and avoid out of bound errors
    int xCellOver = int(map(mouseX, 0, width, 0, width/cellSize));
    xCellOver = constrain(xCellOver, 0, width/cellSize-1);
    int yCellOver = int(map(mouseY, 0, height, 0, height/cellSize));
    yCellOver = constrain(yCellOver, 0, height/cellSize-1);

    // Check against cells in buffer
    if (cellsBuffer[xCellOver][yCellOver] > 0) { //if cell is alive or birth
      cells[xCellOver][yCellOver] = 0; // dies
      fill(dead); // Fill with kill color
    }
    else { // Cell is dead
      cells[xCellOver][yCellOver] = 8; // 
      fill(birth_8); // Fill alive color
    }
  } 
  else if (pause && !mousePressed) { 
    // Save cells to buffer 
    for (int x=0; x<width/cellSize; x++) {
      for (int y=0; y<height/cellSize; y++) {
        cellsBuffer[x][y] = cells[x][y];
      }
    }
  }
}

void iteration() { // called in timer
  // save cells to buffer
  int firstRowSum = 0;
  int newRowSum   = 0;
  int zoneSum     = 0;
  for (int x = 0; x < width/cellSize; x++) {
    for (int y = 0; y < height/cellSize; y++) {
      cellsBuffer[x][y] = cells[x][y];
    }
  }
  // visit each cell:
  for (int x = 0; x < width/cellSize; x++) {
    for (int y = 0; y < height/cellSize; y++) {
      int neighbours  = 0;
      
      System.out.println("x: " + x + " y: " + y);
      
      if (x == 0 && y == 0){ // first cell, set up sum
      
        for (int col = -10; col <= 10; col++){
          int col_inBounds = colIndexInBounds(col);
          
          for (int row = -10; row <= 10; row++){
            int row_inBounds = rowIndexInBounds(row);
            
            if (cellsBuffer[col_inBounds][row_inBounds] > 0){
              zoneSum++;

              if (row == -10){
                firstRowSum++;
              }
              if ((Math.abs(col) == 1 || col == 0) && (Math.abs(row) == 1 || row == 0)){
                if (col != 0 && row != 0){
                  neighbours++;
                }
              }
            }
          }
        }
      } else { // all other cells
        zoneSum -= firstRowSum; // update zoneSum, first deduct the first row
        firstRowSum = 0;        // reset firstRowSum
        
        for (int xx = -1; xx <= 1; xx++) {
          for (int yy = -1; yy <= 1; yy++) {
            if ((xx != 0 || yy != 0) && cellsBuffer[colIndexInBounds(x + xx)][rowIndexInBounds(y + yy)] > 0){
              neighbours++;
            }
          }
        }
        int index_firstRow = rowIndexInBounds(y - 10);
        int index_lastRow  = rowIndexInBounds(y + 10);
        
        for (int col = -10; col <= 10; col++) {
          int col_inBounds = colIndexInBounds(x + col);
          
          if (cellsBuffer[col_inBounds][index_firstRow] > 0) {
            firstRowSum++;
          }
          if (cellsBuffer[col_inBounds][index_lastRow] > 0) {
            newRowSum++;
          }
        }
        zoneSum += newRowSum;
        newRowSum = 0;
      }
      // check if it's urban
      if (isUrban(441, zoneSum)){
        // if it is urban, check if it's crowded by checking alive count in the surrounding
        if (!isCrowded(x, y)){
          // if the cell is alive
          if (cellsBuffer[x][y] > 0){
            // help newly alive cell age
            if (cellsBuffer[x][y] > 1){
              cells[x][y]--;
            }
            // meet these condition to die
            if (neighbours <= 1 || neighbours > 5){  
              cells[x][y] = 0;
            }
          } else { // cell is dead
            // meet these condition to become alive
            if (neighbours == 2 || neighbours == 3){       
              cells[x][y] = 8;
            }
          } 
        } else { // is urban but is crowded
          // if cell is alive
          if (cellsBuffer[x][y] > 0){
            // help newly alive cell age
            if (cellsBuffer[x][y] > 1){
              cells[x][y]--;
            }
            // meet these condition to die
            if (neighbours <= 2 || neighbours > 5){  
              cells[x][y] = 0;
            }
          }
        }
      } else { // not urban
        // cell is alive
        if (cellsBuffer[x][y] > 0){
          // help newly alive cell age
          if (cellsBuffer[x][y] > 1){
            cells[x][y]--;
          } 
          // meet these condition to die
          if (neighbours < 2 || neighbours > 3){
            cells[x][y] = 0;  
          }
        } else {
          if (neighbours == 3){
            cells[x][y] = 8;
          }
        }
      }
    }
  }
  System.out.println("----------updated----------");
}

int rowIndexInBounds(int rowIndex){
  if (rowIndex < 0){
    return (height/cellSize) + rowIndex;
  } else if (rowIndex >= height/cellSize){
    return rowIndex - (height/cellSize);
  } else {
    return rowIndex;
  }
}

int colIndexInBounds(int colIndex){
  if (colIndex < 0){
    return (width/cellSize) + colIndex;
  } else if (colIndex >= width/cellSize){
    return colIndex - (width/cellSize);
  } else {
    return colIndex;
  }
}

boolean isUrban(int total, int alive){
  if (total / 4 <= alive){
    return true;
  }
  return false;
}

boolean isCrowded(int x, int y){
  int alive  = 0;
  for (int i = 1; i <= 5; i++){
    int current_upperY  = rowIndexInBounds(y + 10 + i);
    int current_lowerY  = rowIndexInBounds(y - 10 - i);
    
    int current_upperX  = colIndexInBounds(x + 10 + i);
    int current_lowerX  = colIndexInBounds(x - 10 - i);
    
    for(int j = -15; j <= 15; j++){
      // first 5 rowa
      if(cellsBuffer[colIndexInBounds(x + j)][current_upperY] > 0){
        alive++;
      }
      // last 5 rows
      if(cellsBuffer[colIndexInBounds(x + j)][current_lowerY] > 0){
        alive++;
      }
      // if j >= -10 && j <= 10
      if(Math.abs(j) <= 10){
        if(cellsBuffer[current_upperX][rowIndexInBounds(y + j)] > 0){
          alive++;
        }
        if(cellsBuffer[current_lowerX][rowIndexInBounds(y + j)] > 0){
          alive++;
        }
      }
    }
  }
  return isUrban(320, alive);
}

void keyPressed() {
  if (key=='r' || key == 'R') {
    // Restart: reinitialization of cells
    for (int x=0; x<width/cellSize; x++) {
      for (int y=0; y<height/cellSize; y++) {
        float state = random (100);
        if (state > aliveAtStart) {
          state = 0;
        }
        else {
          state = 8;
        }
        cells[x][y] = int(state); // Save state of each cell
      }
    }
  }
  if (key==' ') { // On/off of pause
    pause = !pause;
  }
  if (key=='c' || key == 'C') { // Clear all
    for (int x=0; x<width/cellSize; x++) {
      for (int y=0; y<height/cellSize; y++) {
        cells[x][y] = 0; // Save all to zero
      }
    }
  }
}
