package com.example.wolseytechhr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuickSort {

    public static void sort(List<String[]> arr) {
        quickSort(arr, 0, arr.size() - 1);
    }

    private static void quickSort(List<String[]> arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);

            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private static int partition(List<String[]> arr, int low, int high) {
        String[] pivot = arr.get(high);
        Date pivotDate = parseDate(pivot[2]);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            Date currentDate = parseDate(arr.get(j)[2]);
            if (currentDate.compareTo(pivotDate) < 0) {
                i++;
                swap(arr, i, j);
            }
        }

        swap(arr, i + 1, high);
        return i + 1;
    }

    private static void swap(List<String[]> arr, int i, int j) {
        String[] temp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, temp);
    }

    private static Date parseDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
}
