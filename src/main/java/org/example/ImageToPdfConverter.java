package org.example;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;

public class ImageToPdfConverter {
    public static void main(String[] args) throws IOException, DocumentException {
        String fileName,filePathIn,filePathOut,chose;
        leave();
        System.out.print("Введите:\n[One - для единоразового запуска]\n[Multi - для нескольких]\n[Exit - Для выхода]\nВаш выбор:");
        Scanner scanner = new Scanner(System.in);
        chose = scanner.nextLine();
        switch (chose){
            case("One"):
                //Запрашиваем у пользователя путь папки источника и назначения
                filePathIn = pathFinderIn();
                filePathOut = pathFinderOut(filePathIn);
                //считаем количество изображений в папке
                countFilesWithExtensions(filePathIn);
                //Используем функцию получения имени будущего файла
                fileName = futureFileName();
                //Выполнение основной функции - фасовка фото по PDF
                mainFunc(fileName, filePathIn);
                //Перемещение конечного файла в указанный путь
                fileMove(filePathIn, filePathOut, fileName);
                break;
            case("Multi"):
                // Выводим сообщение в консоль
                System.out.print("Введите путь к папке в которую будут\nформироваться все будущие PDF файлы: ");
                // Получаем у пользователя путь
                filePathOut = scanner.nextLine();
                while (true) {
                    //Запрашиваем у пользователя путь папки источника и назначения
                    filePathIn = pathFinderIn();
                    //считаем количество изображений в папке
                    countFilesWithExtensions(filePathIn);
                    //Используем функцию получения имени будущего файла
                    fileName = futureFileName();
                    //Выполнение основной функции - фасовка фото по PDF
                    mainFunc(fileName, filePathIn);
                    //Перемещение конечного файла в указанный путь
                    fileMove(filePathIn, filePathOut, fileName);
                    leave();
                }
            case("Exit"):
                System.exit(0);
                break;
        }

    }
    public static void mainFunc(String name,String In){
        String fileName = name,filePathIn = In;
        //создаем новый, пустой документ
        Document document = new Document(PageSize.A4, 5, 5, 5, 5);
        // Установка узких полей
        Rectangle rect = new Rectangle(5, 5, PageSize.A4.getWidth() - 5, PageSize.A4.getHeight() - 5);
        document.setPageSize(rect);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        //Открытие и задание пустой страницы
        document.open();
        try {
            document.add(new Chunk(""));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        //Создание файла
        File directory = new File(filePathIn);
        //Создание фильтров для выборки из директории только изображений
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                return lowercaseName.endsWith(".jpg") || lowercaseName.endsWith(".png");
            }
        };
        //Применение фильтров и запись в массив файлов
        File[] files = directory.listFiles(filter);

        Image img = null;
        Integer i = 0;
        //Цикл записи фото в PDF до тех пор пока не кончатся фото
        while (i != files.length){
            try {
                img = Image.getInstance(files[i].toURI().toString());
                //Выравнивание изображения по проценту покрытия страницы
                img.scaleToFit(document.getPageSize().getWidth() * 0.98f, document.getPageSize().getHeight() * 0.98f);
            } catch (BadElementException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                //Вставка форматированного фото на страницу
                document.add(img);
                //Создание пустой страницы
                document.newPage();
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
            i+=1;
        }
        document.close();
    }//Функция фасовки фото по DPF
    public static void countFilesWithExtensions(String pathD) {
        File directory = new File(pathD);
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                return lowercaseName.endsWith(".jpg") || lowercaseName.endsWith(".png");
            }
        };
        File[] files = directory.listFiles(filter);
        //проверочный вывод названия изображений и их количества
        Integer i=0;
        while (i != files.length){
            System.out.printf("[%d]  Имя файла:%s\n",i+1,files[i]);
            i+=1;
        }
        System.out.printf("Количество изображений в директории:%d\n",files.length);

    }//Функция подсчета изображений в файле
    public static String futureFileName(){
        // Выводим сообщение в консоль
        System.out.print("Введите название будущего PDF файла:");
        // Запрашиваем у пользователя название файла
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();
        // Добавляем приставку ".pdf"
        fileName += ".pdf";
        return fileName;
    }//Функция получения от пользователя имени будущего файла
    public static String pathFinderIn(){
        // Выводим сообщение в консоль
        System.out.print("Введите путь к папке с фотографиями:");
        // Получаем от пользователя путь
        Scanner scanner = new Scanner(System.in);
        String pathIn = scanner.nextLine();
        return pathIn;
    }//Функция получения от пользователя папки с фото
    public static String pathFinderOut(String filePathIn){
        // Выводим сообщение в консоль
        System.out.println("Введите путь к папке для создания PDF файла");
        System.out.print("Или введите 1 для создания файла в том же месте что и фото:");
        // Получаем у пользователя путь
        Scanner scanner = new Scanner(System.in);
        String pathOut = scanner.nextLine();
        if (Objects.equals(pathOut, "1")) {
            pathOut=filePathIn;
        }
        return pathOut;
    }//Функция определения пути назначения
    public static void fileMove(String In, String Out, String name){
        Path pathIn = Path.of(name);
        Path pathOut = Path.of(Out +"\\" + name);
        try {
            Files.move(pathIn,pathOut);
            System.out.println("DPF файл успешно сформирован по указанному пути.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Указаный путь: "+pathOut+"\n\n\n");
    }//Функция перемещения конечного файла в нужную директ.
    public static void leave(){
        Integer exit=1;
        System.out.println("Что бы начать/продолжить введите любой символ");
        System.out.print("Что бы завершить программу введите 0: ");
        Scanner scanner = new Scanner(System.in);
        if (Objects.equals(scanner.nextLine(),"0")){
            System.exit(0);
        }
    }//Функция проверки выхода пользователя
}

