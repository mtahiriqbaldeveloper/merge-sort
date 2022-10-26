public class Main {

    public static void main(String[] args){
        System.out.print("Merge Sort Algo");
        int[] arr = {12,4,9,12,8,1,3};

        System.out.println(" unsorted array");

        print(arr);

        sort(arr);

        System.out.println(" sorted array");

        print(arr);

    }

    private static void print(int[] arr){
        for (int i:arr) {
            System.out.print(i+" , ");
        }
    }

    private static void sort(int[] arr){
        int size  = arr.length;
        if(arr.length < 2){
            return;
        }
        int mid = size/2;
        int[] left = new int[mid];
        int[] right = new int[size - mid];

        for (int i =0; i<mid; i++){
            left[i]=arr[i];
        }
        for (int j =mid; j<size; j++){
            right[j-mid] = arr[j];
        }
        sort(left);
        sort(right);
        merge(arr,left,right);
    }

    private static void merge(int[] arr,int[] left,int[] right){

        int inputArrayIndex =0,leftIndex = 0,rightIndex = 0;
        int leftSize = left.length; int rightSize = right.length;



        while(leftIndex < leftSize && rightIndex< rightSize){
            if(left[leftIndex] <= right[rightIndex]){
                arr[inputArrayIndex]=left[leftIndex];
                leftIndex++;
            }
            else {
                arr[inputArrayIndex]=right[rightIndex];
                rightIndex++;
            }
            inputArrayIndex++;
        }

        while (leftIndex < leftSize){
            arr[inputArrayIndex]=left[leftIndex];
            leftIndex++;
            inputArrayIndex++;
        }

        while (rightIndex < rightSize){
            arr[inputArrayIndex] = right[rightIndex];
            rightIndex++;
            inputArrayIndex++;
        }


    }
}
