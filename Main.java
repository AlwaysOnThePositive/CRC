package mirea.missiles;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import static java.lang.Math.abs;

public class Main {

    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
        int countOfNums = 1000;

        Main main = new Main();

        // последовательность           ИЗМЕНИТЬ!!!!!!!!!!!!!!
        byte[] sequenceOfNums = main.generateRand(countOfNums);
//        System.out.println("initial sequence: " + Arrays.toString(sequenceOfNums));

        // полином       ИЗМЕНИТЬ!!!!!!!!!!!!!!
        byte[] poly = {1, 0, 0, 0, 1, 0, 0, 1};
        System.out.println("polynom: " + Arrays.toString(poly));

        byte[] sequenceOfNumsChanged = main.sequenceChange(sequenceOfNums, poly);
//        System.out.println("changed sequence (add zeroes): " + Arrays.toString(sequenceOfNumsChanged));

        byte[] CRC = main.findCRC(sequenceOfNumsChanged, poly);
        System.out.println("CRC: " + Arrays.toString(CRC));

        // проверка вычислений
        System.out.println("-----------------------------------");
        System.out.println("test");
        sequenceOfNumsChanged = main.sequenceChangeWithCRC (sequenceOfNums, CRC);
//        System.out.println("changed sequence (add CRC): " + Arrays.toString(sequenceOfNumsChanged));
        byte[] checkCRC = main.findCRC(sequenceOfNumsChanged, poly);
        System.out.println("result of checking: " + Arrays.toString(checkCRC));
        main.test(checkCRC);
    }

    private void test(byte[] checkCRC) {
        boolean ToF = true;
        for (int i = 0; i < checkCRC.length; i++) {
            if (checkCRC[i] != 0) {
                ToF = false;
                break;
            }
        }

        if (ToF == true) {
            System.out.println("everything is correct");
        } else {
            System.out.println("mistake");
        }
    }

    private byte[] sequenceChangeWithCRC(byte[] sequenceOfNums, byte[] crc) {
        byte[] tempSequenceForChange = new byte[sequenceOfNums.length + (crc.length)];
        int numInCRC= 0;
        for (int i = 0; i < tempSequenceForChange.length; i++) {
            if (i < sequenceOfNums.length) {
                tempSequenceForChange[i] = sequenceOfNums[i];
            } else {
                tempSequenceForChange[i] = crc[numInCRC];
                numInCRC++;
            }
        }

        return tempSequenceForChange;
    }

    // изменение последовательности (добавление в конец нулей или полинома)
    private byte[] sequenceChange(byte[] sequenceOfNums, byte[] poly) {
        byte[] tempSequenceForChange = new byte[sequenceOfNums.length + (poly.length - 1)];

        for (int i = 0; i < tempSequenceForChange.length; i++) {
            if (i < sequenceOfNums.length) {
                tempSequenceForChange[i] = sequenceOfNums[i];
            } else {
                tempSequenceForChange[i] = 0;
            }
        }

        return tempSequenceForChange;
    }

    // рабочая часть для вычислений
    private byte[] findCRC(byte[] sequenceOfNumsChanged, byte[] poly) {
        // создание массива
        byte[] workingPart = new byte[poly.length];
        for (int i = 0; i < poly.length; i++) {
            workingPart[i] = sequenceOfNumsChanged[i];
        }

        //первое изменение
        byte[] result;
        result = XOR(workingPart, poly);
        System.out.println("r - " + Arrays.toString(result));
        workingPart = offset(result, sequenceOfNumsChanged[poly.length]);
        System.out.println("first change: " + Arrays.toString(workingPart));

        // проход по битам
        for (int i = poly.length; i < sequenceOfNumsChanged.length - 1; i++) {
            if (workingPart[0] == 0) {
                workingPart = offset(workingPart, sequenceOfNumsChanged[i + 1]);
                System.out.println("w propusk - " + Arrays.toString(workingPart));
                continue;
            }
            result = XOR(workingPart, poly);
            System.out.println("r - " + Arrays.toString(result));
            workingPart = offset(result, sequenceOfNumsChanged[i + 1]);

            System.out.println("w - " + Arrays.toString(workingPart));
        }

        // преобразование working part в CRC
        byte[] CRC = new byte[workingPart.length - 1];
        for (int i = 0; i < CRC.length; i++) {
            CRC[i] = workingPart[i + 1];
        }

        return CRC;
    }

    // смещение последовательности
    private byte[] offset(byte[] result, byte b) {
        byte[] temp = new byte[result.length];
        for (int i = 0; i < temp.length - 1; i++) {
            temp[i] = result[i + 1];
        }
        temp[result.length - 1] = b;

        return temp;
    }

    // XOR для рабочей части и полинома
    private byte[] XOR(byte[] workingPart, byte[] poly) {
        byte[] temp = new byte[poly.length];
        for (int i = 0; i < poly.length;i++) {
            if (workingPart[i] == poly[i]) {
                temp[i] = 0;
            } else {
                temp[i] = 1;
            }
        }

        return temp;
    }

    // создание рандомной последовательности
    private byte[] generateRand(int countOfNums) {
        Random random = new Random();

        byte[] tempArray = new byte[countOfNums];
        for (int i = 0; i < countOfNums; i++) {
            tempArray[i] = (byte) (abs(random.nextInt()) % 2);
        }

        return tempArray;
    }
}
