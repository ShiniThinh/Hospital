package appointmentsystem;

public class HeapSort {
    public static void heapSort(DoctorAppointmentCount[] arr) {
        int n = arr.length;

        // Xây dựng heap (tạo max heap)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // Trích xuất từng phần tử từ heap
        for (int i = n - 1; i >= 0; i--) {
            // Đổi chỗ phần tử đầu (lớn nhất) với phần tử cuối
            DoctorAppointmentCount temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;

            // Heapify lại heap còn lại
            heapify(arr, i, 0);
        }
    }

    private static void heapify(DoctorAppointmentCount[] arr, int n, int i) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;

        if (l < n && arr[l].compareTo(arr[largest]) > 0)
            largest = l;

        if (r < n && arr[r].compareTo(arr[largest]) > 0)
            largest = r;

        if (largest != i) {
            DoctorAppointmentCount swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;

            heapify(arr, n, largest);
        }
    }
}
