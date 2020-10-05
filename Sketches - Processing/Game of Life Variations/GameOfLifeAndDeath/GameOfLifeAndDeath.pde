int cellSize = 5;

float aliveAtStart = 12;

int interval = 100;
int lastRecordedTime = 0;

color dead = color(0); //main: 0, under: 0, 5, 6
//main colors
color fading_main =color(45,32,114); //1
color justDied_main = color(62,46,142); //2
color alive_main = color(213, 255, 0); //3
color birth_main = color(255,255,255); //4
//underworld colors
color fading_under = color(11,19,46); //1
color justDied_under = color(10,35,118); //2
color alive_under = color(43,0,255); //3
color birth_under = color(30,35,110); //4

// Arrays: main cells
int[][] cells_main; 
int[][] cellsBuffer_main; 

// Arrays: underworld cells
int[][] cells_under; 
int[][] cellsBuffer_under; 
int[][] changes_under; 

// Pause boolean
boolean pause = false;

void setup() {
  size (600, 600);

  // Instantiate main arrays 
  cells_main = new int[width/cellSize][height/cellSize];
  cellsBuffer_main = new int[width/cellSize][height/cellSize];
  
  // Instantiate underworld arrays
  cells_under = new int[width/cellSize][height/cellSize];
  cellsBuffer_under = new int[width/cellSize][height/cellSize]; 
  changes_under = new int[width/cellSize][height/cellSize];
  
  // draw background grid
  stroke(10);
  noSmooth();

  // Initialization of cells
  for (int x=0; x<width/cellSize; x++) {
    for (int y=0; y<height/cellSize; y++) {
      float state = random (100);
      if (state > aliveAtStart) { 
        state = 0;
      }
      else {
        state = 3;
      }
      cells_main[x][y] = int(state); 
      cells_under[x][y] = 0; 
    }
  }
  background(0); // Fill in black in case cells don't cover all the windows
}

void draw() {

  //fill grid
  for (int x=0; x<width/cellSize; x++) {
    for (int y=0; y<height/cellSize; y++) {
      if (cells_main[x][y] != 0) {  //main cell is alive or not completely dead yet
        int currentState = cells_main[x][y];
        switch (currentState){
          case 4:  fill(birth_main);
                   break;
          case 3:  fill(alive_main);
                   break;
          case 2:  fill(justDied_main); 
                   break;
          case 1:  fill(fading_main);      
                   break;
          default: fill(dead);    
                   break;
        }
      } else {  //main cell is 0:dead
        int currentState = cells_under[x][y];
        switch (currentState){
          case 7: 
          case 6: 
          case 5: 
          case 0:   fill(dead);
                    break;
          case 4:   fill(birth_under); 
                    break;
          case 3:   fill(alive_under);
                    break;
          case 2:   fill(justDied_under);
                    break;
          case 1:   fill(fading_under);
                    break;
          default:  fill(dead);
                    break; 
        }
      }
      rect (x*cellSize, y*cellSize, cellSize, cellSize);
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
    if (cellsBuffer_main[xCellOver][yCellOver] >= 3) { //if cell is alive or birth
      cells_main[xCellOver][yCellOver]= 0; // dies
      fill(dead); // Fill with kill color
    }
    else { // Cell is dead
      cells_main[xCellOver][yCellOver] = 3; // 
      fill(alive_main); // Fill alive color
    }
  } 
  else if (pause && !mousePressed) { 
    // Save cells to buffer 
    for (int x=0; x<width/cellSize; x++) {
      for (int y=0; y<height/cellSize; y++) {
        cellsBuffer_main[x][y] = cells_main[x][y];
      }
    }
  }
}

void iteration() { // called in timer
  // Save cells to buffer
  for (int x=0; x<width/cellSize; x++) {
    for (int y=0; y<height/cellSize; y++) {
      cellsBuffer_main[x][y] = cells_main[x][y];
      cellsBuffer_under[x][y] = cells_under[x][y];
    }
  }
  
  // Visit each cell:
  for (int x=0; x<width/cellSize; x++) {
    for (int y=0; y<height/cellSize; y++) {
      // visit all the neighbours of each cell
      int neighbours_main = 0;
      int neighbours_under = 0;
      for (int xx=x-1; xx<=x+1;xx++) {
        for (int yy=y-1; yy<=y+1;yy++) {  
          if (((xx>=0)&&(xx<width/cellSize))&&((yy>=0)&&(yy<height/cellSize))) { //out of bound check
            if (!((xx==x)&&(yy==y))) { //check against self
              if (cellsBuffer_main[xx][yy] >= 3){
                neighbours_main ++; // check alive neighbours in main world
              }
              if (cellsBuffer_under[xx][yy] >= 3){
                neighbours_under ++; //check alive neighbours in underworld 
              }
            } // End of if
          } // End of if
        } // End of yy loop
      } //End of xx loop
      // neigbours check completed
      
      // edit main array
      // 0:dead - 1:fading - 2:just died - 3:alive - 4:birth
      if (cellsBuffer_main[x][y] >= 3) { // The cell is alive
        if (neighbours_main < 2 || neighbours_main > 3) {
          cells_main[x][y] = 2; // dies
          
          int randomHeight = (int)random (-3,3);
          int randomWidth = (int)random (-3,3);
          int newHeight = (y+randomHeight >= height-1 && y+randomHeight <= 0)? y+randomWidth : y;
          int newWidth = (x+randomWidth >= width-1 && x+randomWidth <= 0)? x+randomWidth : x;
          cellsBuffer_under[newWidth][newHeight] = 7;
          
        } else {
          cells_main[x][y] = 3;
        }
      } 
      else { // The cell is dead     
        if (neighbours_main == 3 ) { //make it alive if...
          cells_main[x][y] = 4; 
        } else { 
          if (cells_main[x][y] > 0){ //staying dead, but state changes if xy is larger than 0:dead
            cells_main[x][y] --;
          }
        }
      }
      
      //edit underworld array
      // 0:dead - 1:fading - 2:just died - 3:alive - 4:birth - 5/6:pre-birth - 7: main cell death
      if (cellsBuffer_under[x][y] >= 5){
        cells_under[x][y] --;
      } else if (cellsBuffer_under[x][y] >= 3){ // ghost is alive
        if (neighbours_under <= 1 || neighbours_under >= 4 || changes_under[x][y] > 4){ // ghost dissipates if...
          cells_under[x][y] = 2;
        } else {
          cells_under[x][y] = 3;
        }
        if(cellsBuffer_under[x][y] == 3){
          changes_under[x][y] ++;
        }
      } else { // no ghost
        changes_under[x][y] = 0;
        if (neighbours_under == 3){ //make ghost
          cells_under[x][y] = 4;
        } else {
          if (cellsBuffer_under[x][y] > 0){
            cells_under[x][y]--;
          }
        }
      }// End of array editing
    } // End of y loop
  } // End of x loop
} // End of function

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
          state = 1;
        }
        cells_main[x][y] = int(state); // Save state of each cell
      }
    }
  }
  if (key==' ') { // On/off of pause
    pause = !pause;
  }
  if (key=='c' || key == 'C') { // Clear all
    for (int x=0; x<width/cellSize; x++) {
      for (int y=0; y<height/cellSize; y++) {
        cells_main[x][y] = 0; // Save all to zero
      }
    }
  }
}
