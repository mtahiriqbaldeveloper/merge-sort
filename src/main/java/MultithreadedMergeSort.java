

    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.Iterator;
    import java.util.List;
    import java.util.Random;
    import java.util.concurrent.Executors;
    import java.util.concurrent.ExecutorService;

    public class MultithreadedMergeSort {

        private int[] array;
        private int numThreads;
        private List<int[]> sortedFragments;

        private MultithreadedMergeSort(int numThreads, int[] array) {
            this.numThreads = numThreads;
            this.array = array;
        }

        // Basic algorithm: it sorts recursively a fragment
        private static void recursiveMergeSort(int[] array, int begin, int end) {
            if (end - begin > 1) {
                int middle = (begin + end) / 2;
                recursiveMergeSort(array, begin, middle);
                recursiveMergeSort(array, middle, end);
                merge(array, begin, middle, end);
            }
        }

        // Basic algorithm: it merges two consecutives sorted fragments
        private static void merge(int[] array, int begin, int middle, int end) {
            int[] firstPart = Arrays.copyOfRange(array, begin, middle);
            int i = 0;
            int j = middle;
            int k = begin;
            while (i < firstPart.length && j < end) {
                if (firstPart[i] <= array[j]) {
                    array[k++] = firstPart[i++];
                } else {
                    array[k++] = array[j++];
                }
            }
            if (i < firstPart.length) {
                System.arraycopy(firstPart, i, array, k, firstPart.length - i);
            }
        }

        public static void sort(int[] array, int numThreads) throws InterruptedException {
            if (array != null && array.length > 1) {
                if (numThreads > 1) {
                    new MultithreadedMergeSort(numThreads, array).mergeSort();
                } else {
                    recursiveMergeSort(array, 0, array.length);
                }
            }
        }

        private synchronized void mergeSort() throws InterruptedException {
            // A thread pool
            ExecutorService executors = Executors.newFixedThreadPool(numThreads);
            this.sortedFragments = new ArrayList<>(numThreads - 1);
            int begin = 0;
            int end = 0;

            // it split the work
            for (int i = 1; i <= (numThreads - 1); i++) {
                begin = end;
                end = (array.length * i) / (numThreads - 1);
                // sending the work to worker
                executors.execute(new MergeSortWorker(begin, end));
            }
            // this is waiting until work is done
            wait();

            // shutdown the thread pool.
            executors.shutdown();
        }

        private synchronized int[] notifyFragmentSorted(int begin, int end) {
            if (begin > 0 || end < array.length) {
                // the array is not completely sorted

                Iterator<int[]> it = sortedFragments.iterator();
                // searching a previous or next fragment
                while (it.hasNext()) {
                    int[] f = it.next();
                    if (f[1] == begin || f[0] == end) {
                        // It found a previous/next fragment
                        it.remove();
                        return f;
                    }
                }
                sortedFragments.add(new int[]{begin, end});
            } else {
                // the array is sorted
                notify();
            }
            return null;
        }

        private class MergeSortWorker implements Runnable {

            int begin;
            int end;

            public MergeSortWorker(int begin, int end) {
                this.begin = begin;
                this.end = end;
            }

            @Override
            public void run() {
                // Sort a fragment
                recursiveMergeSort(array, begin, end);
                // notify the sorted fragment
                int[] nearFragment = notifyFragmentSorted(begin, end);

                while (nearFragment != null) {
                    // there's more work: merge two consecutive sorted fragments, (begin, end) and nearFragment
                    int middle;
                    if (nearFragment[0] < begin) {
                        middle = begin;
                        begin = nearFragment[0];
                    } else {
                        middle = nearFragment[0];
                        end = nearFragment[1];
                    }
                    merge(array, begin, middle, end);
                    nearFragment = notifyFragmentSorted(begin, end);
                }
            }
        }

        public static void main(String[] args) throws InterruptedException {
            int numThreads = 5;

            Random rand = new Random();
            int[] original = new int[9000000];
            for (int i = 0; i < original.length; i++) {
                original[i] = rand.nextInt(1000);
            }

            long startTime = System.currentTimeMillis();

            MultithreadedMergeSort.sort(original, numThreads);

            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            // warning: Take care with microbenchmarks
            System.out.println(numThreads + "-thread MergeSort takes: " + (float) elapsedTime / 1000 + " seconds");
        }
    }