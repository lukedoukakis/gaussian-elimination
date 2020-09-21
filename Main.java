import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
			
		// get matrix from user input
		float[][] system = null;
		try {
			system = promptUserInput();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.print("\nUser input failed: file not found");
			e.printStackTrace();
		}
		
		System.out.print("\nSystem entered:\n");
		printMatrix(system);
		
		// perform Gaussian elimination with scaled partial pivoting
		if(system != null){
			solveGaussian(system);
		}

	}
	 
	static void solveGaussian(float[][] _matrix){
		
		int n;
		int[] iv;			// index vector
		float[] sv;			// scale vector
		float[] ratios;		// scaled ratios to determine pivot row
		int pivotRow;		
		int pivotRowIndex;
		int pivotColumn;
		
		// initialize n, iv, sv
		n = _matrix.length;
		iv = new int[n];
		sv = new float[n];
		for(int i = 0; i < n; i++){
			iv[i] = i;
			sv[i] = getMaxCoefficient(_matrix[i]);
		}
		ratios = new float[n];
		pivotRow = 0;
		pivotRowIndex = 0;
		pivotColumn = 0;

		while(pivotColumn < n){
			
			// calculate pivot row
			float max = 0;
			for(int i = 0; i < n; i++){
				ratios[i] = 0;
				if(i >= pivotColumn){
					ratios[i] = Math.abs(_matrix[iv[i]][pivotColumn] / sv[iv[i]]);
					if(ratios[i] > max){
						max = ratios[i];
						pivotRow = iv[i];
						pivotRowIndex = i;
					}
				}
			}
			
			// calculate intermediate matrix
			for(int i = 0; i < n; i++){
				if(i != pivotRow){
					_matrix[i] = augmentRow(_matrix, i, pivotRow, pivotColumn);
				}
			}
			
			// print data of current step to console
			System.out.print("\n~~~~~~~~~~~~~~~~~ k=" + (pivotColumn+1) + " ~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.print("\nIndex vector: ");
			System.out.print("[");
			for(int i = 0; i < iv.length; i++){
				System.out.print(" " + (iv[i]+1));
			}
			System.out.print(" ]");
			System.out.print("\nScaled ratios: ");
			System.out.print("[");
			for(int i = 0; i < n; i++){
				System.out.print(" " + (Math.round(ratios[i]*100f)/100f));
			}
			System.out.print(" ]");
			System.out.print("\nPivot row: " + (pivotRow+1));
			System.out.print("\nIntermediate matrix:\n");
			printMatrix(_matrix);
			
			if(pivotColumn+1 >= n){
				break;
			}
			
			// update index vector
			iv = swapElements(iv, pivotRowIndex, pivotColumn);
			
			// increment k
			pivotColumn++;
		}
		
		// round final matrix values to account for error
		int rows = _matrix.length;
		int columns = _matrix[0].length;
		for(int row = 0; row < rows; row++){
			for(int col = 0; col < columns; col++){
				_matrix[row][col] = Math.round(_matrix[row][col]*100f)/100f;
			}
		}
	
		// calculate and print solutions from matrix
		System.out.print("\n~~~~~~~~~~~~~~~~~~ Solutions ~~~~~~~~~~~~~~~~~~~~~~~~~");
		float[] solutions = calculateSolutions(_matrix);
		for(int i = 0; i < solutions.length; i++){
			System.out.print("\nx" + (i+1) + " = " + solutions[i]);
		}
		

	}
	
	// augment target row using scalar multiple between the row and pivot row
	static float[] augmentRow(float[][] _matrix, int rowIndex, int pRowIndex, int column){
		float[] row = _matrix[rowIndex];
		float[] pRow = _matrix[pRowIndex];
		float multiplier = row[column]/pRow[column] * -1f;

		float[] temp = new float[pRow.length];
		for(int i = 0; i < temp.length; i++){
			temp[i] = pRow[i] * multiplier;
		}
		
		float[] returnRow = new float[pRow.length];
		for(int i = 0; i < returnRow.length; i++){
			returnRow[i] = temp[i] + row[i];
		}

		return returnRow;
	}
	
	// return array of solutions from fully augmented matrix
	static float[] calculateSolutions(float[][] _matrix){
		int n = _matrix.length;
		float[] sol = new float[n];
		for(int row = 0; row < n; row++){
			for(int col = 0; col < n; col++){
				if(_matrix[row][col] != 0){
					sol[col] = Math.round((_matrix[row][n] / _matrix[row][col])*100f)/100f;
					break;
				}
			}
		}
		return sol;
	}
	
	// swap places of two elements in array
	static int[] swapElements(int[] arr, int i1, int i2){
		int temp = arr[i1];
		arr[i1] = arr[i2];
		arr[i2] = temp;
		
		int[] returnArr = new int[arr.length];
		for(int i = 0; i < returnArr.length; i++){
			returnArr[i] = arr[i];
		}
		return returnArr;
	}
	
	
	// return the value of highest magnitude in array
	static float getMaxCoefficient(float[] arr){
		float max = Math.abs(arr[0]);
		for(int i = 0; i < arr.length-2; i++){
			max = Math.max(max, Math.abs(arr[i+1]));
		}
		return max;
		
	}
	
	// print matrix to console
	static void printMatrix(float[][] _matrix){
		int rows = _matrix.length;
		int columns = _matrix[0].length;
		for(int row = 0; row < rows; row++){
			for(int col = 0; col < columns; col++){
				System.out.print((Math.round(_matrix[row][col]*100f)/100f) + " ");
			}
			System.out.print("\n");
		}
	}
	
	// get linear from user input, by entering manually or accessing txt file
	static float[][] promptUserInput() throws FileNotFoundException{
		
		Scanner s = new Scanner(System.in);
		float[][] _system;
		
		System.out.println("How many equations to solve? n = ");
		int n = Integer.parseInt(s.nextLine());
		_system = new float[n][n+1];
		
		System.out.println(	"(1) Enter coefficients manually\n(2) Enter coefficients from txt file");
		int format = Integer.parseInt(s.nextLine());
		
		
		String cells[];
		switch (format) {
		case 1 :
			
			
			for(int row = 0; row < n; row++){
				System.out.println("Enter row " + (row+1) + " (space separated, including b-value): ");
				cells = s.nextLine().split(" ");
				for(int col = 0; col < n+1; col++){
					_system[row][col] = Float.parseFloat(cells[col]);
				}
			}

			break;
		case 2:
	
			System.out.println("Enter file path:");
			String filePath = s.nextLine();
			File f = new File(filePath);
			Scanner fScanner = new Scanner(f);
			
			for(int row = 0; row < n; row++){
				cells = fScanner.nextLine().split(" ");
				for(int col = 0; col < n+1; col++){
					_system[row][col] = Float.parseFloat(cells[col]);
				}
			}
			break;
		default:
			System.out.println("default");
			break;
		}
		
		s.close();
		return _system;
	}
}